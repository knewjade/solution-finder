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
    Kicks(SingleArgOption.full("K", "kicks", "name", "Specify the kick name or @filename")),
    Drop(SingleArgOption.full("d", "drop", "hard or soft", "Specify drop")),
    Mirror(SingleArgOption.full("m", "mirror", "yes or no", "Specify expanding mirror")),
    Mode(SingleArgOption.full("M", "mode", "normal or tspin", "Specify cover mode")),
    StartingB2B(SingleArgOption.full("sb", "starting-b2b", "integer", "If mode is tspin*, specify required times B2B continues immediately after the start.")),
    Priority(SingleArgOption.full("P", "priority", "yes or no", "Specify sing prioritized mode")),
    LastSoftdrop(SingleArgOption.full("l", "last-sd", "integer", "Specify the depth to allow softdrop")),
    MaxSoftdropTimes(SingleArgOption.full("ms", "max-softdrop", "integer", "Specify max times to allow softdrop (N-Lines [or PC] mode only)")),
    MaxClearLineTimes(SingleArgOption.full("mc", "max-clearline", "integer", "Specify max times to allow clearline (N-Lines [or PC] mode only)")),
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
