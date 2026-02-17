package br.com.estacionamento.controller;

import br.com.estacionamento.dao.VeiculoDAO;
import br.com.estacionamento.model.Veiculo;
import br.com.estacionamento.util.CalculadoraEstadia;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ListaController {
    @FXML private TableView<Veiculo> tblVeiculos;
    @FXML private TableColumn<Veiculo, String> colPlaca;
    @FXML private TableColumn<Veiculo, String> colModelo;
    @FXML private TableColumn<Veiculo, String> colCor;
    @FXML private TableColumn<Veiculo, String> colTipo;

    private VeiculoDAO dao = new VeiculoDAO();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

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
            // Verificação de segurança para evitar erro de data nula
            if (selecionado.getDataEntrada() == null) {
                new Alert(Alert.AlertType.ERROR, "Erro: Veículo sem registro de entrada no banco!").show();
                return;
            }

            LocalDateTime entrada = selecionado.getDataEntrada();
            LocalDateTime agora = LocalDateTime.now();

            // Calcula o valor com base na nossa regra de negócio
            double valorTotal = CalculadoraEstadia.calcular(entrada, agora);

            // Interface de confirmação com o resumo financeiro
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Confirmar Saída");
            alerta.setHeaderText("Resumo do Pagamento - Placa: " + selecionado.getPlaca());
            alerta.setContentText(String.format(
                    "🕒 Entrada: %s\n" +
                            "🕒 Saída: %s\n\n" +
                            "💰 VALOR TOTAL: R$ %.2f",
                    entrada.format(formatter),
                    agora.format(formatter),
                    valorTotal));

            // Processamento da saída após confirmação do usuário
            Optional<ButtonType> result = alerta.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // CHAMADA DO NOVO MÉTODO: Move para transações e remove do pátio
                dao.finalizarTransacao(selecionado.getPlaca(), valorTotal);

                atualizarTabela();
                System.out.println("Pagamento de R$ " + valorTotal + " registrado para a placa " + selecionado.getPlaca());
            }

        } else {
            new Alert(Alert.AlertType.WARNING, "Por favor, selecione um veículo na tabela!").show();
        }
    }
    @FXML
    private void mostrarFaturamento() {
        double total = dao.calcularTotalFaturado();
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Relatório de Vendas");
        alerta.setHeaderText("Faturamento Total Acumulado");
        alerta.setContentText(String.format("O total arrecadado até agora é: R$ %.2f", total));
        alerta.show();
    }
}