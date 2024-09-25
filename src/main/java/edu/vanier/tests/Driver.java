package edu.vanier.tests;

import edu.vanier.controllers.PostalCodeController;
import edu.vanier.models.PostalCode;

import java.util.HashMap;

public class Driver {

    public static void main(String[] args){
        String csvFilePath = "src/main/resources/postalcodes.csv";

        PostalCodeController controller = new PostalCodeController(csvFilePath);
        
        testParse(controller);
        testDistanceTo(controller);
        testNearbyLocations(controller,"X0G");
    }
    public static void testParse(PostalCodeController controller) {
        System.out.println("Testing parse() method...");

        HashMap<String, PostalCode> postalCodes = controller.getPostalCodes();

        if (postalCodes.isEmpty()) {
            System.out.println("Test Failed: No postal codes were parsed. The map is empty.");
            return;
        } else {
            System.out.println("Test Passed: Postal codes were successfully parsed.");
        }

        System.out.println("Total postal codes parsed: " + postalCodes.size());

        System.out.println("\nChecking specific postal codes...");
        String[] testPostalCodes = {"Y1A", "V5K", "H0H"};

        for (String code : testPostalCodes) {
            if (postalCodes.containsKey(code)) {
                System.out.println("Test Passed: Postal code " + code + " exists in the map.");
            } else {
                System.out.println("Test Failed: Postal code " + code + " is missing from the map.");
            }
        }

        System.out.println("\ntestParse() method completed.");
    }

    public static void testDistanceTo(PostalCodeController controller){
        // 1. Valid Input Test
        System.out.println("Valid Input Test:");
        controller.distanceTo("H1E", "H1M");

        // 2. Invalid Postal Codes Test
        System.out.println("\nInvalid Postal Codes Test:");
        controller.distanceTo("XXX", "YYY");

        // 3. Identical Postal Codes Test
        System.out.println("\nIdentical Postal Codes Test:");
        controller.distanceTo("H1E", "H1E");

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
        controller.distanceTo("X0A", "X0G");
    }

    public static void testNearbyLocations(PostalCodeController controller, String from) {
        System.out.println("Testing nearby locations for postal code: " + from);

        // Test with a small radius (10 km)
        System.out.println("\nTest 1: Small Radius (10 km)");
        controller.nearbyLocations(from, 10);

        // Test with a moderate radius (50 km)
        System.out.println("\nTest 2: Moderate Radius (50 km)");
        controller.nearbyLocations(from, 50);

        // Test with a large radius (200 km)
        System.out.println("\nTest 3: Large Radius (200 km)");
        controller.nearbyLocations(from, 200);

        // Test with a very large radius (1000 km) to cover a larger area
        System.out.println("\nTest 4: Very Large Radius (1000 km)");
        controller.nearbyLocations(from, 1000);

        // Test with a radius that should cover all locations
        System.out.println("\nTest 5: Max Radius (5000 km)");
        controller.nearbyLocations(from, 10000);

        // Test with a postal code that does not exist
        System.out.println("\nTest 6: Non-Existent Postal Code");
        controller.nearbyLocations("XYZ", 100);
    }
}