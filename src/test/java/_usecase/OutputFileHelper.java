package _usecase;

import common.datastore.Operations;
import common.parser.OperationInterpreter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OutputFileHelper {
    private static final String UNIQUE_PATH = concatPath("output", "path_unique.html");
    private static final String MINIMAL_PATH = concatPath("output", "path_minimal.html");
    private static final String UNIQUE_CSV = concatPath("output", "path_unique.csv");
    private static final String MINIMAL_CSV = concatPath("output", "path_minimal.csv");
    private static final String ERROR_PATH = concatPath("output", "error.txt");

    private static String concatPath(String... names) {
        return FileHelper.concatPath(names);
    }

    public static PathHTML loadPathUniqueHTML() throws IOException {
        return loadPathUniqueHTML(UNIQUE_PATH);
    }

    public static PathHTML loadPathUniqueHTML(String path) throws IOException {
        String html = Files.lines(Paths.get(path)).collect(Collectors.joining());
        int pattern = extractPattern(html);

        String[] split = html.split("ライン消去あり");
        String noDeletedLine = split[0];
        String deletedLine = split[1];
        List<String> noDeletedLineFumens = extractTetfu(noDeletedLine);
        List<String> deletedLineFumens = extractTetfu(deletedLine);
        return new PathHTML(pattern, noDeletedLineFumens, deletedLineFumens);
    }

    public static PathCSV loadPathUniqueCSV() throws IOException {
        List<Operations> operations = Files.lines(Paths.get(UNIQUE_CSV))
                .map(OperationInterpreter::parseToOperations)
                .collect(Collectors.toList());
        return new PathCSV(operations);
    }

    public static PathHTML loadPathMinimalHTML() throws IOException {
        return loadPathMinimalHTML(MINIMAL_PATH);
    }

    public static PathHTML loadPathMinimalHTML(String path) throws IOException {
        String html = Files.lines(Paths.get(path)).collect(Collectors.joining());
        int pattern = extractPattern(html);

        String[] split = html.split("ライン消去あり");
        String noDeletedLine = split[0];
        String deletedLine = split[1];
        List<String> noDeletedLineFumens = extractTetfu(noDeletedLine);
        List<String> deletedLineFumens = extractTetfu(deletedLine);
        return new PathHTML(pattern, noDeletedLineFumens, deletedLineFumens);
    }

    public static PathCSV loadPathMinimalCSV() throws IOException {
        List<Operations> operations = Files.lines(Paths.get(MINIMAL_CSV))
                .map(OperationInterpreter::parseToOperations)
                .collect(Collectors.toList());
        return new PathCSV(operations);
    }

    private static int extractPattern(String html) {
        Pattern pattern = Pattern.compile("<div>(\\d+)パターン</div>");
        Matcher matcher = pattern.matcher(html);
        assert matcher.find() : html;
        assert matcher.groupCount() == 1;
        return Integer.valueOf(matcher.group(1));
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
        FileHelper.deleteFile(file);
    }

    public static void deletePathMinimalHTML() {
        File file = new File(MINIMAL_PATH);
        FileHelper.deleteFile(file);
    }

    public static void deletePathUniqueCSV() {
        File file = new File(UNIQUE_PATH);
        FileHelper.deleteFile(file);
    }

    public static void deletePathMinimalCSV() {
        File file = new File(MINIMAL_PATH);
        FileHelper.deleteFile(file);
    }

    public static void deleteErrorText() {
        File file = new File(ERROR_PATH);
        FileHelper.deleteFile(file);
    }

    public static String loadErrorText() throws IOException {
        return Files.lines(Paths.get(ERROR_PATH)).collect(Collectors.joining(System.lineSeparator()));
    }
}
