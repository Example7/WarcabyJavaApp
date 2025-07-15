package ModeleGry;
/**
 * Jest to abstrakcyjna klasa reprezentująca gracza w Warcabach.
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public abstract class Gracz {
    public abstract boolean czyOffline();
	
    public abstract void updateGame(Gra game);
}