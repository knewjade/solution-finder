package entry.path;

import entry.common.option.NoArgOption;
import entry.common.option.OptionBuilder;
import entry.common.option.SingleArgOption;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public enum PathOptions {
    Help(NoArgOption.full("h", "help", "Usage")),
    Fumen(SingleArgOption.full("t", "tetfu", "v115@~", "Specify tetfu data for s-finder settings")),
    Page(SingleArgOption.full("P", "page", "number", "Specify pages of tetfu data for s-finder settings")),
    Patterns(SingleArgOption.full("p", "patterns", "definition", "Specify pattern definition, directly")),
    PatternsPath(SingleArgOption.full("pp", "patterns-path", "path", "File path of pattern definition")),
    Kicks(SingleArgOption.full("K", "kicks", "name", "Specify the kick name or @filename")),
    Drop(SingleArgOption.full("d", "drop", "hard or soft", "Specify drop")),
    Hold(SingleArgOption.full("H", "hold", "use or avoid", "If use hold, set 'use'. If not use hold, set 'avoid'")),
    MinimalSpecifiedOnly(SingleArgOption.full("so", "specified-only", "yes or no", "Set 'yes' if refine the minimal solutions from the specified patterns, or 'no' from the patterns extended with hold")),
    ClearLine(SingleArgOption.full("c", "clear-line", "number", "'Specify max clear line")),
    MaxLayer(SingleArgOption.full("L", "max-layer", "int", "Specify max layer")),
    Format(SingleArgOption.full("f", "format", "string", "Format type for output")),
    Key(SingleArgOption.full("k", "key", "string", "Format key for output")),
    Split(SingleArgOption.full("s", "split", "boolean", "Split outputted tetfu page")),
    Reserved(SingleArgOption.full("r", "reserved", "boolean", "If reserve block, set 'yes'")),
    FieldPath(SingleArgOption.full("fp", "field-path", "path", "File path of field definition")),
    LogPath(SingleArgOption.full("lp", "log-path", "path", "File path of output log")),
    OutputBase(SingleArgOption.full("o", "output-base", "path", "Base file path of result to output")),
    Threads(SingleArgOption.full("th", "threads", "number", "Specify number of used thread")),
    CachedBit(SingleArgOption.full("cb", "cached-bit", "int", "Minimum bit of cached basic solution used by inner algorithm")),;

    private final OptionBuilder optionBuilder;

    PathOptions(OptionBuilder optionBuilder) {
        this.optionBuilder = optionBuilder;
    }

    public String optName() {
        return optionBuilder.getLongName();
    }

    public static Options create() {
        Options allOptions = new Options();

        for (PathOptions options : PathOptions.values()) {
            Option option = options.optionBuilder.toOption();
            allOptions.addOption(option);
        }

        return allOptions;
    }
}
