import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A client for fetching the current position of the ISS from the Open Notify API.
 */
public class IssApiClient {
   
    private static final String API_URL = "http://api.open-notify.org/iss-now.json";

    /**
     * Fetches the current position of the ISS from the API.
     * @return a SatellitePosition object with latitude, longitude, and timestamp, or null if failed
     */
    public SatellitePosition fetchCurrentPosition() {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(API_URL); // Creates a URL object from the API string
            connection = (HttpURLConnection) url.openConnection(); // Opens an HTTP connection to the URL
            connection.setRequestMethod("GET"); // Sets the request method to GET
            connection.setConnectTimeout(5000); // Sets timeout for connecting (5 seconds)
            connection.setReadTimeout(5000); // Sets timeout for reading response (5 seconds)

            int responseCode = connection.getResponseCode();
            System.out.println("HTTP response code: " + responseCode); // for debugging
            //Code 200 is Successful Connection
            if (responseCode != 200) {
                return null;
            }

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            ); // Creates a reader to read the response input stream

            StringBuilder json = new StringBuilder(); // Builds the JSON string from response lines
            String line;
            
            while ((line = reader.readLine()) != null) { // Reads each line until end
                json.append(line); // Adds line to the builder
            }
            
            reader.close(); // Closes the reader to free resources
            
            String jsonText = json.toString();
            System.out.println("JSON received: " + jsonText); // for debugging

            SatellitePosition position = parseJson(jsonText);
            System.out.println("Parsed position: " + position); // for debugging

            return position;

        } catch (Exception e) {
            System.out.println("Error fetching ISS data:"); // for debugging
            e.printStackTrace(); // for debugging
            return null;
        } finally { 
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Parses the JSON response to get latitude, longitude, and timestamp.
     * @param json the JSON string from the API
     * @return a SatellitePosition object, or null if parsing fails
     */
    private SatellitePosition parseJson(String json) {
        try {
            String latitudeText = extractQuotedValue(json, "latitude"); // Extracts latitude string from JSON
            String longitudeText = extractQuotedValue(json, "longitude"); // Extracts longitude string from JSON
            String timestampText = extractNumberValue(json, "timestamp"); // Extracts timestamp number from JSON

            System.out.println("latitudeText = " + latitudeText); // for debugging
            System.out.println("longitudeText = " + longitudeText); // for debugging
            System.out.println("timestampText = " + timestampText); // for debugging
            

            double latitude = Double.parseDouble(latitudeText); // Converts string to double
            double longitude = Double.parseDouble(longitudeText); // Converts string to double
            long timestamp = Long.parseLong(timestampText); // Converts string to long

            return new SatellitePosition(latitude, longitude, timestamp); // Creates position object

        } catch (Exception e) {
            System.out.println("Error parsing JSON:"); // for debugging
            e.printStackTrace(); // for debugging
            return null;
        }
    }
   
    /**
     * Gets a quoted string value from JSON for a key.
     * @param json the JSON string
     * @param key the key to find
     * @return the value as a string
     */
    private String extractQuotedValue(String json, String key) {
        String pattern = "\"" + key + "\""; // Creates search pattern like "latitude"
        int keyIndex = json.indexOf(pattern); // Finds where the key starts in JSON

        if (keyIndex == -1) {
            throw new RuntimeException("Key not found: " + key);
        }

        int colonIndex = json.indexOf(":", keyIndex); // Finds the colon after the key
        int firstQuoteAfterColon = json.indexOf("\"", colonIndex + 1); // Finds first quote after colon
        int secondQuoteAfterColon = json.indexOf("\"", firstQuoteAfterColon + 1); // Finds second quote (end of value)

        if (colonIndex == -1 || firstQuoteAfterColon == -1 || secondQuoteAfterColon == -1) {
            throw new RuntimeException("Could not parse quoted value for key: " + key);
        }

        return json.substring(firstQuoteAfterColon + 1, secondQuoteAfterColon); // Extracts the value between quotes
    }
    /**
     * Gets a number value from JSON for a key.
     * @param json the JSON string
     * @param key the key to find
     * @return the value as a string
     */
    private String extractNumberValue(String json, String key) {
        String pattern = "\"" + key + "\""; // Creates search pattern like "timestamp"
        int keyIndex = json.indexOf(pattern); // Finds where the key starts in JSON

        if (keyIndex == -1) {
            throw new RuntimeException("Key not found: " + key);
        }

        int colonIndex = json.indexOf(":", keyIndex); // Finds the colon after the key
        if (colonIndex == -1) {
            throw new RuntimeException("Colon not found for key: " + key);
        }

        int start = colonIndex + 1; // Start after colon
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) { // Skip whitespace
            start++;
        }

        int end = start; // End at start
        while (end < json.length() &&
               (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) { // Collect digits and minus
            end++;
        }

        return json.substring(start, end); // Extracts the number string
    }
}