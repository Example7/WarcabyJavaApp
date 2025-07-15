package ModeleGry;
/**
 * Ta klasa reprezentuje gracza Online, który jest w stanie połączyć się z innym graczem Online dzięki package Sieć.
 * 
 * @author Kacper Kałużny
 * @version 12.06.2024
 */
public class GraczOnline extends Gracz {
    @Override
    public boolean czyOffline() {
        return false;
    }

    @Override
    public void updateGame(Gra game) {}

}