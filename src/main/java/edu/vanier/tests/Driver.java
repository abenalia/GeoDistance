package edu.vanier.tests;

import edu.vanier.controllers.PostalCodeController;
import edu.vanier.models.PostalCode;

import java.util.HashMap;

public class Driver {

    public static void main(String[] args){
        String csvFilePath = "src/main/resources/postalcodes.csv";

        PostalCodeController controller = new PostalCodeController(csvFilePath);

        // Explicitly call the parse() method to parse the CSV file
        controller.parse();

        // Run the testParse method to test the parsing functionality
        testParse(controller);
        testDistanceTo(controller);
        testNearbyLocations(controller,"E2E");
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

        // Test case 2: Validate specific postal code data
        System.out.println("\nTest Case 2: Validate specific postal code data for 'E2E'");
        PostalCode sampleCode = controller.getPostalCodes().get("E2E");
        if (sampleCode != null) {
            // Check if all fields match expected values
            String expectedCity = "Rothesay, Quispamsis";
            String expectedProvince = "NB";
            double expectedLatitude = 45.4165;
            double expectedLongitude = -65.9913;

            boolean isCityCorrect = sampleCode.getCity().equals(expectedCity);
            boolean isProvinceCorrect = sampleCode.getProvince().equals(expectedProvince);
            boolean isLatitudeCorrect = sampleCode.getLatitude() == expectedLatitude;
            boolean isLongitudeCorrect = sampleCode.getLongitude() == expectedLongitude;

            if (isCityCorrect && isProvinceCorrect && isLatitudeCorrect && isLongitudeCorrect) {
                System.out.println("Test Passed: Postal code 'E2E' data is correct.");
            } else {
                System.out.println("Test Failed: Postal code 'E2E' data is incorrect.");
                System.out.println("Actual Data: " + sampleCode);
                System.out.printf("Expected: City='%s', Province='%s', Latitude=%.4f, Longitude=%.4f%n",
                        expectedCity, expectedProvince, expectedLatitude, expectedLongitude);
            }
        } else {
            System.out.println("Test Failed: Postal code 'E2E' not found.");
        }

        // Test case 3: Verify if a non-existent postal code is handled correctly
        System.out.println("\nTest Case 3: Check for non-existent postal code 'XYZ'");
        PostalCode nonExistentCode = controller.getPostalCodes().get("XYZ");
        if (nonExistentCode == null) {
            System.out.println("Test Passed: Non-existent postal code 'XYZ' is not found, as expected.");
        } else {
            System.out.println("Test Failed: Non-existent postal code 'XYZ' should not exist.");
        }

        // Test case 4: Validate postal code with complex city name
        System.out.println("\nTest Case 4: Validate complex city name for 'K0H'");
        PostalCode complexCode = controller.getPostalCodes().get("K0H");
        if (complexCode != null) {
            String expectedComplexCity = "Frontenac County, Addington County, Loyalist Shores and Southwest Leeds (Inverary)";
            String expectedComplexProvince = "ON";
            if (complexCode.getCity().equals(expectedComplexCity) && complexCode.getProvince().equals(expectedComplexProvince)) {
                System.out.println("Test Passed: Complex city name for 'K0H' is correct.");
            } else {
                System.out.println("Test Failed: Complex city name for 'K0H' is incorrect.");
                System.out.println("Actual Data: " + complexCode);
                System.out.printf("Expected: City='%s', Province='%s'%n", expectedComplexCity, expectedComplexProvince);
            }
        } else {
            System.out.println("Test Failed: Postal code 'K0H' not found.");
        }

        System.out.println("\ntestParse() method completed.");
    }


    public static void testDistanceTo(PostalCodeController controller){
        // 1. Valid Input Test
        System.out.println("Valid Input Test:");
        controller.distanceTo("H1E", "J7C");

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