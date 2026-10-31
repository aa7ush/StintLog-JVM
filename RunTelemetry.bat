@echo off
E:
cd "E:\Motorsport Stint Telemetry Analysis"
echo Compiling telemetry architecture...
javac *.java
if %errorlevel% neq 0 (
    echo Compilation failed. Check syntax.
    pause
    exit /b
)
echo Launching dashboard...
java Main