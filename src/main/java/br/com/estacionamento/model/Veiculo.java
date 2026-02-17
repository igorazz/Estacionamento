package br.com.estacionamento.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional; // Necessário para o botão do Alert

public class Veiculo {
    private Integer id;
    private String placa;
    private String modelo;
    private String cor;
    private String tipo;
    private Integer idMensalista;
    private LocalDateTime dataEntrada;

    // Construtor Completo
    public Veiculo(Integer id, String placa, String modelo, String cor, String tipo, Integer idMensalista) {
        this.id = id;
        this.placa = placa;
        this.modelo = modelo;
        this.cor = cor;
        this.tipo = tipo;
        this.idMensalista = idMensalista;
    }

    // Construtor Vazio
    public Veiculo() {
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getIdMensalista() { return idMensalista; }
    public void setIdMensalista(Integer idMensalista) { this.idMensalista = idMensalista; }

    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }
}