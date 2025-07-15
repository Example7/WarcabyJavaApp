package Interfejs;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 * Ta klasa zajmuje się oknem zawierającym ustawienia połączenia dla graczy Online.
 * 
 * <p>Klasa pozwala na konfigurację połączeń sieciowych, w tym ustawienie portu
 * oraz adresu hosta docelowego.</p>
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class OkienkoPolaczenia extends JFrame {
    public static int polaczHostPrzycisk = 0;
    public static int ustawPortPrzycisk = 1;
    public static String nazwaOkienka = "Skonfiguruj Połączenie";
    public int szerokoscOkienka = 480;
    public int wysokoscOkienka = 150;

    private JTextField portText;
    private JTextField docelowyHost;
    private JTextField docelowyPort;
    private JButton ustawPrzycisk;
    private JButton polaczPrzycisk;
    private JPanel portPanel;
    private JPanel hostPanel;
    private JLabel wiadomosc;

    private ActionListener actionListener;

    public OkienkoPolaczenia() {
        super(nazwaOkienka);
        super.setSize(szerokoscOkienka, wysokoscOkienka);
        super.setResizable(false);
        init();
    }
	
    // Tworzy OkienkoPolaczenia z pustymi polami i ActionListener
    public OkienkoPolaczenia(ActionListener actionListener) {
        this();
        this.actionListener = actionListener;
    }
	
    // Tworzy OkienkoPolaczenia z wypełnionymi polami i ActionListener
    public OkienkoPolaczenia(ActionListener actionListener, int port, String docelowyHost, int docelowyPort) {
        this();
        this.actionListener = actionListener;
        setPort(port);
        setDocelowyHost(docelowyHost);
        setDocelowyPort(docelowyPort);
    }
	
    // Inicjalizuje komponenty do wyświetlenia w oknienku
    private void init() {
        this.getContentPane().setLayout(new GridLayout(3, 1));
        this.portText = new JTextField(4);
        this.docelowyHost = new JTextField(14);
        this.docelowyHost.setText("127.0.0.1");
        this.docelowyPort = new JTextField(4);
        this.ustawPrzycisk = new JButton("Ustaw");
        this.ustawPrzycisk.addActionListener(new ButtonListener());
        this.polaczPrzycisk = new JButton("Połącz");
        this.polaczPrzycisk.addActionListener(new ButtonListener());
        this.portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.hostPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.wiadomosc = new JLabel();
        this.portPanel.add(new JLabel("PORT:"));
        this.portPanel.add(portText);
        this.portPanel.add(ustawPrzycisk);
        this.hostPanel.add(new JLabel("Host docelowy:"));
        this.hostPanel.add(docelowyHost);
        this.hostPanel.add(docelowyPort);
        this.hostPanel.add(polaczPrzycisk);
        mozliwoscAktualizacjiUstawieniaPolaczenia(false);

        this.portText.setToolTipText("Wpisz port z zakresu (1025 - 65535)");
        this.docelowyPort.setToolTipText("Wpisz port z zakresu (1025 - 65535)");

        stworzUklad(null);
    }
	
    // Tworzy lub aktualizuje układ z dodatkową wiadomością
    private void stworzUklad(String wiadomosc) {
        this.getContentPane().removeAll();

        this.getContentPane().add(portPanel);
        this.getContentPane().add(hostPanel);
        this.wiadomosc.setText(wiadomosc);
        this.getContentPane().add(this.wiadomosc);
        this.wiadomosc.setVisible(false);
	this.wiadomosc.setVisible(true);
    }
	
    // Aktualizuje stan komponentów wymaganych do aktualizacji portu, na którym nasłuchuje ten klient.
    public void mozliwoscAktualizacjiUstawieniaPortu(boolean czyMozeAktualizowac) {
        this.portText.setEnabled(czyMozeAktualizowac);
        this.ustawPrzycisk.setEnabled(czyMozeAktualizowac);
    }
    
    // Aktualizuje stan komponentów wymaganych do nawiązania zdalnego połączenia z innym klientem.
    public void mozliwoscAktualizacjiUstawieniaPolaczenia(boolean czyMozeAktualizowac) {
        this.docelowyHost.setEnabled(czyMozeAktualizowac);
        this.docelowyPort.setEnabled(czyMozeAktualizowac);
        this.polaczPrzycisk.setEnabled(czyMozeAktualizowac);
    }

    public int getPort() {
        return parseField(portText);
    }
	
    public void setPort(int port) {
        this.portText.setText("" + port);
    }

    String getDocelowyHost() {
        return docelowyHost.getText();
    }
	
    public void setDocelowyHost(String host) {
        this.docelowyHost.setText(host);
    }
	
    public int getDocelowyPort() {
        return parseField(docelowyPort);
    }
	
    public void setDocelowyPort(int port) {
        this.docelowyPort.setText("" + port);
    }
	
    public void setWiadomosc(String wiadomosc) {
        stworzUklad(wiadomosc);
    }
	
    // Parsuje text na int
    private int parseField(JTextField tekst) {
        if (tekst == null) {
            return 0;
        }

        int wartosc = 0;
        try {
            wartosc = Integer.parseInt(tekst.getText());
        } catch (NumberFormatException e) {}

        return wartosc;
    }
	
    // Nasłuchuje kliknięcia przycisków
    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (actionListener != null) {
                JButton source = (JButton) e.getSource();
                ActionEvent event = null;
                if (source == ustawPrzycisk) {
                    event = new ActionEvent(OkienkoPolaczenia.this, ustawPortPrzycisk, null);
                } else {
                    event = new ActionEvent(OkienkoPolaczenia.this, polaczHostPrzycisk, null);
                }
                actionListener.actionPerformed(event);
            }
        }
    }
}