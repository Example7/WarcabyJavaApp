package ModeleGry;

import java.awt.Point;
import java.util.List;

import LogikaGry.GeneratorRuchow;
import LogikaGry.LogikaRuchow;
/**
 * Klasa ta reprezentuje grę w Warcaby i zawiera metodę do aktualizacji stanu gry i sprawdzania czyja jest tura.
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class Gra {
    private Plansza plansza;
    private boolean czyTuraGracza1;
    private int indeksBitegoPionka;
	
    public Gra() {
        restart();
    }

    public Gra(String stan) {
        setStanGry(stan);
    }

    public Gra(Plansza plansza, boolean czyTuraGracza1, int indeksBitegoPionka) {
        this.plansza = (plansza == null)? new Plansza() : plansza;
        this.czyTuraGracza1 = czyTuraGracza1;
        this.indeksBitegoPionka = indeksBitegoPionka;
    }

    // Tworzy kopie gry
    public Gra skopujGre() {
        Gra g = new Gra();
        g.plansza = plansza.skopiujPlansze();
        g.czyTuraGracza1 = czyTuraGracza1;
        g.indeksBitegoPionka = indeksBitegoPionka;
        return g;
    }

    // Resetuje gre do stanu początkowego
    public void restart() {
        this.plansza = new Plansza();
        this.czyTuraGracza1 = true;
        this.indeksBitegoPionka = -1;
    }

    // Wykonuje ruch zPunktu -> naPunkt
    public boolean wykonajRuch(Point zPunktu, Point naPunkt) {
        if (zPunktu == null || naPunkt == null) {
            return false;
        }
        return wykonajRuch(Plansza.naIndexZPunktu(zPunktu), Plansza.naIndexZPunktu(naPunkt));
    }

    // Wykonuje ruch zIndexu -> naIndex
    public boolean wykonajRuch(int zIndexu, int naIndex) {
        if (!LogikaRuchow.czyPoprawnyRuch(this, zIndexu, naIndex)) {
            return false;
        }

        // ruch
        Point srodek = Plansza.srodkowyPunkt(zIndexu, naIndex);
        int srodkowyIndex = Plansza.naIndexZPunktu(srodek);
        this.plansza.ustawStanPola(naIndex, plansza.pobierzStanPolaZIndexu(zIndexu));
        this.plansza.ustawStanPola(srodkowyIndex, Plansza.PUSTY);
        this.plansza.ustawStanPola(zIndexu, Plansza.PUSTY);

        // Zamiana pionka w króla
        Point koniec = Plansza.naPunkt(naIndex);
        int id = plansza.pobierzStanPolaZIndexu(naIndex);
        boolean zmianaTury = false;
        if (koniec.y == 0 && id == Plansza.BIALY_PIONEK) {
            this.plansza.ustawStanPola(naIndex, Plansza.BIALY_KROL);
            zmianaTury = true;
        } else if (koniec.y == 7 && id == Plansza.CZARNY_PIONEK) {
            this.plansza.ustawStanPola(naIndex, Plansza.CZARNY_KROL);
            zmianaTury = true;
        }

        // Sprawdza czy tura powinna się zmienić (brak ruchów)
        boolean czySrodekPoprawny = Plansza.czyPrawidlowyIndex(srodkowyIndex);
        if (czySrodekPoprawny) {
            this.indeksBitegoPionka = naIndex;
        }
        if (!czySrodekPoprawny || GeneratorRuchow.pobierzSkoki(plansza.skopiujPlansze(), naIndex).isEmpty()) {
            zmianaTury = true;
        }
        if (zmianaTury) {
            this.czyTuraGracza1 = !czyTuraGracza1;
            this.indeksBitegoPionka = -1;
        }

        return true;
    }

    // Pobiera kopie aktualnej planszy
    public Plansza pobierzPlansze() {
        return plansza.skopiujPlansze();
    }

    // Sprawdza czy koniec gry (jeśli któryś z graczy nie ma już żadnych ruchów)
    public boolean czyKoniecGry() {
        // Sprawdza czy na planszy jest przynajmniej po 1 pionku
        List<Point> czarne = plansza.znajdzPola(Plansza.CZARNY_PIONEK);
        czarne.addAll(plansza.znajdzPola(Plansza.CZARNY_KROL));
        if (czarne.isEmpty()) {
                return true;
        }
        
        List<Point> biale = plansza.znajdzPola(Plansza.BIALY_PIONEK);
        biale.addAll(plansza.znajdzPola(Plansza.BIALY_KROL));
        if (biale.isEmpty()) {
                return true;
        }

        // Sprawdza czy aktualny gracz jest się w stanie ruszyć
        List<Point> test = czyTuraGracza1? czarne : biale;
        for (Point p : test) {
            int i = Plansza.naIndexZPunktu(p);
            if (!GeneratorRuchow.pobierzRuchy(plansza, i).isEmpty() || !GeneratorRuchow.pobierzSkoki(plansza, i).isEmpty()) {
                return false;
            }
        }
        // Brak ruchów
        return true;
    }

    public boolean czyTuraGracza1() {
        return czyTuraGracza1;
    }

    public int getIndeksBitegoPionka() {
        return indeksBitegoPionka;
    }

    // Pobiera obecny stan gry jako String 
    public String getStanGry() {
        // Dodaje plansze
        String stan = "";
        for (int i = 0; i < 32; i ++) {
            stan += "" + plansza.pobierzStanPolaZIndexu(i);
        }
        
        // Dodaje informacje o turze gracza
        stan += (czyTuraGracza1 ? "1" : "0");

        return stan;
    }

    // Parsuje stan gry pobrany z getStanGry
    public void setStanGry(String stan) {
        restart();

        if (stan == null || stan.isEmpty()) {
                return;
        }

        int n = stan.length();
        for (int i = 0; i < 32 && i < n; i ++) {
            try {
                int id = Integer.parseInt("" + stan.charAt(i));
                this.plansza.ustawStanPola(i, id);
            } catch (NumberFormatException e) {}
        }

        if (n > 32) {
            this.czyTuraGracza1 = (stan.charAt(32) == '1');
        }
        if (n > 33) {
            try {
                this.indeksBitegoPionka = Integer.parseInt(stan.substring(33));
            } catch (NumberFormatException e) {
                this.indeksBitegoPionka = -1;
            }
        }
    }
}