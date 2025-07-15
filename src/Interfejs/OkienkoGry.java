package Interfejs;

import javax.swing.*;
import java.awt.BorderLayout;

import ModeleGry.Gracz;
import ModeleGry.Plansza;
import Siec.ObslugaSieci;
import Siec.NasluchiwaczPolaczen;
import Siec.Sesja;
/**
 * Klasa @OkienkoGry wyświetla okno do gry w Warcaby.
 * 
 * <p>Klasa odpowiada za stworzenie głównego okna aplikacji, w którym użytkownik może grać w warcaby. 
 * Zawiera komponenty graficzne oraz obsługę sieciową do gry wieloosobowej.</p>
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class OkienkoGry extends JFrame {
    public static String nazwaOkienka = "Warcaby";
    public static int szerokoscOkienka = 600;
    public static int wysokoscOkienka = 750;

    private PlanszaGry planszaGry;
    private OpcjeGry opcjeGry;
    private Sesja sesja1;
    private Sesja sesja2;

    public OkienkoGry() {
        this(szerokoscOkienka, wysokoscOkienka, nazwaOkienka);
    }
	
    public OkienkoGry(Gracz gracz1, Gracz gracz2) {
        this();
        setGracz1(gracz1);
        setGracz2(gracz2);
    }
	
    public OkienkoGry(int szerokosc, int wysokosc, String nazwa) {
        // Ustawienia okna
        super(nazwa);
        super.setSize(szerokosc, wysokosc);

        // Ustawienia komponentów
        JPanel panelGry = new JPanel(new BorderLayout());
        this.planszaGry = new PlanszaGry(this);
        this.opcjeGry = new OpcjeGry(this);
        panelGry.add(planszaGry, BorderLayout.CENTER);
        panelGry.add(opcjeGry, BorderLayout.SOUTH);
        this.add(panelGry);

        // Ustawienia sieci
        ObslugaSieci session1Handler;
        ObslugaSieci session2Handler;
        session1Handler = new ObslugaSieci(true, this, planszaGry, opcjeGry);
        session2Handler = new ObslugaSieci(false, this, planszaGry, opcjeGry);
        this.sesja1 = new Sesja(new NasluchiwaczPolaczen( 0, session1Handler), null, null, -1);
        this.sesja2 = new Sesja(new NasluchiwaczPolaczen( 0, session2Handler), null, null, -1);
    }
    
    public Plansza getPlansza() {
        return planszaGry.getPlansza();
    }
    
    public PlanszaGry getPlanszaGry() {
        return planszaGry;
    }
    
    public Gracz getGracz1() {
        return planszaGry.getGracz1();
    }

    public void setGracz1(Gracz gracz1) {
        this.planszaGry.setGracz1(gracz1);
        this.planszaGry.aktualizujGre();
    }
    
    public Gracz getGracz2() {
        return planszaGry.getGracz2();
    }
	
    public void setGracz2(Gracz gracz2) {
        this.planszaGry.setGracz2(gracz2);
        this.planszaGry.aktualizujGre();
    }
	
    public void restart() {
        this.planszaGry.getGra().restart();
        this.planszaGry.aktualizujGre();
    }
	
    public void setStanGry(String state) {
        this.planszaGry.getGra().setStanGry(state);
    }

    public Sesja getSesje1() {
        return sesja1;
    }

    public Sesja getSesje2() {
        return sesja2;
    }
}