package br.com.estacionamento.dao;

import br.com.estacionamento.model.Veiculo;
import br.com.estacionamento.model.Mensalista;
import br.com.estacionamento.util.ConnectionFactory;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VeiculoDAO {

    // --- PÁTIO E OPERAÇÕES ---
    public void salvar(Veiculo veiculo) {
        String sql = "INSERT INTO veiculos (placa, modelo, cor, tipo, data_entrada) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setString(3, veiculo.getCor());
            stmt.setString(4, veiculo.getTipo());
            stmt.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
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
                Timestamp ts = rs.getTimestamp("data_entrada");
                if (ts != null) v.setDataEntrada(ts.toLocalDateTime());
                lista.add(v);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return lista;
    }

    public void finalizarTransacao(String placa, double valor, String formaPagamento) {
        String sqlInsert = "INSERT INTO transacoes (placa_veiculo, data_entrada, data_saida, valor_pago, status_transacao, tipo_veiculo, forma_pagamento) " +
                "SELECT v.placa, v.data_entrada, NOW(), ?, 'CONCLUIDA', v.tipo, ? " +
                "FROM veiculos v WHERE v.placa = ?";
        String sqlDelete = "DELETE FROM veiculos WHERE placa = ?";
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert);
                 PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete)) {
                stmtInsert.setDouble(1, valor);
                stmtInsert.setString(2, formaPagamento);
                stmtInsert.setString(3, placa);
                stmtInsert.executeUpdate();
                stmtDelete.setString(1, placa);
                stmtDelete.executeUpdate();
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw new RuntimeException(e); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // --- GESTÃO DE MENSALISTAS (COM LOCALDATE) ---
    public void salvarMensalista(String nome, String cpf, String placa, LocalDate vencimento, double valor) {
        String sql = "INSERT INTO mensalistas (nome, cpf, placa_veiculo, data_vencimento, valor_mensalidade) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, cpf);
            stmt.setString(3, placa);
            stmt.setDate(4, Date.valueOf(vencimento)); // Converte LocalDate para SQL Date
            stmt.setDouble(5, valor);
            stmt.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Mensalista> listarTodosMensalistas() {
        List<Mensalista> lista = new ArrayList<>();
        String sql = "SELECT * FROM mensalistas ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Mensalista m = new Mensalista();
                m.setId(rs.getInt("id"));
                m.setNome(rs.getString("nome"));
                m.setCpf(rs.getString("cpf"));
                m.setPlaca(rs.getString("placa_veiculo"));
                Date d = rs.getDate("data_vencimento");
                if (d != null) m.setVencimento(d.toLocalDate());
                m.setValor(rs.getDouble("valor_mensalidade"));
                lista.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public void excluirMensalista(int id) {
        String sql = "DELETE FROM mensalistas WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean eMensalista(String placa) {
        String sql = "SELECT id FROM mensalistas WHERE placa_veiculo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) { return false; }
    }

    // --- CONFIGURAÇÕES E FATURAMENTO ---
    public double buscarValorHora() {
        String sql = "SELECT valor FROM configuracoes WHERE chave = 'valor_hora'";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return Double.parseDouble(rs.getString("valor"));
        } catch (Exception e) { return 20.0; }
        return 20.0;
    }

    public void atualizarValorHora(double novoValor) {
        String sql = "UPDATE configuracoes SET valor = ? WHERE chave = 'valor_hora'";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, String.valueOf(novoValor));
            stmt.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Map<String, Double> obterFaturamentoPorTipo() {
        Map<String, Double> dados = new HashMap<>();
        String sql = "SELECT IFNULL(tipo_veiculo, 'OUTROS') as tipo, SUM(valor_pago) as total FROM transacoes GROUP BY tipo";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) dados.put(rs.getString("tipo"), rs.getDouble("total"));
        } catch (SQLException e) { e.printStackTrace(); }
        return dados;
    }

    public Map<String, Double> obterFaturamentoPorForma() {
        Map<String, Double> dados = new HashMap<>();
        String sql = "SELECT IFNULL(forma_pagamento, 'NÃO INF.') as forma, SUM(valor_pago) as total FROM transacoes GROUP BY forma";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) dados.put(rs.getString("forma"), rs.getDouble("total"));
        } catch (SQLException e) { e.printStackTrace(); }
        return dados;
    }

    public double calcularTotalFaturado() {
        String sql = "SELECT SUM(valor_pago) FROM transacoes";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public List<String> buscarHistoricoPorPlaca(String placa) {
        List<String> resultados = new ArrayList<>();
        String sql = "SELECT * FROM transacoes WHERE placa_veiculo LIKE ? ORDER BY data_saida DESC";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + placa + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultados.add(String.format("Placa: %s | Saída: %s | Valor: R$ %.2f | Pagto: %s",
                        rs.getString("placa_veiculo"), rs.getTimestamp("data_saida").toLocalDateTime().format(fmt),
                        rs.getDouble("valor_pago"), rs.getString("forma_pagamento")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return resultados;
    }
}