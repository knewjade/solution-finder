package _usecase;

public class Log {
    private final int returnCode;
    private final String output;
    private final String error;

    Log(int returnCode, String out, String error) {
        this.returnCode = returnCode;
        this.output = out;
        this.error = error;
    }

    public String getOutput() {
        return output;
    }

    public String getError() {
        return error;
    }

    public int getReturnCode() {
        return returnCode;
    }
}
