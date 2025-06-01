/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package debugger;

import java.awt.event.MouseEvent;

public class DebugClickLogger {
    public static void logClickCoordinates(MouseEvent e, int mapX, int mapY) {
        System.out.println("[DEBUG] Clicked map at: (" + mapX + ", " + mapY + ")");
    }
}
