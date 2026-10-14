import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
// This class represents the main application window for the ISS Tracker. 
// It contains the map panel, information labels, and a button to manually update the ISS position.
// It also sets up a timer to automatically refresh the ISS position every 5 seconds.
// The TrackerFrame class extends JFrame and serves as the main window for the ISS Tracker application.
public class TrackerFrame extends JFrame {
    private final MapPanel mapPanel;
    private final TrailQueue trailQueue;
    private final IssApiClient apiClient;

    private final JLabel latitudeLabel;
    private final JLabel longitudeLabel;
    private final JLabel timestampLabel;
    private final JLabel statusLabel;

    private final Timer timer;
 // Constructor to initialize the TrackerFrame, set up the UI components, and start the timer for updating the ISS position
    public TrackerFrame() {
        trailQueue = new TrailQueue(120);
        apiClient = new IssApiClient();
        mapPanel = new MapPanel(trailQueue);

        // Initialize labels to display the ISS position information and status
        latitudeLabel = new JLabel("Latitude: --");
        longitudeLabel = new JLabel("Longitude: --");
        timestampLabel = new JLabel("Timestamp: --");
        statusLabel = new JLabel("Status: waiting...");
 // Create a button to allow the user to manually update the ISS position on demand
        JButton locateButton = new JButton("Locate ISS Now");
// Set up the main window properties, including title, size, default close operation, and layout
        
        setTitle("ISS Tracker");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

// Add the map panel to the center of the window and set up the information panel at the bottom

        add(mapPanel, BorderLayout.CENTER);
// Create an information panel to display the latitude, longitude, timestamp, and status of the ISS position
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 2));
        infoPanel.add(latitudeLabel);
        infoPanel.add(longitudeLabel);
        infoPanel.add(timestampLabel);
        infoPanel.add(statusLabel);

// Create a bottom panel to hold the information panel and the locate button, and add it to the south of the window
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        bottomPanel.add(locateButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

// Add an action listener to the locate button to trigger an update of the ISS position when clicked

        locateButton.addActionListener(e -> updateIssPosition());

        timer = new Timer(5000, e -> updateIssPosition());
        timer.start();

        updateIssPosition();
    }

    // This method fetches the current position of the ISS from the API, updates the trail queue and map panel, and refreshes the information labels with the latest data
    // It also handles the case where the API call fails and updates the status label accordingly

   private void updateIssPosition() {
    SatellitePosition position = apiClient.fetchCurrentPosition();
    System.out.println("position object = " + position);

    // Check if the position is not null before updating the UI components and map panel
    if (position != null) {
        trailQueue.enqueue(position);
        mapPanel.setCurrentPosition(position);
        // Update the information labels with the latest latitude, longitude, and timestamp of the ISS position

        latitudeLabel.setText(String.format("Latitude: %.4f", position.getLatitude()));
        longitudeLabel.setText(String.format("Longitude: %.4f", position.getLongitude()));
        timestampLabel.setText("Timestamp: " + formatTimestamp(position.getTimestamp()));
        statusLabel.setText("Status: updated");

        mapPanel.repaint();
    } else {
        statusLabel.setText("Status: failed to fetch data");
    }
}

    private String formatTimestamp(long unixSeconds) {
        Date date = new Date(unixSeconds * 1000L);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}