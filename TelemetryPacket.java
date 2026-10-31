public class TelemetryPacket {
    public double time;
    public double distance;
    public int lap;
    public double currentLapTime;
    public double lastLapTime;
    public double bestLapTime;
    public double speed;
    public double throttle;
    public double brake;
    public double rpm;
    public int gear;

    public TelemetryPacket(double time, double distance, int lap, double currentLapTime, double lastLapTime, double bestLapTime, double speed, double throttle, double brake, double rpm, int gear) {
        this.time = time;
        this.distance = distance;
        this.lap = lap;
        this.currentLapTime = currentLapTime;
        this.lastLapTime = lastLapTime;
        this.bestLapTime = bestLapTime;
        this.speed = speed;
        this.throttle = throttle;
        this.brake = brake;
        this.rpm = rpm;
        this.gear = gear;
    }
}