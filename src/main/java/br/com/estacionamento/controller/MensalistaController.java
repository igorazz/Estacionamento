package br.com.estacionamento.controller;

import br.com.estacionamento.dao.VeiculoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class MensalistaController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtPlaca;
    @FXML private DatePicker dpVencimento; // Agora usando DatePicker
    @FXML private TextField txtValor;

    private VeiculoDAO dao = new VeiculoDAO();

    @FXML
    private void handleSalvar() {
        try {
            String nome = txtNome.getText();
            String cpf = txtCpf.getText();
            String placa = txtPlaca.getText().toUpperCase();
            LocalDate vencimento = dpVencimento.getValue();

            if (nome.isEmpty() || placa.isEmpty() || vencimento == null) {
                new Alert(Alert.AlertType.ERROR, "Nome, Placa e Vencimento são obrigatórios!").show();
                return;
            }

            double valor = Double.parseDouble(txtValor.getText().replace(",", "."));

            dao.salvarMensalista(nome, cpf, placa, vencimento, valor);

            new Alert(Alert.AlertType.INFORMATION, "Mensalista cadastrado com sucesso!").show();
            limparCampos();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Valor da mensalidade inválido.").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar: " + e.getMessage()).show();
        }
    }

    @FXML
    private void handleCancelar() {
        limparCampos();
    }

    private void limparCampos() {
        txtNome.clear();
        txtCpf.clear();
        txtPlaca.clear();
        dpVencimento.setValue(null);
        txtValor.setText("300.00");
    }
    @FXML
    public void initialize() {
        // Formata o CPF enquanto o usuário digita
        txtCpf.textProperty().addListener((obs, antigo, novo) -> {
            if (novo == null) return;
            // Remove tudo que não for número
            String apenasNumeros = novo.replaceAll("[^\\d]", "");

            if (apenasNumeros.length() > 11) {
                apenasNumeros = apenasNumeros.substring(0, 11);
            }

            // Aplica a máscara 000.000.000-00
            StringBuilder sb = new StringBuilder(apenasNumeros);
            if (sb.length() > 3) sb.insert(3, ".");
            if (sb.length() > 7) sb.insert(7, ".");
            if (sb.length() > 11) sb.insert(11, "-");

            // Atualiza o texto sem causar um loop infinito
            if (!novo.equals(sb.toString())) {
                txtCpf.setText(sb.toString());
                txtCpf.positionCaret(sb.length()); // Mantém o cursor no final
            }
        });
    }
}