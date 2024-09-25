package edu.vanier.controllers;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import edu.vanier.models.PostalCode;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class PostalCodeController {
    private final HashMap<String, PostalCode> postalCodes = new HashMap<>();
    private final String csvFilePath;

    public PostalCodeController(String filePath) {
        this.csvFilePath = filePath;
        parse();
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

    private void parse(){
        try {
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath)).build();
            String[] nextLine;
            int lineNumber = 0;

            while((nextLine = reader.readNext()) != null){
                lineNumber ++;
                try{
                    if (nextLine.length != 7) {
                        continue;
                    }

                    String id = nextLine[0];
                    String country = nextLine[1];
                    String postalCodeStr = nextLine[2];
                    String city = nextLine[3];
                    String province = nextLine[4];
                    double latitude = Double.parseDouble(nextLine[5]);
                    double longitude = Double.parseDouble(nextLine[6]);

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

    public void distanceTo(String from, String to){
        PostalCode fromCode = postalCodes.get(from);
        PostalCode toCode = postalCodes.get(to);

        if (fromCode == null || toCode == null) {
            System.out.println("One or both of the postal codes do not exist in the database.");
            return;
        }

        double latitude1 = fromCode.getLatitude();
        double longitude1 = fromCode.getLongitude();
        double latitude2 = toCode.getLatitude();
        double longitude2 = toCode.getLongitude();

        double distance = haversine(latitude1, longitude1, latitude2, longitude2);

        System.out.printf("The distance between %s and %s is %.2f km.%n", from, to, distance);
    }

    public void nearbyLocations(String from, int radius){
        PostalCode fromPostalCode = postalCodes.get(from);

        if (fromPostalCode == null) {
            System.out.println("The postal codes do not exist in the database.");
            return;
        }

        double latitude1 = fromPostalCode.getLatitude();
        double longitude1 = fromPostalCode.getLongitude();

        System.out.printf("Locations within %d km of postal code %s:%n", radius, from);
        boolean foundNearby = false;

        for (PostalCode toPostalCode : postalCodes.values()) {
            if (toPostalCode.getPostalCode().equals(from)) {
                continue;
            }

            double latitude2 = toPostalCode.getLatitude();
            double longitude2 = toPostalCode.getLongitude();

            double distance = haversine(latitude1, longitude1, latitude2, longitude2);

            if (distance <= radius) {
                foundNearby = true;
                System.out.printf("Postal Code: %s, City: %s, Province: %s, Distance: %.2f km%n",
                        toPostalCode.getPostalCode(), toPostalCode.getCity(), toPostalCode.getProvince(), distance);
            }
        }

        if (!foundNearby) {
            System.out.println("No locations found within the specified radius.");
        }

    }
}
