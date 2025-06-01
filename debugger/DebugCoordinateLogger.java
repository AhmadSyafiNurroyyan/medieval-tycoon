/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package debugger;

import model.Player.PlayerMovement;

public class DebugCoordinateLogger {
    public static void logPlayerCoordinates(PlayerMovement playerMovement) {
        System.out.println("[DEBUG] Player Coords: (" + playerMovement.getX() + ", " + playerMovement.getY() + ")");
    }
}
