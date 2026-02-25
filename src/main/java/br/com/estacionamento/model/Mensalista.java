package br.com.estacionamento.model;
import java.time.LocalDate;

public class Mensalista {
    private int id;
    private String nome;
    private String cpf;
    private String placa;
    private LocalDate vencimento; // Alterado para LocalDate
    private double valor;

    // Getters e Setters atualizados
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public LocalDate getVencimento() { return vencimento; }
    public void setVencimento(LocalDate vencimento) { this.vencimento = vencimento; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}