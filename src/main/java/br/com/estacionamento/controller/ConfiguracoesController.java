package br.com.estacionamento.controller;

import br.com.estacionamento.dao.VeiculoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ConfiguracoesController {

    @FXML private TextField txtValorHora;
    private VeiculoDAO dao = new VeiculoDAO();

    @FXML
    public void initialize() {
        // Ao abrir a aba, ele carrega o valor que já está no banco
        double atual = dao.buscarValorHora();
        txtValorHora.setText(String.format("%.2f", atual).replace(",", "."));
    }

    @FXML
    private void handleSalvarPreco() {
        try {
            double novoValor = Double.parseDouble(txtValorHora.getText().replace(",", "."));
            dao.atualizarValorHora(novoValor);
            new Alert(Alert.AlertType.INFORMATION, "Preço atualizado com sucesso!").show();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Por favor, insira um valor numérico válido.").show();
        }
    }
}