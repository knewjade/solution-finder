package _usecase.cover;

class Messages {
    static String foundSolutions(int count, int all, String data) {
        double p = (count * 100.0) / all;
        return String.format("%.2f %% [%d/%d]: http://fumen.zui.jp/?%s", p, count, all, data);
    }

    static String foundSolutions(int count, int all, int or, int and, String data) {
        double p = (count * 100.0) / all;
        return String.format("%.2f %% [%d/%d] (accum. OR=%d, AND=%d): http://fumen.zui.jp/?%s", p, count, all, or, and, data);
    }

    static String foundMirrorSolutions(int count, int all, String data) {
        double p = (count * 100.0) / all;
        return String.format("%.2f %% [%d/%d]: http://fumen.zui.jp/?%s (mirror)", p, count, all, data);
    }

    static String foundOrSolutions(int count, int all) {
        double p = (count * 100.0) / all;
        return String.format("OR  = %.2f %% [%d/%d]", p, count, all);
    }

    static String foundAndSolutions(int count, int all) {
        double p = (count * 100.0) / all;
        return String.format("AND = %.2f %% [%d/%d]", p, count, all);
    }

    static String maxSoftdropTiems(int times) {
        return String.format("Max Softdrop Times: %d", times);
    }

    static String maxClearLineTimes(int times) {
        return String.format("Max Clear Line Times: %d", times);
    }
}
