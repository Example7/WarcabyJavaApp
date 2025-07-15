package Siec;

import java.io.*;
import java.net.Socket;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ModeleGry.GraczOnline;
import Interfejs.PlanszaGry;
import Interfejs.OkienkoGry;
import Interfejs.OkienkoPolaczenia;
import Interfejs.OpcjeGry;
/**
 * Klasa ta obsługuje połączenia na określonym porcie między dwoma klientami. 
 * <p>Odbiera ona połączenia i wysyła odpowiedzi.</p>
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class ObslugaSieci implements ActionListener {
    public static final String ODPOWIEDZ_PRZYJETA = "PRZYJETA";
    public static final String ODPOWIEDZ_ODRZUCONA = "ODRZUCONA";

    private OkienkoGry okienkoGry;
    private PlanszaGry plansza;
    private OpcjeGry opcje;
    private boolean czyGracz1;

    // Konstruktor przyjmujący informacje o graczach i interfejsie
    public ObslugaSieci(boolean czyGracz1, OkienkoGry okienkoGry, PlanszaGry plansza, OpcjeGry opcje) {
        this.czyGracz1 = czyGracz1;
        this.okienkoGry = okienkoGry;
        this.plansza = plansza;
        this.opcje = opcje;
    }

    // Obsługuje połączenie od NasłuchiwaczaPołączeń
    @Override
    public void actionPerformed(ActionEvent e) {
        // Pobiera dane od połączenia
        ObslugaPolaczenia obslugaPolaczenia = (ObslugaPolaczenia) e.getSource();
        String dane = NasluchiwaczPolaczen.czytajDane(obslugaPolaczenia.getSocket());
        dane = dane.replace("\r\n", "\n");

        Sesja s1 = okienkoGry.getSesje1();
        Sesja s2 = okienkoGry.getSesje2();

        // Sprawdza czy prawidłowy gracz
        String[] linie = dane.split("\n");
        String polecenie = linie[0].split(" ")[0].toUpperCase();
        String sid = linie.length > 1 ? linie[1] : "";
        String odpowiedz = "";
        boolean czyPasuje = false;
        if (czyGracz1) {
            czyPasuje = sid.equals(s1.getSid());
        } else {
            czyPasuje = sid.equals(s2.getSid());
        }
        // Połączony gracz chce zaktualizowac plansze
        if (polecenie.equals(Polecenie.POLECENIE_AKTUALIZUJ)) {
            String nowyStan = (czyPasuje && linie.length > 2 ? linie[2] : "");
            odpowiedz = aktualizujStanGry(nowyStan);
        }
        // Gracz chce się połączyć z tym hostem
        else if (polecenie.equals(Polecenie.POLECENIE_POLACZ)) {
            // Pobiera port
            int port = -1;
            try {
                port = Integer.parseInt(sid);
            } catch (NumberFormatException err) {}

            // Sprawdza czy gracz próbujący się połączyć jest graczem1
            String ktoryGracz = (linie.length > 2 ? linie[2] : "");
            boolean czyGraczZdalny = ktoryGracz.startsWith("1");

            odpowiedz = obsluzPolaczenie(obslugaPolaczenia.getSocket(), port, czyGraczZdalny);
        }
        // Połączony gracz chce pobrać obecny stan gry
        else if (polecenie.equals(Polecenie.POLECENIE_POBRANIE_STANU)) {
            // Wysyła stanGry jeśli sID się zgadza
            if (czyPasuje) {
                odpowiedz = ODPOWIEDZ_PRZYJETA + "\n" + plansza.getGra().getStanGry();
            } else {
                odpowiedz = ODPOWIEDZ_ODRZUCONA;
            }
        }
        // Połączony gracz chce się rozłączyć
        else if (polecenie.equals(Polecenie.POLECENIE_ROZLACZ)) {
            if (czyPasuje) {
                odpowiedz = ODPOWIEDZ_PRZYJETA + "\nKlient został odłączony.";
                if (czyGracz1) {
                    s1.setSid(null);
                    this.opcje.getOkienkoPolaczenia1().mozliwoscAktualizacjiUstawieniaPolaczenia(true);
                } else {
                    s2.setSid(null);
                    this.opcje.getOkienkoPolaczenia2().mozliwoscAktualizacjiUstawieniaPolaczenia(true);
                }
            }
        }
        // Wysłanie odpowiedzi do połączonego gracza
        wyslijOdpowiedz(obslugaPolaczenia, odpowiedz);
    }
	
    // Obsługa aktualizacji stanu gry
    private String aktualizujStanGry(String nowyStan) {
        if (nowyStan.isEmpty()) {
            return ODPOWIEDZ_ODRZUCONA;
        }

        // Aktualizacja stanu gry dla obecnego gracza
        this.plansza.ustawStanGry(false, nowyStan, null);
        if (!plansza.getObecnyGracz().czyOffline()) {
            plansza.aktualizujGre();
        }

        // Sprawdza czy obydwoje graczy są online, jeśli tak to przekazuje stan gry
        if (czyGracz1 && plansza.getGracz2() instanceof GraczOnline) {
            plansza.wyslijStanGry(okienkoGry.getSesje2());
        } else if (!czyGracz1 && plansza.getGracz1() instanceof GraczOnline) {
            plansza.wyslijStanGry(okienkoGry.getSesje1());
        }

        return ODPOWIEDZ_PRZYJETA;
    }

    // Obsługa próby połączenia
    private String obsluzPolaczenie(Socket s, int port, boolean czyGraczZdalny) {
        // Sprawdza czy jest już ktoś połączony
        Sesja s1 = okienkoGry.getSesje1();
        Sesja s2 = okienkoGry.getSesje2();
        String sid1 = s1.getSid();
        String sid2 = s2.getSid();
        if ((czyGracz1 && sid1 != null && !sid1.isEmpty()) || (!czyGracz1 && sid2 != null && !sid2.isEmpty())) {
            return ODPOWIEDZ_ODRZUCONA + "\nBłąd: użytkownik jest już podłączony.";
        }
        
        // Sprawdza czy jest możliwe połączenie
        if (czyGracz1 == czyGraczZdalny) {
            return ODPOWIEDZ_ODRZUCONA + "\nBłąd: drugi klient jest już graczem " + (czyGraczZdalny ? "1." : "2.");
        }
        String host = s.getInetAddress().getHostAddress();
        if (host.equals("127.0.0.1")) {
            if ((czyGracz1 && port == s2.getPort()) || (!czyGracz1 && port == s1.getPort())) {
                return ODPOWIEDZ_ODRZUCONA + "\nBłąd: klient nie może połączyć się ze sobą.";
            }
        }
        
        // Aktualizuje połączenie
        String sid = generateSessionID();
        Sesja sesja = czyGracz1 ? s1 : s2;
        OkienkoPolaczenia okienkoPolaczenia = (czyGracz1 ? opcje.getOkienkoPolaczenia1() : opcje.getOkienkoPolaczenia2());
        sesja.setSid(sid);
        sesja.setHostDocelowy(host);
        sesja.setPortDocelowy(port);

        // Aktualizuje interfejs
        okienkoPolaczenia.setDocelowyHost(host);
        okienkoPolaczenia.setDocelowyPort(port);
        okienkoPolaczenia.mozliwoscAktualizacjiUstawieniaPolaczenia(false);
        okienkoPolaczenia.setWiadomosc("Połączony na: " + host + " z portem " + port + ".");

        return ODPOWIEDZ_PRZYJETA + "\n" + sid + "\nUdało się połączyć.";
    }

    // Wysyłanie odpowiedzi do klienta
    private static void wyslijOdpowiedz(ObslugaPolaczenia obslugaPolaczenia, String odpowiedz) {
        if (obslugaPolaczenia == null) {
            return;
        }
        Socket s = obslugaPolaczenia.getSocket();
        if (s == null || s.isClosed()) {
            return;
        }
        if (odpowiedz == null) {
            odpowiedz = "";
        }

        try (OutputStream os = s.getOutputStream()) {
            os.write(odpowiedz.getBytes());
            os.flush();
        } catch (IOException e) { } finally {
            try {
                s.close();
            } catch (IOException e) { }
        }
    }

    // Generuje losowe ID dla sesji 
    private static String generateSessionID() {
        int randomNumber = (int) (Math.random() * Integer.MAX_VALUE);
        return String.valueOf(randomNumber);
    }
}