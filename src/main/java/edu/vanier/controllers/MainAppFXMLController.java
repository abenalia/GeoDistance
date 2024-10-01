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

/**
 * Controller class for the main application. Handles user interactions with the UI, including
 * switching between forms, calculating distances between postal codes, and finding nearby locations.
 */
public class MainAppFXMLController {

    @FXML
    private AnchorPane mainView;
    @FXML
    private AnchorPane distanceForm;
    @FXML
    private AnchorPane nearbyLocationForm;
    @FXML
    private TextField postalCodeField1;
    @FXML
    private TextField postalCodeField2;
    @FXML
    private TextField postalCodeFieldNearby;
    @FXML
    private Label resultLabel;
    @FXML
    private ChoiceBox<Integer> radiusChoiceBox;
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


    /**
     * Initializes the controller after the root element has been completely processed.
     * Sets up the CSV parser, initializes UI components, and sets default values for the ChoiceBox.
     */
    @FXML
    public void initialize() {
        String csvFilePath = "src/main/resources/postalcodes.csv";
        controller = new PostalCodeController(csvFilePath);
        controller.parse();

        radiusChoiceBox.getItems().addAll(5, 10, 15, 25, 50, 100, 500, 1000, 2000, 5000, 10000);
        radiusChoiceBox.setValue(10);

        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        provinceColumn.setCellValueFactory(new PropertyValueFactory<>("province"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distanceToReference"));

        locationsTableView.setVisible(false);
    }

    /**
     * Switches the view to the Distance Form where users can input two postal codes to calculate the distance.
     */
    @FXML
    private void switchToDistanceForm() {
        mainView.setVisible(false);
        distanceForm.setVisible(true);
    }

    /**
     * Switches back to the main view from any sub-form.
     *
     * @param event The ActionEvent triggered by a button press.
     */
    @FXML
    private void switchToMainView(ActionEvent event) {
        mainView.setVisible(true);
        distanceForm.setVisible(false);
        nearbyLocationForm.setVisible(false);
    }

    /**
     * Handles the submit action for the Distance Form. Validates postal codes and calculates the
     * distance between them using the PostalCodeController.
     *
     * @param event The ActionEvent triggered by the submit button.
     */
    @FXML
    private void submitDistanceForm(ActionEvent event) {
        String postalCode1 = postalCodeField1.getText().trim();
        String postalCode2 = postalCodeField2.getText().trim();

        String postalCodePattern = "^[A-Z][0-9][A-Z]$";

        if (postalCode1.isEmpty() || postalCode2.isEmpty()) {
            resultLabel.setText("Please enter both postal codes.");
        } else if (!postalCode1.matches(postalCodePattern)) {
            resultLabel.setText("Postal Code 1 is invalid. Format must be Letter-Digit-Letter (e.g., H1E).");
        } else if (!postalCode2.matches(postalCodePattern)) {
            resultLabel.setText("Postal Code 2 is invalid. Format must be Letter-Digit-Letter (e.g., H1E).");
        } else {
            double distance = controller.distanceTo(postalCode1, postalCode2);

            if (distance == -1) {
                resultLabel.setText("One or both of the postal codes do not exist in the database.");
            } else {
                resultLabel.setText(String.format("The distance between %s and %s is %.2f km.", postalCode1, postalCode2, distance));
            }
        }

        switchToMainView(event);
    }

    /**
     * Switches the view to the Nearby Locations Form where users can input a postal code
     * and search for nearby locations within a selected radius.
     */
    @FXML
    private void switchToNearbyLocationsForm() {
        mainView.setVisible(false);
        nearbyLocationForm.setVisible(true);
    }

    /**
     * Handles the submit action for the Nearby Locations Form. Validates the postal code
     * and radius, and retrieves nearby locations from the PostalCodeController.
     *
     * @param event The ActionEvent triggered by the submit button.
     */
    @FXML
    private void submitNearbyLocationsForm(ActionEvent event) {
        String postalCode = postalCodeFieldNearby.getText().trim();
        Integer radius = radiusChoiceBox.getValue();

        String postalCodePattern = "^[A-Z][0-9][A-Z]$";

        if (postalCode.isEmpty()) {
            resultLabel.setText("Please enter a postal code.");
        } else if (!postalCode.matches(postalCodePattern)) {
            resultLabel.setText("Postal code is invalid. Format must be Letter-Digit-Letter (e.g., H1E).");
        } else if (radius == null) {
            resultLabel.setText("Please select a radius.");
        } else {
            List<PostalCode> nearbyLocationsResults = controller.nearbyLocations(postalCode, radius);

            if (nearbyLocationsResults.isEmpty()) {
                resultLabel.setText("No locations found within the specified radius.");
                locationsTableView.setItems(null);
            } else {
                for (PostalCode pc : nearbyLocationsResults) {
                    double distance = controller.distanceTo(postalCode, pc.getPostalCode());
                    pc.setDistanceToReference(distance);
                }

                ObservableList<PostalCode> data = FXCollections.observableArrayList(nearbyLocationsResults);
                locationsTableView.setItems(data);
                resultLabel.setText("");
                locationsTableView.setVisible(true);
            }
        }

        switchToMainView(event);
    }
}
