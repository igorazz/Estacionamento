package br.com.estacionamento.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class CalculadoraEstadia {
    private static final double VALOR_PRIMEIRA_HORA = 10.00;
    private static final double VALOR_FRACAO_15_MIN = 2.50;

    public static double calcular(LocalDateTime entrada, LocalDateTime saida) {
        Duration duracao = Duration.between(entrada, saida);
        long minutosTotal = duracao.toMinutes();

        // RN01: Tolerância de 15 minutos
        if (minutosTotal <= 15) {
            return 0.0;
        }

        // Se passou de 15 min, cobra a primeira hora cheia
        if (minutosTotal <= 60) {
            return VALOR_PRIMEIRA_HORA;
        }

        // RN04: Valor fixo + acréscimo proporcional por fração de 15 min
        long minutosExcedentes = minutosTotal - 60;
        long fracoes = (long) Math.ceil(minutosExcedentes / 15.0);

        return VALOR_PRIMEIRA_HORA + (fracoes * VALOR_FRACAO_15_MIN);
    }
}