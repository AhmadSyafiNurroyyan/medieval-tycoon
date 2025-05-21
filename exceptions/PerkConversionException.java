package exceptions;

public class PerkConversionException extends GameException {

    public PerkConversionException(String message) {
        super(message);
    }

    public PerkConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
