package edu.vanier.models;

/**
 * Represents a PostalCode with various attributes such as id, postal code, province, city, latitude, and longitude.
 */
public class PostalCode {
    private final String id;
    private final String postalCode;
    private final String province;
    private final String city;
    private final double latitude;
    private final double longitude;
    private double distanceToReference;

    /**
     * Constructs a PostalCode instance with specified attributes.
     *
     * @param id        Unique identifier for the postal code.
     * @param postalCode The actual postal code string.
     * @param province  The province or state associated with the postal code.
     * @param city      The city associated with the postal code.
     * @param latitude  The latitude coordinate of the postal code area.
     * @param longitude The longitude coordinate of the postal code area.
     */
    public PostalCode(String id, String postalCode, String province, String city, double latitude, double longitude) {
        this.id = id;
        this.postalCode = postalCode;
        this.city = city;
        this.province = province;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets the unique identifier of the postal code.
     *
     * @return The postal code's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the actual postal code string.
     *
     * @return The postal code.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Gets the province or state associated with the postal code.
     *
     * @return The province or state.
     */
    public String getProvince() {
        return province;
    }

    /**
     * Gets the city associated with the postal code.
     *
     * @return The city name.
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets the latitude coordinate of the postal code.
     *
     * @return The latitude value.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude coordinate of the postal code.
     * Throws an exception if the longitude is not within the range of -180 to 180 degrees.
     *
     * @return The longitude value.
     * @throws IllegalArgumentException if longitude is out of valid range (-180 to 180).
     */
    public double getLongitude() {
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180.");
        }
        return longitude;
    }

    /**
     * Gets the distance to a reference point in kilometers.
     *
     * @return The distance to the reference point.
     */
    public double getDistanceToReference() {
        return distanceToReference;
    }

    /**
     * Sets the distance to a reference point in kilometers.
     *
     * @param distanceToReference The new distance to the reference point.
     */
    public void setDistanceToReference(double distanceToReference) {
        this.distanceToReference = distanceToReference;
    }

    /**
     * Returns a string representation of the PostalCode object.
     *
     * @return A string describing the postal code's details.
     */
    @Override
    public String toString() {
        return "PostalCode{" +
                "id='" + id + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}