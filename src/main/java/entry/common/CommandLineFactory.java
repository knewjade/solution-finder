package entry.common;

import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandLineFactory {
    private final Options options;
    private final CommandLineParser parser;

    CommandLineFactory(Options options, CommandLineParser parser) {
        this.options = options;
        this.parser = parser;
    }

    public CommandLine parse(List<String> commands) throws FinderParseException {
        String[] commandArray = toArray(commands);
        return parse(commandArray);
    }

    private String[] toArray(List<String> list) {
        String[] array = new String[list.size()];
        list.toArray(array);
        return array;
    }

    public CommandLine parse(String[] commands) throws FinderParseException {
        try {
            return parser.parse(options, commands);
        } catch (Exception e) {
            String commandsStr = Arrays.stream(commands).collect(Collectors.joining(" "));
            throw new FinderParseException(String.format("Cannot parse options: commands='%s'", commandsStr), e);
        }
    }
}
