package ModeleGry;
/**
 * Ta klasa reprezentuje gracza offline, który jest domyślnie ustawiony po włączeniu aplikacji.
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class GraczOffline extends Gracz {
    @Override
    public boolean czyOffline() {
        return true;
    }

    @Override
    public void updateGame(Gra game) {}
}