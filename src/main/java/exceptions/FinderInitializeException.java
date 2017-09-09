package exceptions;


public class FinderInitializeException extends FinderException {
    public FinderInitializeException(Throwable cause) {
        this("Failed to initialize", cause);
    }

    public FinderInitializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FinderInitializeException(String message) {
        super(message);
    }
}
