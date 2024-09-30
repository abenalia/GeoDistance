package edu.vanier.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    private TextArea resultsArea; // TextArea to display nearby locations results
    @FXML
    private ChoiceBox<Integer> radiusChoiceBox; // ChoiceBox for selecting the radius
    private PostalCodeController controller;

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
    }

    // Switch to Distance Form
    @FXML
    private void switchToDistanceForm() {
        mainView.setVisible(false);
        distanceForm.setVisible(true);
    }

    // Switch back to Main View
    @FXML
    private void switchToMainView() {
        mainView.setVisible(true);
        distanceForm.setVisible(false);
        nearbyLocationForm.setVisible(false);
    }

    // Handle Submit for Distance Form
    @FXML
    private void submitDistanceForm() {
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
        switchToMainView();
    }

    // Switch to Nearby Locations Form
    @FXML
    private void switchToNearbyLocationsForm() {
        mainView.setVisible(false);
        nearbyLocationForm.setVisible(true);
    }

    // Handle Submit for Nearby Locations Form
    @FXML
    private void submitNearbyLocationsForm() {
        String postalCode = postalCodeFieldNearby.getText().trim();
        Integer radius = radiusChoiceBox.getValue();

        // Regular expression for a valid postal code (Letter-Digit-Letter)
        String postalCodePattern = "^[A-Z][0-9][A-Z]$";

        // Validate postal code and radius
        if (postalCode.isEmpty()) {
            resultsArea.setText("Please enter a postal code.");
        } else if (!postalCode.matches(postalCodePattern)) {
            resultsArea.setText("Postal code is invalid. Format must be Letter-Digit-Letter (e.g., H1E).");
        } else if (radius == null) {
            resultsArea.setText("Please select a radius.");
        } else {
            // Use the nearbyLocations method from PostalCodeController to get nearby locations
            List<String> nearbyLocationsResults = controller.nearbyLocations(postalCode, radius);

            // Display the results in the resultsArea TextArea
            resultsArea.clear();
            nearbyLocationsResults.forEach(result -> resultsArea.appendText(result + "\n"));
        }

        // Switch back to the main view if needed
        switchToMainView();
    }
}
