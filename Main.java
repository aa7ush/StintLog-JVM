// Main.java
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelemetryDashboard dashboard = new TelemetryDashboard();
            dashboard.setVisible(true);

            TelemetryPlaybackEngine engine = new TelemetryPlaybackEngine(
                "spa_telemetry.csv", 
                packet -> dashboard.updateData(packet)
            );

            Thread playbackThread = new Thread(engine);
            playbackThread.start();
        });
    }
}