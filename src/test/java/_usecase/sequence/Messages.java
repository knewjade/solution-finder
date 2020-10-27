package _usecase.sequence;

class Messages {
    static String foundSolutions(int count, int all, String data) {
        double p = (count * 100.0) / all;
        return String.format("%.2f %% [%d/%d]: http://fumen.zui.jp/?%s", p, count, all, data);
    }

    static String foundOrSolutions(int count, int all) {
        double p = (count * 100.0) / all;
        return String.format("OR  = %.2f %% [%d/%d]", p, count, all);
    }

    static String foundAndSolutions(int count, int all) {
        double p = (count * 100.0) / all;
        return String.format("AND = %.2f %% [%d/%d]", p, count, all);
    }
}
