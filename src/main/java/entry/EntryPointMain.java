package entry;

import core.FinderConstant;
import entry.cover.CoverEntryPoint;
import entry.cover.CoverOptions;
import entry.cover.CoverSettingParser;
import entry.cover.CoverSettings;
import entry.path.PathEntryPoint;
import entry.path.PathOptions;
import entry.path.PathSettingParser;
import entry.path.PathSettings;
import entry.percent.PercentEntryPoint;
import entry.percent.PercentOptions;
import entry.percent.PercentSettingParser;
import entry.percent.PercentSettings;
import entry.ren.RenEntryPoint;
import entry.ren.RenOptions;
import entry.ren.RenSettingParser;
import entry.ren.RenSettings;
import entry.setup.SetupEntryPoint;
import entry.setup.SetupOptions;
import entry.setup.SetupSettingParser;
import entry.setup.SetupSettings;
import entry.spin.SpinEntryPoint;
import entry.spin.SpinOptions;
import entry.spin.SpinSettingParser;
import entry.spin.SpinSettings;
import entry.util.fig.FigUtilEntryPoint;
import entry.util.fig.FigUtilSettingParser;
import entry.util.fig.FigUtilSettings;
import entry.util.fumen.FumenUtilEntryPoint;
import entry.util.fumen.FumenUtilOptions;
import entry.util.fumen.FumenUtilSettingParser;
import entry.util.fumen.FumenUtilSettings;
import entry.util.seq.SeqUtilEntryPoint;
import entry.util.seq.SeqUtilOptions;
import entry.util.seq.SeqUtilSettingParser;
import entry.util.seq.SeqUtilSettings;
import entry.verify.kicks.VerifyKicksEntryPoint;
import entry.verify.kicks.VerifyKicksOptions;
import entry.verify.kicks.VerifyKicksSettingParser;
import entry.verify.kicks.VerifyKicksSettings;
import exceptions.FinderInitializeException;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EntryPointMain {
    private static final List<String> COMMANDS = Arrays.asList(
            "percent",
            "path",
            "setup",
            "ren(or combo)",
            "spin",
            "cover",
            "util fig",
            "util fumen",
            "util seq",
            "verify kicks"
    );

    public static int main(String[] args) {
        if (args.length < 1 || args[0].equals("-h") || args[0].equals("-v")) {
            System.out.println("Version: " + FinderConstant.VERSION);
            if (args.length < 1 || args[0].equals("-h")) {
                System.out.println("Usage: <command> [options]");
                System.out.println("  <command>:");
                for (String command : COMMANDS)
                    System.out.println("    - " + command);
            }
            return 0;
        }

        // 引数リストの作成
        List<String> argsList = new ArrayList<>(Arrays.asList(args).subList(1, args.length));

        // 実行を振り分け
        EntryPoint entryPoint;
        try {
            Optional<EntryPoint> optional = createEntryPoint(args[0], argsList);
            if (!optional.isPresent()) return 0;
            entryPoint = optional.get();
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
                writer.printf("# Version: %s%n", FinderConstant.VERSION);

                // Output command
                writer.printf("# command: %s%n", String.join(" ", commands));

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

    private static Optional<EntryPoint> createEntryPoint(String type, List<String> commands) throws FinderInitializeException, FinderParseException {
        // 実行を振り分け
        switch (type) {
            case "percent":
                return getPercentEntryPoint(commands);
            case "path":
                return getPathEntryPoint(commands);
            case "util":
                return getUtilEntryPoint(commands);
            case "setup":
                return getSetupEntryPoint(commands);
            case "ren":
            case "combo":
                return getRenEntryPoint(commands);
            case "cover":
                return getCoverEntryPoint(commands);
            case "spin":
                return getSpinEntryPoint(commands);
            case "verify":
                return getVerifyEntryPoint(commands);
            default:
                throw new IllegalArgumentException("Invalid type: Use percent, path, util, setup, ren, cover, spin");
        }
    }

    private static Optional<EntryPoint> getPercentEntryPoint(List<String> commands) throws FinderInitializeException, FinderParseException {
        Options options = PercentOptions.create();
        CommandLineParser parser = new DefaultParser();
        PercentSettingParser settingParser = new PercentSettingParser(options, parser);
        Optional<PercentSettings> settingsOptional = settingParser.parse(commands);
        if (settingsOptional.isPresent()) {
            PercentSettings settings = settingsOptional.get();
            return Optional.of(new PercentEntryPoint(settings));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<EntryPoint> getPathEntryPoint(List<String> commands) throws FinderInitializeException, FinderParseException {
        Options options = PathOptions.create();
        CommandLineParser parser = new DefaultParser();
        PathSettingParser settingParser = new PathSettingParser(options, parser);
        Optional<PathSettings> settingsOptional = settingParser.parse(commands);
        if (settingsOptional.isPresent()) {
            PathSettings settings = settingsOptional.get();
            return Optional.of(new PathEntryPoint(settings));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<EntryPoint> getCoverEntryPoint(List<String> commands) throws FinderInitializeException, FinderParseException {
        Options options = CoverOptions.create();
        CommandLineParser parser = new DefaultParser();
        CoverSettingParser settingParser = new CoverSettingParser(options, parser);
        Optional<CoverSettings> settingsOptional = settingParser.parse(commands);
        if (settingsOptional.isPresent()) {
            CoverSettings settings = settingsOptional.get();
            return Optional.of(new CoverEntryPoint(settings));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<EntryPoint> getUtilEntryPoint(List<String> commands) throws FinderParseException, FinderInitializeException {
        String subcommand = commands.get(0);
        List<String> parameters = commands.subList(1, commands.size());

        switch (subcommand) {
            case "fig": {
                FigUtilSettingParser settingParser = new FigUtilSettingParser(parameters);
                Optional<FigUtilSettings> settingsOptional = settingParser.parse();

                if (settingsOptional.isPresent()) {
                    FigUtilSettings settings = settingsOptional.get();
                    return Optional.of(new FigUtilEntryPoint(settings));
                }

                break;
            }
            case "fumen": {
                Options options = FumenUtilOptions.create();
                CommandLineParser parser = new DefaultParser();
                FumenUtilSettingParser settingParser = new FumenUtilSettingParser(options, parser);
                Optional<FumenUtilSettings> settingsOptional = settingParser.parse(parameters);
                if (settingsOptional.isPresent()) {
                    FumenUtilSettings settings = settingsOptional.get();
                    return Optional.of(new FumenUtilEntryPoint(settings));
                } else {
                    return Optional.empty();
                }
            }
            case "seq": {
                Options options = SeqUtilOptions.create();
                CommandLineParser parser = new DefaultParser();
                SeqUtilSettingParser settingParser = new SeqUtilSettingParser(options, parser);
                Optional<SeqUtilSettings> settingsOptional = settingParser.parse(parameters);
                if (settingsOptional.isPresent()) {
                    SeqUtilSettings settings = settingsOptional.get();
                    return Optional.of(new SeqUtilEntryPoint(settings));
                } else {
                    return Optional.empty();
                }
            }
            default:
                throw new IllegalArgumentException("util: Invalid type: Use fig or seq or fumen");
        }

        return Optional.empty();
    }

    private static Optional<EntryPoint> getSetupEntryPoint(List<String> commands) throws FinderParseException, FinderInitializeException {
        Options options = SetupOptions.create();
        CommandLineParser parser = new DefaultParser();
        SetupSettingParser settingParser = new SetupSettingParser(options, parser);
        Optional<SetupSettings> settingsOptional = settingParser.parse(commands);
        if (settingsOptional.isPresent()) {
            SetupSettings settings = settingsOptional.get();
            return Optional.of(new SetupEntryPoint(settings));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<EntryPoint> getRenEntryPoint(List<String> commands) throws FinderParseException, FinderInitializeException {
        Options options = RenOptions.create();
        CommandLineParser parser = new DefaultParser();
        RenSettingParser settingParser = new RenSettingParser(options, parser);
        Optional<RenSettings> settingsOptional = settingParser.parse(commands);
        if (settingsOptional.isPresent()) {
            RenSettings settings = settingsOptional.get();
            return Optional.of(new RenEntryPoint(settings));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<EntryPoint> getSpinEntryPoint(List<String> commands) throws FinderParseException, FinderInitializeException {
        Options options = SpinOptions.create();
        CommandLineParser parser = new DefaultParser();
        SpinSettingParser settingParser = new SpinSettingParser(options, parser);
        Optional<SpinSettings> settingsOptional = settingParser.parse(commands);
        if (settingsOptional.isPresent()) {
            SpinSettings settings = settingsOptional.get();
            return Optional.of(new SpinEntryPoint(settings));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<EntryPoint> getVerifyEntryPoint(List<String> commands) throws FinderParseException {
        String subcommand = commands.get(0);
        List<String> parameters = commands.subList(1, commands.size());

        if ("kicks".equals(subcommand)) {
            Options options = VerifyKicksOptions.create();
            CommandLineParser parser = new DefaultParser();
            VerifyKicksSettingParser settingParser = new VerifyKicksSettingParser(options, parser);
            Optional<VerifyKicksSettings> settingsOptional = settingParser.parse(parameters);
            if (settingsOptional.isPresent()) {
                VerifyKicksSettings settings = settingsOptional.get();
                return Optional.of(new VerifyKicksEntryPoint(settings));
            } else {
                return Optional.empty();
            }
        } else {
            throw new IllegalArgumentException("verify: Invalid type");
        }
    }
}
