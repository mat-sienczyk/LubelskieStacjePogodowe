# LubelskieStacjePogodowe

Lubelskie Stacje Pogodowe v2

Techologie:
- Koltin
- Android Jetpack (Room, LivaData, DataBinding, ViewModel, WorkManager)
- Retrofit2, OkHttp3
- Google Play Services (Maps, Location)

Ekran głowny:

Bottom bar navigation podzielona na 3 sekcje: Temperatura, Smog, Mapa

Temperatura:
- Na górze sekcja Ulubione
- pozniej sekcja "Pozostałe" z posortowanymi stacjami po lookalizacji albo (jesli brak zgody) to alfabetycznie

Smog:
- Na górze sekcja Ulubione
- pozniej sekcja "Pozostałe" z posortowanymi stacjami po lookalizacji albo (jesli brak zgody) to alfabetycznie

Mapa:
- lokalizacja stacji pogodowych i smogowych rozróżnione kolorami, możliwość przełączania warstwy (satelitarna/zwykła) (?)

Na toolbarze nazwa aplikacji (Lubelskie Stacje Pogodowe) a w overflowmenu 3 opcje (te co były) - Ustawienia, Co nowego? i O aplikacji

To wszystko w jednej aktywności, stacje wyświetlane w liscie recyclerview na cardView, po kliknięciu w stacje lub odnośnik na mapie otwiera nam się aktywność ze stacją (ta sama dla smogowej i pogodowej)

Stacje na liście:

- na cardview
- na górze nazwa, data, rodzaj stacji, odległość
- najwazneijsze 3-4 dane (temp, wiatr, wilgotność, ciśnienie, PM2,5, PM10) poziomo z ikonkami

Po kliknięciu przechodzmi z animacja do nowej aktywności - szczegóły stacji.

Ekran szczegółów stacji:

W actionbarze strzałka wstecz, nazwa stacji i serduszko po kliknięciu któego ddoajemy stacje do ulubionych (czerwone lub puste)

- widok danych stacji na cardview z danymmi stacji i odnośnikiem do jej strony który otwiera customTabs (tak jak jest obecnie)
- przy danych z wykresami, wykres wysuwa sie z dołu - na wykresie linia pokazująca wczoraj/dziś

TODO:
- rysowanie wykresu
- zaznaczanie nieaktualnych danych
- stałe powiadomienie z temperaturą
- powiadomienie gdy jakość powietrza jest słaba
- widgety dla temperatury (z konfiguracją?)
- widget dla smogu

