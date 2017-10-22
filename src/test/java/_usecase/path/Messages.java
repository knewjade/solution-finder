package _usecase.path;

class Messages {
    static String uniqueCount(int count) {
        return String.format("Found path [unique] = %d", count);
    }

    static String minimalCount(int count) {
        return String.format("Found path [minimal] = %d", count);
    }

    static String minimalCount() {
        return "Found path [minimal]";
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

    static String foundPath(int count) {
        return String.format("Found path = %d", count);
    }

    static String foundPieceCombinations(int count) {
        return String.format("Found piece combinations = %d", count);
    }

    static String success(int successCount, int allCount) {
        double percent = successCount * 100.0 / allCount;
        return String.format("success = %.2f%% (%d/%d)", percent, successCount, allCount);
    }
}
