package _usecase.cover.files;

import _usecase.FileHelper;
import helper.CSVStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class OutputFileHelper {
    private static final String DEFAULT_CSV = concatPath("output", "cover.csv");

    private static String concatPath(String... names) {
        return FileHelper.concatPath(names);
    }

    public static CSVStore loadCoverCSV(List<String> columnNames) throws IOException {
        return loadCSVStore(Paths.get(DEFAULT_CSV), columnNames);
    }

    private static CSVStore loadCSVStore(Path path, List<String> columnNames) throws IOException {
        CSVStore csvStore = new CSVStore(columnNames);
        Files.lines(path)
                .skip(1)  // skip header
                .forEach(csvStore::load);
        return csvStore;
    }
}
