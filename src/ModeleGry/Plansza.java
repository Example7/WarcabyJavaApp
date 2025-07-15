package ModeleGry;

import java.util.*;
import java.awt.Point;
/**
 * Klasa ta reprezentuje plansze do gry w warcaby i umożliwia manipulację stanami pól za pomocą bitów.
 * <p>Pionki mogą poruszać się po skosie tylko po czarnych polach.
 * Plansza używa 3 bitów do przedstawienia stanu jednej czarnej kratki.</p>
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class Plansza {
    // Stałe reprezentujące różne stany pól na planszy
    public static final int NIEPRAWIDLOWY = -1;
    public static final int PUSTY = 0;
    // 1 bit określa czy jest to figura, 2 bit kolor figury, 3 bit czy jest to pionek czy król
    public static final int CZARNY_PIONEK = (4 * 1) + (2 * 1) + (1 * 0); // 3 bity: 1 1 0
    public static final int BIALY_PIONEK = (4 * 1) + (2 * 0) + (1 * 0);  // 3 bity: 1 0 0
    public static final int CZARNY_KROL = (4 * 1) + (2 * 1) + (1 * 1);   // 3 bity: 1 1 1
    public static final int BIALY_KROL = (4 * 1) + (2 * 0) + (1 * 1);    // 3 bity: 1 0 1
    private int[] stanPlanszy; // zawiera 3 tablice dla poszczególnych bitów do przechowywania stanu planszy
	
    // Inicjalizuje planszę i ją resetuje
    public Plansza() {
        reset();
    }
    
    // Tworzy kopie planszy i ją zwraca
    public Plansza skopiujPlansze() {
        Plansza copy = new Plansza();
        copy.stanPlanszy = stanPlanszy.clone();
        return copy;
    }
	
    // Metoda resetująca planszę do stanu początkowego
    public void reset() {
        this.stanPlanszy = new int[3];
        for (int i = 0; i < 12; i ++) {
            ustawStanPola(i, CZARNY_PIONEK);
            ustawStanPola(31 - i, BIALY_PIONEK);
        }
    }
    
    // Metoda znajdująca wszystkie pola o określonym identyfikatorze
    public List<Point> znajdzPola(int id) {
        List<Point> punkty = new ArrayList<>();
        for (int i = 0; i < 32; i ++) {
            if (pobierzStanPolaZIndexu(i) == id) {
                punkty.add(naPunkt(i));
            }
        }
        return punkty;
    }
    
    // Metoda ustawiająca stan pola o indeksie index
    public void ustawStanPola(int index, int id) {
        if (!czyPrawidlowyIndex(index)) {
            return;
        }

        if (id < 0) {
            id = PUSTY;
        }

        for (int i = 0; i < stanPlanszy.length; i ++) {
            boolean set = ((1 << (stanPlanszy.length - i - 1)) & id) != 0;
            this.stanPlanszy[i] = ustawBit(stanPlanszy[i], index, set);
        }
    }
	
    public int pobierzStanPola(int x, int y) {
        return pobierzStanPolaZIndexu(naIndex(x, y));
    }
	
    public int pobierzStanPolaZIndexu(int index) {
        if (!czyPrawidlowyIndex(index)) {
            return NIEPRAWIDLOWY;
        }
        return pobierzBit(stanPlanszy[0], index) * 4 + pobierzBit(stanPlanszy[1], index) * 2 + pobierzBit(stanPlanszy[2], index);
    }
    
    // Metoda przekształcająca indeks na współrzędne Point
    public static Point naPunkt(int index) {
        int y = index / 4;
        int x = 2 * (index % 4) + (y + 1) % 2;
        return !czyPrawidlowyIndex(index)? new Point(-1, -1) : new Point(x, y);
    }
	
    // Metoda przekształcająca współrzędne x, y na indeks
    public static int naIndex(int x, int y) {
        if (!czyPrawidlowyPunkt(new Point(x, y))) {
            return -1;
        }
        return y * 4 + x / 2;
    }

    // Metoda przekształcająca obiekt Point na indeks
    public static int naIndexZPunktu(Point p) {
        return (p == null)? -1 : naIndex(p.x, p.y);
    }
	
    // Metoda ustawiająca określony bit w celu zmiany stanu
    public static int ustawBit(int ktoryStan, int bit, boolean czyUstawic) {
        if (bit < 0 || bit > 31) {
            return ktoryStan;
        }

        if (czyUstawic) {
        // Ustawienie bitu na 1
            ktoryStan |= (1 << bit);
        } else {
        // Reset bitu na 0 (przy każdym ruchu pionka, pole na którym znajdował sie pionek jest ustawiane na 0)
            ktoryStan &= (~(1 << bit));
        }
        return ktoryStan;
    }
	
    public static int pobierzBit(int ktoryStan, int bit) {
        if (bit < 0 || bit > 31) {
            return 0;
        }
        return (ktoryStan & (1 << bit)) != 0 ? 1 : 0;
    }
    
    // Metoda obliczająca środkowe pole pomiędzy dwoma punktami
    public static Point srodkowyPunkt(Point p1, Point p2) {
        if (p1 == null || p2 == null) {
            return new Point(-1, -1);
        }
        return srodkowyPunkt(p1.x, p1.y, p2.x, p2.y);
    }
	
    public static Point srodkowyPunkt(int index1, int index2) {
        return srodkowyPunkt(naPunkt(index1), naPunkt(index2));
    }
	
    public static Point srodkowyPunkt(int x1, int y1, int x2, int y2) {
        int roznicaX = x2 - x1;
        int roznicaY = y2 - y1;
        if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0 || x1 > 7 || y1 > 7 || x2 > 7 || y2 > 7) {
            return new Point(-1, -1);
        } else if (x1 % 2 == y1 % 2 || x2 % 2 == y2 % 2) {
            return new Point(-1, -1);
        } else if (Math.abs(roznicaX) != Math.abs(roznicaY) || Math.abs(roznicaX) != 2) {
            return new Point(-1, -1);
        }
        return new Point(x1 + roznicaX / 2, y1 + roznicaY / 2);
    }
    
    // Metoda sprawdzająca czy indeks jest prawidłowy
    public static boolean czyPrawidlowyIndex(int testIndex) {
        return testIndex >= 0 && testIndex < 32;
    }
	
    public static boolean czyPrawidlowyPunkt(Point testPoint) {
        if (testPoint == null) {
            return false;
        }

        final int x = testPoint.x, y = testPoint.y;
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            return false;
        }

        if (x % 2 == y % 2) {
            return false;
        }
        return true;
    }
    
    // Metoda sprawdzająca czy pionek jest czarny
    public static boolean czyCzarnyPionek(int id) {
        return id == Plansza.CZARNY_PIONEK || id == Plansza.CZARNY_KROL;
    }

    public static boolean czyBialyPionek(int id) {
        return id == Plansza.BIALY_PIONEK || id == Plansza.BIALY_KROL;
    }
	
    public static boolean czyKrol(int id) {
        return id == Plansza.CZARNY_KROL || id == Plansza.BIALY_KROL;
    }
    
    public int[] getStanPlanszy() {
        return stanPlanszy;
    }
}