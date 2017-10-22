package _usecase;

import common.datastore.Operations;
import common.parser.OperationInterpreter;
import helper.CSVStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OutputFileHelper {
    private static final String UNIQUE_PATH = concatPath("output", "path_unique.html");
    private static final String MINIMAL_PATH = concatPath("output", "path_minimal.html");
    private static final String UNIQUE_CSV = concatPath("output", "path_unique.csv");
    private static final String MINIMAL_CSV = concatPath("output", "path_minimal.csv");
    private static final String DEFAULT_CSV = concatPath("output", "path.csv");
    private static final String ERROR_PATH = concatPath("output", "error.txt");

    private static String concatPath(String... names) {
        return FileHelper.concatPath(names);
    }

    public static PathHTML loadPathUniqueHTML() throws IOException {
        return loadPathUniqueHTML(UNIQUE_PATH);
    }

    public static PathHTML loadPathUniqueHTML(String path) throws IOException {
        return loadHTML(path);
    }

    private static PathHTML loadHTML(String path) throws IOException {
        String html = Files.lines(Paths.get(path)).collect(Collectors.joining());
        int pattern = extractPattern(html);

        if (html.contains("ライン消去あり")) {
            String[] split = html.split("ライン消去あり");
            String noDeletedLine = split[0];
            String deletedLine = split[1];
            List<String> noDeletedLineFumens = extractTetfu(noDeletedLine);
            List<String> deletedLineFumens = extractTetfu(deletedLine);
            return new PathHTML(html, pattern, noDeletedLineFumens, deletedLineFumens);
        } else {
            List<String> noDeletedLineFumens = extractTetfu(html);
            List<String> deletedLineFumens = extractTetfu("");
            return new PathHTML(html, pattern, noDeletedLineFumens, deletedLineFumens);
        }
    }

    public static PathHTML loadPathMinimalHTML() throws IOException {
        return loadPathMinimalHTML(MINIMAL_PATH);
    }

    public static PathHTML loadPathMinimalHTML(String path) throws IOException {
        return loadHTML(path);
    }

    public static PathCSV loadPathUniqueNoneCSV() throws IOException {
        return loadPathCSV(Paths.get(UNIQUE_CSV));
    }

    public static PathCSV loadPathMinimalNoneCSV() throws IOException {
        return loadPathCSV(Paths.get(MINIMAL_CSV));
    }

    private static PathCSV loadPathCSV(Path path) throws IOException {
        List<Operations> operations = Files.lines(path)
                .map(OperationInterpreter::parseToOperations)
                .collect(Collectors.toList());
        return new PathCSV(operations);
    }

    public static CSVStore loadPathSolutionCSV() throws IOException {
        return loadCSVStore(Paths.get(DEFAULT_CSV), Arrays.asList("fumen", "use", "num-solutions", "num-patterns", "solutions", "patterns"));
    }

    public static CSVStore loadPathUseCSV() throws IOException {
        return loadCSVStore(Paths.get(DEFAULT_CSV), Arrays.asList("use", "num-solutions", "num-patterns", "fumens", "patterns"));
    }

    public static CSVStore loadPathPatternCSV() throws IOException {
        return loadCSVStore(Paths.get(DEFAULT_CSV), Arrays.asList("pattern", "num-solutions", "use", "nouse", "fumens"));
    }

    private static CSVStore loadCSVStore(Path path, List<String> columnNames) throws IOException {
        CSVStore csvStore = new CSVStore(columnNames);
        Files.lines(path)
                .skip(1)  // skip header
                .forEach(csvStore::load);
        return csvStore;
    }

    private static int extractPattern(String html) {
        Pattern pattern = Pattern.compile("<div>(\\d+)パターン</div>");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            assert matcher.groupCount() == 1 : html;
            return Integer.valueOf(matcher.group(1));
        } else {
            throw new IllegalStateException("Not found pattern: " + html);
        }
    }

    private static List<String> extractTetfu(String html) {
        Pattern pattern = Pattern.compile("'http://fumen\\.zui\\.jp/\\?v115@(.+?)'");
        Matcher matcher = pattern.matcher(html);

        ArrayList<String> fumens = new ArrayList<>();
        while (matcher.find()) {
            assert matcher.groupCount() == 1;
            fumens.add(matcher.group(1));
        }
        return fumens;
    }

    public static boolean existsPathUniqueHTML() {
        return new File(UNIQUE_PATH).exists();
    }

    public static boolean existsPathMinimalHTML() {
        return new File(MINIMAL_PATH).exists();
    }

    public static boolean existsErrorText() {
        return new File(ERROR_PATH).exists();
    }

    public static void deletePathUniqueHTML() {
        File file = new File(UNIQUE_PATH);
        FileHelper.deleteFileAndClose(file);
    }

    public static void deletePathMinimalHTML() {
        File file = new File(MINIMAL_PATH);
        FileHelper.deleteFileAndClose(file);
    }

    public static void deletePathUniqueCSV() {
        File file = new File(UNIQUE_PATH);
        FileHelper.deleteFileAndClose(file);
    }

    public static void deletePathMinimalCSV() {
        File file = new File(MINIMAL_PATH);
        FileHelper.deleteFileAndClose(file);
    }

    public static void deleteErrorText() {
        File file = new File(ERROR_PATH);
        FileHelper.deleteFileAndClose(file);
    }

    public static String loadErrorText() throws IOException {
        return Files.lines(Paths.get(ERROR_PATH)).collect(Collectors.joining(System.lineSeparator()));
    }
}
