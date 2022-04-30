package entry.spin;

import common.tetfu.common.ColorConverter;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.mino.MinoFactory;
import entry.CommandLineWrapper;
import entry.PriorityCommandLineWrapper;
import entry.common.CommandLineFactory;
import entry.common.Loader;
import entry.common.SettingParser;
import entry.common.field.FieldData;
import entry.common.field.FumenLoader;
import entry.path.PathOptions;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SpinSettingParser extends SettingParser<SpinSettings> {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_PATTERNS_TXT = "input/patterns.txt";
    private static final String DEFAULT_FIELD_TXT = "input/field.txt";
    private static final String PATTERN_DELIMITER = ";";

    public SpinSettingParser(Options options, CommandLineParser parser) {
        super(options, parser, "spin");
    }

    protected Optional<SpinSettings> parse(CommandLineWrapper wrapper) throws FinderParseException {
        SpinSettings settings = new SpinSettings();

        CommandLineFactory commandLineFactory = this.getCommandLineFactory();
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        FumenLoader fumenLoader = new FumenLoader(commandLineFactory, minoFactory, colorConverter);

        // フィールドの読み込み
        Optional<FieldData> fieldDataOptional = Loader.loadFieldData(
                wrapper,
                fumenLoader,
                SpinOptions.Page.optName(),
                SpinOptions.Fumen.optName(),
                SpinOptions.FieldPath.optName(),
                DEFAULT_FIELD_TXT,
                Charset.forName(CHARSET_NAME),
                Optional::of,
                fieldLines -> {
                    // フィールドの設定
                    String fieldMarks = String.join("", fieldLines);
                    ColoredField coloredField = ColoredFieldFactory.createColoredField(fieldMarks);
                    return Optional.of(new FieldData(coloredField));
                }
        );

        if (fieldDataOptional.isPresent()) {
            FieldData fieldData = fieldDataOptional.get();

            Optional<CommandLineWrapper> commandLineWrapper = fieldData.getCommandLineWrapper();
            if (commandLineWrapper.isPresent()) {
                wrapper = new PriorityCommandLineWrapper(Arrays.asList(wrapper, commandLineWrapper.get()));
            }

            // フィールドの設定
            settings.setField(fieldData.toField(24));
        }

        // パターンの読み込み
        List<String> patterns = Loader.loadPatterns(
                wrapper,
                SpinOptions.Patterns.optName(),
                PATTERN_DELIMITER,
                SpinOptions.PatternsPath.optName(),
                DEFAULT_PATTERNS_TXT,
                Charset.forName(CHARSET_NAME)
        );
        settings.setPatterns(patterns);

        // 埋めることを許可する最も下のy座標(include)
        Optional<Integer> fillBottom = wrapper.getIntegerOption(SpinOptions.FillBottom.optName());
        fillBottom.ifPresent(settings::setFillBottom);

        // 埋めることを許可する最も上のy座標(exclude)
        Optional<Integer> fillTop = wrapper.getIntegerOption(SpinOptions.FillTop.optName());
        fillTop.ifPresent(settings::setFillTop);

        // マージンエリアの高さ
        Optional<Integer> marginHeight = wrapper.getIntegerOption(SpinOptions.MarginHeight.optName());
        marginHeight.ifPresent(settings::setMarginHeight);

        // 消去するライン数
        Optional<Integer> clearLineByT = wrapper.getIntegerOption(SpinOptions.ClearLineByT.optName());
        clearLineByT.ifPresent(settings::setRequiredClearLine);

        // 屋根の探索をスキップ
        Optional<Boolean> searchRoof = wrapper.getBoolOption(SpinOptions.Roof.optName());
        searchRoof.ifPresent(settings::setSearchRoof);

        // 屋根として使える最大のミノ数
        Optional<Integer> maxRoofNum = wrapper.getIntegerOption(SpinOptions.MaxRoof.optName());
        maxRoofNum.ifPresent(settings::setMaxRoofNum);

        // 解を制限するモード
        Optional<String> filter = wrapper.getStringOption(SpinOptions.Filter.optName());
        filter.ifPresent(settings::setFilterMode);

        // 出力フォーマット
        Optional<String> format = wrapper.getStringOption(SpinOptions.Format.optName());
        format.ifPresent(settings::setOutputType);

        // 出力分割の設定
        Optional<Boolean> isSplit = wrapper.getBoolOption(PathOptions.Split.optName());
        isSplit.ifPresent(settings::setTetfuSplit);

        // ログファイルの設定
        Optional<String> logFilePath = wrapper.getStringOption(SpinOptions.LogPath.optName());
        logFilePath.ifPresent(settings::setLogFilePath);

        // アウトプットファイルの設定
        Optional<String> outputBaseFilePath = wrapper.getStringOption(SpinOptions.OutputBase.optName());
        outputBaseFilePath.ifPresent(settings::setOutputBaseFilePath);

        return Optional.of(settings);
    }
}
