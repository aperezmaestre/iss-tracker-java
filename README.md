# ISS Tracker — Java Swing Desktop App

A Java Swing desktop project that requests the **International Space Station (ISS)** coordinates from the Open Notify service and plots the reported position and recent tracking trail on a world map.

## Features
- Calls the ISS position endpoint and extracts latitude, longitude and timestamp.
- Displays coordinates and status in a desktop GUI.
- Automatically requests an update every **5 seconds**, with a manual **Locate ISS Now** button.
- Retains a bounded history of recent positions (`TrailQueue`, capacity 120) for drawing a track.
- Renders a map image with a position marker and trail.

## Tech stack
Java, Swing/AWT, `HttpURLConnection`, JSON field extraction, queue-based state.

## Run
Requires a JDK and desktop graphical session. From the repository root:

```bash
javac Driver.java IssApiClient.java SatellitePosition.java TrailQueue.java MapPanel.java TrackerFrame.java
java Driver
```

The application expects a map image at `photos/Map.jpg`. Internet access and availability of `http://api.open-notify.org/iss-now.json` are required; this legacy public endpoint may be unreliable or unavailable, and the project has not been retested against the current service.

## Architecture
`Driver` starts `TrackerFrame`; `TrackerFrame` uses a Swing timer to fetch data via `IssApiClient`, stores recent locations in `TrailQueue`, and asks `MapPanel` to redraw the displayed position and trail.

## Limitations and next steps
- The original project notes that map placement was not fully accurate; map scaling/projection should be improved.
- Parsing uses manual string extraction rather than a JSON library.
- API requests run in UI callbacks and could temporarily freeze the Swing event thread.
- Add simulated-position tests to validate the mapping and orbital track.

## Security and attribution
An unrelated experimental satellite API client from the supplied archive contained a hard-coded API credential and is **not included** in this public release. It must not be reintroduced into Git history. The map image is retained from the original archive; verify redistribution rights before publishing publicly.

## Status
Educational project / prototype, not a precision tracking service.
