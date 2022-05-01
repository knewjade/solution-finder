package _usecase.spin.files;

import _usecase.FileHelper;
import helper.CSVStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OutputFileHelper {
    private static final String SPIN_HTML_PATH = concatPath("output", "spin.html");

    private static final String SPIN_CSV_PATH = concatPath("output", "spin.csv");

    private static String concatPath(String... names) {
        return FileHelper.concatPath(names);
    }

    public static SpinHTML loadSpinHTML() throws IOException {
        return loadSpinHTML(SPIN_HTML_PATH);
    }

    private static SpinHTML loadSpinHTML(String path) throws IOException {
        return loadHTML(path);
    }

    private static SpinHTML loadHTML(String path) throws IOException {
        String html = Files.lines(Paths.get(path)).collect(Collectors.joining());

        String mergedFumen;
        {
            String[] split = html.split("</header>");
            html = split[1];

            List<String> list = extractTetfu(split[0]);
            assert list.size() <= 1 : split[0];
            mergedFumen = list.isEmpty() ? null : list.get(0);
        }

        Map<TSpinType, List<String>> fumens = getFumensEachSpin(html);
        return new SpinHTML(html, mergedFumen, fumens);
    }

    private static Map<TSpinType, List<String>> getFumensEachSpin(String html) {
        Pattern pattern = Pattern.compile("(<section.*?</section>)");
        Matcher matcher = pattern.matcher(html);

        Map<TSpinType, List<String>> maps = new HashMap<>();
        while (matcher.find()) {
            String section = matcher.group();

            TSpinType spin = extractTSpinType(section);
            List<String> fumens = extractTetfu(section);

            maps.put(spin, fumens);
        }

        return maps;
    }

    private static TSpinType extractTSpinType(String html) {
        Pattern pattern = Pattern.compile("<h2>(.*?)</h2>");
        Matcher matcher = pattern.matcher(html);
        if (!matcher.find()) {
            throw new IllegalStateException();
        }
        String header = matcher.group();

        if (header.contains("Single [Regular]") || header.contains(">Single<")) {
            return TSpinType.RegularSingle;
        }
        if (header.contains("Double [Regular]") || header.contains(">Double<")) {
            return TSpinType.RegularDouble;
        }
        if (header.contains("Triple [Regular]") || header.contains(">Triple<")) {
            return TSpinType.RegularTriple;
        }
        if (header.contains("Single [FIN]")) {
            return TSpinType.FinSingle;
        }
        if (header.contains("Double [FIN]")) {
            return TSpinType.FinDouble;
        }
        if (header.contains("Single [NEO]")) {
            return TSpinType.NeoSingle;
        }
        if (header.contains("Double [NEO]")) {
            return TSpinType.NeoDouble;
        }
        if (header.contains("Single [ISO]")) {
            return TSpinType.IsoSingle;
        }
        if (header.contains("Double [ISO]")) {
            return TSpinType.IsoDouble;
        }
        throw new IllegalStateException("Unexpected header: " + header);
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

    public static void deleteSpinHTML() {
        File file = new File(SPIN_HTML_PATH);
        FileHelper.deleteFileAndClose(file);
    }

    public static CSVStore loadSpinCSV() throws IOException {
        return loadSpinCSV(Paths.get(SPIN_CSV_PATH));
    }

    public static CSVStore loadSpinCSV(Path path) throws IOException {
        return loadSpinCSV(Files.lines(path));
    }

    public static CSVStore loadSpinCSV(Stream<String> content) {
        return loadCSVStore(content, Arrays.asList(
                "fumen", "valid", "use", "num-use", "t-spin-lines", "mini", "name", "total-lines", "hole",
                "t-rotate", "t-x", "t-y", "t-deleted-linekey"
        ));
    }

    private static CSVStore loadCSVStore(Stream<String> content, List<String> columnNames) {
        CSVStore csvStore = new CSVStore(columnNames);
        content
                .skip(1)  // skip header
                .forEach(csvStore::load);
        return csvStore;
    }
}
