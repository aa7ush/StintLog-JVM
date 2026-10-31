import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TelemetryGraphPanel extends JPanel {
    private final List<TelemetryPacket> packets = new ArrayList<>();
    private final int MAX_POINTS = 300;

    public TelemetryGraphPanel() {
        setBackground(new Color(30, 30, 30)); // Dark telemetry background
    }

    public void addPacket(TelemetryPacket packet) {
        if (packets.size() >= MAX_POINTS) {
            packets.remove(0);
        }
        packets.add(packet);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Subtle dark grid lines
        g2.setColor(new Color(50, 50, 50));
        for (int i = 1; i < 4; i++) {
            int y = i * (h / 4);
            g2.drawLine(0, y, w, y);
        }

        if (packets.size() < 2) return;

        g2.setStroke(new BasicStroke(2.0f));

        for (int i = 1; i < packets.size(); i++) {
            TelemetryPacket p1 = packets.get(i - 1);
            TelemetryPacket p2 = packets.get(i);

            int x1 = (i - 1) * w / MAX_POINTS;
            int x2 = i * w / MAX_POINTS;

            // Speed Trace (Luminous Cyan, scaled to 320 km/h max)
            int ySpd1 = h - (int) ((p1.speed / 320.0) * (h - 20)) - 10;
            int ySpd2 = h - (int) ((p2.speed / 320.0) * (h - 20)) - 10;
            g2.setColor(new Color(0, 200, 255));
            g2.drawLine(x1, ySpd1, x2, ySpd2);

            // Throttle Trace (Neon Green, 0-100%)
            int yThr1 = h - (int) ((p1.throttle / 100.0) * (h - 20)) - 10;
            int yThr2 = h - (int) ((p2.throttle / 100.0) * (h - 20)) - 10;
            g2.setColor(new Color(0, 255, 100));
            g2.drawLine(x1, yThr1, x2, yThr2);

            // Brake Trace (Bright Red, 0-100%)
            int yBrk1 = h - (int) ((p1.brake / 100.0) * (h - 20)) - 10;
            int yBrk2 = h - (int) ((p2.brake / 100.0) * (h - 20)) - 10;
            g2.setColor(new Color(255, 50, 50));
            g2.drawLine(x1, yBrk1, x2, yBrk2);
        }

        // Legend Overlay
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.setColor(new Color(0, 200, 255));
        g2.drawString("■ SPEED (km/h)", 15, 20);
        g2.setColor(new Color(0, 255, 100));
        g2.drawString("■ THROTTLE (%)", 140, 20);
        g2.setColor(new Color(255, 50, 50));
        g2.drawString("■ BRAKE (%)", 265, 20);
    }
}