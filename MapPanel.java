import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * A panel that displays a world map with the ISS position and trail.
 */
public class MapPanel extends JPanel {
    private BufferedImage worldMap;
    private final TrailQueue trailQueue;
    private SatellitePosition currentPosition;

    /**
     * Creates a MapPanel with the given trail queue.
     * @param trailQueue the queue holding recent ISS positions
     */
    public MapPanel(TrailQueue trailQueue) {
        this.trailQueue = trailQueue;
        loadMap();
        setBackground(Color.BLACK);
    }

    /**
     * Loads the world map image from the photos folder.
     */
    private void loadMap() {
        try {
            File file = new File("photos/Map.jpg"); // Path to the map image file
            System.out.println("Trying to load map from: " + file.getAbsolutePath()); // for debugging
            System.out.println("Exists? " + file.exists()); // for debugging

            worldMap = ImageIO.read(file); // Reads the image from file
            if (worldMap == null) {
                System.out.println("Map image could not be read."); // for debugging
            } else {
                System.out.println("Map loaded: " + worldMap.getWidth() + " x " + worldMap.getHeight()); // for debugging
            }
        } catch (Exception e) {
            System.out.println("Could not load map image."); // for debugging
            e.printStackTrace(); // for debugging
        }
    }

    /**
     * Sets the current ISS position to display on the map.
     * @param currentPosition the latest ISS position
     */
    public void setCurrentPosition(SatellitePosition currentPosition) {
        this.currentPosition = currentPosition;
    }

    /**
     * Paints the map, trail, and current ISS position on the panel.
     * @param g the graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (worldMap != null) {
            int panelWidth = getWidth(); // Panel's current width
            int panelHeight = getHeight(); // Panel's current height

            double imageAspect = (double) worldMap.getWidth() / worldMap.getHeight(); // Image aspect ratio
            double panelAspect = (double) panelWidth / panelHeight; // Panel aspect ratio

            int drawWidth;
            int drawHeight;
            int drawX;
            int drawY;

            // Calculate draw dimensions to fit image in panel while keeping aspect ratio
            if (panelAspect > imageAspect) {
                drawHeight = panelHeight; // Fit height
                drawWidth = (int) (drawHeight * imageAspect); // Scale width
                drawX = (panelWidth - drawWidth) / 2; // Center horizontally
                drawY = 0; // Top align
            } else {
                drawWidth = panelWidth; // Fit width
                drawHeight = (int) (drawWidth / imageAspect); // Scale height
                drawX = 0; // Left align
                drawY = (panelHeight - drawHeight) / 2; // Center vertically
            }

            g.drawImage(worldMap, drawX, drawY, drawWidth, drawHeight, null); // Draw the scaled map
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight()); // Fill with gray if no map
            g.setColor(Color.WHITE);
            g.drawString("Map image not found", 20, 20); // Show error message
        }

        drawTrail(g); // Draw the ISS trail
        drawCurrentPoint(g); // Draw the current ISS position
    }

    /**
     * Draws the trail of recent ISS positions as blue lines.
     * @param g the graphics context
     */
    private void drawTrail(Graphics g) {
        List<SatellitePosition> list = new ArrayList<>(trailQueue.getPositions()); // Copy positions to avoid modification

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLUE); // Set trail color
        g2.setStroke(new BasicStroke(2)); // Set line thickness

        for (int i = 0; i < list.size() - 1; i++) { // Loop through positions except last
            Point p1 = latLonToPoint(list.get(i).getLatitude(), list.get(i).getLongitude()); // Convert first point
            Point p2 = latLonToPoint(list.get(i + 1).getLatitude(), list.get(i + 1).getLongitude()); // Convert next point

            if (Math.abs(p1.x - p2.x) < getWidth() / 2) { // Avoid drawing across map edges
                g2.drawLine(p1.x, p1.y, p2.x, p2.y); // Draw line between points
            }
        }
    }

    /**
     * Draws the current ISS position as a black circle.
     * @param g the graphics context
     */
    private void drawCurrentPoint(Graphics g) {
        if (currentPosition == null) { // Skip if no position set
            return;
        }

        Point point = latLonToPoint(currentPosition.getLatitude(), currentPosition.getLongitude()); // Convert to pixel point

        g.setColor(Color.BLACK); // Set color for ISS dot
        g.fillOval(point.x - 6, point.y - 6, 12, 12); // Draw circle centered on point
    }

    /**
     * Converts latitude and longitude to pixel coordinates on the map.
     * @param latitude the latitude (-90 to 90)
     * @param longitude the longitude (-180 to 180)
     * @return the pixel point on the panel
     */
    private Point latLonToPoint(double latitude, double longitude) {
        int panelWidth = getWidth(); // Panel width
        int panelHeight = getHeight(); // Panel height

        int imageWidth = worldMap.getWidth(); // Original image width
        int imageHeight = worldMap.getHeight(); // Original image height

        double imageAspect = (double) imageWidth / imageHeight; // Image aspect ratio
        double panelAspect = (double) panelWidth / panelHeight; // Panel aspect ratio

        int drawWidth;
        int drawHeight;
        int drawX;
        int drawY;

        // Recalculate draw area (same as in paintComponent)
        if (panelAspect > imageAspect) {
            drawHeight = panelHeight;
            drawWidth = (int) (drawHeight * imageAspect);
            drawX = (panelWidth - drawWidth) / 2;
            drawY = 0;
        } else {
            drawWidth = panelWidth;
            drawHeight = (int) (drawWidth / imageAspect);
            drawX = 0;
            drawY = (panelHeight - drawHeight) / 2;
        }

        // Margins to account for map borders (specific to this image) needs to change !
        double leftMargin = 16.0 / imageWidth; // Left border margin as fraction
        double rightMargin = 16.0 / imageWidth; // Right border margin as fraction
        double topMargin = 16.0 / imageHeight; // Top border margin as fraction
        double bottomMargin = 68.0 / imageHeight; // Bottom border margin as fraction

        double usableWidth = drawWidth * (1.0 - leftMargin - rightMargin); // Usable width after margins
        double usableHeight = drawHeight * (1.0 - topMargin - bottomMargin); // Usable height after margins

        // Convert longitude (-180 to 180) to x (0 to usableWidth)
        double x = drawX + drawWidth * leftMargin + ((longitude + 180.0) / 360.0) * usableWidth;
        // Convert latitude (90 to -90) to y (0 to usableHeight), flipped
        double y = drawY + drawHeight * topMargin + ((90.0 - latitude) / 180.0) * usableHeight;

        return new Point((int) x, (int) y); // Return pixel point
    }
}