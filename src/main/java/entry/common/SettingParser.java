package entry.common;

import entry.CommandLineWrapper;
import entry.NormalCommandLineWrapper;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class SettingParser<T> {
    private final Options options;
    private final CommandLineFactory commandLineFactory;

    public SettingParser(Options options, CommandLineParser parser) {
        this.options = options;
        this.commandLineFactory = new CommandLineFactory(options, parser);
    }

    public Optional<T> parse(List<String> commands) throws FinderParseException {
        // コマンドラインパーサーの準備
        List<String> commandArgs = commands.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        CommandLine commandLine = commandLineFactory.parse(commandArgs);
        CommandLineWrapper wrapper = new NormalCommandLineWrapper(commandLine);

        // help
        if (wrapper.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("<command> [options]", options);
            return Optional.empty();
        }

        return parse(wrapper);
    }

    abstract protected Optional<T> parse(CommandLineWrapper wrapper) throws FinderParseException;

    protected CommandLineFactory getCommandLineFactory() {
        return commandLineFactory;
    }
}
