package entry.util.fumen;

import entry.common.option.ListArgOption;
import entry.common.option.NoArgOption;
import entry.common.option.OptionBuilder;
import entry.common.option.SingleArgOption;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public enum FumenUtilOptions {
    Help(NoArgOption.full("h", "help", "Usage")),
    Fumen(ListArgOption.fullSpace("t", "tetfu", "v115@~", "Specify tetfu data for s-finder settings")),
    Mode(SingleArgOption.full("M", "mode", "normal or tspin", "Specify cover mode")),
    FieldPath(SingleArgOption.full("fp", "field-path", "path", "File path of field definition")),
    LogPath(SingleArgOption.full("lp", "log-path", "path", "File path of output log")),
    OutputBase(SingleArgOption.full("o", "output-base", "path", "Base file path of result to output")),
    ;

    private final OptionBuilder optionBuilder;

    FumenUtilOptions(OptionBuilder optionBuilder) {
        this.optionBuilder = optionBuilder;
    }

    public String optName() {
        return optionBuilder.getLongName();
    }

    public static Options create() {
        Options allOptions = new Options();

        for (FumenUtilOptions options : FumenUtilOptions.values()) {
            Option option = options.optionBuilder.toOption();
            allOptions.addOption(option);
        }

        return allOptions;
    }
}
