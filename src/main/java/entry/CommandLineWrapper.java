package entry;

import org.apache.commons.cli.CommandLine;

import java.util.Optional;

public class CommandLineWrapper {
    private final CommandLine commandLine;

    public CommandLineWrapper(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    public boolean hasOption(String name) {
        return commandLine.hasOption(name);
    }

    public Optional<Boolean> getBoolOption(String name) {
        String value = commandLine.getOptionValue(name);

        if (value == null)
            return Optional.empty();

        switch (value.toLowerCase()) {
            case "true":
                return Optional.of(true);
            case "false":
                return Optional.of(false);
            case "t":
                return Optional.of(true);
            case "f":
                return Optional.of(false);
            case "y":
                return Optional.of(true);
            case "n":
                return Optional.of(false);
            case "ok":
                return Optional.of(true);
            case "no":
                return Optional.of(false);
            case "ng":
                return Optional.of(false);
            case "on":
                return Optional.of(true);
            case "off":
                return Optional.of(false);
            case "use":
                return Optional.of(true);
            case "avoid":
                return Optional.of(false);
        }
        throw new IllegalArgumentException(String.format("Option[%s=%s]: cannot understand", name, value));
    }

    public Optional<String> getStringOption(String name) {
        return Optional.ofNullable(commandLine.getOptionValue(name));
    }

    public Optional<Integer> getIntegerOption(String name) {
        String value = commandLine.getOptionValue(name);

        if (value == null)
            return Optional.empty();

        return Optional.of(Integer.valueOf(value));
    }
}
