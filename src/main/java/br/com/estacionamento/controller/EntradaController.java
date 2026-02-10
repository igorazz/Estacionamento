package br.com.estacionamento.controller;

import br.com.estacionamento.dao.VeiculoDAO;
import br.com.estacionamento.model.Veiculo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;

public class EntradaController {

    @FXML private TextField txtPlaca;
    @FXML private TextField txtModelo;
    @FXML private TextField txtCor;
    @FXML private ComboBox<String> cbTipo;
    @FXML private Label lblStatus;

    private VeiculoDAO dao = new VeiculoDAO();

    @FXML
    public void initialize() {
        // Preenche o ComboBox com as opções de tipo
        cbTipo.setItems(FXCollections.observableArrayList("CARRO", "MOTO"));
    }
    @FXML
    private void handleSalvar() {
        try {
            // Criando o objeto com os dados da tela
            Veiculo v = new Veiculo();
            v.setPlaca(txtPlaca.getText().toUpperCase());
            v.setModelo(txtModelo.getText());
            v.setCor(txtCor.getText());
            v.setTipo(cbTipo.getValue());

            // Salvando no Banco
            dao.salvar(v);

            lblStatus.setText("Veículo " + v.getPlaca() + " registrado!");
            limparCampos();
        } catch (Exception e) {
            lblStatus.setText("Erro ao salvar: " + e.getMessage());
        }
    }
    private void limparCampos() {
        txtPlaca.clear();
        txtModelo.clear();
        txtCor.clear();
    }
}