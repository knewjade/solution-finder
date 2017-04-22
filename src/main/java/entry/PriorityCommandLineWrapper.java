package entry;

import org.apache.commons.cli.CommandLine;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PriorityCommandLineWrapper implements CommandLineWrapper {
    private final List<NormalCommandLineWrapper> commandLines;

    public PriorityCommandLineWrapper(List<CommandLine> commandLines) {
        this.commandLines = commandLines.stream().map(NormalCommandLineWrapper::new).collect(Collectors.toList());
    }

    @Override
    public boolean hasOption(String name) {
        for (NormalCommandLineWrapper commandLine : commandLines)
            if (commandLine.hasOption(name))
                return true;
        return false;
    }

    @Override
    public Optional<Boolean> getBoolOption(String name) {
        for (NormalCommandLineWrapper commandLine : commandLines) {
            Optional<Boolean> option = commandLine.getBoolOption(name);
            if (option.isPresent())
                return option;
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getStringOption(String name) {
        for (NormalCommandLineWrapper commandLine : commandLines) {
            Optional<String> option = commandLine.getStringOption(name);
            if (option.isPresent())
                return option;
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getIntegerOption(String name) {
        for (NormalCommandLineWrapper commandLine : commandLines) {
            Optional<Integer> option = commandLine.getIntegerOption(name);
            if (option.isPresent())
                return option;
        }
        return Optional.empty();
    }
}
