@echo off
cd /d "%~dp0"
if exist "%~dp0PoeBuildExporter-1.0-SNAPSHOT.jar" (
    set JAR="%~dp0PoeBuildExporter-1.0-SNAPSHOT.jar"
) else (
    set JAR="%~dp0target\PoeBuildExporter-1.0-SNAPSHOT.jar"
)
java -jar %JAR%
if %ERRORLEVEL% neq 0 (
    echo.
    echo Application exited with an error. Check logs\poebuildexporter.log for details.
    pause
)
