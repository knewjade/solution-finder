package _usecase;

class Log {
    private final String output;
    private final String error;

    Log(String out, String error) {
        this.output = out;
        this.error = error;
    }

    String getOutput() {
        return output;
    }

    String getError() {
        return error;
    }
}
