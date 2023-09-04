package _usecase.ren.files;

import _usecase.FileHelper;
import lib.MyFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OutputFileHelper {
    private static final String REN_PATH = concatPath("output", "ren.html");

    private static String concatPath(String... names) {
        return FileHelper.concatPath(names);
    }

    public static SetupHTML loadRenHTML() throws IOException {
        return loadRenHTML(REN_PATH);
    }

    private static SetupHTML loadRenHTML(String path) throws IOException {
        return loadHTML(path);
    }

    private static SetupHTML loadHTML(String path) throws IOException {
        String html = MyFiles.lines(Paths.get(path)).collect(Collectors.joining());
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

    public static void deleteRenHTML() {
        File file = new File(REN_PATH);
        FileHelper.deleteFileAndClose(file);
    }
}
