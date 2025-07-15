package LogikaGry;

import java.awt.Point;
import java.util.List;

import ModeleGry.Plansza;
import ModeleGry.Gra;

/**
 * Klasa ta sprawdza czy ruch jest poprawny.
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class LogikaRuchow {

    public static boolean czyPoprawnyRuch(Gra gra, int zIndexu, int naIndex) {
        return gra == null ? false : czyPoprawnyRuch(gra.pobierzPlansze(), gra.czyTuraGracza1(), zIndexu, naIndex, gra.getIndeksBitegoPionka());
    }
	
    public static boolean czyPoprawnyRuch(Plansza plansza, boolean czyTuraGracza1, int zIndexu, int naIndex, int przezIndex) {
        if (plansza == null || !Plansza.czyPrawidlowyIndex(zIndexu) || !Plansza.czyPrawidlowyIndex(naIndex)) {
            return false;
        } else if (zIndexu == naIndex) {
            return false;
        } else if (Plansza.czyPrawidlowyIndex(przezIndex) && przezIndex != zIndexu) {
            return false;
        }

        if (!sprawdzID(plansza, czyTuraGracza1, zIndexu, naIndex)) {
            return false;
        } else if (!sprawdzOdleglosc(plansza, czyTuraGracza1, zIndexu, naIndex)) {
            return false;
        }

        return true;
    }
	
    // Sprawdza czy identyfikatory pól są poprawne dla danego ruchu na planszy.
    private static boolean sprawdzID(Plansza plansza, boolean czyTuraGracza1, int zIndexu, int naIndex) {
        if (plansza.pobierzStanPolaZIndexu(naIndex) != Plansza.PUSTY) {
            return false;
        }

        int id = plansza.pobierzStanPolaZIndexu(zIndexu);
        if ((czyTuraGracza1 && !Plansza.czyCzarnyPionek(id)) || (!czyTuraGracza1 && !Plansza.czyBialyPionek(id))) {
            return false;
        }

        Point srodkowyPunkt = Plansza.srodkowyPunkt(zIndexu, naIndex);
        int srodkoweID = plansza.pobierzStanPolaZIndexu(Plansza.naIndexZPunktu(srodkowyPunkt));
        if (srodkoweID != Plansza.NIEPRAWIDLOWY && ((!czyTuraGracza1 && !Plansza.czyCzarnyPionek(srodkoweID)) || (czyTuraGracza1 && !Plansza.czyBialyPionek(srodkoweID)))) {
            return false;
        }

        return true;
    }
	
    // Sprawdza czy ruch jest po przekątnej i o wielkości 1 czy 2 we właściwym kierunku
    private static boolean sprawdzOdleglosc(Plansza plansza, boolean czyTuraGracza1, int zIndexu, int naIndex) {
        
        // Sprawdz czy ruch po przekątnej
        Point zPunktu = Plansza.naPunkt(zIndexu);
        Point naPunkt = Plansza.naPunkt(naIndex);
        int odlegloscX = naPunkt.x - zPunktu.x;
        int odlegloscY= naPunkt.y - zPunktu.y;
        if (Math.abs(odlegloscX) != Math.abs(odlegloscY) || Math.abs(odlegloscX) > 2 || odlegloscX == 0) {
            return false;
        }

        // Sprawdza czy ruch w poprawnym kierunku
        int id = plansza.pobierzStanPolaZIndexu(zIndexu);
        if ((id == Plansza.BIALY_PIONEK && odlegloscY > 0) || (id == Plansza.CZARNY_PIONEK && odlegloscY < 0)) {
            return false;
        }

        // Sprawdza czy jeśli nie jest to pominięcie, to nie ma żadnych dostępnych ruchów
        Point srodkowyPunkt = Plansza.srodkowyPunkt(zIndexu, naIndex);
        int srodkoweID = plansza.pobierzStanPolaZIndexu(Plansza.naIndexZPunktu(srodkowyPunkt));
        if (srodkoweID < 0) {
            List<Point> pionki;
            if (czyTuraGracza1) {
                pionki = plansza.znajdzPola(Plansza.CZARNY_PIONEK);
                pionki.addAll(plansza.znajdzPola(Plansza.CZARNY_KROL));
            } else {
                pionki = plansza.znajdzPola(Plansza.BIALY_PIONEK);
                pionki.addAll(plansza.znajdzPola(Plansza.BIALY_KROL));
            }

            for (Point pionek : pionki) {
                int index = Plansza.naIndexZPunktu(pionek);
                if (!GeneratorRuchow.pobierzSkoki(plansza, index).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}