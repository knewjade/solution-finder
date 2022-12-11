package entry.path;

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

public class PathSettingParser extends SettingParser<PathSettings> {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_PATTERNS_TXT = "input/patterns.txt";
    private static final String DEFAULT_FIELD_TXT = "input/field.txt";
    private static final String PATTERN_DELIMITER = ";";

    public PathSettingParser(Options options, CommandLineParser parser) {
        super(options, parser, "path");
    }

    @Override
    protected Optional<PathSettings> parse(CommandLineWrapper wrapper) throws FinderParseException {
        PathSettings settings = new PathSettings();

        CommandLineFactory commandLineFactory = this.getCommandLineFactory();
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        FumenLoader fumenLoader = new FumenLoader(commandLineFactory, minoFactory, colorConverter);

        // 固定ピースの指定があるか
        Optional<Boolean> reservedOption = wrapper.getBoolOption(PathOptions.Reserved.optName());
        reservedOption.ifPresent(settings::setReserved);

        // フィールドの読み込み
        Optional<FieldData> fieldDataOptional = Loader.loadFieldData(
                wrapper,
                fumenLoader,
                PathOptions.Page.optName(),
                PathOptions.Fumen.optName(),
                PathOptions.FieldPath.optName(),
                DEFAULT_FIELD_TXT,
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
                        CommandLine commandLineTetfu = commandLineFactory.parse(Arrays.asList("--" + PathOptions.ClearLine.optName(), String.valueOf(maxClearLine)));
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

            // 高さの設定
            Optional<Integer> heightOptional = wrapper.getIntegerOption(PathOptions.ClearLine.optName());
            heightOptional.ifPresent(settings::setMaxClearLine);

            // フィールドの設定
            ColoredField coloredField = fieldData.toColoredField();
            if (settings.isReserved()) {
                settings.setFieldWithReserved(coloredField);
            } else {
                settings.setColoredField(coloredField);
            }
        }

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

        // Load kicks
        Optional<String> kicks = wrapper.getStringOption(PathOptions.Kicks.optName());
        kicks.ifPresent(settings::setKicks);

        // ドロップの設定
        Optional<String> dropType = wrapper.getStringOption(PathOptions.Drop.optName());
        if (dropType.isPresent()) {
            settings.setDropType(dropType.get());
        }

        // ホールドの設定
        Optional<Boolean> isUsingHold = wrapper.getBoolOption(PathOptions.Hold.optName());
        isUsingHold.ifPresent(settings::setUsingHold);

        // Minimalの厳密化
        Optional<Boolean> isMinimalSpecifiedOnly = wrapper.getBoolOption(
                PathOptions.MinimalSpecifiedOnly.optName()
        );
        isMinimalSpecifiedOnly.ifPresent(settings::setMinimalSpecifiedOnly);

        // キャッシュ
        Optional<Integer> cachedMinBit = wrapper.getIntegerOption(PathOptions.CachedBit.optName());
        cachedMinBit.ifPresent(settings::setCachedMinBit);

        // ログファイルの設定
        Optional<String> logFilePath = wrapper.getStringOption(PathOptions.LogPath.optName());
        logFilePath.ifPresent(settings::setLogFilePath);

        // 最大レイヤーの設定
        Optional<Integer> maxLayerNumber = wrapper.getIntegerOption(PathOptions.MaxLayer.optName());
        Optional<PathLayer> pathLayer = maxLayerNumber.map(this::getPathLayer);
        pathLayer.ifPresent(settings::setPathLayer);

        // 出力タイプの設定
        Optional<String> outputType = wrapper.getStringOption(PathOptions.Format.optName());
        Optional<String> keyType = wrapper.getStringOption(PathOptions.Key.optName());
        try {
            outputType.ifPresent(type -> {
                String key = keyType.orElse("none");
                try {
                    settings.setOutputType(type, key);
                } catch (FinderParseException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new FinderParseException("Unsupported format: format=" + outputType.orElse("<empty>"));
        }

        // アウトプットファイルの設定
        Optional<String> outputBaseFilePath = wrapper.getStringOption(PathOptions.OutputBase.optName());
        outputBaseFilePath.ifPresent(v -> {
            if (settings.getOutputType().isCSV() && "-".equals(v)) {
                settings.useOutputToConsole();
            } else {
                settings.useOutputToFile(v);
            }
        });

        // 出力分割の設定
        Optional<Boolean> isSplit = wrapper.getBoolOption(PathOptions.Split.optName());
        isSplit.ifPresent(settings::setTetfuSplit);

        // スレッド数の設定
        Optional<Integer> threadCount = wrapper.getIntegerOption(PathOptions.Threads.optName());
        threadCount.ifPresent(settings::setThreadCount);

        return Optional.of(settings);
    }

    private PathLayer getPathLayer(int number) {
        return PathLayer.parse(number);
    }
}
