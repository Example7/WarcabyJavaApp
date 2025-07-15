package Interfejs;

import Siec.Sesja;
import Siec.Polecenie;
import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import LogikaGry.GeneratorRuchow;
import ModeleGry.Plansza;
import ModeleGry.Gra;
import ModeleGry.GraczOffline;
import ModeleGry.GraczOnline;
import ModeleGry.Gracz;
import Siec.Polecenie;
import Siec.Sesja;
/**
 * Klasa ta reprezentuje graficzny interfejs planszy gry. 
 * <p>Odpowiada za rysowanie planszy i pionków oraz wykonywanie ruchów.</p>
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class PlanszaGry extends JButton {
    private Gra gra;
    private OkienkoGry okienko;
    private Gracz gracz1, gracz2;
    private Color kratka1, kratka2;
    private Point zaznaczonyPunkt;
    private boolean poprawnyWybor;
    private boolean czyKoniecGry;

    public PlanszaGry(OkienkoGry okienko) {
        this(okienko, new Gra(), null, null);
    }
	
    public PlanszaGry(OkienkoGry okienko, Gra gra, Gracz gracz1, Gracz gracz2) {
        // Konfiguracja planszy
        super.setContentAreaFilled(false); // Bez tego widoczny jest obszar JButton wokół planszy
        super.setBorderPainted(false); // Bez tego widoczny jest border wogól planszy
        this.addActionListener(new ClickListener());

        // Konfiguracja gry
        this.gra = (gra == null) ? new Gra() : gra;
        this.kratka1 = Color.WHITE;
        this.kratka2 = Color.BLACK;
        this.okienko = okienko;
        setGracz1(gracz1);
        setGracz2(gracz2);
    }
    
    public Plansza getPlansza() {
        return gra.pobierzPlansze();
    }
    
    // Sprawdza czy gra jest skończona i przerysowywuje grafike
    public void aktualizujGre() {
        this.czyKoniecGry = gra.czyKoniecGry();
        repaint();
    }
	
    // Metoda aktualizująca stan gry w sieci poprzez wysłanie danych o stanie gry do sesji.
    public void aktualizujSiec() {
        List<Sesja> sesjeGier = new ArrayList<>();
        if (gracz1 instanceof GraczOnline) {
            sesjeGier.add(okienko.getSesje1());
        }
        if (gracz2 instanceof GraczOnline) {
            sesjeGier.add(okienko.getSesje2());
        }

        for (Sesja sesjaGry : sesjeGier) {
            wyslijStanGry(sesjaGry);
        }
    }

    // Metoda ustawiająca nowy stan gry
    public synchronized boolean ustawStanGry(boolean sprawdzWartosc, String nowyStan, String oczekiwany) {
        if (sprawdzWartosc && !gra.getStanGry().equals(oczekiwany)) {
            return false;
        }

        this.gra.setStanGry(nowyStan);
        repaint();

        return true;
    }
	
    // Metoda wysyłająca stan gry do określonej sesji w sieci.
    public void wyslijStanGry(Sesja sesjaGry) {
        if (sesjaGry == null) {
            return;
        }

        Polecenie aktualizuj = new Polecenie(Polecenie.POLECENIE_AKTUALIZUJ, sesjaGry.getSid(), gra.getStanGry());
        String host = sesjaGry.getHostDocelowy();
        int port = sesjaGry.getPortDocelowy();
        aktualizuj.wyslijDane(host, port);
    }
	
    // Metoda rysująca wygląd planszy gry oraz pionków na JButton.
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Dla lepszej wizualizaji pionków

        // Wstępny kalkulacje do lepszego ustawienia wielkości planszy 
        int paddingDlaPola = 6;
        int wysokosc = getWidth();
        int szerokosc = getHeight();
        int wymiar = wysokosc < szerokosc ? wysokosc : szerokosc; 
        int wielkocPola = (wymiar - 2 * 16) / 8;
        int wielkoscPionka = wielkocPola - 2 * paddingDlaPola;
        int przesuniecieX = (wysokosc - wielkocPola * 8) / 2;
        int przesuniecieY = (szerokosc - wielkocPola * 8) / 2;

        // Odpowiada za rysowanie plaszy
        g.setColor(Color.BLACK);
        g.drawRect(przesuniecieX - 1, przesuniecieY - 1, wielkocPola * 8 + 1, wielkocPola * 8 + 1);
        g.setColor(kratka1);
        g.fillRect(przesuniecieX, przesuniecieY, wielkocPola * 8, wielkocPola * 8);
        g.setColor(kratka2);
        for (int y = 0; y < 8; y ++) {
            for (int x = (y + 1) % 2; x < 8; x += 2) {
                g.fillRect(przesuniecieX + x * wielkocPola, przesuniecieY + y * wielkocPola, wielkocPola, wielkocPola);
            }
        }
        
        // Podświetlanie kratki jeśli jest valid
        if (Plansza.czyPrawidlowyPunkt(zaznaczonyPunkt)) {
            g.setColor(poprawnyWybor ? Color.GREEN : Color.RED);
            g.fillRect(przesuniecieX + zaznaczonyPunkt.x * wielkocPola, przesuniecieY + zaznaczonyPunkt.y * wielkocPola, wielkocPola, wielkocPola);
        }
        
        // Odpowiada za rysowanie pionków
        Plansza plansza = gra.pobierzPlansze();
        for (int y = 0; y < 8; y ++) {
            int pionekY = przesuniecieY + y * wielkocPola + paddingDlaPola;
            for (int x = (y + 1) % 2; x < 8; x += 2) {
                int id = plansza.pobierzStanPola(x, y);

                if (id == Plansza.PUSTY) {
                    continue;
                }

                int pionekX = przesuniecieX + x * wielkocPola + paddingDlaPola;
                
                switch (id) {
                    case Plansza.CZARNY_PIONEK:
                        g.setColor(Color.DARK_GRAY);
                        g.fillOval(pionekX + 1, pionekY + 2, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.LIGHT_GRAY);
                        g.drawOval(pionekX + 1, pionekY + 2, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.BLACK);
                        g.fillOval(pionekX, pionekY, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.LIGHT_GRAY);
                        g.drawOval(pionekX, pionekY, wielkoscPionka, wielkoscPionka);
                        break;
                    case Plansza.CZARNY_KROL:
                        g.setColor(Color.DARK_GRAY);
                        g.fillOval(pionekX + 1, pionekY + 2, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.LIGHT_GRAY);
                        g.drawOval(pionekX + 1, pionekY + 2, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.DARK_GRAY);
                        g.fillOval(pionekX, pionekY, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.LIGHT_GRAY);
                        g.drawOval(pionekX, pionekY, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.BLACK);
                        g.fillOval(pionekX - 1, pionekY - 2, wielkoscPionka, wielkoscPionka);
                        break;
                    case Plansza.BIALY_PIONEK:
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillOval(pionekX + 1, pionekY + 2, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.DARK_GRAY);
                        g.drawOval(pionekX + 1, pionekY + 2, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.WHITE);
                        g.fillOval(pionekX, pionekY, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.DARK_GRAY);
                        g.drawOval(pionekX, pionekY, wielkoscPionka, wielkoscPionka);
                        break;
                    case Plansza.BIALY_KROL:
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillOval(pionekX + 1, pionekY + 2, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.DARK_GRAY);
                        g.drawOval(pionekX + 1, pionekY + 2, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillOval(pionekX, pionekY, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.DARK_GRAY);
                        g.drawOval(pionekX, pionekY, wielkoscPionka, wielkoscPionka);
                        g.setColor(Color.WHITE);
                        g.fillOval(pionekX - 1, pionekY - 2, wielkoscPionka, wielkoscPionka);
                        break;
                    default:
                        break;
                }
                // Dla króla lekkie wizualne zmiany od zwykłego pionka
                if (Plansza.czyKrol(id)) {
                    g.setColor(new Color(255, 200, 0));
                    g.drawOval(pionekX - 1, pionekY - 2, wielkoscPionka, wielkoscPionka);
                    g.drawOval(pionekX + 1, pionekY, wielkoscPionka - 4, wielkoscPionka - 4);
                }
            }
        }

        // Wyświetlenie którego gracza tura
        String kogoTura = gra.czyTuraGracza1() ? "Tura gracza nr.1 " : "Tura gracza nr. 2";
        Font font = new Font("Arial", Font.BOLD, 15);
        g.setFont(font);
        int width = g.getFontMetrics().stringWidth(kogoTura);
        Color back = gra.czyTuraGracza1() ? Color.BLACK : Color.WHITE;
        Color front = gra.czyTuraGracza1() ? Color.WHITE : Color.BLACK;
        g.setColor(back);
        g.fillRect(wysokosc / 2 - width / 2 - 5, przesuniecieY + 8 * wielkocPola + 2, width + 10, 15);
        g.setColor(front);
        g.drawString(kogoTura, wysokosc / 2 - width / 2, przesuniecieY + 8 * wielkocPola + 2 + 12);

        // Wyświetlenie końca gry
        if (czyKoniecGry) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String koniecGry = "Koniec gry!";
            width = g.getFontMetrics().stringWidth(koniecGry);
            g.setColor(new Color(240, 240, 255));
            g.fillRoundRect(wysokosc / 2 - width / 2 - 5, przesuniecieY + wielkocPola * 4 - 16, width + 10, 30, 10, 10);
            g.setColor(Color.RED);
            g.drawString(koniecGry, wysokosc / 2 - width / 2, przesuniecieY + wielkocPola * 4 + 7);
        }
    }
	
    public Gra getGra() {
        return gra;
    }

    public Gracz getGracz1() {
        return gracz1;
    }

    public void setGracz1(Gracz player1) {
        this.gracz1 = (player1 == null) ? new GraczOffline() : player1;
        if (gra.czyTuraGracza1() && !this.gracz1.czyOffline()) {
            this.zaznaczonyPunkt = null;
        }
    }

    public Gracz getGracz2() {
        return gracz2;
    }

    public void setGracz2(Gracz player2) {
        this.gracz2 = (player2 == null)? new GraczOffline() : player2;
        if (!gra.czyTuraGracza1() && !this.gracz2.czyOffline()) {
            this.zaznaczonyPunkt = null;
        }
    }

    public Gracz getObecnyGracz() {
        return gra.czyTuraGracza1() ? gracz1 : gracz2;
    }

    // Metoda obsługująca kliknięcie na planszy.
    private void obsluzKlikniecie(int x, int y) {
        if (czyKoniecGry || !getObecnyGracz().czyOffline()) {
            return;
        }

        Gra kopiaGry = gra.skopujGre();

        // Sprawdza która kratka została zaznaczona
        int szerokosc = getWidth();
        int wysokosc = getHeight();
        int rozmiar = szerokosc < wysokosc ? szerokosc : wysokosc;
        int wielkoscPola = (rozmiar - 2 * 16) / 8;
        int przesuniecieX = (szerokosc - wielkoscPola * 8) / 2;
        int przesuniecieY = (wysokosc - wielkoscPola * 8) / 2;
        x = (x - przesuniecieX) / wielkoscPola;
        y = (y - przesuniecieY) / wielkoscPola;
        Point zazPunkt = new Point(x, y);

        // Sprawdza czy ruch powienien zostać wykonany
        if (Plansza.czyPrawidlowyPunkt(zazPunkt) && Plansza.czyPrawidlowyPunkt(zaznaczonyPunkt)) {
            boolean zmiana = kopiaGry.czyTuraGracza1();
            String oczekiwanyStan = kopiaGry.getStanGry();
            boolean ruch = kopiaGry.wykonajRuch(zaznaczonyPunkt, zazPunkt);
            boolean zaktualizowanyRuch = (ruch ? ustawStanGry(true, kopiaGry.getStanGry(), oczekiwanyStan) : false);
            if (zaktualizowanyRuch) {
                aktualizujSiec();
            }
            zmiana = (kopiaGry.czyTuraGracza1() != zmiana);
            this.zaznaczonyPunkt = zmiana ? null : zazPunkt;
        } else {
            this.zaznaczonyPunkt = zazPunkt;
        }
        
        // Sprawdzenie czy zaznaczenie jest prawidłowe
        this.poprawnyWybor = czyPoprawnyWybor( kopiaGry.pobierzPlansze(), kopiaGry.czyTuraGracza1(), zaznaczonyPunkt);
        aktualizujGre();
    }

    // Metoda sprawdzająca, czy zaznaczenie na planszy jest poprawne.
    private boolean czyPoprawnyWybor(Plansza plansza, boolean czyTuraGracza1, Point zaznaczonyPunkt) {
        int indeks = Plansza.naIndexZPunktu(zaznaczonyPunkt);
        int id = plansza.pobierzStanPolaZIndexu(indeks);
        if (id == Plansza.PUSTY || id == Plansza.NIEPRAWIDLOWY) { // Brak pionka?
            return false;
        } else if(czyTuraGracza1 != Plansza.czyCzarnyPionek(id)) { // Zły pionek
            return false;
        } else if (!GeneratorRuchow.pobierzSkoki(plansza, indeks).isEmpty()) { // możliwy skok
            return true;
        } else if (GeneratorRuchow.pobierzRuchy(plansza, indeks).isEmpty()) { //brak możliwych ruchów
            return false;
        }

        // Sprawdza czy jest możliwość skoku dla innego pionka
        List<Point> punktyPionkow = plansza.znajdzPola(czyTuraGracza1 ? Plansza.CZARNY_PIONEK : Plansza.BIALY_PIONEK);
        punktyPionkow.addAll(plansza.znajdzPola(czyTuraGracza1 ? Plansza.CZARNY_KROL : Plansza.BIALY_KROL));
        for (Point punktPionka : punktyPionkow) {
            int checker = Plansza.naIndexZPunktu(punktPionka);
            if (checker == indeks) {
                continue;
            }
            if (!GeneratorRuchow.pobierzSkoki(plansza, checker).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    private class ClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Point punktMyszki = PlanszaGry.this.getMousePosition();
            if (punktMyszki != null) {
                obsluzKlikniecie(punktMyszki.x, punktMyszki.y);
            }
        }
    }
}