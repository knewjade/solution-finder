package _usecase;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import core.field.Field;
import core.field.FieldView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ConfigFileHelper {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    public static void createFieldFile(String text) throws IOException {
        String directoryPath = concatPath("input");
        String fileName = "field";
        createNewTextFile(directoryPath, fileName, text);
    }

    public static void createFieldFile(Field field, int height) throws IOException {
        createFieldFile(field, height, "field");
    }

    public static void createFieldFile(Field field, int height, String fileName) throws IOException {
        String path = concatPath("input");
        createFieldFile(field, height, fileName, path);
    }

    public static void createFieldFile(Field field, int height, String fileName, String directoryPath) throws IOException {
        String text = height + LINE_SEPARATOR + FieldView.toString(field, height);
        createNewTextFile(directoryPath, fileName, text);
    }

    public static void deleteFieldFile() throws IOException {
        deleteTextFile(concatPath("input"), "field", "txt");
        deleteTextFile(concatPath("input"), "field", "csv");
    }

    private static String concatPath(String... names) {
        return Arrays.stream(names).collect(Collectors.joining(File.separator));
    }

    private static void deleteTextFile(String parentDirectoryPath, String fileName, String extension) throws IOException {
        File file = new File(concatPath(parentDirectoryPath, fileName + "." + extension));
        deleteFile(file);
    }

    private static void deleteFile(File file) {
        if (file.exists()) {
            // noinspection ResultOfMethodCallIgnored
            file.delete();

            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createNewTextFile(String parentDirectoryPath, String fileName, String text) throws IOException {
        File file = new File(concatPath(parentDirectoryPath, fileName + ".txt"));
        deleteFile(file);

        // noinspection ResultOfMethodCallIgnored
        file.createNewFile();
        Files.append(text, file, Charsets.UTF_8);
    }

    public static void createPatternFile(String pattern) throws IOException {
        createPatternFile(pattern, "patterns");
    }

    public static void createPatternFile(String pattern, String fileName) throws IOException {
        createPatternFile(pattern, concatPath("input"), fileName);
    }

    public static void createPatternFile(String pattern, String directoryPath, String fileName) throws IOException {
        createNewTextFile(directoryPath, fileName, pattern);
    }

    public static void deletePatternFile() throws IOException {
        deleteTextFile(concatPath("input"), "patterns", "txt");
        deleteTextFile(concatPath("input"), "patterns", "csv");
    }
}