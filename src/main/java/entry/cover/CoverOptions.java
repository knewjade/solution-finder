package entry.cover;

import entry.common.option.ListArgOption;
import entry.common.option.NoArgOption;
import entry.common.option.OptionBuilder;
import entry.common.option.SingleArgOption;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public enum CoverOptions {
    Help(NoArgOption.full("h", "help", "Usage")),
    Fumen(ListArgOption.fullSpace("t", "tetfu", "v115@~", "Specify tetfu data for s-finder settings")),
    Patterns(SingleArgOption.full("p", "patterns", "definition", "Specify pattern definition, directly")),
    Hold(SingleArgOption.full("H", "hold", "use or avoid", "If use hold, set 'use'. If not use hold, set 'avoid'")),
    Drop(SingleArgOption.full("d", "drop", "hard or soft", "Specify drop")),
    Mirror(SingleArgOption.full("m", "mirror", "yes or no", "Specify expanding mirror")),
    Mode(SingleArgOption.full("M", "mode", "normal or tspin", "Specify cover mode")),
    Prioritized(SingleArgOption.full("P", "prioritized", "yes or no", "Specify sing prioritized mode")),
    PatternsPath(SingleArgOption.full("pp", "patterns-path", "path", "File path of pattern definition")),
    FieldPath(SingleArgOption.full("fp", "field-path", "path", "File path of field definition")),
    LogPath(SingleArgOption.full("lp", "log-path", "path", "File path of output log")),
    OutputBase(SingleArgOption.full("o", "output-base", "path", "Base file path of result to output")),
    ;

    private final OptionBuilder optionBuilder;

    CoverOptions(OptionBuilder optionBuilder) {
        this.optionBuilder = optionBuilder;
    }

    public String optName() {
        return optionBuilder.getLongName();
    }

    public static Options create() {
        Options allOptions = new Options();

        for (CoverOptions options : CoverOptions.values()) {
            Option option = options.optionBuilder.toOption();
            allOptions.addOption(option);
        }

        return allOptions;
    }
}
