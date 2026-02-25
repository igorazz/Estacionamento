    package br.com.estacionamento.controller;

    import br.com.estacionamento.dao.VeiculoDAO;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;
    import javafx.scene.chart.PieChart;
    import javafx.scene.control.Label;
    import java.util.Map;

    public class FaturamentoController {

        @FXML private PieChart chartFaturamento;
        @FXML private PieChart chartFormas;
        @FXML private Label lblTotalGeral;

        private VeiculoDAO dao = new VeiculoDAO();

        @FXML
        public void initialize() {
            atualizarGrafico();
        }

        @FXML
        private void atualizarGrafico() {
            double total = dao.calcularTotalFaturado();
            lblTotalGeral.setText(String.format("R$ %.2f", total));
            configurarGraficoVeiculos();
            configurarGraficoPagamentos();
        }

        private void configurarGraficoVeiculos() {
            Map<String, Double> dados = dao.obterFaturamentoPorTipo();
            ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

            dados.forEach((tipo, valor) -> {
                String nome = (tipo == null || tipo.isEmpty()) ? "OUTROS" : tipo;
                data.add(new PieChart.Data(nome, valor));
            });
            chartFaturamento.setData(data);

            for (PieChart.Data d : chartFaturamento.getData()) {
                if (d.getName() != null) {
                    if (d.getName().equals("CARRO")) d.getNode().setStyle("-fx-pie-color: #1e88e5;");
                    else if (d.getName().equals("MOTO")) d.getNode().setStyle("-fx-pie-color: #43a047;");
                    else d.getNode().setStyle("-fx-pie-color: #757575;");
                }
            }
        }

        private void configurarGraficoPagamentos() {
            Map<String, Double> dados = dao.obterFaturamentoPorForma();
            ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

            dados.forEach((forma, valor) -> {
                String nome = (forma == null || forma.isEmpty()) ? "NÃO INF." : forma;
                data.add(new PieChart.Data(nome, valor));
            });
            chartFormas.setData(data);

            for (PieChart.Data d : chartFormas.getData()) {
                if (d.getName() != null) {
                    switch (d.getName()) {
                        case "DINHEIRO": d.getNode().setStyle("-fx-pie-color: #ffb300;"); break;
                        case "PIX": d.getNode().setStyle("-fx-pie-color: #00897b;"); break;
                        case "CARTÃO": d.getNode().setStyle("-fx-pie-color: #5e35b1;"); break;
                        default: d.getNode().setStyle("-fx-pie-color: #cfd8dc;");
                    }
                }
            }
        }
    }