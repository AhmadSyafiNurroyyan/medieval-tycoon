/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package exceptions;

public class PerkConversionException extends RuntimeException {

    public PerkConversionException(String message) {
        super(message);
    }

    public PerkConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
