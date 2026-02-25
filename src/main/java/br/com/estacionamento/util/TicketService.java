package br.com.estacionamento.util;

import br.com.estacionamento.model.Veiculo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TicketService {
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String gerarTextoTicket(Veiculo v) {
        return "================================\n" +
                "       ESTACIONAMENTO  \n" +
                "================================\n" +
                " PLACA:   " + v.getPlaca() + "\n" +
                " MODELO:  " + v.getModelo() + "\n" +
                " COR:     " + v.getCor() + "\n" +
                " ENTRADA: " + v.getDataEntrada().format(fmt) + "\n" +
                "--------------------------------\n" +
                "      CONSERVE ESTE TICKET      \n" +
                "   NÃO SOMOS RESPONSÁVEIS POR   \n" +
                "   OBJETOS DEIXADOS NO VEÍCULO  \n" +
                "================================";
    }

    public static String gerarTicketSaida(Veiculo v, double valor, double pago, double troco, String forma) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "================================\n" +
                "       RECIBO DE PAGAMENTO      \n" +
                "================================\n" +
                "PLACA:    " + v.getPlaca() + "\n" +
                "ENTRADA:  " + v.getDataEntrada().format(fmt) + "\n" +
                "SAÍDA:    " + LocalDateTime.now().format(fmt) + "\n" +
                "--------------------------------\n" +
                "FORMA PAGTO: " + forma + "\n" +
                "VALOR TOTAL: R$ " + String.format("%.2f", valor) + "\n" +
                "VALOR PAGO:  R$ " + String.format("%.2f", pago) + "\n" +
                "TROCO:       R$ " + String.format("%.2f", troco) + "\n" +
                "================================";
    }
}