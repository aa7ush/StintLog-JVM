import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.function.Consumer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TelemetryPlaybackEngine implements Runnable {
    private String csvFile;
    private Consumer<TelemetryPacket> onNewData;
    private boolean running = true;

    private int idxTime = -1, idxSpd = -1, idxThr = -1, idxBrk = -1, idxRpm = -1, idxGear = -1;

    public TelemetryPlaybackEngine(String csvFile, Consumer<TelemetryPacket> onNewData) {
        this.csvFile = csvFile;
        this.onNewData = onNewData;
    }

    private void mapHeaders(String headerLine) {
        String[] headers = headerLine.split(",");
        for (int i = 0; i < headers.length; i++) {
            String h = headers[i].toLowerCase().replace("\"", "").trim();
            if (h.equals("time") && idxTime == -1) idxTime = i;
            else if (h.equals("speed") && idxSpd == -1) idxSpd = i;
            else if (h.equals("throttle") && idxThr == -1) idxThr = i;
            else if (h.equals("brake") && idxBrk == -1) idxBrk = i;
            else if ((h.equals("rpms") || h.equals("rpm")) && idxRpm == -1) idxRpm = i;
            else if (h.equals("gear") && idxGear == -1) idxGear = i;
        }
    }

    private double parseSafe(String val) {
        val = val.replace("\"", "").trim();
        if (val.isEmpty() || val.equals("..")) return 0.0;
        try { return Double.parseDouble(val); } 
        catch (NumberFormatException e) { return 0.0; }
    }

    private double getSafeValue(String[] v, int index) {
        if (index < 0 || index >= v.length) return 0.0;
        return parseSafe(v[index]);
    }

    @Override
    public void run() {
        // Define target directory and ensure it exists
        String folderPath = "E:\\StintLog-JVM\\Session logs";
        File dir = new File(folderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Generate timestamped log filename inside the folder
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String outputLogPath = folderPath + File.separator + "session_log_" + timestamp + ".csv";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputLogPath))) {
             
            // Write CSV Header to the new log file
            bw.write("Time,Distance,Lap,Speed_Kmh,Throttle,Brake,RPM,Gear");
            bw.newLine();

            String line;
            double prevTime = -1;
            boolean dataStarted = false;
            
            double lapDistance = 0;
            int currentLap = 1;
            double currentLapTime = 0;
            double lastLapTime = 0;
            double bestLapTime = 0;
            final double SPA_LENGTH = 7004.0; 

            while ((line = br.readLine()) != null && running) {
                String cleanLine = line.replace("\"", "").trim();
                
                if (!dataStarted) {
                    if (cleanLine.toLowerCase().contains("speed") && cleanLine.toLowerCase().contains("throttle")) {
                        mapHeaders(line);
                    }
                    if (cleanLine.matches("^\\d+\\.\\d+.*") || cleanLine.matches("^\\d+.*")) {
                        dataStarted = true;
                    } else continue;
                }
                
                String[] v = line.split(",");
                double currTime = getSafeValue(v, idxTime);
                double rawSpeedMS = getSafeValue(v, idxSpd);
                
                if (prevTime != -1) {
                    double deltaTime = currTime -prevTime;
                    lapDistance += rawSpeedMS * deltaTime; 
                    currentLapTime += deltaTime; 

                    if (lapDistance >= SPA_LENGTH) {
                        lapDistance -= SPA_LENGTH;
                        currentLap++;
                        lastLapTime = currentLapTime;
                        
                        if (bestLapTime == 0 || lastLapTime < bestLapTime) {
                            bestLapTime = lastLapTime;
                        }
                        currentLapTime = 0; 
                    }
                }
                
                double speedKmh = rawSpeedMS * 3.6;
                double throttle = getSafeValue(v, idxThr);
                double brake = getSafeValue(v, idxBrk);
                double rpm = getSafeValue(v, idxRpm);
                int gear = (int) getSafeValue(v, idxGear);

                TelemetryPacket packet = new TelemetryPacket(
                    currTime, lapDistance, currentLap, currentLapTime, lastLapTime, bestLapTime,
                    speedKmh, throttle, brake, rpm, gear
                );

                // Write packet data to the session log CSV in E:\StintLog-JVM\Session logs
                bw.write(String.format("%.3f,%.2f,%d,%.2f,%.1f,%.1f,%.1f,%d",
                    currTime, lapDistance, currentLap, speedKmh, throttle, brake, rpm, gear));
                bw.newLine();

                if (prevTime != -1) {
                    long sleepMs = (long) ((currTime - prevTime) * 1000);
                    if (sleepMs > 0) Thread.sleep(sleepMs);
                }
                
                onNewData.accept(packet);
                prevTime = currTime;
            }
            System.out.println("Session successfully logged to: " + outputLogPath);
        } catch (Exception e) {
            System.err.println("Playback/Logging Error: " + e.getMessage());
        }
    }
}