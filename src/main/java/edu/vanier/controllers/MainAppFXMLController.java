package edu.vanier.controllers;

import edu.vanier.ui.MainApp;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainAppFXMLController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToMainView(Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/MainView.fxml"));

        Parent root = (Parent) loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToDistanceForm(Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/DistanceForm.fxml"));

        Parent root = (Parent) loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToNearbyLocationsForm(Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/NearbyLocationForm.fxml"));

        Parent root = (Parent) loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
