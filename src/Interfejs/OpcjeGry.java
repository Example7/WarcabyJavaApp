package Interfejs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import ModeleGry.GraczOffline;
import ModeleGry.GraczOnline;
import ModeleGry.Gracz;
import Siec.ObslugaSieci;
import Siec.Polecenie;
import Siec.NasluchiwaczPolaczen;
import Siec.Sesja;
import java.util.Scanner;
/**
 * Ta klasa zajmuje się widokiem pod planszą do interakcji z aktywną grą.
 * <p>
 * Klasa zawiera komponenty do zarządzania opcjami gry, takimi jak restart gry,
 * zapisywanie i wczytywanie stanu gry oraz konfigurację graczy (offline i online).
 * </p>
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class OpcjeGry extends JPanel {
    private OkienkoGry okienkoGry;
    private JComboBox<String> gracz1Opcje, gracz2Opcje;
    private JButton restartPrzycisk, gracz1Przycisk, gracz2Przycisk, zapiszStanGryPrzycisk, wczytajStanGryPrzycisk;
    private OkienkoPolaczenia gracz1OknoPolaczenia, gracz2OknoPolaczenia;
    
    public OpcjeGry(OkienkoGry okienkoGry) {
        super(new GridLayout(0, 1));
        this.okienkoGry = okienkoGry;

        OpcjeListener ol = new OpcjeListener(okienkoGry);
        String[] opcjeGraczy = {"Offline", "Online"};
        this.zapiszStanGryPrzycisk = new JButton("Zapisz gre");
        this.wczytajStanGryPrzycisk = new JButton("Wczytaj gre");
        this.restartPrzycisk = new JButton("Rozpocznij gre od nowa");
        this.gracz1Opcje = new JComboBox<>(opcjeGraczy);
        this.gracz2Opcje = new JComboBox<>(opcjeGraczy);
        this.restartPrzycisk.addActionListener(ol);
        this.zapiszStanGryPrzycisk.addActionListener(ol);
        this.wczytajStanGryPrzycisk.addActionListener(ol);
        this.gracz1Opcje.addActionListener(ol);
        this.gracz2Opcje.addActionListener(ol);
        JPanel goraPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel srodekPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel dolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.gracz1OknoPolaczenia = new OkienkoPolaczenia(ol);
        this.gracz1OknoPolaczenia.setTitle("Gracz 1 - Konfiguracja połączenia");
        this.gracz2OknoPolaczenia = new OkienkoPolaczenia(ol);
        this.gracz2OknoPolaczenia.setTitle("Gracz 2 - Konfiguracja połączenia");
        this.gracz1Przycisk = new JButton("Nawiąż połączenie");
        this.gracz1Przycisk.addActionListener(ol);
        this.gracz1Przycisk.setVisible(false);
        this.gracz2Przycisk = new JButton("Nawiąż połączenien");
        this.gracz2Przycisk.addActionListener(ol);
        this.gracz2Przycisk.setVisible(false);

        zapiszStanGryPrzycisk.setPreferredSize(new Dimension(110, 30));
        wczytajStanGryPrzycisk.setPreferredSize(new Dimension(110, 30));
        restartPrzycisk.setPreferredSize(new Dimension(200, 30));
        gracz1Opcje.setPreferredSize(new Dimension(100, 20));
        gracz2Opcje.setPreferredSize(new Dimension(100, 20));
        gracz1Przycisk.setPreferredSize(new Dimension(150, 20));
        gracz2Przycisk.setPreferredSize(new Dimension(150, 20));
        
        goraPanel.add(restartPrzycisk);
        goraPanel.add(zapiszStanGryPrzycisk);
        goraPanel.add(wczytajStanGryPrzycisk);
        srodekPanel.add(new JLabel("(CZARNE) Gracz nr.1: ")).setFont(new Font("Arial", Font.BOLD, 15));
        srodekPanel.add(gracz1Opcje);
        srodekPanel.add(gracz1Przycisk);
        dolPanel.add(new JLabel("(BIAŁE) Gracz nr.2: ")).setFont(new Font("Arial", Font.BOLD, 15));
        dolPanel.add(gracz2Opcje);
        dolPanel.add(gracz2Przycisk);
        this.add(goraPanel);
        this.add(srodekPanel);
        this.add(dolPanel);
    }
    
    // Metoda zwracająca okno połączenia dla gracza 1
    public OkienkoPolaczenia getOkienkoPolaczenia1() {
        return gracz1OknoPolaczenia;
    }

    // Metoda zwracająca okno połączenia dla gracza 2
    public OkienkoPolaczenia getOkienkoPolaczenia2() {
        return gracz2OknoPolaczenia;
    }
    
    // Metoda obsługująca aktualizację ustawień sieciowych
    private void aktualizacjaSieciowa(OkienkoPolaczenia okienkoPolaczenia, ActionEvent e) {
        int port = okienkoPolaczenia.getPort();
        int portDocelowy = okienkoPolaczenia.getDocelowyPort();
        String hostDocelowy = okienkoPolaczenia.getDocelowyHost();
        boolean czyGracz1 = (okienkoPolaczenia == gracz1OknoPolaczenia);
        Sesja sesja = (czyGracz1 ? okienkoGry.getSesje1() : okienkoGry.getSesje2());
        
        // Ustawienie nowego portu
        if (e.getID() == OkienkoPolaczenia.ustawPortPrzycisk) {
            // Sprawdzenie czy poprawny port
            if (port < 1025 || port > 65535) {
                okienkoPolaczenia.setWiadomosc("Błąd: port musi miescić się w przedziale od 1025 do 65535. ");
                return;
            }
            
            if (!NasluchiwaczPolaczen.czyDostepnyPort(port)) {
                okienkoPolaczenia.setWiadomosc("Błąd: port " + port + " jest niedostępne.");
                return;
            }

            // Aktualizacja sesji
            if (sesja.getListener().getPort() != port) {
                sesja.getListener().czyZamknijPolaczenie();
            }
            
            sesja.getListener().setPort(port);
            sesja.getListener().listen();
            okienkoPolaczenia.setWiadomosc("Ten klient nasłuchuje na porcie " + port);
            okienkoPolaczenia.mozliwoscAktualizacjiUstawieniaPortu(false);
            okienkoPolaczenia.mozliwoscAktualizacjiUstawieniaPolaczenia(true);
        // Próba połączenia
        } else if (e.getID() == OkienkoPolaczenia.polaczHostPrzycisk) {
            // Sprawdzenie portu i hosta
            if (portDocelowy < 1025 || portDocelowy > 65535) {
                okienkoPolaczenia.setWiadomosc("Błąd: port musi miescić się w przedziale od 1025 do 65535. ");
                return;
            }
            if (hostDocelowy == null || hostDocelowy.isEmpty()) {
                hostDocelowy = "127.0.0.1";
            }
            // Połączenie się z hostem
            Polecenie polacz = new Polecenie(Polecenie.POLECENIE_POLACZ, okienkoPolaczenia.getPort() + "", czyGracz1 ? "1" : "0");
            String odpowiedz = polacz.wyslijDane(hostDocelowy, portDocelowy);

            // Brak odpowiedzi
            if (odpowiedz.isEmpty()) {
                okienkoPolaczenia.setWiadomosc("Błąd: nie udało się połączyć " + hostDocelowy + ":" + portDocelowy + ".");
            // Poprawny gracz, natomiast połączenie odrzucone
            } else if (odpowiedz.startsWith(ObslugaSieci.ODPOWIEDZ_ODRZUCONA)) {
                String[] linie = odpowiedz.split("\n");
                String blad = linie.length > 1 ? linie[1] : "";
                if (blad.isEmpty()) {
                    okienkoPolaczenia.setWiadomosc("Błąd: drugi klient odmówił połączenia.");
                } else {
                    okienkoPolaczenia.setWiadomosc(blad);
                }
            // Połączenie zaakceptowane przez klienta
            } else if (odpowiedz.startsWith(ObslugaSieci.ODPOWIEDZ_PRZYJETA)){
                sesja.setHostDocelowy(hostDocelowy);
                sesja.setPortDocelowy(portDocelowy);
                okienkoPolaczenia.setWiadomosc("Pomyślnie rozpoczęto sesję na " + hostDocelowy + " z portem " + portDocelowy + ".");
                okienkoPolaczenia.mozliwoscAktualizacjiUstawieniaPolaczenia(false);

                // Aktualizacja sesji ID
                String[] linie = odpowiedz.split("\n");
                String sid = linie.length > 1? linie[1] : "";
                sesja.setSid(sid);

                // Pobranie nowego stanu gry
                Polecenie pobierzStan = new Polecenie(Polecenie.POLECENIE_POBRANIE_STANU, sid, null);
                odpowiedz = pobierzStan.wyslijDane(hostDocelowy, portDocelowy);
                linie = odpowiedz.split("\n");
                String state = linie.length > 1? linie[1] : "";
                okienkoGry.setStanGry(state);
            }
        }
    }

    // Metoda zwracająca odpowiedniego gracza w zależności od wybranej opcji
    private static Gracz getGracz(JComboBox<String> opcjeGracza) {
        Gracz player = new GraczOffline();
        if (opcjeGracza == null) {
            return player;
        }

        String type = "" + opcjeGracza.getSelectedItem();
        if (type.equals("Online")) {
            player = new GraczOnline();
        }

        return player;
    }
    
    // Metoda ta zapisuje obecny stan gry do pliku
    private void zapiszStanGryDoPliku() {
        try (PrintWriter writer = new PrintWriter(new File("stan_gry.txt"))) {
            String stanGry = okienkoGry.getPlanszaGry().getGra().getStanGry();
            writer.println(stanGry);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    // Metoda ta wczytuje zapisany stan gry
    private void wczytajStanGryZPliku() {
        try (Scanner scanner = new Scanner(new File("stan_gry.txt"))) {
            StringBuilder stanGryBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stanGryBuilder.append(scanner.nextLine());
            }
            String stanGry = stanGryBuilder.toString();
            okienkoGry.setStanGry(stanGry);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    // ActionListener dla poszczególnych elementów
    private class OpcjeListener implements ActionListener {
        public OkienkoGry okienko;

        public OpcjeListener(OkienkoGry okienko) {
            this.okienko = okienko;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (okienkoGry == null) {
                return;
            }
            
            Object source = e.getSource();
            JButton przycisk = null;
            boolean czyOnline = false;
            boolean czyGracz1 = true;
            Sesja sesja = null;
            
            if (source == restartPrzycisk) {
                okienkoGry.restart();
                okienkoGry.getPlanszaGry().aktualizujSiec();
            } else if (source == gracz1Opcje) {
                Gracz player = getGracz(gracz1Opcje);
                okienkoGry.setGracz1(player);
                czyOnline = (player instanceof GraczOnline);
                przycisk = gracz1Przycisk;
                sesja = okienkoGry.getSesje1();
            } else if (source == gracz2Opcje) {
                Gracz player = getGracz(gracz2Opcje);
                okienkoGry.setGracz2(player);
                czyOnline = (player instanceof GraczOnline);
                przycisk = gracz2Przycisk;
                sesja = okienkoGry.getSesje2();
                czyGracz1 = false;
            } else if (source == gracz1Przycisk) {
                gracz1OknoPolaczenia.setVisible(true);
            } else if (source == gracz2Przycisk) {
                gracz2OknoPolaczenia.setVisible(true);
            } else if (source == zapiszStanGryPrzycisk){
                zapiszStanGryDoPliku();
                JOptionPane.showMessageDialog(null, "Stan planszy został zapisany do pliku.");
            } else if (source == wczytajStanGryPrzycisk){
                wczytajStanGryZPliku();
                JOptionPane.showMessageDialog(null, "Stan planszy został wczytany z pliku.");
            } else if (source == gracz1OknoPolaczenia || source == gracz2OknoPolaczenia) {
                aktualizacjaSieciowa((OkienkoPolaczenia) source, e);
            }

            if (przycisk != null) {
                String idSesji = sesja.getSid();
                if (!czyOnline && przycisk.isVisible() && idSesji != null && !idSesji.isEmpty()) {
                    Polecenie rozlacz = new Polecenie(Polecenie.POLECENIE_ROZLACZ, idSesji);
                    rozlacz.wyslijDane(sesja.getHostDocelowy(), sesja.getPortDocelowy());

                    sesja.setSid(null);
                    OkienkoPolaczenia okienkoPolaczenia = czyGracz1 ? gracz1OknoPolaczenia : gracz2OknoPolaczenia;
                    okienkoPolaczenia.mozliwoscAktualizacjiUstawieniaPolaczenia(true);
                }

                przycisk.setVisible(czyOnline);
                przycisk.repaint();
            }
            
            if (gracz1Opcje.getSelectedItem().equals("Online") || gracz2Opcje.getSelectedItem().equals("Online")) {
                wczytajStanGryPrzycisk.setEnabled(false);
            } else {
                wczytajStanGryPrzycisk.setEnabled(true);
            }
        }
    }
}