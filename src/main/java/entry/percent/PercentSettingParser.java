package entry.percent;

import common.tetfu.field.ColoredFieldView;
import entry.CommandLineWrapper;
import entry.PriorityCommandLineWrapper;
import entry.common.Loader;
import entry.common.SettingParser;
import entry.common.field.FieldData;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PercentSettingParser extends SettingParser<PercentSettings> {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_PATTERNS_TXT = "input/patterns.txt";
    private static final String DEFAULT_FIELD_TXT = "input/field.txt";
    private static final String PATTERN_DELIMITER = ";";

    public PercentSettingParser(Options options, CommandLineParser parser) {
        super(options, parser);
    }

    @Override
    protected Optional<PercentSettings> parse(CommandLineWrapper wrapper) throws FinderParseException {
        PercentSettings settings = new PercentSettings();

        // フィールドの読み込み
        Optional<FieldData> fieldDataOptional = Loader.loadFieldData(
                wrapper,
                this.getCommandLineFactory(),
                PercentOptions.Page.optName(),
                PercentOptions.Fumen.optName(),
                PercentOptions.FieldPath.optName(),
                DEFAULT_FIELD_TXT,
                Charset.forName(CHARSET_NAME)
        );

        if (fieldDataOptional.isPresent()) {
            FieldData fieldData = fieldDataOptional.get();

            Optional<CommandLineWrapper> commandLineWrapper = fieldData.getCommandLineWrapper();
            if (commandLineWrapper.isPresent()) {
                wrapper = new PriorityCommandLineWrapper(Arrays.asList(wrapper, commandLineWrapper.get()));
            }

            // フィールドの設定
            int height = wrapper.getIntegerOption(PercentOptions.ClearLine.optName()).orElse(4);
            settings.setField(fieldData.toColoredField(), height);
            settings.setMaxClearLine(height);
        }

        // パターンの読み込み
        List<String> patterns = Loader.loadPatterns(
                wrapper,
                PercentOptions.Patterns.optName(),
                PATTERN_DELIMITER,
                PercentOptions.PatternsPath.optName(),
                DEFAULT_PATTERNS_TXT,
                Charset.forName(CHARSET_NAME)
        );
        settings.setPatterns(patterns);

        // ドロップの設定
        Optional<String> dropType = wrapper.getStringOption(PercentOptions.Drop.optName());
        try {
            dropType.ifPresent(type -> {
                String key = dropType.orElse("softdrop");
                try {
                    settings.setDropType(key);
                } catch (FinderParseException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new FinderParseException("Unsupported format: format=" + dropType.orElse("<empty>"));
        }

        // パフェ成功確率ツリーの深さの設定
        Optional<Integer> treeDepth = wrapper.getIntegerOption(PercentOptions.TreeDepth.optName());
        treeDepth.ifPresent(settings::setTreeDepth);

        // パフェ失敗パターンの表示個数の設定
        Optional<Integer> failedCount = wrapper.getIntegerOption(PercentOptions.FailedCount.optName());
        failedCount.ifPresent(settings::setFailedCount);

        // ログファイルの設定
        Optional<String> logFilePath = wrapper.getStringOption(PercentOptions.LogPath.optName());
        logFilePath.ifPresent(settings::setLogFilePath);

        // ホールドの設定
        Optional<Boolean> isUsingHold = wrapper.getBoolOption(PercentOptions.Hold.optName());
        isUsingHold.ifPresent(settings::setUsingHold);

        // スレッド数の設定
        Optional<Integer> threadCount = wrapper.getIntegerOption(PercentOptions.Threads.optName());
        threadCount.ifPresent(settings::setThreadCount);

        return Optional.of(settings);
    }
}
