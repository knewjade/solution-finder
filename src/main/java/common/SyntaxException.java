package common;

public class SyntaxException extends Exception {
    public static String formatMessageOnLine(String message, int lineIndex) {
        return String.format("on %d line :: %s", lineIndex, message);
    }

    public SyntaxException(String message, int index) {
        super(String.format("%s [position=%dchar]", message, index));
    }

    public SyntaxException(SyntaxException e, int lineIndex) {
        super(formatMessageOnLine(e.getMessage(), lineIndex), e);
    }

    public SyntaxException(String message) {
        super(message);
    }
}
