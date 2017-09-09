package exceptions;

public class FinderExecuteException extends FinderException {
    public FinderExecuteException(Throwable cause) {
        this("Failed to execute", cause);
    }

    public FinderExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public FinderExecuteException(String message) {
        super(message);
    }
}
