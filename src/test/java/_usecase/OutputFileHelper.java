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

class OutputFileHelper {
    private static final String UNIQUE_PATH = "output/path_unique.html";
    private static final String MINIMAL_PATH = "output/path_minimal.html";
    private static final String UNIQUE_CSV = "output/path_unique.csv";
    private static final String MINIMAL_CSV = "output/path_minimal.csv";

    static PathHTML loadPathUniqueHTML() throws IOException {
        return loadPathUniqueHTML(UNIQUE_PATH);
    }

    static PathHTML loadPathUniqueHTML(String path) throws IOException {
        String html = Files.lines(Paths.get(path)).collect(Collectors.joining());
        int pattern = extractPattern(html);

        String[] split = html.split("ライン消去あり");
        String noDeletedLine = split[0];
        String deletedLine = split[1];
        List<String> noDeletedLineFumens = extractTetfu(noDeletedLine);
        List<String> deletedLineFumens = extractTetfu(deletedLine);
        return new PathHTML(pattern, noDeletedLineFumens, deletedLineFumens);
    }

    static PathCSV loadPathUniqueCSV() throws IOException {
        List<Operations> operations = Files.lines(Paths.get(UNIQUE_CSV))
                .map(OperationInterpreter::parseToOperations)
                .collect(Collectors.toList());
        return new PathCSV(operations);
    }

    static PathHTML loadPathMinimalHTML() throws IOException {
        return loadPathMinimalHTML(MINIMAL_PATH);
    }

    static PathHTML loadPathMinimalHTML(String path) throws IOException {
        String html = Files.lines(Paths.get(path)).collect(Collectors.joining());
        int pattern = extractPattern(html);

        String[] split = html.split("ライン消去あり");
        String noDeletedLine = split[0];
        String deletedLine = split[1];
        List<String> noDeletedLineFumens = extractTetfu(noDeletedLine);
        List<String> deletedLineFumens = extractTetfu(deletedLine);
        return new PathHTML(pattern, noDeletedLineFumens, deletedLineFumens);
    }

    static PathCSV loadPathMinimalCSV() throws IOException {
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

    static boolean existsPathUniqueHTML() {
        return new File(UNIQUE_PATH).exists();
    }

    static boolean existsPathMinimalHTML() {
        return new File(MINIMAL_PATH).exists();
    }

    static void deletePathUniqueHTML() {
        File file = new File(UNIQUE_PATH);
        deleteFile(file);
    }

    static void deletePathMinimalHTML() {
        File file = new File(MINIMAL_PATH);
        deleteFile(file);
    }

    static void deletePathUniqueCSV() {
        File file = new File(UNIQUE_PATH);
        deleteFile(file);
    }

    static void deletePathMinimalCSV() {
        File file = new File(MINIMAL_PATH);
        deleteFile(file);
    }

    private static void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
