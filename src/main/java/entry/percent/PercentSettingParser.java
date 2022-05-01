package entry.percent;

import common.tetfu.common.ColorConverter;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.mino.MinoFactory;
import entry.CommandLineWrapper;
import entry.NormalCommandLineWrapper;
import entry.PriorityCommandLineWrapper;
import entry.common.CommandLineFactory;
import entry.common.Loader;
import entry.common.SettingParser;
import entry.common.field.FieldData;
import entry.common.field.FumenLoader;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLine;
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
        super(options, parser, "percent");
    }

    @Override
    protected Optional<PercentSettings> parse(CommandLineWrapper wrapper) throws FinderParseException {
        PercentSettings settings = new PercentSettings();

        CommandLineFactory commandLineFactory = this.getCommandLineFactory();
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        FumenLoader fumenLoader = new FumenLoader(commandLineFactory, minoFactory, colorConverter);

        // フィールドの読み込み
        Optional<FieldData> fieldDataOptional = Loader.loadFieldData(
                wrapper,
                fumenLoader,
                PercentOptions.Page.optName(),
                PercentOptions.Fumen.optName(),
                PercentOptions.FieldPath.optName(),
                DEFAULT_FIELD_TXT,
                Charset.forName(CHARSET_NAME),
                Optional::of,
                fieldLines -> {
                    try {
                        // 最大削除ラインの設定
                        String firstLine = fieldLines.pollFirst();
                        int maxClearLine = Integer.parseInt(firstLine != null ? firstLine : "error");

                        // フィールドの設定
                        String fieldMarks = String.join("", fieldLines);
                        ColoredField coloredField = ColoredFieldFactory.createColoredField(fieldMarks);

                        // 最大削除ラインをコマンドラインのオプションに設定
                        CommandLine commandLineTetfu = commandLineFactory.parse(Arrays.asList("--" + PercentOptions.ClearLine.optName(), String.valueOf(maxClearLine)));
                        CommandLineWrapper newWrapper = new NormalCommandLineWrapper(commandLineTetfu);
                        return Optional.of(new FieldData(coloredField, newWrapper));
                    } catch (NumberFormatException e) {
                        throw new FinderParseException("Cannot read clear-line from field file");
                    }
                }
        );

        if (fieldDataOptional.isPresent()) {
            FieldData fieldData = fieldDataOptional.get();

            Optional<CommandLineWrapper> commandLineWrapper = fieldData.getCommandLineWrapper();
            if (commandLineWrapper.isPresent()) {
                wrapper = new PriorityCommandLineWrapper(Arrays.asList(wrapper, commandLineWrapper.get()));
            }

            // フィールドの設定
            Optional<Integer> heightOptinal = wrapper.getIntegerOption(PercentOptions.ClearLine.optName());
            if (heightOptinal.isPresent()) {
                int height = heightOptinal.get();
                settings.setField(fieldData.toColoredField(), height);
                settings.setMaxClearLine(height);
            } else {
                settings.setField(fieldData.toColoredField());
            }
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
        if (dropType.isPresent()) {
            settings.setDropType(dropType.get());
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
