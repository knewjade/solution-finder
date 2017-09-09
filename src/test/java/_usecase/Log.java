package _usecase;

class Log {
    private final int returnCode;
    private final String output;
    private final String error;

    Log(int returnCode, String out, String error) {
        this.returnCode = returnCode;
        this.output = out;
        this.error = error;
    }

    String getOutput() {
        return output;
    }

    String getError() {
        return error;
    }

    public int getReturnCode() {
        return returnCode;
    }
}
