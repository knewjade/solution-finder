package entry.setup;

import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import entry.CommandLineWrapper;
import entry.NormalCommandLineWrapper;
import entry.PriorityCommandLineWrapper;
import entry.common.CommandLineFactory;
import entry.common.Loader;
import entry.common.SettingParser;
import entry.common.field.FieldData;
import entry.common.field.FumenLoader;
import entry.path.PathOptions;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SetupSettingParser extends SettingParser<SetupSettings> {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_PATTERNS_TXT = "input/patterns.txt";
    private static final String DEFAULT_FIELD_TXT = "input/field.txt";
    private static final String PATTERN_DELIMITER = ";";

    public SetupSettingParser(Options options, CommandLineParser parser) {
        super(options, parser, "setup");
    }

    @Override
    protected Optional<SetupSettings> parse(CommandLineWrapper wrapper) throws FinderParseException {
        SetupSettings settings = new SetupSettings();

        CommandLineFactory commandLineFactory = this.getCommandLineFactory();
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        FumenLoader fumenLoader = new FumenLoader(commandLineFactory, minoFactory, colorConverter);

        // フィールドの読み込み
        final CommandLineWrapper initWrapper = wrapper;
        Optional<FieldData> fieldDataOptional = Loader.loadFieldData(
                initWrapper,
                fumenLoader,
                SetupOptions.Page.optName(),
                SetupOptions.Fumen.optName(),
                SetupOptions.FieldPath.optName(),
                DEFAULT_FIELD_TXT,
                fieldData -> {
                    ColoredField coloredField = fieldData.toColoredField();

                    // 高さの設定
                    Optional<Integer> heightOptional = initWrapper.getIntegerOption(SetupOptions.Line.optName());
                    int maxHeight = heightOptional.orElse(coloredField.getUsingHeight());

                    // フィールドの設定
                    loadTetfu(coloredField, initWrapper, maxHeight, settings);

                    // 最大削除ラインをコマンドラインのオプションに設定
                    CommandLine commandLineTetfu = commandLineFactory.parse(Arrays.asList("--" + SetupOptions.Line.optName(), String.valueOf(maxHeight)));
                    CommandLineWrapper newWrapper = new NormalCommandLineWrapper(commandLineTetfu);

                    Optional<CommandLineWrapper> FieldLineWrapper = fieldData.getCommandLineWrapper();
                    if (FieldLineWrapper.isPresent()) {
                        newWrapper = new PriorityCommandLineWrapper(Arrays.asList(FieldLineWrapper.get(), newWrapper));
                    }

                    return Optional.of(new FieldData(coloredField, newWrapper));
                },
                fieldLines -> {
                    // 最大削除ラインの設定
                    Integer maxHeightForce = null;
                    try {
                        assert fieldLines.peekFirst() != null;
                        maxHeightForce = Integer.valueOf(fieldLines.peekFirst());
                        fieldLines.pollFirst();  // 読み込みに成功したときだけ進める
                    } catch (Exception ignore) {
                    }

                    // フィールドの設定
                    String fieldMarks = String.join("", fieldLines);

                    ColoredField coloredField = ColoredFieldFactory.createColoredField(fieldMarks);
                    int maxHeight = maxHeightForce != null ? maxHeightForce : coloredField.getUsingHeight();
                    loadTetfu(fieldMarks, maxHeight, settings);

                    // 最大削除ラインをコマンドラインのオプションに設定
                    CommandLine commandLineTetfu = commandLineFactory.parse(Arrays.asList("--" + SetupOptions.Line.optName(), String.valueOf(maxHeight)));
                    CommandLineWrapper newWrapper = new NormalCommandLineWrapper(commandLineTetfu);
                    return Optional.of(new FieldData(coloredField, newWrapper));
                }
        );

        if (fieldDataOptional.isPresent()) {
            FieldData fieldData = fieldDataOptional.get();

            Optional<CommandLineWrapper> commandLineWrapper = fieldData.getCommandLineWrapper();
            if (commandLineWrapper.isPresent()) {
                wrapper = new PriorityCommandLineWrapper(Arrays.asList(initWrapper, commandLineWrapper.get()));
            }
        }

        // パターンの読み込み
        List<String> patterns = Loader.loadPatterns(
                wrapper,
                SetupOptions.Patterns.optName(),
                PATTERN_DELIMITER,
                SetupOptions.PatternsPath.optName(),
                DEFAULT_PATTERNS_TXT,
                Charset.forName(CHARSET_NAME)
        );
        settings.setPatterns(patterns);

        // Load kicks
        Optional<String> kicks = wrapper.getStringOption(SetupOptions.Kicks.optName());
        kicks.ifPresent(settings::setKicks);

        // ドロップの設定
        Optional<String> dropType = wrapper.getStringOption(SetupOptions.Drop.optName());
        if (dropType.isPresent()) {
            settings.setDropType(dropType.get());
        }

        // ホールドの設定
        Optional<Boolean> isUsingHold = wrapper.getBoolOption(SetupOptions.Hold.optName());
        isUsingHold.ifPresent(settings::setUsingHold);

        // ミノの組み合わせ
        Optional<Boolean> combination = wrapper.getBoolOption(SetupOptions.Combination.optName());
        combination.ifPresent(settings::setCombination);

        // 除外の設定
        Optional<String> excludeType = wrapper.getStringOption(SetupOptions.Exclude.optName());
        try {
            excludeType.ifPresent(type -> {
                String key = excludeType.orElse("none");
                try {
                    settings.setExcludeType(key);
                } catch (FinderParseException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new FinderParseException("Unsupported format: exclude=" + excludeType.orElse("<empty>"));
        }

        // 除外前に加えるミノ
        List<String> addOperations = wrapper.getStringOptions(SetupOptions.Operations.optName());
        settings.setAddOperations(addOperations);

        // 必ず使用するミノの数
        Optional<Integer> numOfPieces = wrapper.getIntegerOption(SetupOptions.NPieces.optName());
        numOfPieces.ifPresent(settings::setNumOfPieces);

        // 出力分割の設定
        Optional<Boolean> isSplit = wrapper.getBoolOption(PathOptions.Split.optName());
        isSplit.ifPresent(settings::setTetfuSplit);

        // ログファイルの設定
        Optional<String> logFilePath = wrapper.getStringOption(SetupOptions.LogPath.optName());
        logFilePath.ifPresent(settings::setLogFilePath);

        // 出力タイプの設定
        Optional<String> outputType = wrapper.getStringOption(SetupOptions.Format.optName());
        try {
            outputType.ifPresent(type -> {
                try {
                    settings.setOutputType(type);
                } catch (FinderParseException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new FinderParseException("Unsupported format: format=" + outputType.orElse("<empty>"));
        }

        // アウトプットファイルの設定
        Optional<String> outputBaseFilePath = wrapper.getStringOption(SetupOptions.OutputBase.optName());
        outputBaseFilePath.ifPresent(v -> {
            if (settings.getOutputType().isCSV() && "-".equals(v)) {
                settings.useOutputToConsole();
            } else {
                settings.useOutputToFile(v);
            }
        });

        return Optional.of(settings);
    }

    private void loadTetfu(String fieldMarks, int maxHeight, SetupSettings settings) {
        // Load init field
        String initFieldMarks = fieldMarks
                .replace(".", "_")
                .replace("+", "_")
                .replace("*", "_");
        Field initField = FieldFactory.createField(initFieldMarks);
        for (int y = maxHeight; y < initField.getMaxFieldHeight(); y++)
            for (int x = 0; x < 10; x++)
                initField.removeBlock(x, y);

        // Load free field
        String freeFieldMarks = filterString(fieldMarks, '+', '_');
        Field freeField = FieldFactory.createField(freeFieldMarks);
        for (int y = maxHeight; y < freeField.getMaxFieldHeight(); y++)
            for (int x = 0; x < 10; x++)
                freeField.removeBlock(x, y);

        // Load need filled field
        String needFilledFieldMarks = filterString(fieldMarks, '*', '_');
        Field needFilledField = FieldFactory.createField(needFilledFieldMarks);
        for (int y = maxHeight; y < needFilledField.getMaxFieldHeight(); y++)
            for (int x = 0; x < 10; x++)
                needFilledField.removeBlock(x, y);

        // Load not filled field
        Field notFilledField = FieldFactory.createInverseField(fieldMarks.replace("X", "_"));
        for (int y = maxHeight; y < notFilledField.getMaxFieldHeight(); y++)
            for (int x = 0; x < 10; x++)
                notFilledField.removeBlock(x, y);

        settings.setField(initField, needFilledField, notFilledField, freeField, maxHeight);
    }

    private void loadTetfu(ColoredField coloredField, CommandLineWrapper wrapper, int maxHeight, SetupSettings settings) throws FinderParseException {
        // 固定色の指定があるか
        Optional<String> fillColorOption = wrapper.getStringOption(SetupOptions.Fill.optName());
        if (fillColorOption.isPresent()) {
            settings.setFillColorType(fillColorOption.get());
        }

        // マージン色（穴あり）の指定があるか
        Optional<String> freeColorOption = wrapper.getStringOption(SetupOptions.Free.optName());
        if (freeColorOption.isPresent()) {
            settings.setFreeColorType(freeColorOption.get());
        }

        // マージン色（穴なし）の指定があるか
        Optional<String> marginColorOption = wrapper.getStringOption(SetupOptions.Margin.optName());
        if (marginColorOption.isPresent()) {
            settings.setMarginColorType(marginColorOption.get());
        }

        // フィールドの設定
        Field initField = FieldFactory.createField(maxHeight);
        Field needFilledField = FieldFactory.createField(maxHeight);
        Field notFilledField = FieldFactory.createField(maxHeight);
        Field freeField = FieldFactory.createField(maxHeight);

        List<ColorType> marginColorType = settings.getMarginColorType();
        List<ColorType> fillColorType = settings.getFillColorType();
        List<ColorType> freeColorType = settings.getFreeColorType();

        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < 10; x++) {
                ColorType colorType = coloredField.getColorType(x, y);

                if (freeColorType.contains(colorType)) {
                    freeField.setBlock(x, y);
                } else if (fillColorType.contains(colorType)) {
                    needFilledField.setBlock(x, y);
                } else if (marginColorType.contains(colorType)) {
                    // skip
                } else {
                    switch (colorType) {
                        case Empty:
                            notFilledField.setBlock(x, y);
                            break;
                        case Gray:
                        default:
                            initField.setBlock(x, y);
                            notFilledField.setBlock(x, y);
                            break;
                    }
                }
            }
        }

        settings.setField(initField, needFilledField, notFilledField, freeField, maxHeight);
    }

    private String filterString(String str, char allow, char notAllowTo) {
        char[] chars = str.toCharArray();
        for (int index = 0; index < chars.length; index++)
            if (chars[index] != allow)
                chars[index] = notAllowTo;
        return String.valueOf(chars);
    }
}
