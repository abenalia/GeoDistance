package edu.vanier.ui;


import edu.vanier.controllers.MainAppFXMLController;
import edu.vanier.controllers.PostalCodeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;


public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start (Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/MainView.fxml"));
        loader.setController(new MainAppFXMLController());

        Parent root = (Parent) loader.load();

    }
}