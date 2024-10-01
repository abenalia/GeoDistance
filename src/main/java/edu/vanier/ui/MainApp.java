package edu.vanier.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the GeoDistance application.
 * This class extends {@link javafx.application.Application} and sets up the main window.
 */
public class MainApp extends Application {

    /**
     * Main method, which serves as the entry point for the JavaFX application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes and displays the primary stage (main window) of the application.
     *
     * @param primaryStage The main stage for the application, onto which the application scene is set.
     * @throws Exception if there is an error during the loading of the FXML file.
     */
    public void start (Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/MainView.fxml"));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("GeoDistance");
        primaryStage.show();
    }
}