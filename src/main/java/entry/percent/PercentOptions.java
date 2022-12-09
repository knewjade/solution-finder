package entry.percent;

import entry.common.option.NoArgOption;
import entry.common.option.OptionBuilder;
import entry.common.option.SingleArgOption;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public enum PercentOptions {
    Help(NoArgOption.full("h", "help", "Usage")),
    Fumen(SingleArgOption.full("t", "tetfu", "v115@~", "Specify tetfu data for s-finder settings")),
    Page(SingleArgOption.full("P", "page", "number", "Specify pages of tetfu data for s-finder settings")),
    Patterns(SingleArgOption.full("p", "patterns", "definition", "Specify pattern definition, directly")),
    PatternsPath(SingleArgOption.full("pp", "patterns-path", "path", "File path of pattern definition")),
    Hold(SingleArgOption.full("H", "hold", "use or avoid", "If use hold, set 'use'. If not use hold, set 'avoid'")),
    Kicks(SingleArgOption.full("K", "kicks", "name", "Specify the kick name or @filename")),
    Drop(SingleArgOption.full("d", "drop", "hard or soft", "Specify drop")),
    ClearLine(SingleArgOption.full("c", "clear-line", "number", "'Specify max clear line")),
    TreeDepth(SingleArgOption.full("td", "tree-depth", "number", "Success tree depth")),
    FailedCount(SingleArgOption.full("fc", "failed-count", "number", "Max count of failed patterns when output")),
    Threads(SingleArgOption.full("th", "threads", "number", "Specify number of used thread")),
    FieldPath(SingleArgOption.full("fp", "field-path", "path", "File path of field definition")),
    LogPath(SingleArgOption.full("lp", "log-path", "path", "File path of output log")),;

    private final OptionBuilder optionBuilder;

    PercentOptions(OptionBuilder optionBuilder) {
        this.optionBuilder = optionBuilder;
    }

    public String optName() {
        return optionBuilder.getLongName();
    }

    public static Options create() {
        Options allOptions = new Options();

        for (PercentOptions options : PercentOptions.values()) {
            Option option = options.optionBuilder.toOption();
            allOptions.addOption(option);
        }

        return allOptions;
    }
}
