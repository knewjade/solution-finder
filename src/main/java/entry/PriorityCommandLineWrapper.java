package entry;

import exceptions.FinderParseException;

import java.util.List;
import java.util.Optional;

public class PriorityCommandLineWrapper implements CommandLineWrapper {
    private final List<CommandLineWrapper> commandLines;

//    public PriorityCommandLineWrapper(List<CommandLine> commandLines) {
//        this(commandLines.stream().map(NormalCommandLineWrapper::new).collect(Collectors.toList()));
//    }

    public PriorityCommandLineWrapper(List<CommandLineWrapper> commandLines) {
        this.commandLines = commandLines;
    }

    @Override
    public boolean hasOption(String name) {
        for (CommandLineWrapper commandLine : commandLines)
            if (commandLine.hasOption(name))
                return true;
        return false;
    }

    @Override
    public Optional<Boolean> getBoolOption(String name) throws FinderParseException {
        for (CommandLineWrapper commandLine : commandLines) {
            Optional<Boolean> option = commandLine.getBoolOption(name);
            if (option.isPresent())
                return option;
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getStringOption(String name) {
        for (CommandLineWrapper commandLine : commandLines) {
            Optional<String> option = commandLine.getStringOption(name);
            if (option.isPresent())
                return option;
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getIntegerOption(String name) throws FinderParseException {
        for (CommandLineWrapper commandLine : commandLines) {
            Optional<Integer> option = commandLine.getIntegerOption(name);
            if (option.isPresent())
                return option;
        }
        return Optional.empty();
    }
}
