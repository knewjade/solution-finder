import core.field.Field;
import core.field.FieldFactory;
import entry.CheckerEntry;
import entry.CheckmateEntry;
import entry.InvokeType;
import entry.Settings;
import misc.PiecesGenerator;
import misc.SyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

// TODO: List of ALL
// Write unittest for searcher.common
// Computerize from main
// Write unittest for main
public class Main {
    private static final String INPUT_DIRECTORY = "input";
    private static final String OUTPUT_DIRECTORY = "output";

    private static final String SEPARATOR = File.separator;
    private static final String CHARSET_NAME = "utf-8";

    private static final String FIELD_TXT = concatPath(INPUT_DIRECTORY, "field.txt");
    private static final String PATTERNS_TXT = concatPath(INPUT_DIRECTORY, "patterns.txt");

    private static String concatPath(String... str) {
        return String.join(SEPARATOR, str);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        List<String> argsList = Arrays.asList(args);
        Settings settings = new Settings();

        // 処理の決定
        InvokeType invokeType = getInvokeType(argsList);
        settings.setInvokeType(invokeType);

        // ホールドの使用の決定
        boolean usingHold = getUsingHold(argsList);
        settings.setUsingHold(usingHold);

        // 実行
        switch (settings.getInvokeType()) {
            case AllPath:
                generatePath(settings);
                break;
            case PerfectPercent:
                calcPercent(settings);
                break;
        }
    }

    private static InvokeType getInvokeType(List<String> argsList) {
        int modeIndex = argsList.indexOf("-m");

        if (0 <= modeIndex && modeIndex < argsList.size() - 1) {
            String modeName = argsList.get(modeIndex + 1);
            switch (modeName) {
                case "allpath":
                    return InvokeType.AllPath;
                case "perfect-percent":
                    return InvokeType.PerfectPercent;
            }
        }

        return InvokeType.PerfectPercent;
    }

    private static boolean getUsingHold(List<String> argsList) {
        int modeIndex = argsList.indexOf("--hold");

        if (0 <= modeIndex && modeIndex < argsList.size() - 1) {
            String modeName = argsList.get(modeIndex + 1);
            switch (modeName) {
                case "true":
                    return true;
                case "false":
                    return false;
                case "yes":
                    return true;
                case "no":
                    return false;
                case "avoid":
                    return false;
                case "use":
                    return true;
            }
        }

        return true;
    }

    private static void calcPercent(Settings settings) throws IOException, ExecutionException, InterruptedException {
        int maxClearLine;
        String marks = "";
        try (Scanner scanner = new Scanner(new File(FIELD_TXT), CHARSET_NAME)) {
            if (!scanner.hasNextInt())
                throw new IllegalArgumentException("Cannot read Field Height from " + FIELD_TXT);
            maxClearLine = scanner.nextInt();

            if (maxClearLine < 2 || 12 < maxClearLine)
                throw new IllegalArgumentException("Field Height should be 2 <= height <= 12 in " + FIELD_TXT);

            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNext())
                stringBuilder.append(scanner.nextLine());

            marks = stringBuilder.toString();
        }

        ArrayList<String> patterns = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(PATTERNS_TXT), CHARSET_NAME)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.equals(""))
                    continue;

                try {
                    PiecesGenerator.verify(line);
                    patterns.add(line);
                } catch (SyntaxException e) {
                    System.err.println("Cannot parse pattern from " + PATTERNS_TXT);
                    System.err.println("Syntax Error: " + e.getMessage());
                    System.exit(1);
                }
            }
        }

        if (patterns.isEmpty())
            throw new IllegalArgumentException("Cannot read patterns from " + PATTERNS_TXT);

        Field field = FieldFactory.createField(marks);

        File outputDirectory = new File(OUTPUT_DIRECTORY);
        if (!outputDirectory.exists()) {
            boolean mairSuccess = outputDirectory.mkdir();
            if (!mairSuccess) {
                throw new IllegalStateException("Failed to make output directory");
            }
        }

        String outputPath = concatPath(OUTPUT_DIRECTORY, "last_output.txt");
        try (Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), CHARSET_NAME))) {
            CheckerEntry entry = new CheckerEntry(fileWriter, settings);
            entry.invoke(field, patterns, maxClearLine);
        }
    }

    private static void generatePath(Settings settings) throws IOException, ExecutionException, InterruptedException {
        int maxClearLine;
        String marks = "";
        try (Scanner scanner = new Scanner(new File(FIELD_TXT), CHARSET_NAME)) {
            if (!scanner.hasNextInt())
                throw new IllegalArgumentException("Cannot read Field Height from " + FIELD_TXT);
            maxClearLine = scanner.nextInt();

            if (maxClearLine < 2 || 12 < maxClearLine)
                throw new IllegalArgumentException("Field Height should be 2 <= height <= 12 in " + FIELD_TXT);

            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNext())
                stringBuilder.append(scanner.nextLine());

            marks = stringBuilder.toString();
        }

        ArrayList<String> patterns = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(PATTERNS_TXT), CHARSET_NAME)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.equals(""))
                    continue;

                try {
                    PiecesGenerator.verify(line);
                    patterns.add(line);
                } catch (SyntaxException e) {
                    System.err.println("Cannot parse pattern from " + PATTERNS_TXT);
                    System.err.println("Syntax Error: " + e.getMessage());
                    System.exit(1);
                }
            }
        }

        if (patterns.isEmpty())
            throw new IllegalArgumentException("Cannot read patterns from " + PATTERNS_TXT);

        Field field = FieldFactory.createField(marks);

        String outputPath = concatPath(OUTPUT_DIRECTORY, "last_output.txt");
        try (Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), CHARSET_NAME))) {
            String allOutputPath = concatPath(OUTPUT_DIRECTORY, "all_path.csv");
            String uniqueOutputPath = concatPath(OUTPUT_DIRECTORY, "unique_path.csv");
            CheckmateEntry entry = new CheckmateEntry(fileWriter, new File(allOutputPath), new File(uniqueOutputPath), settings);
            entry.invoke(field, patterns, maxClearLine);
        }
    }
}
