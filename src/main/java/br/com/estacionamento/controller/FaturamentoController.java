package br.com.estacionamento.controller;

import br.com.estacionamento.dao.VeiculoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FaturamentoController {

    @FXML private Label lblTotal;
    private VeiculoDAO dao = new VeiculoDAO();

    @FXML
    public void initialize() {
        atualizarSaldo();
    }

    @FXML
    private void atualizarSaldo() {
        double total = dao.calcularTotalFaturado();
        lblTotal.setText(String.format("%.2f", total));
    }
}