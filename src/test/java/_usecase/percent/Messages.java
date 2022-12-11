package _usecase.percent;

class Messages {
    static String success(int successCount, int allCount) {
        double percent = successCount * 100.0 / allCount;
        return String.format("success = %.2f%% (%d/%d)", percent, successCount, allCount);
    }

    static String patternSize(int noDuplicateCount) {
        return String.format("Searching pattern size ( no dup. ) = %d", noDuplicateCount);
    }

    static String treeHeadSize(int count) {
        return String.format("Head %d pieces", count);
    }

    static String tree(String sequence, double percent) {
        return String.format("%s -> %.2f %%", sequence, percent);
    }

    static String tree(String sequence) {
        return String.format("%s ->", sequence);
    }

    static String failPatternSize(int max) {
        return String.format("Fail pattern (max. %d)", max);
    }

    static String failNothing() {
        return "nothing";
    }

    static String clearLine(int height) {
        return String.format("Max clear lines: %d", height);
    }

    static String useHold() {
        return "Using hold: use";
    }

    static String noUseHold() {
        return "Using hold: avoid";
    }

    static String failPatternAllSize() {
        return "Fail pattern (all)";
    }

    static String harddrop() {
        return "Drop: harddrop";
    }

    static String softdrop() {
        return "Drop: softdrop";
    }

    static String softdrop180() {
        return "Drop: softdrop180";
    }

    static String singleThread() {
        return "Threads = 1";
    }
}
