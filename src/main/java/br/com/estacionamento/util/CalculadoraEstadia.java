package br.com.estacionamento.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class CalculadoraEstadia {
    private static final int TOLERANCIA_MINUTOS = 15;

    public static double calcular(LocalDateTime entrada, LocalDateTime saida, double valorHora) {
        if (entrada == null || saida == null) return 0.0;

        Duration duracao = Duration.between(entrada, saida);
        long minutosTotal = duracao.toMinutes();

        if (minutosTotal <= TOLERANCIA_MINUTOS) return 0.0;
        if (minutosTotal <= 60) return valorHora;

        double valorFracao = valorHora / 4.0;
        double fracoesExtras = Math.ceil((minutosTotal - 60) / 15.0);
        return valorHora + (fracoesExtras * valorFracao);
    }
}