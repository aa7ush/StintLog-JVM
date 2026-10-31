# StintLog-JVM

**Real-Time Motorsport Telemetry Data Logger & Stint Analysis System**

StintLog-JVM is a multithreaded Java Swing application designed to ingest, parse, analyze, and log high-frequency motorsport telemetry data from standard MoTeC CSV logs. Built with a clean, print-optimized light theme interface, it provides real-time physics-based distance integration, sector splitting, broadcast-style timing (`M:SS.mmm`), zero-latency acoustic cues, and automated session disk logging for race engineering and driver performance evaluation.

> **Data Acquisition & Simulation Note:** While this system is architected for general motorsport telemetry applications and standard MoTeC CSV data streams, acquiring physical track data from a real-world race car is prohibitively expensive. To simulate authentic, high-frequency vehicle dynamics and ECU telemetry at zero cost, **Assetto Corsa Competizione (ACC)** was utilized alongside MoTeC i2 Pro to generate the baseline telemetry dataset (`spa_telemetry.csv`).

---

## Key Features

* **Real-Time Multi-Trace Telemetry Graphing:** Custom anti-aliased Java Swing `Graphics2D` rendering engine tracking **Speed** (Blue), **Throttle** (Dark Green), and **Brake** (Red) across a 500-point rolling window.
* **First-Principles Physics Integration:** Dynamically calculates cumulative track distance using instantaneous velocity and time deltas ($\text{Distance} = \sum v \cdot \Delta t$), overcoming the absence of raw GPS coordinates in standard telemetry exports.
* **Automated Sector & Lap Splitting:** Configured out-of-the-box for **Circuit de Spa-Francorchamps** ($7,004\text{ m}$), automatically triggering transitions across Sector 1 ($2,200\text{ m}$), Sector 2 ($5,300\text{ m}$), and Lap completions (customizable for any circuit).
* **Motorsport Live Timing:** Tracks and formats **Current Lap**, **Last Lap**, and **Best Lap** times into standard broadcast notation (`M:SS.mmm`).
* **Zero-Latency Audio Synthesis:** Pre-computes sine-wave audio buffers into RAM (`javax.sound.sampled.Clip`) at startup to trigger instantaneous $800\text{ Hz}$ ($0.5\text{ s}$) sector beeps and $1000\text{ Hz}$ ($1.0\text{ s}$) lap completion alerts without stalling the UI thread.
* **Automated Session Logging:** Concurrently writes processed, clean telemetry packets to timestamped CSV files (`session_log_YYYYMMDD_HHMMSS.csv`) inside the `Session logs` directory for post-stint archiving.

---

## Project Structure

```text
StintLog-JVM/
├── Session logs/                  # Output directory for generated timestamped CSV session logs
├── Main.java                      # Application entry point & thread initializer
├── TelemetryDashboard.java        # Swing JFrame UI, live timing formatter & audio Clip synthesizer
├── TelemetryGraphPanel.java       # Custom JPanel for anti-aliased multi-trace line plotting
├── TelemetryPacket.java           # Data Transfer Object (DTO) encapsulating frame metrics
├── TelemetryPlaybackEngine.java   # Background thread for CSV parsing, physics & disk logging
├── spa_telemetry.csv              # MoTeC telemetry dataset (Circuit de Spa-Francorchamps)
├── RunTelemetry.bat               # Windows batch script for automated compilation and execution
├── README.md                      # Project overview and architecture documentation
└── INSTRUCTIONS.md                # Setup, path configuration, and execution guide
```

---

## System Architecture

1. **Ingestion & Header Locking (`TelemetryPlaybackEngine`):** Scans MoTeC CSV exports, strips quotation formatting, and locks onto primary telemetry channels while ignoring duplicate session timers.
2. **Kinematics & Timing Loop:** Computes $\Delta t$ between frames to pace playback speed accurately, integrates raw velocity ($\text{m/s}$) for spatial tracking, and converts speed to $\text{km/h}$ (`rawSpeedMS * 3.6`) for dashboard presentation.
3. **Concurrent I/O & UI Dispatch:** Streams formatted data rows via `BufferedWriter` to disk while dispatching `TelemetryPacket` objects to the Swing Event Dispatch Thread (`SwingUtilities.invokeLater`).

---

## Quick Start

1. Ensure **Java Development Kit (JDK 8 or higher)** is installed and added to your system `PATH`.
2. Clone this repository or download the project folder.
3. Review `INSTRUCTIONS.md` to configure your local folder paths if running outside `E:\StintLog-JVM`.
4. Double-click `RunTelemetry.bat` (or run `javac *.java && java Main` via terminal) to launch the telemetry logger.

---