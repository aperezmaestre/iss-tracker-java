import java.util.LinkedList;
import java.util.Queue;
// This class implements a fixed-size queue to store the recent positions of the ISS for the trail effect on the map
// the queue will hold a maximum number of SatellitePosition objects, and when new positions are added beyond the limit,
// the oldest positions will be removed to maintain the fixed size of the trail
// 
public class TrailQueue {
    private final Queue<SatellitePosition> positions;
    private final int maxSize;
    // constructor 
    public TrailQueue(int maxSize) {
        this.maxSize = maxSize;
        this.positions = new LinkedList<>();
    }
// Adds a new SatellitePosition to the queue and ensures that the size does not exceed the maximum limit
    public void enqueue(SatellitePosition position) {
        if (position == null) {
            return;
        }

        positions.add(position);

        while (positions.size() > maxSize) {
            positions.poll();
        }
    }
 // Returns the current positions in the queue as a collection that can be iterated over for drawing the trail on the map
    public Queue<SatellitePosition> getPositions() {
        return positions;
    }
 // Clears all positions from the queue to reset the trail when needed
    public void clear() {
        positions.clear();
    }
    // Returns the current number of positions in the queue
       public int size() {
               return positions.size();
    }
}