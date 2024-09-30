package edu.vanier.controllers;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import edu.vanier.models.PostalCode;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PostalCodeController {
    private final HashMap<String, PostalCode> postalCodes = new HashMap<>();
    private final String csvFilePath;

    public PostalCodeController(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

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

                    // Parse fields
                    String id = nextLine[0];
                    String country = nextLine[1];
                    String postalCodeStr = nextLine[2];
                    String city = nextLine[3];
                    String province = nextLine[4];
                    double latitude = Double.parseDouble(nextLine[5]);
                    double longitude = Double.parseDouble(nextLine[6]);

                    // Create PostalCode object
                    PostalCode postalCode = new PostalCode(id, country, postalCodeStr, city, province, latitude, longitude);
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

    public void validateParsedData() {
        boolean allValid = true;

        for (PostalCode postalCode : postalCodes.values()) {
            boolean valid = true;

            // Validate postal code (length 3)
            if (postalCode.getPostalCode().length() != 3) {
                System.out.println("Invalid Postal Code: " + postalCode.getPostalCode() + " (ID: " + postalCode.getId() + ")");
                valid = false;
            }

            // Validate non-empty city and province
            if (postalCode.getCity() == null || postalCode.getCity().isEmpty()) {
                System.out.println("Invalid City for Postal Code: " + postalCode.getPostalCode() + " (ID: " + postalCode.getId() + ")");
                valid = false;
            }
            if (postalCode.getProvince() == null || postalCode.getProvince().isEmpty()) {
                System.out.println("Invalid Province for Postal Code: " + postalCode.getPostalCode() + " (ID: " + postalCode.getId() + ")");
                valid = false;
            }

            // Validate latitude and longitude
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

    private String[] fixCsvLine(String[] line) {
        if (line.length > 7) {
            // Combine all fields from the 4th field (index 3) to the third-to-last field into a single city name
            StringBuilder cityBuilder = new StringBuilder(line[3]);

            // Merge fields into the city name until we reach the last three fields (province, latitude, longitude)
            for (int i = 4; i < line.length - 3; i++) {
                cityBuilder.append(", ").append(line[i].trim()); // Append city parts with commas
            }

            // Rebuild the corrected line with exactly 7 fields
            String[] fixedLine = new String[7];
            fixedLine[0] = line[0];  // id
            fixedLine[1] = line[1];  // country
            fixedLine[2] = line[2];  // postalCode
            fixedLine[3] = cityBuilder.toString();  // merged city field
            fixedLine[4] = line[line.length - 3];  // province
            fixedLine[5] = line[line.length - 2];  // latitude
            fixedLine[6] = line[line.length - 1];  // longitude

            return fixedLine;
        }

        // If the line already has exactly 7 fields, return as-is
        return line;
    }

    public double distanceTo(String from, String to) {
        PostalCode fromCode = postalCodes.get(from);
        PostalCode toCode = postalCodes.get(to);

        if (fromCode == null || toCode == null) {
            System.out.println("One or both of the postal codes do not exist in the database.");
            return -1; // Return an invalid distance if postal codes are not found
        }

        double latitude1 = fromCode.getLatitude();
        double longitude1 = fromCode.getLongitude();
        double latitude2 = toCode.getLatitude();
        double longitude2 = toCode.getLongitude();

        double distance = haversine(latitude1, longitude1, latitude2, longitude2);

        return distance; // Return the calculated distance
    }

    public List<String> nearbyLocations(String from, int radius) {
        List<String> results = new ArrayList<>();
        PostalCode fromPostalCode = postalCodes.get(from);

        if (fromPostalCode == null) {
            results.add("The postal code does not exist in the database.");
            return results;
        }

        double latitude1 = fromPostalCode.getLatitude();
        double longitude1 = fromPostalCode.getLongitude();

        boolean foundNearby = false;

        for (PostalCode toPostalCode : postalCodes.values()) {
            if (toPostalCode.getPostalCode().equals(from)) {
                continue;  // Skip the same postal code
            }

            double latitude2 = toPostalCode.getLatitude();
            double longitude2 = toPostalCode.getLongitude();

            double distance = haversine(latitude1, longitude1, latitude2, longitude2);

            if (distance <= radius) {
                foundNearby = true;
                results.add(String.format("Postal Code: %-3s | Province: %-2s | City: %-10s | Distance: %1.2f km",
                        toPostalCode.getPostalCode(), toPostalCode.getCity(), toPostalCode.getProvince(), distance));
            }
        }

        if (!foundNearby) {
            results.add("No locations found within the specified radius.");
        }

        return results;
    }

    public HashMap<String, PostalCode> getPostalCodes() {
        return postalCodes;
    }

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
