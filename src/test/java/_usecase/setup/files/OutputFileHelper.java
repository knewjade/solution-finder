package _usecase.setup.files;

import _usecase.FileHelper;

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
    private static final String SETUP_PATH = concatPath("output", "setup.html");

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
        String html = Files.lines(Paths.get(path)).collect(Collectors.joining());
        List<String> fumens = extractTetfu(html);
        return new SetupHTML(html, fumens);
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

    public static void deleteSetupHTML() {
        File file = new File(SETUP_PATH);
        FileHelper.deleteFileAndClose(file);
    }
}
