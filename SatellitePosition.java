
// This class represents the position of the satellite (ISS) at a specific point in time, including its latitude, longitude, and timestamp
// It serves as a data structure to hold the information about the satellite's location, which can be used for tracking and displaying on the map
public class SatellitePosition {
    private final double latitude;
    private final double longitude;
    private final long timestamp;
// Constructor to initialize the SatellitePosition with latitude, longitude, and timestamp values
    public SatellitePosition(double latitude, double longitude, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }
 // Getter methods to access the latitude, longitude, and timestamp values of the SatellitePosition object
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }
// Override the toString method to provide a readable representation of the SatellitePosition object for debuggin
    @Override
    public String toString() {
        return "Latitude: " + latitude +
               ", Longitude: " + longitude +
               ", Timestamp: " + timestamp;
    }
}