package entry.util.seq;

import entry.common.option.ListArgOption;
import entry.common.option.NoArgOption;
import entry.common.option.OptionBuilder;
import entry.common.option.SingleArgOption;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public enum SeqUtilOptions {
    Help(NoArgOption.full("h", "help", "Usage")),
    Patterns(ListArgOption.fullSpace("p", "patterns", "definition", "Specify pattern definition, directly")),
    Mode(SingleArgOption.full("M", "mode", "name", "Specify sequence mode")),
    Length(SingleArgOption.full("l", "length", "length", "Specify max sequence size for output")),
    Distinct(SingleArgOption.full("d", "distinct", "yes or no", "Specify whether or not to remove duplicates")),
    Expression(SingleArgOption.full("e", "expression", "expression", "Specify regular expression to filter sequences after cutting")),
    NotExpression(SingleArgOption.full("ne", "not-expression", "expression", "Specify inverted regular expression to filter sequences after cutting")),
    CountEquations(ListArgOption.fullSpace("n", "num", "equation", "Specify equations with count to filter sequences after cutting")),
    HoldByHead(SingleArgOption.full("hh", "head-hold", "yes or no", "If true, initialize first hold by the head of pattern")),
    ;

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
