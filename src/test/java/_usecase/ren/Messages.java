package _usecase.ren;

class Messages {
    static String foundSolutions(int count) {
        return String.format("Found solutions = %d", count);
    }

    static String foundSubSolutions(int count) {
        return String.format("Found sub solutions = %d", count);
    }

     static String maxRen(int count) {
        return String.format("(Max %d Ren)", count);
    }
}
