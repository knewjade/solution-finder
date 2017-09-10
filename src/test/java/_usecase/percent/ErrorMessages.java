package _usecase.percent;

class ErrorMessages {
    static String failPreMain() {
        return "Error: Failed to execute pre-main. Output stack trace to output/error.txt";
    }

    static String failMain() {
        return "Error: Failed to execute main. Output stack trace to output/error.txt";
    }
}
