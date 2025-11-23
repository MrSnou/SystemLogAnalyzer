## 🖥️ Installation (2 clicks — for recruiters)

The application is distributed as a **standalone EXE** built with `jpackage`.

### ✔ Requirements
Nothing except:
- Windows 10/11 (Currently on win 10 System Logs are not working - TODO)
- Local user profile
- Windows PowerShell (built-in on Windows 10/11)

> **Java is bundled inside the EXE**.  
> You do NOT need to install Java.

### ✔ Running the app
1. Download `SystemLogAnalyzer.rar`
2. unpack it with winrar anywhere.
3. Double-click on SystemLogAnalyzer.exe
4. (Optional) If Security logs are selected → confirm Windows UAC popup 

That's all.

## 📦 How it works

### 1. Choose:
- directory for storing exported CSVs
- directory for saving reports
- log types (Application / System / Security)

### 2. The app:
- runs PowerShell → exports CSV
- parses records
- loads them into a JavaFX table

### 3. You can:
- filter
- search
- inspect details
- refresh logs anytime

All without touching Event Viewer manually.