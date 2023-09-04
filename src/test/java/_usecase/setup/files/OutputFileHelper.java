package _usecase.setup.files;

import _usecase.FileHelper;
import helper.CSVStore;
import lib.MyFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OutputFileHelper {
    private static final String SETUP_PATH = concatPath("output", "setup.html");
    private static final String SETUP_CSV = concatPath("output", "setup.csv");

    private static String concatPath(String... names) {
        return FileHelper.concatPath(names);
    }

    public static SetupHTML loadSetupHTML() throws IOException {
        return loadSetupHTML(SETUP_PATH);
    }

    private static SetupHTML loadSetupHTML(String path) throws IOException {
        return loadHTML(path);
    }

    private static SetupHTML loadHTML(String path) throws IOException {
        String html = MyFiles.lines(Paths.get(path)).collect(Collectors.joining());

        String mergedFumen;
        {
            String[] split = html.split("</header>");
            html = split[1];

            List<String> list = extractTetfu(split[0]);
            assert list.size() <= 1 : split[0];
            mergedFumen = list.isEmpty() ? null : list.get(0);
        }

        List<String> fumens = extractTetfu(html);
        return new SetupHTML(html, mergedFumen, fumens);
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

    public static CSVStore loadSetupCSV() throws IOException {
        Path path = Paths.get(SETUP_CSV);
        return loadSetupCSV(MyFiles.lines(path));
    }

    public static CSVStore loadSetupCSV(Stream<String> content) {
        return loadCSVStore(content, Arrays.asList("fumen", "use", "num-build"));
    }

    private static CSVStore loadCSVStore(Stream<String> content, List<String> columnNames) {
        CSVStore csvStore = new CSVStore(columnNames);
        content
                .skip(1)  // skip header
                .forEach(csvStore::load);
        return csvStore;
    }

    public static void deleteSetupHTML() {
        File file = new File(SETUP_PATH);
        FileHelper.deleteFileAndClose(file);
    }
}
