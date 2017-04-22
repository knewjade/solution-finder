package entry.percent;

import core.mino.MinoFactory;
import entry.CommandLineWrapper;
import entry.EntryPoint;
import org.apache.commons.cli.*;
import tetfu.Tetfu;
import tetfu.TetfuPage;
import tetfu.common.ColorConverter;

import java.util.List;
import java.util.Optional;

public class PercentEntryPoint implements EntryPoint {
    private final String[] commands;

    public PercentEntryPoint(List<String> commands) {
        this.commands = new String[commands.size()];
        commands.toArray(this.commands);
    }

    public PercentEntryPoint(String[] commands) {
        this.commands = commands;
    }

    @Override
    public void run() throws ParseException {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, commands);
        CommandLineWrapper wrapper = new CommandLineWrapper(commandLine);
        Settings settings = new Settings();

        // help
        if (wrapper.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("p-percent [options]", options);
            return;
        }

        // using hold
        Optional<Boolean> isUsingHold = wrapper.getBoolOption("hold");
        isUsingHold.ifPresent(settings::setUsingHold);

        // using tetfu
        Optional<String> tetfuData = wrapper.getStringOption("tetfu");
        tetfuData.ifPresent(value -> {
            MinoFactory minoFactory = new MinoFactory();
            ColorConverter colorConverter = new ColorConverter();
            Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
            if (value.startsWith("v115@")) {
                List<TetfuPage> decode = tetfu.decode(value.substring(5));
                int page = wrapper.getIntegerOption("page").orElse(1);
                if (page < 1)
                    throw new IllegalArgumentException(String.format("Option[page=%d]: Should 1 <= page of tetfu", page));
                if (page <= decode.size()) {
                    TetfuPage tetfuPage = decode.get(page - 1);
                    String[] splitComment = tetfuPage.getComment().split(" ");
                    if (splitComment.length < 1)
                        throw new IllegalArgumentException(String.format("Cannot max-clear-line in comment of tetfu in %d pages", page));
                    int maxClearLine = Integer.valueOf(splitComment[0]);
                    settings.setMaxClearLine(maxClearLine);
                    settings.setField(tetfuPage.getField(), maxClearLine);
                } else {
                    throw new IllegalArgumentException(String.format("Option[page=%d]: Over page", page));
                }
            } else {
                throw new UnsupportedOperationException("Unsupported tetfu older than v115");
            }
        });
    }

    private Options createOptions() {
        Options options = new Options();

        Option helpOption = Option.builder("h")
                .optionalArg(true)
                .longOpt("help")
                .desc("Usage")
                .build();
        options.addOption(helpOption);

        Option holdOption = Option.builder("H")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("use or avoid")
                .longOpt("hold")
                .desc("Use Hold")
                .build();
        options.addOption(holdOption);

        Option tetfuOption = Option.builder("t")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("data-of-tetfu")
                .longOpt("tetfu")
                .desc("Import field from Tetfu")
                .build();
        options.addOption(tetfuOption);

        Option tetfuPageOption = Option.builder("p")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("page-of-tetfu")
                .longOpt("page")
                .desc("Page of Tetfu to import field")
                .build();
        options.addOption(tetfuPageOption);

        Option inputFileOption = Option.builder("i")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("input-file-path")
                .longOpt("input")
                .desc("File path of field define")
                .build();
        options.addOption(inputFileOption);

        Option logFileOption = Option.builder("l")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("log-file-path")
                .longOpt("log")
                .desc("File path of output log")
                .build();
        options.addOption(logFileOption);

        return options;
    }
}
