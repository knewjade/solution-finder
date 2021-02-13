package entry.util.seq;

import entry.CommandLineWrapper;
import entry.common.Loader;
import entry.common.SettingParser;
import entry.path.PathOptions;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

public class SeqUtilSettingParser extends SettingParser<SeqUtilSettings> {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_PATTERNS_TXT = "input/patterns.txt";
    private static final String PATTERN_DELIMITER = ";";

    public SeqUtilSettingParser(Options options, CommandLineParser parser) {
        super(options, parser);
    }

    @Override
    protected Optional<SeqUtilSettings> parse(CommandLineWrapper wrapper) throws FinderParseException {
        SeqUtilSettings settings = new SeqUtilSettings();

        // パターンの読み込み
        List<String> patterns = Loader.loadPatterns(
                wrapper,
                PathOptions.Patterns.optName(),
                PATTERN_DELIMITER,
                PathOptions.PatternsPath.optName(),
                DEFAULT_PATTERNS_TXT,
                Charset.forName(CHARSET_NAME)
        );
        settings.setPatterns(patterns);

        // シーケンス内の最大ミノ数
        Optional<Integer> cuttingSize = wrapper.getIntegerOption(SeqUtilOptions.CuttingSize.optName());
        cuttingSize.ifPresent(settings::setCuttingSize);

        // 重複を取り除く
        Optional<Boolean> distinct = wrapper.getBoolOption(SeqUtilOptions.Distinct.optName());
        distinct.ifPresent(settings::setDistinct);

        // モードの設定
        Optional<String> modeType = wrapper.getStringOption(SeqUtilOptions.Mode.optName());
        try {
            modeType.ifPresent(value -> {
                try {
                    settings.setSeqUtilMode(value);
                } catch (FinderParseException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            throw new FinderParseException(e.getMessage());
        }

        return Optional.of(settings);
    }
}
