package entry;

import entry.path.PathEntryPoint;
import entry.path.PathSettingParser;
import entry.path.PathSettings;
import entry.percent.PercentEntryPoint;
import entry.percent.PercentSettingParser;
import entry.percent.PercentSettings;
import entry.util.fig.FigUtilEntryPoint;
import entry.util.fig.FigUtilSettingParser;
import entry.util.fig.FigUtilSettings;
import exceptions.FinderInitializeException;
import exceptions.FinderParseException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EntryPointMain {
    private static final String VERSION = "0.47";

    public static int main(String[] args) {
        if (args.length < 1)
            throw new IllegalArgumentException("No command: Use percent, path");

        if (args[0].equals("-h")) {
            System.out.println("Usage: <command> [options]");
            System.out.println("  command: percent, path, util fig");
            return 0;
        }

        if (args[0].equals("-v")) {
            System.out.println("Version: " + VERSION);
            return 0;
        }

        // 引数リストの作成
        List<String> argsList = new ArrayList<>(Arrays.asList(args).subList(1, args.length));

        // 実行を振り分け
        EntryPoint entryPoint;
        try {
            entryPoint = createEntryPoint(args[0], argsList);
        } catch (Exception e) {
            System.err.println("Error: Failed to execute pre-main. Output stack trace to output/error.txt");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            outputError(e, args);
            return 1;
        }

        // メイン処理を実行する
        try {
            entryPoint.run();
        } catch (Exception e) {
            System.err.println("Error: Failed to execute main. Output stack trace to output/error.txt");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();

            // 終了処理をする（メイン処理失敗後）
            try {
                entryPoint.close();
                outputError(e, args);
            } catch (Exception e2) {
                System.err.println("Error: Failed to execute post-main. Output stack trace to output/error.txt");
                System.err.println("Message: " + e.getMessage());
                e.printStackTrace();
                outputError(Arrays.asList(e, e2), args);
            }

            return 1;
        }

        // 終了処理をする（メイン処理成功後）
        try {
            entryPoint.close();
        } catch (Exception e) {
            System.err.println("Error: Failed to terminate. Output stack trace to output/error.txt");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            outputError(e, args);
            return 1;
        }

        return 0;
    }

    private static void outputError(Exception exception, String[] commands) {
        outputError(Collections.singletonList(exception), commands);
    }

    private static void outputError(List<Exception> exceptions, String[] commands) {
        // Make directory
        makeOutputDirectory();

        // Output error to file
        File errorFile = new File("output/error.txt");
        try {
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(errorFile, false)))) {
                // Output datetime
                LocalDateTime now = LocalDateTime.now();
                String dateTimeStr = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(now);
                writer.printf("# DateTime: %s%n", dateTimeStr);

                // Output version
                writer.printf("# Version: %s%n", VERSION);

                // Output command
                writer.printf("# command: %s%n", Arrays.stream(commands).collect(Collectors.joining(" ")));

                // Output error messages
                writer.println("# Error message summary:");
                for (Exception exception : exceptions) {
                    writer.printf("  * %s [%s]%n", exception.getMessage(), exception.getClass().getSimpleName());
                    Throwable cause = exception.getCause();
                    while (cause != null) {
                        String message = cause.getMessage();
                        writer.printf("    - %s [%s]%n", message != null ? message : "<no message>", cause.getClass().getSimpleName());
                        System.out.printf("%s [%s]%n", message != null ? message : "<no message>", cause.getClass().getSimpleName());
                        cause = cause.getCause();
                    }
                }
                writer.println();
                writer.println();

                // Output stack traces
                writer.println("------------------------------");
                writer.println("# Stack trace:");
                writer.println("------------------------------");
                writer.println();

                for (Exception exception : exceptions) {
                    exception.printStackTrace(writer);
                    writer.println("==============================");
                }
            }
        } catch (IOException e) {
            System.err.println("Error: Failed to output error to output/error.txt");
            e.printStackTrace();
        }
    }

    private static void makeOutputDirectory() {
        File outputDirectory = new File("output");
        if (!outputDirectory.exists()) {
            // noinspection ResultOfMethodCallIgnored
            outputDirectory.mkdirs();
        }
    }

    private static EntryPoint createEntryPoint(String type, List<String> commands) throws FinderInitializeException, FinderParseException {
        // 実行を振り分け
        switch (type) {
            case "percent":
                return getPercentEntryPoint(commands);
            case "path":
                return getPathEntryPoint(commands);
            case "util":
                return getUtilEntryPoint(commands);
            default:
                throw new IllegalArgumentException("Invalid type: Use percent, path, util");
        }
    }

    private static EntryPoint getPercentEntryPoint(List<String> commands) throws FinderInitializeException, FinderParseException {
        PercentSettingParser settingParser = new PercentSettingParser(commands);
        Optional<PercentSettings> settings = settingParser.parse();

        if (!settings.isPresent())
            throw new FinderParseException("Cannot parse setting for percent");

        return new PercentEntryPoint(settings.get());
    }

    private static EntryPoint getPathEntryPoint(List<String> commands) throws FinderInitializeException, FinderParseException {
        PathSettingParser settingParser = new PathSettingParser(commands);
        Optional<PathSettings> settings = settingParser.parse();

        if (!settings.isPresent())
            throw new FinderParseException("Cannot parse setting for path");

        return new PathEntryPoint(settings.get());
    }

    private static EntryPoint getUtilEntryPoint(List<String> commands) throws FinderParseException {
        if (!commands.get(0).equals("fig"))
            throw new IllegalArgumentException("util: Invalid type: Use fig");

        List<String> figCommands = commands.subList(1, commands.size());
        FigUtilSettingParser settingParser = new FigUtilSettingParser(figCommands);
        Optional<FigUtilSettings> settings = settingParser.parse();

        if (!settings.isPresent())
            throw new FinderParseException("Cannot parse setting for util fig");

        return new FigUtilEntryPoint(settings.get());
    }
}
