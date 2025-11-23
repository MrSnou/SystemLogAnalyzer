## 🖥️ Instalacja (2 kliknięcia — dla rekruterów)

Aplikacja jest dystrybuowana jako **samodzielny plik EXE** zbudowany przy użyciu `jpackage`.

### ✔ Wymagania
Nic poza:
- Windows 10/11
- Lokalnym profilem użytkownika
- Windows PowerShell (built-in on Windows 10/11)

> **Java jest dołączona do pliku EXE**.
> NIE musisz instalować Javy.

### ✔ Uruchamianie aplikacji
1. Pobierz `SystemLogAnalyzer.exe`
2. Wypakuj program w dowolne miejsce
3. Kliknij go dwukrotnie na SystemLogAnalyzer.exe
4. (Opcjonalnie) Jeśli wybrano dzienniki zabezpieczeń → potwierdź wyskakujące okienko UAC systemu Windows

To wszystko.

## 📦 Jak to działa

### 1. Wybierz:
- katalog do przechowywania wyeksportowanych plików CSV
- katalog do zapisywania raportów
- typy logów (Aplikacja/System/Zabezpieczenia)

### 2. Aplikacja:
- uruchamia program PowerShell → eksportuje plik CSV
- analizuje rekordy
- ładuje je do tabeli JavaFX

### 3. Możesz:
- filtrować
- wyszukiwać
- sprawdzać szczegóły
- odświeżać logi w dowolnym momencie

Wszystko to bez ręcznego uruchamiania Podglądu zdarzeń.