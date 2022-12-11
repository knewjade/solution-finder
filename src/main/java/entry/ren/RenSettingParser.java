package entry.ren;

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
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RenSettingParser extends SettingParser<RenSettings> {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_PATTERNS_TXT = "input/patterns.txt";
    private static final String DEFAULT_FIELD_TXT = "input/field.txt";
    private static final String PATTERN_DELIMITER = ";";

    public RenSettingParser(Options options, CommandLineParser parser) {
        super(options, parser, "ren");
    }

    protected Optional<RenSettings> parse(CommandLineWrapper wrapper) throws FinderParseException {
        RenSettings settings = new RenSettings();

        CommandLineFactory commandLineFactory = this.getCommandLineFactory();
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        FumenLoader fumenLoader = new FumenLoader(commandLineFactory, minoFactory, colorConverter);

        // フィールドの読み込み
        Optional<FieldData> fieldDataOptional = Loader.loadFieldData(
                wrapper,
                fumenLoader,
                RenOptions.Page.optName(),
                RenOptions.Fumen.optName(),
                RenOptions.FieldPath.optName(),
                DEFAULT_FIELD_TXT,
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
                RenOptions.Patterns.optName(),
                PATTERN_DELIMITER,
                RenOptions.PatternsPath.optName(),
                DEFAULT_PATTERNS_TXT,
                Charset.forName(CHARSET_NAME)
        );
        settings.setPatterns(patterns);

        // Load kicks
        Optional<String> kicks = wrapper.getStringOption(RenOptions.Kicks.optName());
        kicks.ifPresent(settings::setKicks);

        // ログファイルの設定
        Optional<String> logFilePath = wrapper.getStringOption(RenOptions.LogPath.optName());
        logFilePath.ifPresent(settings::setLogFilePath);

        // アウトプットファイルの設定
        Optional<String> outputBaseFilePath = wrapper.getStringOption(RenOptions.OutputBase.optName());
        outputBaseFilePath.ifPresent(settings::setOutputBaseFilePath);

        // ホールドの設定
        Optional<Boolean> isUsingHold = wrapper.getBoolOption(RenOptions.Hold.optName());
        isUsingHold.ifPresent(settings::setUsingHold);

        // ドロップの設定
        Optional<String> dropType = wrapper.getStringOption(RenOptions.Drop.optName());
        if (dropType.isPresent()) {
            settings.setDropType(dropType.get());
        }

        return Optional.of(settings);
    }
}
