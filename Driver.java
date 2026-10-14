import javax.swing.SwingUtilities;

// Main driver class to launch the ISS Tracker application

public class Driver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrackerFrame frame = new TrackerFrame();
            frame.setVisible(true);
        });
    }
}