package Siec;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * Klasa ta służy do przekazywania poleceń między klientami. 
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class Polecenie {
    public static final String POLECENIE_AKTUALIZUJ = "AKTUALIZUJ";
    public static final String POLECENIE_POLACZ = "POLACZ";
    public static final String POLECENIE_ROZLACZ = "ROZLACZ";
    public static final String POLECENIE_POBRANIE_STANU = "POBIERZ-STAN";

    private String polecenie;
    private String[] dane;

    public Polecenie(String polecenie, String... dane) {
        this.polecenie = polecenie;
        this.dane = dane;
    }
	
    // Wysyłanie danych do danego hosta i portu
    public String wyslijDane(String host, int port) {
        String dane = pobierzDane();
        String odpowiedz = "";
        
        try {
            Socket socket = new Socket(host, port);
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.println(dane);
            writer.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                odpowiedz += line + "\n";
            }
            if (!odpowiedz.isEmpty()) {
                odpowiedz = odpowiedz.substring(0, odpowiedz.length() - 1);
            }
            socket.close();

        } catch (UnknownHostException e) { } catch (IOException e) { }
        return odpowiedz;
    }
	
    // Formatowanie danych
    public String pobierzDane() {
        String out = polecenie;

        int n = dane == null ? 0 : dane.length;
        for (int i = 0; i < n; i ++) {
            if (dane[i] == null) {
                break;
            }
            out += "\n" + dane[i];
        }
        return out;
    }
}