# System Log Analyzer (W trakcie realizacji — v0.1 MVP)

**System Log Analyzer** Samodzielna aplikacja desktopowa dla systemu Windows, przeznaczona dla specjalistów IT, umożliwiająca eksportowanie,
analizowanie i analizowanie dzienników zdarzeń systemu Windows za pomocą szybkiego, przejrzystego i nowoczesnego interfejsu użytkownika.
Projekt został stworzony w celach edukacyjnych, aby nauczyć się programowania aplikacji graficznych i zdobyć umiejętności w zakresie tworzenia aplikacji w oparciu o wzorzec **MVC**.

---

## Opis

Aplikacja wyodrębnia dzienniki aplikacji, systemu i (opcjonalnie) zabezpieczeń bezpośrednio z Podglądu zdarzeń systemu Windows,
konwertuje je do ustrukturyzowanych plików CSV i wyświetla w interfejsie użytkownika JavaFX, oferując zaawansowane filtrowanie,
wyszukiwanie i szczegółową inspekcję każdego zdarzenia.

---

## Funkcje

Obsługa dziennika zdarzeń systemu Windows:

- **Dzienniki aplikacji**
- **Dzienniki systemowe**
- **Dzienniki zabezpieczeń (wymagają podniesienia uprawnień UAC — obsługiwane automatycznie)**
  **Dzienniki są eksportowane bezpośrednio za pomocą polecenia Get-WinEvent z poziomu programu PowerShell.**

Przejrzysty i responsywny interfejs użytkownika (JavaFX 25):

- **Szybka tabela zdarzeń**
- **Kliknij dowolny wpis, aby wyświetlić szczegółowe informacje w wyskakującym okienku**
- **Automatyczne zawijanie długich wiadomości**
- **Dynamiczna zmiana rozmiaru**
- **Płynny ekran ładowania z zadaniem w tle**

Filtrowanie i wyszukiwanie zaawansowane

- **Pasek wyszukiwania w czasie rzeczywistym**
- **Czyszczenie danych wejściowych dla bezpiecznego wyszukiwania**
- **Filtry poziomów: INFO · WARN · ERROR**
- **Licznik zdarzeń: „Wyświetlanie wpisów X / Y”**

## Dlaczego ta aplikacja istnieje

Podgląd zdarzeń systemu Windows jest:

- **wolny**
- **nieintuicyjny**
- **słabo wyświetla długie wiadomości**
- **trudny do filtrowania**
- **podatny na zawieszanie się**

System Log Analyzer oferuje:

- **szybki eksport CSV**
- **natychmiastowe wyszukiwanie**
- **czytelne formatowanie**
- **czysty UX**
- **responsywny UI**
- **przewidywalną wydajność**

Idealny dla:

- **administratorów systemów**
- **inżynierów wsparcia**
- **programistów**
- **zespołów ds. zapewnienia jakości**
- **zespołów ds. bezpieczeństwa**
- **studentów informatyki**

## Stos technologiczny

- **Java 25**
- **JavaFX 25** – interfejs graficzny
- **Maven** – zarządzanie zależnościami
- **Spring Framework (Core)** – do wstrzykiwania zależności
- **MVC (Model-View-Controller)** – projekt architektura
- **PowerShell Get-WinEvent do eksportowania logów**
  Backend (Spring Core) jest osadzony w aplikacji JavaFX i
  jest inicjowany ręcznie podczas uruchamiania — zapewniając pełną kontrolę nad przepływem aplikacji.

📦 Dystrybucja (wersja EXE)
Wkrótce zostanie wydana samodzielna kompilacja EXE z wykorzystaniem Launch4j (z dołączonym JRE).

Struktura finalna:
- **System_Log_Analyzer.exe**
- **/lib** (wszystkie wymagane biblioteki)
- **/jre** (dołączony JRE 25)

(Instalacja Javy nie jest wymagana!)

📝 Licencja
Ten projekt jest licencjonowany na zasadach:
Creative Commons Attribution–NonCommercial 4.0 International (CC BY-NC 4.0)

🤝 Współpraca
Zgłaszanie żądań ściągnięcia, sugestii i błędów jest mile widziane.
Zgłaszanie błędów jest mile widziane!! 😄