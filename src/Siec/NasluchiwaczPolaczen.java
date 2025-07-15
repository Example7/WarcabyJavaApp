package Siec;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.DatagramSocket;
import java.awt.event.ActionListener;
/**
 * Ta klasa pełni rolę serwera i nasłuchuje połączeń na określonym porcie.
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class NasluchiwaczPolaczen extends Thread {
    private ServerSocket serverSocket;
    private ActionListener obslugaPolaczenia;

    // Tworzy listener na podanym porcie
    public NasluchiwaczPolaczen(int port) {
        setPort(port);
    }

    // Tworzy listener na podanym porcie z dodatkowym ActionListenerem
    public NasluchiwaczPolaczen(int port, ActionListener obslugaPolaczenia) {
        setPort(port);
        this.obslugaPolaczenia = obslugaPolaczenia;
    }
	
    // Rozpoczyna nasłuchiwanie na porcie w nowym wątku
    public void listen() {
        start();
    }

    @Override
    public void run() {
        if (serverSocket == null) {
            return;
        }
        if (serverSocket.isClosed()) {
            try {
                this.serverSocket = new ServerSocket(serverSocket.getLocalPort());
            } catch (IOException e) { }
        }

        // Nasłucuje przychodzących prób połączenia się z serwerem
        while (!serverSocket.isClosed()) {
            try {
                ObslugaPolaczenia connection = new ObslugaPolaczenia(this, serverSocket.accept());
                connection.start();
            } catch (IOException e) { } 
        }
    }
	
    // Metoda zatrzymująca nasłuchiwanie
    public boolean czyZamknijPolaczenie() {
        if (serverSocket == null || serverSocket.isClosed()) {
            return true;
        }

        // Zamknięcie połączenia
        boolean blad = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            blad = true;
        }
        return !blad;
    }
	
    public int getPort() {
        return serverSocket.getLocalPort();
    }
	
    public void setPort(int port) {
        czyZamknijPolaczenie();

        try {
            if (port < 0) {
                this.serverSocket = new ServerSocket(0);
            } else {
                this.serverSocket = new ServerSocket(port);
            }
        } catch (IOException e) { }
    }

    public ActionListener getObslugaPolaczenia() {
        return obslugaPolaczenia;
    }

    // Metoda czytająca dane z socketu
    public static String czytajDane(Socket socket) {
        if (socket == null) {
            return "";
        }

        String data = "";
        try {
            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = br.readLine()) != null) {
                data += line + "\n";
                if (!br.ready()) {break;}
            }
            if (!data.isEmpty()) {
                data = data.substring(0, data.length() - 1);
            }
        } catch (IOException e) { }

        return data;
    }
	
    // Metoda sprawdzająca dostępność danego portu
    public static boolean czyDostepnyPort(int port) {
        if (port < 0 || port > 65535) {
            return false;
        }

        ServerSocket serverSocket = null;
        DatagramSocket datagramSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            datagramSocket = new DatagramSocket(port);
            datagramSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }

            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {}
            }
        }
        return false;
    }
}