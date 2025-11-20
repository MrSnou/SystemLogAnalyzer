#  System Log Analyzer (Work in progress — v0.1 MVP)

**System Log Analyzer** A standalone Windows desktop application for IT professionals to export,
parse and analyze Windows Event Logs with a fast, clean and modern UI.
Project was made for learning purposes to learn how to program graphic applications and gaining more skill in building apps in **MVC** pattern.

---

##  Description

The app extracts Application, System and (optionally) Security logs directly from Windows Event Viewer,
converts them to structured CSV files, and displays them inside a JavaFX UI offering advanced filtering,
searching and detailed inspection of each event.

---

## Features

  Windows Event Log support:

- **Application logs**
- **System logs**
- **Security logs (requires UAC elevation — handled automatically)**
**Logs are exported directly using Get-WinEvent from PowerShell.**

  Clean and responsive UI (JavaFX 25):

- **Fast event table**
- **Click any entry to show full details in a popup**
- **Auto-wrapping long messages**
- **Dynamic resizing**
- **Smooth loading screen with a background task**

  Filtering & Advanced Search

- **Real-time search bar**
- **Input sanitization for safe searching**
- **Level filters: INFO · WARN · ERROR**
- **Event counter: "Showing X / Y entries"**

## Why this app exists

  Windows Event Viewer is:

- **slow**
- **unintuitive**
- **bad at displaying long messages**
- **difficult to filter**
- **prone to freezing**

  System Log Analyzer offers:

- **fast CSV export**
- **instant searching**
- **readable formatting**
- **clean UX**
- **responsive UI**
- **predictable performance**

  Perfect for:

- **system administrators**
- **support engineers**
- **developers**
- **QA**
- **security teams**
- **IT students**

##  Tech stack 

- **Java 25**
- **JavaFX 25** – graphic interface
- **Maven** – dependency management
- **Spring Framework (Core)** – for dependency injection
- **MVC (Model-View-Controller)** – project architecture
- **PowerShell Get-WinEvent for log exporting**
  The backend (Spring Core) is embedded inside the JavaFX application and
  initialized manually during startup — ensuring full control over application flow.

  📦 Distribution (EXE version)
  A standalone EXE build using Launch4j (with bundled JRE) will be released soon.

  Final structure:
- **System_Log_Analyzer.exe**
- **/lib**   (all required libraries)
- **/jre**   (bundled JRE 25)

  (No Java installation needed!)


  📝 License
This project is licensed under:
Creative Commons Attribution–NonCommercial 4.0 International (CC BY-NC 4.0)

  🤝 Contributing
Pull requests, suggestions and bug reports are welcome.
Bug reports ironically encouraged 😄
