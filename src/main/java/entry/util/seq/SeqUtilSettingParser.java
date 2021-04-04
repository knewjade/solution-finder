package entry.util.seq;

import entry.CommandLineWrapper;
import entry.common.Loader;
import entry.common.SettingParser;
import entry.path.PathOptions;
import entry.util.seq.equations.EquationInterpreter;
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
        Optional<Integer> length = wrapper.getIntegerOption(SeqUtilOptions.Length.optName());
        length.ifPresent(settings::setLength);

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

        // ミノのフィルタリングの指定
        Optional<String> expression = wrapper.getStringOption(SeqUtilOptions.Expression.optName());
        expression.ifPresent(settings::setExpression);

        Optional<String> notExpression = wrapper.getStringOption(SeqUtilOptions.NotExpression.optName());
        notExpression.ifPresent(settings::setNotExpression);

        // カウントの条件の指定
        List<String> countEquations = wrapper.getStringOptions(SeqUtilOptions.CountEquations.optName());
        EquationInterpreter interpreter = EquationInterpreter.parse(countEquations);
        interpreter.getHoldEquation().ifPresent(settings::setHoldEquation);
        settings.setPieceEquations(interpreter.getPieceEquation());

        // ホールドが空でスタートするか (最初に指定されたミノをホールドとして扱うか)
        Optional<Boolean> holdByHead = wrapper.getBoolOption(SeqUtilOptions.HoldByHead.optName());
        holdByHead.ifPresent(it -> settings.setStartsWithoutHold(!it));

        return Optional.of(settings);
    }
}
