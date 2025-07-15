package Siec;

import java.net.Socket;
import java.awt.event.ActionEvent;
/**
 * Klasa ta zajmuje się połączeniem z instancją klasy NasłuchiwaczPołączeń. Po utworzeniu będzie działać w nowym wątku.
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class ObslugaPolaczenia extends Thread {

    private NasluchiwaczPolaczen np;
    private Socket socket;

    public ObslugaPolaczenia(NasluchiwaczPolaczen np, Socket socket) {
        this.np = np;
        this.socket = socket;
    }
	
    @Override
    public void run() {
        if (np == null) {
            return;
        }

        ActionEvent e = new ActionEvent(this, 0, "POLACZENIE PRZYJETE");
        if (np.getObslugaPolaczenia() != null) {
            this.np.getObslugaPolaczenia().actionPerformed(e);
        }
    }

    public Socket getSocket() {
        return socket;
    }
}