package br.com.estacionamento.dao;

import br.com.estacionamento.model.Veiculo;
import br.com.estacionamento.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp; // Importante para datas
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

    public void salvar(Veiculo veiculo) {
        // Adicionamos a data_entrada explicitamente para garantir que o registro nasça com data
        String sql = "INSERT INTO veiculos (placa, modelo, cor, tipo, data_entrada) VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setString(3, veiculo.getCor());
            stmt.setString(4, veiculo.getTipo());

            stmt.executeUpdate();
            System.out.println("Veículo salvo com sucesso!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar veículo: ", e);
        }
    }

    public List<Veiculo> listarNoPatio() {
        List<Veiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM veiculos";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Veiculo v = new Veiculo();
                v.setId(rs.getInt("id"));
                v.setPlaca(rs.getString("placa"));
                v.setModelo(rs.getString("modelo"));
                v.setCor(rs.getString("cor"));
                v.setTipo(rs.getString("tipo"));

                // Conversão segura de Timestamp para LocalDateTime
                Timestamp ts = rs.getTimestamp("data_entrada");
                if (ts != null) {
                    v.setDataEntrada(ts.toLocalDateTime());
                }
                lista.add(v);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar veículos: ", e);
        }
        return lista;
    }

    /**
     * NOVO MÉTODO DE FINALIZAÇÃO FINANCEIRA
     * Move os dados para a tabela transacoes e remove do pátio apenas se der tudo certo.
     */
    public void finalizarTransacao(String placa, double valor) {
        // Usamos aliases (v.) para evitar o erro de 'Unknown column' no SELECT
        String sqlInsert = "INSERT INTO transacoes (placa_veiculo, data_entrada, data_saida, valor_pago, status_transacao) " +
                "SELECT v.placa, v.data_entrada, NOW(), ?, 'CONCLUIDA' " +
                "FROM veiculos v WHERE v.placa = ?";

        String sqlDelete = "DELETE FROM veiculos WHERE placa = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false); // Inicia transação segura (ACID)

            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert);
                 PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete)) {

                // 1. Configura e executa a gravação no histórico
                stmtInsert.setDouble(1, valor);
                stmtInsert.setString(2, placa);
                stmtInsert.executeUpdate();

                // 2. Configura e executa a remoção da vaga ocupada
                stmtDelete.setString(1, placa);
                int linhasAfetadas = stmtDelete.executeUpdate();

                if (linhasAfetadas > 0) {
                    conn.commit(); // Efetiva as duas operações no banco
                    System.out.println("Transação finalizada: Veículo removido e pagamento registrado.");
                } else {
                    conn.rollback(); // Se não deletou ninguém, desfaz o insert
                    System.out.println("Aviso: Placa não encontrada no pátio.");
                }

            } catch (SQLException e) {
                conn.rollback(); // Se o histórico falhar, o carro NÃO sai do pátio (evita perda de dados)
                e.printStackTrace();
                throw new RuntimeException("Erro na transação de saída: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro de conexão ao finalizar saída: ", e);
        }
    }

    // Método antigo mantido apenas por compatibilidade (não recomendado após histórico)
    public void registrarSaida(String placa) {
        String sql = "DELETE FROM veiculos WHERE placa = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, placa);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar saída: ", e);
        }
    }
    public double calcularTotalFaturado() {
        String sql = "SELECT SUM(valor_pago) FROM transacoes";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1); // Pega o resultado da soma
            }
        } catch (SQLException e) {
            System.err.println("Erro ao calcular faturamento: " + e.getMessage());
        }
        return 0.0;
    }
}