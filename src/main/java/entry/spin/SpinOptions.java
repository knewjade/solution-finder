package entry.spin;

import entry.common.option.NoArgOption;
import entry.common.option.OptionBuilder;
import entry.common.option.SingleArgOption;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public enum SpinOptions {
    Help(NoArgOption.full("h", "help", "Usage")),
    Fumen(SingleArgOption.full("t", "tetfu", "v115@~", "Specify tetfu data for s-finder settings")),
    Page(SingleArgOption.full("P", "page", "number", "Specify pages of tetfu data for s-finder settings")),
    Patterns(SingleArgOption.full("p", "patterns", "definition", "Specify pattern definition, directly")),
    FieldPath(SingleArgOption.full("fp", "field-path", "path", "File path of field definition")),
    PatternsPath(SingleArgOption.full("pp", "patterns-path", "path", "File path of pattern definition")),
    FillBottom(SingleArgOption.full("fb", "fill-bottom", "number", "Specify bottom y-index(include) that allow to fill")),
    FillTop(SingleArgOption.full("ft", "fill-top", "number", "Specify top y-index(exclude) that allow to fill")),
    MarginHeight(SingleArgOption.full("m", "margin-height", "number", "Specify max margin height")),
    // FieldHeight(SingleArgOption.full("l", "field-height", "number", "'Specify max height")),
    ClearLineByT(SingleArgOption.full("c", "line", "number", "Specify number of required clear line by T-piece")),
    Roof(SingleArgOption.full("r", "roof", "boolean", "If this option is true, search for roof")),
    MaxRoof(SingleArgOption.full("mr", "max-roof", "number", "Specify the maximum number of pieces that can be used as a roof")),
    Filter(SingleArgOption.full("f", "filter", "mode", "Specify filtering mode")),
    Format(SingleArgOption.full("fo", "format", "string", "Result format type for output")),
    Split(SingleArgOption.full("s", "split", "boolean", "Split outputted tetfu page")),
    LogPath(SingleArgOption.full("lp", "log-path", "path", "File path of output log")),
    OutputBase(SingleArgOption.full("o", "output-base", "path", "Base file path of result to output")),
    ;

    private final OptionBuilder optionBuilder;

    SpinOptions(OptionBuilder optionBuilder) {
        this.optionBuilder = optionBuilder;
    }

    public String optName() {
        return optionBuilder.getLongName();
    }

    public static Options create() {
        Options allOptions = new Options();

        for (SpinOptions options : SpinOptions.values()) {
            Option option = options.optionBuilder.toOption();
            allOptions.addOption(option);
        }

        return allOptions;
    }
}
