package entry.util.seq;

import entry.common.option.ListArgOption;
import entry.common.option.NoArgOption;
import entry.common.option.OptionBuilder;
import entry.common.option.SingleArgOption;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public enum SeqUtilOptions {
    Help(NoArgOption.full("h", "help", "Usage")),
    Fumen(ListArgOption.fullSpace("t", "tetfu", "v115@~", "Specify tetfu data for s-finder settings")),
    Page(SingleArgOption.full("P", "page", "number", "Specify pages of tetfu data for s-finder settings")),
    StartPage(SingleArgOption.full("s", "start", "number", "Specify start page of the fumen")),
    EndPage(SingleArgOption.full("e", "end", "number", "Specify end page of the fumen")),
    Patterns(SingleArgOption.full("p", "patterns", "definition", "Specify pattern definition, directly")),
//    PatternsPath(SingleArgOption.full("pp", "patterns-path", "path", "File path of pattern definition")),
//    Line(SingleArgOption.full("l", "line", "number", "'Specify max height")),
//    Combination(SingleArgOption.full("c", "combination", "boolean", "If yes, `patterns` is interpreted as combinations of piece")),
//    Hold(SingleArgOption.full("H", "hold", "use or avoid", "If use hold, set 'use'. If not use hold, set 'avoid'")),
//    Margin(SingleArgOption.full("m", "margin", "color", "Specify piece color as `margin`")),
//    Free(SingleArgOption.full("F", "free", "color", "Specify piece color as `free`")),
//    Fill(SingleArgOption.full("f", "fill", "color", "Specify piece color as `fill`")),
    Drop(SingleArgOption.full("d", "drop", "hard or soft", "Specify drop")),
//    Exclude(SingleArgOption.full("e", "exclude", "string", "If specify, exclude some solutions")),
//    Operations(ListArgOption.fullSpace("op", "operate", "operation-list", "Operate field before determining to exclude solutions")),
//    NPieces(SingleArgOption.full("np", "n-pieces", "number", "If specify N, must use N pieces")),
//    Format(SingleArgOption.full("rf", "format", "string", "Result format type for output")),
    FieldPath(SingleArgOption.full("fp", "field-path", "path", "File path of field definition")),
    LogPath(SingleArgOption.full("lp", "log-path", "path", "File path of output log")),
    OutputBase(SingleArgOption.full("o", "output-base", "path", "Base file path of result to output")),;

    private final OptionBuilder optionBuilder;

    SeqUtilOptions(OptionBuilder optionBuilder) {
        this.optionBuilder = optionBuilder;
    }

    public String optName() {
        return optionBuilder.getLongName();
    }

    public static Options create() {
        Options allOptions = new Options();

        for (SeqUtilOptions options : SeqUtilOptions.values()) {
            Option option = options.optionBuilder.toOption();
            allOptions.addOption(option);
        }

        return allOptions;
    }
}
