package _usecase.setup;

class Messages {
    static String foundSolutions(int count) {
        return String.format("Found solutions = %d", count);
    }

    static String foundSubSolutions(int count) {
        return String.format("Found sub solutions = %d", count);
    }
}
