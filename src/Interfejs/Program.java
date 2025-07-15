package Interfejs;
/**
 * Klasa główna programu, która uruchamia interfejs użytkownika gry w warcaby.
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class Program {
    /**
     * Temat projektu: Warcaby
     * Autor: Kacper Kałużny
     * Wymagania programu: Należy uruchomić plik Program.java, w którym znajduje się main.
     * Opis działania programu: Jest to program, który uruchamia gre w Warcaby na planszy 8x8.
     * Figury mogą poruszać się tylko po czarnych polach na skos. Możliwe są skoki przez figury przeciwnika.
     * Po dojściu pionka na koniec planszy przeciwnika zamienia się on w króla.
     * Opis wymagań technicznych: Projekt zawiera więcej niż 3 własne klasy z metodami o różnym dostępie (public, private).
     * Występuje dziedziczenie. Występuje Interfejs w Swingu. Występuje obsługa wyjątków. Występuje obsługa zdarzeń.
     * Występują kontenery danych. Występuje czytanie i pisanie do piku. Występuje użycie wątków. Występuje komunikacja przy pomocy socketów.
     * Największe problemy były z komunikacją pomiędzy klientami, natomiast w pewnym momencie udało mi się znaleźć rozwiązanie które akurat pasowało do mojego projektu.
     */
    public static void main(String[] args) {
        OkienkoGry okienkoGry = new OkienkoGry();
        okienkoGry.setDefaultCloseOperation(OkienkoGry.EXIT_ON_CLOSE);
        okienkoGry.setVisible(true);
    }
}
