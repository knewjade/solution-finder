package entry.verify.kicks;

import entry.CommandLineWrapper;
import entry.common.SettingParser;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import java.util.Optional;

public class VerifyKicksSettingParser extends SettingParser<VerifyKicksSettings> {
    public VerifyKicksSettingParser(Options options, CommandLineParser parser) {
        super(options, parser, "util fumen");
    }

    @Override
    protected Optional<VerifyKicksSettings> parse(CommandLineWrapper wrapper) throws FinderParseException {
        VerifyKicksSettings settings = new VerifyKicksSettings();

        // Load kicks
        Optional<String> kicks = wrapper.getStringOption(VerifyKicksOptions.Kicks.optName());
        kicks.ifPresent(settings::setKicks);

        return Optional.of(settings);
    }
}
