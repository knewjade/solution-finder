package entry.common.option;

import org.apache.commons.cli.Option;

public class SingleArgOption implements OptionBuilder {
    public static SingleArgOption full(String shortName, String longName, String valueName, String description) {
        return new SingleArgOption(shortName, longName, valueName, description);
    }

    private final String shortName;
    private final String longName;
    private final String valueName;
    private final String description;

    private SingleArgOption(String shortName, String longName, String valueName, String description) {
        this.shortName = shortName;
        this.longName = longName;
        this.valueName = valueName;
        this.description = description;
    }

    @Override
    public Option toOption() {
        return Option.builder(shortName)
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
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
