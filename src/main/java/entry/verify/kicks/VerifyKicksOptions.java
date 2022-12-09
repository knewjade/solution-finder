package entry.verify.kicks;

import entry.common.option.NoArgOption;
import entry.common.option.OptionBuilder;
import entry.common.option.SingleArgOption;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public enum VerifyKicksOptions {
    Help(NoArgOption.full("h", "help", "Usage")),
    Kicks(SingleArgOption.full("K", "kicks", "name", "Specify the kick name or @filename")),
    ;

    private final OptionBuilder optionBuilder;

    VerifyKicksOptions(OptionBuilder optionBuilder) {
        this.optionBuilder = optionBuilder;
    }

    public String optName() {
        return optionBuilder.getLongName();
    }

    public static Options create() {
        Options allOptions = new Options();

        for (VerifyKicksOptions options : VerifyKicksOptions.values()) {
            Option option = options.optionBuilder.toOption();
            allOptions.addOption(option);
        }

        return allOptions;
    }
}
