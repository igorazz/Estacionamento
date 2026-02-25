package br.com.estacionamento.controller;

import br.com.estacionamento.dao.VeiculoDAO;
import br.com.estacionamento.model.Mensalista;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MensalistasListaController {
    @FXML private TableView<Mensalista> tblMensalistas;
    @FXML private TableColumn<Mensalista, String> colNome;
    @FXML private TableColumn<Mensalista, String> colPlaca;
    @FXML private TableColumn<Mensalista, LocalDate> colVencimento;
    @FXML private TableColumn<Mensalista, Double> colValor;

    private VeiculoDAO dao = new VeiculoDAO();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colVencimento.setCellValueFactory(new PropertyValueFactory<>("vencimento"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        // Formatação da data na célula da tabela
        colVencimento.setCellFactory(column -> new TableCell<Mensalista, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(dtf));
                }
            }
        });

        atualizarTabela();
    }

    @FXML
    public void atualizarTabela() {
        tblMensalistas.setItems(FXCollections.observableArrayList(dao.listarTodosMensalistas()));
    }

    @FXML
    private void handleExcluir() {
        Mensalista selecionado = tblMensalistas.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione um mensalista para remover!").show();
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja realmente remover o mensalista " + selecionado.getNome() + "?",
                ButtonType.YES, ButtonType.NO);

        confirmacao.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.YES) {
                dao.excluirMensalista(selecionado.getId());
                atualizarTabela();
            }
        });
    }
}