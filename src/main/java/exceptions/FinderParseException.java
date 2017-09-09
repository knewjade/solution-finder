package exceptions;

public class FinderParseException extends FinderException {
    public FinderParseException(Throwable cause) {
        this("Failed to parse", cause);
    }

    public FinderParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public FinderParseException(String message) {
        super(message);
    }
}
