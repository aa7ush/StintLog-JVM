import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class TelemetryGraphPanel extends JPanel {
    private final ArrayList<TelemetryPacket> buffer = new ArrayList<>();
    private final int MAX_POINTS = 500;

    public TelemetryGraphPanel() {
        setBackground(Color.WHITE);
    }

    public void addPacket(TelemetryPacket packet) {
        if (buffer.size() >= MAX_POINTS) {
            buffer.remove(0);
        }
        buffer.add(packet);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (buffer.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        
        Path2D speedPath = new Path2D.Double();
        Path2D throttlePath = new Path2D.Double();
        Path2D brakePath = new Path2D.Double();

        double xStep = (double) w / MAX_POINTS;

        for (int i = 0; i < buffer.size(); i++) {
            TelemetryPacket p = buffer.get(i);
            double x = i * xStep;
            
            // Scaling: Speed max ~350, Throttle max 100, Brake max 100
            double ySpeed = h - ((p.speed / 350.0) * h);
            double yThrottle = h - ((p.throttle / 100.0) * h);
            double yBrake = h - ((p.brake / 100.0) * h);

            if (i == 0) {
                speedPath.moveTo(x, ySpeed);
                throttlePath.moveTo(x, yThrottle);
                brakePath.moveTo(x, yBrake);
            } else {
                speedPath.lineTo(x, ySpeed);
                throttlePath.lineTo(x, yThrottle);
                brakePath.lineTo(x, yBrake);
            }
        }

        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.RED);
        g2d.draw(brakePath);
        g2d.setColor(new Color(0, 150, 0)); // Dark green for throttle contrast on white
        g2d.draw(throttlePath);
        g2d.setColor(Color.BLUE); // Blue for speed contrast on white
        g2d.draw(speedPath);
    }
}