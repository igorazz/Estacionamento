package br.com.estacionamento.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = "";
    private static final String USER = "root"; // seu usuário do MySQL
    private static final String PASS = ""; // sua senha do MySQL

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: ", e);
        }
    }
}
