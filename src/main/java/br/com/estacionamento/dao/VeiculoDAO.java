package br.com.estacionamento.dao;

import br.com.estacionamento.model.Veiculo;
import br.com.estacionamento.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; // IMPORTANTE
import java.sql.SQLException;
import java.util.ArrayList;   // IMPORTANTE
import java.util.List;

public class VeiculoDAO {

    public void salvar(Veiculo veiculo) {
        String sql = "INSERT INTO veiculos (placa, modelo, cor, tipo) VALUES (?, ?, ?, ?)";

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
        // Busca todos os veículos cadastrados para testarmos a tabela primeiro
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
                lista.add(v);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar veículos: ", e);
        }
        return lista;
    }
}