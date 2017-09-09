package exceptions;

public class FinderTerminateException extends FinderException {
    public FinderTerminateException(Throwable cause) {
        this("Failed to terminate", cause);
    }

    public FinderTerminateException(String message, Throwable cause) {
        super(message, cause);
    }
}
