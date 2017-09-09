package exceptions;

public abstract class FinderException extends Exception {
    FinderException(String message, Throwable cause) {
        super(message, cause);
    }

    FinderException(String message) {
        super(message);
    }
}
