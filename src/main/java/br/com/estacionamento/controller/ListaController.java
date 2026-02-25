package br.com.estacionamento.controller;

import br.com.estacionamento.dao.VeiculoDAO;
import br.com.estacionamento.model.Veiculo;
import br.com.estacionamento.util.CalculadoraEstadia;
import br.com.estacionamento.util.TicketService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListaController {
    @FXML private TableView<Veiculo> tblVeiculos;
    @FXML private TableColumn<Veiculo, String> colPlaca;
    @FXML private TableColumn<Veiculo, String> colModelo;
    @FXML private TableColumn<Veiculo, String> colCor;
    @FXML private TableColumn<Veiculo, String> colTipo;
    @FXML private TextField txtBuscaPlaca;

    private VeiculoDAO dao = new VeiculoDAO();
    private DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @FXML
    public void initialize() {
        colPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colCor.setCellValueFactory(new PropertyValueFactory<>("cor"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        atualizarTabela();
    }

    @FXML
    public void atualizarTabela() {
        tblVeiculos.setItems(FXCollections.observableArrayList(dao.listarNoPatio()));
    }

    @FXML
    private void handleBuscarPlaca() {
        String placa = txtBuscaPlaca.getText();
        if (placa == null || placa.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Digite uma placa para buscar.").show();
            return;
        }
        List<String> hist = dao.buscarHistoricoPorPlaca(placa);
        if (hist.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Nenhum histórico encontrado.").show();
        } else {
            ListView<String> lv = new ListView<>(FXCollections.observableArrayList(hist));
            lv.setPrefSize(400, 200);
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Histórico");
            a.getDialogPane().setContent(lv);
            a.showAndWait();
        }
    }

    @FXML
    private void handleSaida() {
        Veiculo sel = tblVeiculos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione um veículo na tabela!").show();
            return;
        }

        // Verifica se é Mensalista (Opção C)
        if (dao.eMensalista(sel.getPlaca())) {
            dao.finalizarTransacao(sel.getPlaca(), 0.0, "MENSALISTA");
            new Alert(Alert.AlertType.INFORMATION, "Mensalista liberado.").show();
            atualizarTabela();
            return;
        }

        LocalDateTime entrada = sel.getDataEntrada();
        LocalDateTime agora = LocalDateTime.now();

        // Busca preço do banco (Opção B)
        double precoHora = dao.buscarValorHora();
        double total = CalculadoraEstadia.calcular(entrada, agora, precoHora);

        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Finalizar Pagamento - " + sel.getPlaca());

        // --- INFORMAÇÕES DE TEMPO ADICIONADAS ---
        Label lblEntrada = new Label("📥 Entrada: " + entrada.format(horaFormatter));
        Label lblSaida = new Label("📤 Saída:   " + agora.format(horaFormatter));
        Label lblSeparador = new Label("-----------------------------------");
        Label lblTotal = new Label("VALOR TOTAL: R$ " + String.format("%.2f", total));
        lblTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TextField txtRec = new TextField(String.format("%.2f", total));
        ComboBox<String> cb = new ComboBox<>(FXCollections.observableArrayList("DINHEIRO", "PIX", "CARTÃO"));
        cb.setValue("DINHEIRO");

        VBox layout = new VBox(10,
                lblEntrada, lblSaida, lblSeparador,
                lblTotal,
                new Label("Valor Recebido:"), txtRec,
                new Label("Forma de Pagamento:"), cb
        );
        layout.setStyle("-fx-padding: 20;");

        d.getDialogPane().setContent(layout);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        d.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    double valorPago = Double.parseDouble(txtRec.getText().replace(",", "."));
                    if (valorPago >= total) {
                        dao.finalizarTransacao(sel.getPlaca(), total, cb.getValue());
                        String recibo = TicketService.gerarTicketSaida(sel, total, valorPago, valorPago - total, cb.getValue());
                        exibirRecibo(recibo);
                        atualizarTabela();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Valor insuficiente!").show();
                    }
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Valor inválido.").show();
                }
            }
        });
    }

    private void exibirRecibo(String texto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recibo de Saída");
        alert.setHeaderText(null);
        TextArea area = new TextArea(texto);
        area.setEditable(false);
        area.setStyle("-fx-font-family: 'Courier New';");
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }
}