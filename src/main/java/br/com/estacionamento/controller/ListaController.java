package br.com.estacionamento.controller;

import br.com.estacionamento.dao.VeiculoDAO;
import br.com.estacionamento.model.Veiculo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class ListaController {
    @FXML private TableView<Veiculo> tblVeiculos;
    @FXML private TableColumn<Veiculo, String> colPlaca;
    @FXML private TableColumn<Veiculo, String> colModelo;
    @FXML private TableColumn<Veiculo, String> colCor;
    @FXML private TableColumn<Veiculo, String> colTipo;

    private VeiculoDAO dao = new VeiculoDAO();

    @FXML
    public void initialize() {
        // Configura quais atributos da classe Veiculo aparecem em cada coluna
        colPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colCor.setCellValueFactory(new PropertyValueFactory<>("cor"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        atualizarTabela();
    }

    @FXML
    public void atualizarTabela() {
        List<Veiculo> veiculos = dao.listarNoPatio();
        tblVeiculos.setItems(FXCollections.observableArrayList(veiculos));
    }

    @FXML
    private void handleSaida() {
        Veiculo selecionado = tblVeiculos.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            System.out.println("Processando saída de: " + selecionado.getPlaca());
            // Aqui chamaremos a lógica de cálculo que criamos antes!
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione um veículo na tabela!");
            alert.show();
        }
    }
}