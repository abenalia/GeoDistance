package edu.vanier.controllers;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import edu.vanier.models.PostalCode;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Controller class responsible for managing and manipulating postal codes data.
 * Handles parsing postal codes from a CSV file, validating the parsed data, calculating distances,
 * and finding nearby locations based on a given radius.
 */
public class PostalCodeController {
    private final HashMap<String, PostalCode> postalCodes = new HashMap<>();
    private final String csvFilePath;

    /**
     * Constructs a PostalCodeController with the specified path to the CSV file.
     *
     * @param csvFilePath The path to the CSV file containing postal code data.
     */
    public PostalCodeController(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    /**
     * Parses the CSV file and populates the postalCodes map with PostalCode objects.
     * Handles incorrectly formatted lines
     */
    public void parse() {
        try {
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath)).build();
            String[] nextLine;
            int lineNumber = 0;

            while ((nextLine = reader.readNext()) != null) {
                lineNumber++;
                try {
                    if (nextLine.length != 7) {
                        nextLine = fixCsvLine(nextLine);
                    }

                    if (nextLine.length != 7) {
                        System.err.println("Skipping line " + lineNumber + ": Incorrect number of columns after processing.");
                        continue;
                    }

                    String id = nextLine[0];
                    String country = nextLine[1];
                    String postalCodeStr = nextLine[2];
                    String city = nextLine[3];
                    String province = nextLine[4];
                    double latitude = Double.parseDouble(nextLine[5]);
                    double longitude = Double.parseDouble(nextLine[6]);

                    PostalCode postalCode = new PostalCode(id, postalCodeStr, province, city, latitude, longitude);
                    postalCodes.put(postalCodeStr, postalCode);

                } catch (NumberFormatException e) {
                    System.err.println("Skipping line " + lineNumber + ": Number format error - " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("Skipping line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("CSV file not found: " + csvFilePath, e);
        } catch (CsvValidationException e) {
            throw new RuntimeException("CSV validation error", e);
        } catch (IOException e) {
            throw new RuntimeException("IO error while reading CSV file", e);
        }
    }

    /**
     * Validates the parsed postal codes by checking for valid postal code length,
     * non-empty city and province, and valid latitude and longitude values.
     * Logs invalid entries.
     */
    public void validateParsedData() {
        boolean allValid = true;

        for (PostalCode postalCode : postalCodes.values()) {
            boolean valid = true;

            if (postalCode.getPostalCode().length() != 3) {
                System.out.println("Invalid Postal Code: " + postalCode.getPostalCode() + " (ID: " + postalCode.getId() + ")");
                valid = false;
            }

            if (postalCode.getCity() == null || postalCode.getCity().isEmpty()) {
                System.out.println("Invalid City for Postal Code: " + postalCode.getPostalCode() + " (ID: " + postalCode.getId() + ")");
                valid = false;
            }
            if (postalCode.getProvince() == null || postalCode.getProvince().isEmpty()) {
                System.out.println("Invalid Province for Postal Code: " + postalCode.getPostalCode() + " (ID: " + postalCode.getId() + ")");
                valid = false;
            }

            if (postalCode.getLatitude() < -90 || postalCode.getLatitude() > 90) {
                System.out.println("Invalid Latitude for Postal Code: " + postalCode.getPostalCode() + " (ID: " + postalCode.getId() + ")");
                valid = false;
            }
            if (postalCode.getLongitude() < -180 || postalCode.getLongitude() > 180) {
                System.out.println("Invalid Longitude for Postal Code: " + postalCode.getPostalCode() + " (ID: " + postalCode.getId() + ")");
                valid = false;
            }

            if (!valid) {
                allValid = false;
            }
        }

        if (allValid) {
            System.out.println("All postal codes passed validation.");
        } else {
            System.out.println("Some postal codes failed validation.");
        }
    }

    /**
     * Attempts to fix a CSV line with more than 7 fields by merging fields into the city name.
     *
     * @param line The original CSV line that needs fixing.
     * @return The fixed line with exactly 7 fields.
     */
    private String[] fixCsvLine(String[] line) {
        if (line.length > 7) {
            StringBuilder cityBuilder = new StringBuilder(line[3]);

            for (int i = 4; i < line.length - 3; i++) {
                cityBuilder.append(", ").append(line[i].trim());
            }

            String[] fixedLine = new String[7];
            fixedLine[0] = line[0];
            fixedLine[1] = line[1];
            fixedLine[2] = line[2];
            fixedLine[3] = cityBuilder.toString();
            fixedLine[4] = line[line.length - 3];
            fixedLine[5] = line[line.length - 2];
            fixedLine[6] = line[line.length - 1];

            return fixedLine;
        }
        return line;
    }

    /**
     * Calculates the distance in kilometers between two postal codes using the haversine formula.
     *
     * @param from The postal code from which to calculate the distance.
     * @param to   The postal code to which the distance is calculated.
     * @return The distance in kilometers, or -1 if one or both postal codes are not found.
     */
    public double distanceTo(String from, String to) {
        PostalCode fromCode = postalCodes.get(from);
        PostalCode toCode = postalCodes.get(to);

        if (fromCode == null || toCode == null) {
            System.out.println("One or both of the postal codes do not exist in the database.");
            return -1;
        }

        double latitude1 = fromCode.getLatitude();
        double longitude1 = fromCode.getLongitude();
        double latitude2 = toCode.getLatitude();
        double longitude2 = toCode.getLongitude();

        double distance = haversine(latitude1, longitude1, latitude2, longitude2);

        return distance;
    }

    /**
     * Finds postal codes within a specified radius from a given postal code.
     *
     * @param from   The postal code from which to search.
     * @param radius The radius (in kilometers) within which to find nearby postal codes.
     * @return A list of nearby postal codes.
     */
    public List<PostalCode> nearbyLocations(String from, int radius) {
        System.out.println("Entering nearbyLocations method.");
        System.out.println("Parameters received - from: " + from + ", radius: " + radius);

        List<PostalCode> results = new ArrayList<>();
        PostalCode fromPostalCode = postalCodes.get(from);

        if (fromPostalCode == null) {
            System.out.println("The postal code '" + from + "' does not exist in the database.");
            return results;
        }

        double latitude1 = fromPostalCode.getLatitude();
        double longitude1 = fromPostalCode.getLongitude();
        System.out.println("Reference postal code coordinates - Latitude: " + latitude1 + ", Longitude: " + longitude1);

        int totalPostalCodes = postalCodes.size();
        System.out.println("Total postal codes to check: " + totalPostalCodes);

        int checkedPostalCodes = 0;
        int addedPostalCodes = 0;

        for (PostalCode toPostalCode : postalCodes.values()) {
            checkedPostalCodes++;

            if (toPostalCode.getPostalCode().equals(from)) {
                System.out.println("Skipping same postal code: " + toPostalCode.getPostalCode());
                continue;
            }

            double latitude2 = toPostalCode.getLatitude();
            double longitude2 = toPostalCode.getLongitude();

            double distance = haversine(latitude1, longitude1, latitude2, longitude2);

            System.out.printf("Calculated distance from %s to %s: %.2f km%n", from, toPostalCode.getPostalCode(), distance);

            if (distance <= radius) {
                toPostalCode.setDistanceToReference(distance);
                results.add(toPostalCode);
                addedPostalCodes++;
                System.out.println("Added postal code " + toPostalCode.getPostalCode() + " to results. Total added: " + addedPostalCodes);
            }
        }

        System.out.println("Checked " + checkedPostalCodes + " postal codes.");
        return results;
    }

    /**
     * Returns the map of postal codes.
     *
     * @return A map of postal code strings to PostalCode objects.
     */
    public HashMap<String, PostalCode> getPostalCodes() {
        return postalCodes;
    }

    /**
     * Haversine formula to calculate the great-circle distance between two points
     * on the Earth's surface specified by latitude and longitude.
     *
     * @param latitude1  The latitude of the first point.
     * @param longitude1 The longitude of the first point.
     * @param latitude2  The latitude of the second point.
     * @param longitude2 The longitude of the second point.
     * @return The distance in kilometers between the two points.
     */
    public static double haversine(double latitude1, double longitude1, double latitude2, double longitude2){
        double distanceLongitude = Math.toRadians(latitude2-latitude1);
        double distanceLatitude = Math.toRadians(longitude2-longitude1);

        latitude1 = Math.toRadians(latitude1);
        latitude2 = Math.toRadians(latitude2);

        double a = Math.pow(Math.sin(distanceLatitude/2),2)+Math.pow(Math.sin(distanceLongitude/2),2)*Math.cos(latitude1)*Math.cos(latitude2);
        double earthRadius = 6371;
        double c = 2* Math.asin(Math.sqrt(a));
        return earthRadius * c;
    }

}
