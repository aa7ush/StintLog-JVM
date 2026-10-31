import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;

public class TelemetryDashboard extends JFrame {
    public TelemetryGraphPanel graphPanel;
    private JLabel dataLabel;
    
    private int lastSector = 1;
    private int lastLap = 1;

    private Clip sectorBeep;
    private Clip lapBeep;

    public TelemetryDashboard() {
        setTitle("StintLog-JVM");
        setSize(1450, 400); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        graphPanel = new TelemetryGraphPanel();
        add(graphPanel, BorderLayout.CENTER);

        dataLabel = new JLabel(" LAP: 1 | SEC: 1 | DIST: 0m | TIME: 0:00.000 | LAST: 0:00.000 | BEST: 0:00.000 | SPD: 0 km/h | THR: 0% | BRK: 0% ", SwingConstants.CENTER);
        dataLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        dataLabel.setForeground(Color.BLACK);
        dataLabel.setBackground(Color.WHITE);
        dataLabel.setOpaque(true);
        dataLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        add(dataLabel, BorderLayout.SOUTH);

        sectorBeep = createBeepClip(800, 500);
        lapBeep = createBeepClip(1000, 1000);
    }

    private Clip createBeepClip(int hz, int msecs) {
        try {
            float sampleRate = 8000f;
            int length = (int) (sampleRate * msecs / 1000.0);
            byte[] buf = new byte[length];
            for (int i = 0; i < length; i++) {
                double angle = i / (sampleRate / hz) * 2.0 * Math.PI;
                buf[i] = (byte) (Math.sin(angle) * 127.0);
            }
            AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, false);
            Clip clip = AudioSystem.getClip();
            clip.open(af, buf, 0, buf.length);
            return clip;
        } catch (Exception e) {
            return null;
        }
    }

    private void playInstantBeep(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0); 
            clip.start();
        }
    }

    private String formatLapTime(double timeInSeconds) {
        if (timeInSeconds <= 0) return "0:00.000";
        int minutes = (int) (timeInSeconds / 60);
        double seconds = timeInSeconds % 60;
        return String.format("%d:%06.3f", minutes, seconds);
    }

    public void updateData(TelemetryPacket packet) {
        SwingUtilities.invokeLater(() -> {
            int sector = 1;
            if (packet.distance > 5300) {
                sector = 3;
            } else if (packet.distance > 2200) {
                sector = 2;
            }

            if (packet.lap != lastLap) {
                playInstantBeep(lapBeep); 
                lastLap = packet.lap;
                lastSector = sector; 
            } else if (sector != lastSector) {
                playInstantBeep(sectorBeep); 
                lastSector = sector;
            }

            String currentTimeFormatted = formatLapTime(packet.currentLapTime);
            String lastTimeFormatted = formatLapTime(packet.lastLapTime);
            String bestTimeFormatted = formatLapTime(packet.bestLapTime);

            dataLabel.setText(String.format(" LAP: %d | SEC: %d | DIST: %.0fm | TIME: %s | LAST: %s | BEST: %s | SPD: %.0f km/h | THR: %.0f%% | BRK: %.0f%% ", 
                packet.lap, sector, packet.distance, currentTimeFormatted, lastTimeFormatted, bestTimeFormatted, packet.speed, packet.throttle, packet.brake));
            graphPanel.addPacket(packet);
        });
    }
}