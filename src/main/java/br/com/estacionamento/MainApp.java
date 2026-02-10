package br.com.estacionamento.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Esta linha conecta o código Java ao seu arquivo visual FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main_view.fxml"));
            Parent root = loader.load();

            // Criamos a cena com o conteúdo do FXML
            Scene scene = new Scene(root);

            stage.setTitle("Controle de Estacionamento - Entrada - v1.0");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            // Caso o arquivo não seja encontrado ou haja erro no Controller
            System.err.println("Erro ao carregar a interface principal!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}