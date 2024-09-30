package edu.vanier.controllers;

import edu.vanier.models.PostalCode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class MainAppFXMLController {

    @FXML
    private AnchorPane mainView;
    @FXML
    private AnchorPane distanceForm;
    @FXML
    private AnchorPane nearbyLocationForm;
    @FXML
    private TextField postalCodeField1; // First postal code for distance calculation
    @FXML
    private TextField postalCodeField2; // Second postal code for distance calculation
    @FXML
    private TextField postalCodeFieldNearby; // Postal code for nearby locations
    @FXML
    private Label resultLabel; // Label to display the result
    @FXML
    private ChoiceBox<Integer> radiusChoiceBox; // ChoiceBox for selecting the radius
    private PostalCodeController controller;
    @FXML
    private TableView<PostalCode> locationsTableView;
    @FXML
    private TableColumn<PostalCode, String> postalCodeColumn;
    @FXML
    private TableColumn<PostalCode, String> cityColumn;
    @FXML
    private TableColumn<PostalCode, String> provinceColumn;
    @FXML
    private TableColumn<PostalCode, Double> distanceColumn;


    // Initialize controller
    @FXML
    public void initialize() {
        // Initialize PostalCodeController with the path to the CSV file
        String csvFilePath = "src/main/resources/postalcodes.csv";
        controller = new PostalCodeController(csvFilePath);
        controller.parse();

        // Populate the radius choice box with predefined values
        radiusChoiceBox.getItems().addAll(5, 10, 15, 25, 50, 100, 500, 1000, 2000, 5000, 10000);
        radiusChoiceBox.setValue(10);  // Set a default value

        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        provinceColumn.setCellValueFactory(new PropertyValueFactory<>("province"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distanceToReference"));

        // Hide the TableView initially
        locationsTableView.setVisible(false);
    }

    // Switch to Distance Form
    @FXML
    private void switchToDistanceForm(ActionEvent event) {
        mainView.setVisible(false);
        distanceForm.setVisible(true);
    }

    // Switch back to Main View
    @FXML
    private void switchToMainView(ActionEvent event) {
        mainView.setVisible(true);
        distanceForm.setVisible(false);
        nearbyLocationForm.setVisible(false);
    }

    // Handle Submit for Distance Form
    @FXML
    private void submitDistanceForm(ActionEvent event) {
        String postalCode1 = postalCodeField1.getText().trim();
        String postalCode2 = postalCodeField2.getText().trim();

        // Regular expression for a valid postal code (Letter-Digit-Letter)
        String postalCodePattern = "^[A-Z][0-9][A-Z]$";

        // Validate if postal codes are not empty and follow the pattern
        if (postalCode1.isEmpty() || postalCode2.isEmpty()) {
            resultLabel.setText("Please enter both postal codes.");
        } else if (!postalCode1.matches(postalCodePattern)) {
            resultLabel.setText("Postal Code 1 is invalid. Format must be Letter-Digit-Letter (e.g., H1E).");
        } else if (!postalCode2.matches(postalCodePattern)) {
            resultLabel.setText("Postal Code 2 is invalid. Format must be Letter-Digit-Letter (e.g., H1E).");
        } else {
            // Use the distanceTo method from PostalCodeController to compute the distance
            double distance = controller.distanceTo(postalCode1, postalCode2);

            // Check if the postal codes were found and distance is valid
            if (distance == -1) {
                resultLabel.setText("One or both of the postal codes do not exist in the database.");
            } else {
                // Display the result in the resultLabel
                resultLabel.setText(String.format("The distance between %s and %s is %.2f km.", postalCode1, postalCode2, distance));
            }
        }

        // Switch back to the main view and display results
        switchToMainView(event);
    }

    // Switch to Nearby Locations Form
    @FXML
    private void switchToNearbyLocationsForm(ActionEvent event) {
        mainView.setVisible(false);
        nearbyLocationForm.setVisible(true);
    }

    // Handle Submit for Nearby Locations Form
    @FXML
    private void submitNearbyLocationsForm(ActionEvent event) {
        String postalCode = postalCodeFieldNearby.getText().trim();
        Integer radius = radiusChoiceBox.getValue();

        // Regular expression for a valid postal code (Letter-Digit-Letter)
        String postalCodePattern = "^[A-Z][0-9][A-Z]$";

        // Validate postal code and radius
        if (postalCode.isEmpty()) {
            resultLabel.setText("Please enter a postal code.");
        } else if (!postalCode.matches(postalCodePattern)) {
            resultLabel.setText("Postal code is invalid. Format must be Letter-Digit-Letter (e.g., H1E).");
        } else if (radius == null) {
            resultLabel.setText("Please select a radius.");
        } else {
            // Use the nearbyLocations method from PostalCodeController to get nearby locations
            List<PostalCode> nearbyLocationsResults = controller.nearbyLocations(postalCode, radius);

            if (nearbyLocationsResults.isEmpty()) {
                resultLabel.setText("No locations found within the specified radius.");
                locationsTableView.setItems(null);
            } else {
                // Update the distanceToReference for each PostalCode
                for (PostalCode pc : nearbyLocationsResults) {
                    double distance = controller.distanceTo(postalCode, pc.getPostalCode());
                    pc.setDistanceToReference(distance);
                }

                ObservableList<PostalCode> data = FXCollections.observableArrayList(nearbyLocationsResults);
                locationsTableView.setItems(data);
                resultLabel.setText(""); // Clear any previous messages
                locationsTableView.setVisible(true); // Show the TableView
            }
        }

        // Switch back to the main view
        switchToMainView(event);
    }
}
