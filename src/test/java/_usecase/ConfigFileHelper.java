package _usecase;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import common.SyntaxException;
import common.datastore.blocks.Pieces;
import common.pattern.LoadedPatternGenerator;
import core.field.Field;
import core.field.FieldView;
import core.mino.Piece;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigFileHelper {
    private static final String FIELD_PATH = concatPath("input", "field.txt");
    private static final String PATTERN_PATH = concatPath("input", "patterns.txt");

    private static final String LINE_SEPARATOR = System.lineSeparator();

    public static void createFieldFile(String text) throws IOException {
        createFieldFile(text, "field", concatPath("input"));
    }

    public static void createFieldFile(String text, String fileName, String directoryPath) throws IOException {
        createNewTextFile(text, fileName, directoryPath);
    }

    private static String concatPath(String... names) {
        return FileHelper.concatPath(names);
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
        createNewTextFile(text, fileName, directoryPath);
    }

    public static void deleteFieldFile() throws IOException {
        File file = new File(FIELD_PATH);
        FileHelper.deleteFileAndClose(file);
    }

    private static void createNewTextFile(String text, String fileName, String parentDirectoryPath) throws IOException {
        File file = new File(concatPath(parentDirectoryPath, fileName + ".txt"));
        FileHelper.deleteFileAndClose(file);

        // noinspection ResultOfMethodCallIgnored
        file.createNewFile();
        CharSink charSink = Files.asCharSink(file, Charsets.UTF_8, FileWriteMode.APPEND);
        charSink.write(text);
    }

    public static void createPatternFile(String pattern) throws IOException {
        createPatternFile(pattern, "patterns");
    }

    public static void createPatternFile(String pattern, String fileName) throws IOException {
        createPatternFile(pattern, concatPath("input"), fileName);
    }

    public static void createPatternFile(String pattern, String directoryPath, String fileName) throws IOException {
        createNewTextFile(pattern, fileName, directoryPath);
    }

    public static void createPatternFileFromCommand(String patternCommand) throws IOException, SyntaxException {
        createPatternFileFromCommand(patternCommand, "patterns");
    }

    public static void createPatternFileFromCommand(String patternCommand, String fileName) throws IOException, SyntaxException {
        createNewTextFileAndExpand(patternCommand, fileName, concatPath("input"));
    }

    private static void createNewTextFileAndExpand(String pattern, String fileName, String parentDirectoryPath) throws IOException, SyntaxException {
        File file = new File(concatPath(parentDirectoryPath, fileName + ".txt"));
        FileHelper.deleteFileAndClose(file);

        // noinspection ResultOfMethodCallIgnored
        file.createNewFile();
        CharSink charSink = Files.asCharSink(file, Charsets.UTF_8, FileWriteMode.APPEND);
        String text = new LoadedPatternGenerator(pattern).blocksStream()
                .map(pieces -> pieces.blockStream().map(Piece::getName).collect(Collectors.joining()))
                .collect(Collectors.joining(System.lineSeparator()));
        charSink.write(text);
    }

    public static void deletePatternFile() {
        File file = new File(PATTERN_PATH);
        FileHelper.deleteFileAndClose(file);
    }
}