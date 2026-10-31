# Setup, Configuration & Execution Guide

Follow these instructions to configure directory paths, compile the source code, and run the **StintLog-JVM** application on your local machine, as well as how to push the repository to GitHub cleanly.

---

## 1. Prerequisites

* **Java Development Kit (JDK):** Version 8 or higher must be installed.
* Verify your installation by opening Command Prompt or PowerShell and running:
  ```cmd
  javac -version
  java -version
  ```

---

## 2. Directory & Path Configuration

By default, the project is configured to run from `E:\StintLog-JVM`. If you clone or move this project to a different drive or folder (e.g., `C:\Users\YourName\Documents\StintLog-JVM`), you must update the file paths in the following two locations:

### A. Updating the Session Logs Output Directory (`TelemetryPlaybackEngine.java`)
Open `TelemetryPlaybackEngine.java` in any text editor or IDE and locate the `run()` method (around **Line 49**):

```java
// Current hardcoded path:
String folderPath = "E:\\StintLog-JVM\\Session logs";
```

* **Option 1 (Recommended - Portable Relative Path):** Change this line to use a relative path so it automatically creates and uses the `Session logs` folder inside whatever directory the project is currently located in:
  ```java
  String folderPath = "Session logs";
  ```
* **Option 2 (Custom Absolute Path):** Replace `E:\\StintLog-JVM\\Session logs` with your own custom folder path (ensure you use double backslashes `\\` in Java strings).

### B. Updating the Input Telemetry File (`Main.java`)
Open `Main.java` and verify **Line 10**:

```java
TelemetryPlaybackEngine engine = new TelemetryPlaybackEngine(
    "spa_telemetry.csv", 
    packet -> dashboard.updateData(packet)
);
```
Ensure that `spa_telemetry.csv` (or any custom MoTeC CSV file you wish to analyze) is placed directly in the root project folder alongside `Main.java`. If you use a different CSV file, change `"spa_telemetry.csv"` to match your new file's name.

### C. Updating the Batch Launcher (`RunTelemetry.bat`)
Right-click `RunTelemetry.bat` and select **Edit in Notepad**.
* If your batch file contains a hardcoded directory change like `cd /d "E:\StintLog-JVM"`, either update it to match your new folder path or replace it with `cd /d "%~dp0"` so it automatically runs from the current folder:
  ```bat
  @echo off
  cd /d "%~dp0"
  echo Compiling StintLog-JVM...
  javac *.java
  echo Launching Telemetry Dashboard...
  java Main
  pause
  ```

---

## 3. Customizing Track & Sector Boundaries (Optional)

If you load a CSV file from a circuit other than **Spa-Francorchamps**, update the track geometry constants in:

1. **`TelemetryPlaybackEngine.java` (Line 67):**
   ```java
   final double SPA_LENGTH = 7004.0; // Change 7004.0 to your track's total length in meters
   ```
2. **`TelemetryDashboard.java` (Lines 66–70):**
   ```java
   int sector = 1;
   if (packet.distance > 5300) {        // Change 5300 to Sector 2 end distance (m)
       sector = 3;
   } else if (packet.distance > 2200) { // Change 2200 to Sector 1 end distance (m)
       sector = 2;
   }
   ```

---

## 4. How to Compile and Run the Project

### Method 1: Using the Automated Batch Script (Windows)
1. Double-click `RunTelemetry.bat`.
2. The script will automatically compile all `.java` files into `.class` bytecode and launch the Swing dashboard window.

### Method 2: Manual Terminal Execution (Windows / macOS / Linux)
1. Open a terminal or Command Prompt inside the `StintLog-JVM` project directory.
2. Compile all Java source files:
   ```bash
   javac Main.java TelemetryDashboard.java TelemetryGraphPanel.java TelemetryPacket.java TelemetryPlaybackEngine.java
   ```
3. Run the application:
   ```bash
   java Main
   ```

---