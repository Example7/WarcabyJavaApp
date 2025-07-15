package LogikaGry;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import ModeleGry.Plansza;
/**
 * Klasa ta zajmuje się sprawdzaniem czy dany pionek może się ruszyć czy też przeskoczyć innego pionka.
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class GeneratorRuchow {

    // Pobiera liste ruchów punktowKoncowych dla danego zIndexu
    public static List<Point> pobierzRuchy(Plansza plansza, int zIndexu) {
        List<Point> punktyKoncowe = new ArrayList<>();
        if (plansza == null || !Plansza.czyPrawidlowyIndex(zIndexu)) {
            return punktyKoncowe;
        }

        int id = plansza.pobierzStanPolaZIndexu(zIndexu);
        Point p = Plansza.naPunkt(zIndexu);
        dodajPunkt(punktyKoncowe, p, id, 1);

        for (int i = 0; i < punktyKoncowe.size(); i ++) {
            Point end = punktyKoncowe.get(i);
            if (plansza.pobierzStanPola(end.x, end.y) != Plansza.PUSTY) {
                punktyKoncowe.remove(i --);
            }
        }

        return punktyKoncowe;
    }
    
    // Pobiera liste skoków punktowKoncowych dla danego zIndexu
    public static List<Point> pobierzSkoki(Plansza plansza, int zIndexu) {
        List<Point> punktyKoncowe = new ArrayList<>();
        if (plansza == null || !Plansza.czyPrawidlowyIndex(zIndexu)) {
            return punktyKoncowe;
        }

        int id = plansza.pobierzStanPolaZIndexu(zIndexu);
        Point p = Plansza.naPunkt(zIndexu);
        dodajPunkt(punktyKoncowe, p, id, 2);

        for (int i = 0; i < punktyKoncowe.size(); i ++) {

            Point end = punktyKoncowe.get(i);
            if (!czyMozliwySkok(plansza, zIndexu, Plansza.naIndexZPunktu(end))) {
                punktyKoncowe.remove(i --);
            }
        }
        return punktyKoncowe;
    }
	
    // Sprawdza czy przeskok pionka jest możliwy
    public static boolean czyMozliwySkok(Plansza plansza,
        int zIndex, int naIndex) {

        if (plansza == null) {
            return false;
        }

        // Sprawdza czy naIndex jest pusty
        if (plansza.pobierzStanPolaZIndexu(naIndex) != Plansza.PUSTY) {
            return false;
        }

        // Sprawdza czy pionek pomiędzy jest pionkiem drugiego gracza
        int id = plansza.pobierzStanPolaZIndexu(zIndex);
        int srodkoweID = plansza.pobierzStanPolaZIndexu(Plansza.naIndexZPunktu(Plansza.srodkowyPunkt(zIndex, naIndex)));
        if (id == Plansza.NIEPRAWIDLOWY || id == Plansza.PUSTY) {
            return false;
        } else if (srodkoweID == Plansza.NIEPRAWIDLOWY || srodkoweID == Plansza.PUSTY) {
            return false;
        } else if ((Plansza.czyCzarnyPionek(srodkoweID) && !Plansza.czyBialyPionek(id)) || (!Plansza.czyCzarnyPionek(srodkoweID) && Plansza.czyBialyPionek(id))) {
            return false;
        }
        return true;
    }
	
    // Dodaje punkty do listy które są potencjalnie możliwe do wykonania ruchu lub przeskoku pionka
    public static void dodajPunkt(List<Point> punkty, Point punkt, int id, int przesuniecie) {
        boolean czyKrol = Plansza.czyKrol(id);
        if (czyKrol || id == Plansza.CZARNY_PIONEK) {
            punkty.add(new Point(punkt.x + przesuniecie, punkt.y + przesuniecie));
            punkty.add(new Point(punkt.x - przesuniecie, punkt.y + przesuniecie));
        }

        if (czyKrol || id == Plansza.BIALY_PIONEK) {
            punkty.add(new Point(punkt.x + przesuniecie, punkt.y - przesuniecie));
            punkty.add(new Point(punkt.x - przesuniecie, punkt.y - przesuniecie));
        }
    }
}