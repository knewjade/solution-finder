package entry.common.option;

import org.apache.commons.cli.Option;

public interface OptionBuilder {
    Option toOption();

    String getLongName();
}
