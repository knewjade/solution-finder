package _usecase;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import common.SyntaxException;
import common.pattern.LoadedPatternGenerator;
import core.field.Field;
import core.field.FieldView;
import core.mino.Piece;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

public class ConfigFileHelper {
    private static final String FIELD_PATH = concatPath("input", "field.txt");
    private static final String PATTERN_PATH = concatPath("input", "patterns.txt");

    private static final String LINE_SEPARATOR = System.lineSeparator();
    public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;

    public static void createFieldFile(String text) throws IOException {
        createFieldFile(text, "field", concatPath("input"));
    }

    public static void createFieldFile(String text, String fileName, String directoryPath) throws IOException {
        createNewTextFile(text, fileName, directoryPath, DEFAULT_CHARSET);
    }

    private static String concatPath(String... names) {
        return FileHelper.concatPath(names);
    }

    public static void createFieldFile(Field field, int height) throws IOException {
        createFieldFile(field, height, DEFAULT_CHARSET);
    }

    public static void createFieldFile(Field field, int height, Charset charset) throws IOException {
        createFieldFile(field, height, "field", charset);
    }

    public static void createFieldFile(Field field, int height, String fileName) throws IOException {
        createFieldFile(field, height, fileName, DEFAULT_CHARSET);
    }

    private static void createFieldFile(Field field, int height, String fileName, Charset charset) throws IOException {
        String path = concatPath("input");
        createFieldFile(field, height, fileName, path, charset);
    }

    public static void createFieldFile(Field field, int height, String fileName, String directoryPath) throws IOException {
        createFieldFile(field, height, fileName, directoryPath, DEFAULT_CHARSET);
    }

    private static void createFieldFile(Field field, int height, String fileName, String directoryPath, Charset charset) throws IOException {
        String text = height + LINE_SEPARATOR + FieldView.toString(field, height);
        createNewTextFile(text, fileName, directoryPath, charset);
    }

    public static void deleteFieldFile() throws IOException {
        File file = new File(FIELD_PATH);
        FileHelper.deleteFileAndClose(file);
    }

    private static void createNewTextFile(String text, String fileName, String parentDirectoryPath, Charset charset) throws IOException {
        File file = new File(concatPath(parentDirectoryPath, fileName + ".txt"));
        FileHelper.deleteFileAndClose(file);

        // noinspection ResultOfMethodCallIgnored
        file.createNewFile();
        CharSink charSink = Files.asCharSink(file, charset, FileWriteMode.APPEND);
        charSink.write(text);
    }

    public static void createPatternFile(String pattern) throws IOException {
        createPatternFile(pattern, DEFAULT_CHARSET);
    }

    public static void createPatternFile(String pattern, Charset charset) throws IOException {
        createPatternFile(pattern, "patterns", charset);
    }

    public static void createPatternFile(String pattern, String fileName) throws IOException {
        createPatternFile(pattern, fileName, DEFAULT_CHARSET);
    }

    private static void createPatternFile(String pattern, String fileName, Charset charset) throws IOException {
        createPatternFile(pattern, concatPath("input"), fileName, charset);
    }

    public static void createPatternFile(String pattern, String directoryPath, String fileName) throws IOException {
        createPatternFile(pattern, directoryPath, fileName, DEFAULT_CHARSET);
    }

    private static void createPatternFile(String pattern, String directoryPath, String fileName, Charset charset) throws IOException {
        createNewTextFile(pattern, fileName, directoryPath, charset);
    }

    public static void createPatternFileFromCommand(String patternCommand) throws IOException, SyntaxException {
        createPatternFileFromCommand(patternCommand, "patterns");
    }

    public static void createPatternFileFromCommand(String patternCommand, String fileName) throws IOException, SyntaxException {
        createPatternFileFromCommand(patternCommand, fileName, DEFAULT_CHARSET);
    }

    private static void createPatternFileFromCommand(String patternCommand, String fileName, Charset charset) throws IOException, SyntaxException {
        createNewTextFileAndExpand(patternCommand, fileName, concatPath("input"), charset);
    }

    private static void createNewTextFileAndExpand(String pattern, String fileName, String parentDirectoryPath, Charset charset) throws IOException, SyntaxException {
        File file = new File(concatPath(parentDirectoryPath, fileName + ".txt"));
        FileHelper.deleteFileAndClose(file);

        // noinspection ResultOfMethodCallIgnored
        file.createNewFile();
        CharSink charSink = Files.asCharSink(file, charset, FileWriteMode.APPEND);
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