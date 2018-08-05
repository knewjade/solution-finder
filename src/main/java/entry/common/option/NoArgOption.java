package entry.common.option;

import org.apache.commons.cli.Option;

public class NoArgOption implements OptionBuilder {
    public static NoArgOption full(String shortName, String longName, String description) {
        return new NoArgOption(shortName, longName, description);
    }

    private final String shortName;
    private final String longName;
    private final String description;

    private NoArgOption(String shortName, String longName, String description) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
    }

    @Override
    public Option toOption() {
        return Option.builder(shortName)
                .optionalArg(true)
                .longOpt(longName)
                .desc(description)
                .build();
    }

    @Override
    public String getLongName() {
        return longName;
    }
}
