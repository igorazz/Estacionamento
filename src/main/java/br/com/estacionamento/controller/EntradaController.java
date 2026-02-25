package br.com.estacionamento.controller;

import br.com.estacionamento.dao.VeiculoDAO;
import br.com.estacionamento.model.Veiculo;
import br.com.estacionamento.util.TicketService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;

public class EntradaController {

    @FXML private TextField txtPlaca;
    @FXML private TextField txtModelo;
    @FXML private TextField txtCor;
    @FXML private ComboBox<String> cbTipo; // Certifique-se que o fx:id no FXML é cbTipo

    private VeiculoDAO dao = new VeiculoDAO();

    @FXML
    public void initialize() {
        // ESSA LINHA É A QUE FAZ AS OPÇÕES APARECEREM
        cbTipo.setItems(FXCollections.observableArrayList("CARRO", "MOTO"));
    }

    @FXML
    private void handleSalvar() {
        if (cbTipo.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione o tipo do veículo!").show();
            return;
        }

        Veiculo v = new Veiculo();
        v.setPlaca(txtPlaca.getText().toUpperCase());
        v.setModelo(txtModelo.getText());
        v.setCor(txtCor.getText());
        v.setTipo(cbTipo.getValue());
        v.setDataEntrada(LocalDateTime.now());

        try {
            dao.salvar(v);

            // Exibe o ticket que fizemos antes
            exibirTicketConfirmacao(v);

            limparCampos();
            System.out.println("Veículo registrado com sucesso!");
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar: " + e.getMessage()).show();
        }
    }

    private void exibirTicketConfirmacao(Veiculo v) {
        String texto = TicketService.gerarTextoTicket(v);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ticket de Entrada");
        alert.setHeaderText(null);
        TextArea textArea = new TextArea(texto);
        textArea.setEditable(false);
        textArea.setStyle("-fx-font-family: 'Courier New';");
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private void limparCampos() {
        txtPlaca.clear();
        txtModelo.clear();
        txtCor.clear();
        cbTipo.getSelectionModel().clearSelection();
    }
}