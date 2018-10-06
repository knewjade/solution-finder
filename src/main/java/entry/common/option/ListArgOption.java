package entry.common.option;

import org.apache.commons.cli.Option;

public class ListArgOption implements OptionBuilder {
    public static ListArgOption fullSpace(String shortName, String longName, String valueName, String description) {
        return new ListArgOption(shortName, longName, valueName, description, Integer.MAX_VALUE, ' ');
    }

    private final String shortName;
    private final String longName;
    private final String valueName;
    private final String description;
    private final int numOfArgs;
    private final char separator;

    private ListArgOption(String shortName, String longName, String valueName, String description, int numOfArgs, char separator) {
        this.shortName = shortName;
        this.longName = longName;
        this.valueName = valueName;
        this.description = description;
        this.numOfArgs = numOfArgs;
        this.separator = separator;
    }

    @Override
    public Option toOption() {
        return Option.builder(shortName)
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(numOfArgs)
                .valueSeparator(separator)
                .argName(valueName)
                .longOpt(longName)
                .desc(description)
                .build();
    }

    @Override
    public String getLongName() {
        return longName;
    }
}
