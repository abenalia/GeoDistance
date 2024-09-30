package edu.vanier.tests;

import edu.vanier.controllers.PostalCodeController;
import edu.vanier.models.PostalCode;

import java.util.List;

public class Driver {

    public static void main(String[] args) {
        String csvFilePath = "src/main/resources/postalcodes.csv";

        PostalCodeController controller = new PostalCodeController(csvFilePath);

        // Explicitly call the parse() method to parse the CSV file
        controller.parse();

        // Run the testParse method to test the parsing functionality
        testParse(controller);
        testDistanceTo(controller);
        testNearbyLocations(controller, "E2E");
    }

    public static void testParse(PostalCodeController controller) {
        System.out.println("Testing parse() method...");

        // Check if the map is not empty
        if (controller.getPostalCodes().isEmpty()) {
            System.out.println("Test Failed: No postal codes were parsed. The map is empty.");
            return;
        } else {
            System.out.println("Test Passed: Postal codes were successfully parsed.");
        }

        // Validate all parsed data
        controller.validateParsedData();

        // Check the total number of postal codes parsed
        System.out.println("Total number of postal codes parsed: " + controller.getPostalCodes().size());

        // Test case 1: Check if specific postal codes exist
        System.out.println("\nTest Case 1: Check for specific postal codes");
        String[] testPostalCodes = {"E2E", "Y1A", "V5K", "K0H"}; // Add more postal codes as needed
        for (String postalCode : testPostalCodes) {
            if (controller.getPostalCodes().containsKey(postalCode)) {
                System.out.println("Test Passed: Postal code " + postalCode + " exists.");
            } else {
                System.out.println("Test Failed: Postal code " + postalCode + " is missing.");
            }
        }

        // Test case 2: Verify if a non-existent postal code is handled correctly
        System.out.println("\nTest Case 2: Check for non-existent postal code 'XYZ'");
        PostalCode nonExistentCode = controller.getPostalCodes().get("XYZ");
        if (nonExistentCode == null) {
            System.out.println("Test Passed: Non-existent postal code 'XYZ' is not found, as expected.");
        } else {
            System.out.println("Test Failed: Non-existent postal code 'XYZ' should not exist.");
        }
    }

    public static void testDistanceTo(PostalCodeController controller) {
        // 1. Valid Input Test
        System.out.println("Valid Input Test:");
        double distance1 = controller.distanceTo("H1E", "J7C");
        System.out.printf("Distance between H1E and J7C: %.2f km%n", distance1);

        // 2. Invalid Postal Codes Test
        System.out.println("\nInvalid Postal Codes Test:");
        double invalidDistance = controller.distanceTo("XXX", "YYY");
        if (invalidDistance == -1) {
            System.out.println("Test Passed: Invalid postal codes handled correctly.");
        } else {
            System.out.println("Test Failed: Invalid postal codes should return an error.");
        }

        // 3. Identical Postal Codes Test
        System.out.println("\nIdentical Postal Codes Test:");
        double identicalDistance = controller.distanceTo("H1E", "H1E");
        System.out.printf("Distance between H1E and H1E: %.2f km (should be 0 km)%n", identicalDistance);

        // 4. Null or Empty Input Test
        System.out.println("\nNull or Empty Input Test:");
        try {
            controller.distanceTo(null, "V5K");
        } catch (Exception e) {
            System.out.println("Caught exception for null input: " + e.getMessage());
        }

        try {
            controller.distanceTo("", "V5K");
        } catch (Exception e) {
            System.out.println("Caught exception for empty input: " + e.getMessage());
        }

        // 5. Boundary Cases Test
        System.out.println("\nBoundary Cases Test:");
        double boundaryDistance = controller.distanceTo("X0A", "X0G");
        System.out.printf("Distance between X0A and X0G: %.2f km%n", boundaryDistance);
    }

    public static void testNearbyLocations(PostalCodeController controller, String from) {
        System.out.println("Testing nearby locations for postal code: " + from);

        // Test with a small radius (10 km)
        System.out.println("\nTest 1: Small Radius (10 km)");
        List<PostalCode> resultsSmallRadius = controller.nearbyLocations(from, 10);
        printPostalCodeList(resultsSmallRadius);

        // Test with a moderate radius (50 km)
        System.out.println("\nTest 2: Moderate Radius (50 km)");
        List<PostalCode> resultsModerateRadius = controller.nearbyLocations(from, 50);
        printPostalCodeList(resultsModerateRadius);

        // Test with a large radius (200 km)
        System.out.println("\nTest 3: Large Radius (200 km)");
        List<PostalCode> resultsLargeRadius = controller.nearbyLocations(from, 200);
        printPostalCodeList(resultsLargeRadius);

        // Test with a very large radius (1000 km) to cover a larger area
        System.out.println("\nTest 4: Very Large Radius (1000 km)");
        List<PostalCode> resultsVeryLargeRadius = controller.nearbyLocations(from, 1000);
        printPostalCodeList(resultsVeryLargeRadius);

        // Test with a radius that should cover all locations
        System.out.println("\nTest 5: Max Radius (5000 km)");
        List<PostalCode> resultsMaxRadius = controller.nearbyLocations(from, 5000);
        printPostalCodeList(resultsMaxRadius);

        // Test with a postal code that does not exist
        System.out.println("\nTest 6: Non-Existent Postal Code");
        List<PostalCode> resultsNonExistent = controller.nearbyLocations("XYZ", 100);
        if (resultsNonExistent.isEmpty()) {
            System.out.println("No locations found or postal code does not exist.");
        } else {
            printPostalCodeList(resultsNonExistent);
        }
    }

    private static void printPostalCodeList(List<PostalCode> postalCodes) {
        if (postalCodes.isEmpty()) {
            System.out.println("No locations found within the specified radius.");
            return;
        }

        for (PostalCode pc : postalCodes) {
            System.out.printf("Postal Code: %-3s | City: %-3s | Province: %-3s | Distance: %1.2f km%n",
                    pc.getPostalCode(), pc.getCity(), pc.getProvince(), pc.getDistanceToReference());
        }
    }

}
