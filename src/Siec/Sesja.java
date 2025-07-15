package Siec;
/**
 * Klasa ta zajmuje się sesją pomiędzy dwoma klientami.
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class Sesja {
    private NasluchiwaczPolaczen listener;
    private String sid;
    private String hostDocelowy;
    private int portDocelowy;

    public Sesja(NasluchiwaczPolaczen listener, String sid, String hostDocelowy, int portDocelowy) {
        this.listener = listener;
        this.sid = sid;
        this.hostDocelowy = hostDocelowy;
        this.portDocelowy = portDocelowy;
    }
	
    public Sesja(String sid, int port, String hostDocelowy, int portDocelowy) {
        this.listener = new NasluchiwaczPolaczen(port);
        this.sid = sid;
        this.hostDocelowy = hostDocelowy;
        this.portDocelowy = portDocelowy;
    }

    public NasluchiwaczPolaczen getListener() {
        return listener;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getHostDocelowy() {
        return hostDocelowy;
    }

    public void setHostDocelowy(String hostDocelowy) {
        this.hostDocelowy = hostDocelowy;
    }

    public int getPortDocelowy() {
        return portDocelowy;
    }

    public void setPortDocelowy(int portDocelowy) {
        this.portDocelowy = portDocelowy;
    }

    public int getPort() {
        return (listener == null ? -1 : listener.getPort());
    }
}