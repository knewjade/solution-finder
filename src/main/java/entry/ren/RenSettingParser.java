package entry.ren;

import common.tetfu.common.ColorConverter;
import core.mino.MinoFactory;
import entry.CommandLineWrapper;
import entry.PriorityCommandLineWrapper;
import entry.common.CommandLineFactory;
import entry.common.SettingParser;
import entry.common.field.FieldData;
import entry.common.field.FieldTextLoader;
import entry.common.field.FumenLoader;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RenSettingParser extends SettingParser<RenSettings> {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_PATTERNS_TXT = "input/patterns.txt";
    private static final String DEFAULT_FIELD_TXT = "input/field.txt";
    private static final String PATTERN_DELIMITER = ";";

    public RenSettingParser(Options options, CommandLineParser parser) {
        super(options, parser);
    }

    protected Optional<RenSettings> parse(CommandLineWrapper wrapper) throws FinderParseException {
        RenSettings settings = new RenSettings();

        // フィールドの読み込み
        Optional<FieldData> fieldDataOptional = loadField(wrapper);
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
        List<String> patterns = loadPatterns(wrapper);
        settings.setPatterns(patterns);

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

        return Optional.of(settings);
    }

    // フィールドの情報を読み込む
    private Optional<FieldData> loadField(CommandLineWrapper wrapper) throws FinderParseException {
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();

        CommandLineFactory commandLineFactory = this.getCommandLineFactory();
        FumenLoader fumenLoader = new FumenLoader(commandLineFactory, minoFactory, colorConverter);

        // 指定されたページを抽出
        int page = wrapper.getIntegerOption(RenOptions.Page.optName()).orElse(1);

        if (wrapper.hasOption(RenOptions.Fumen.optName())) {
            // テト譜から
            Optional<String> tetfuData = wrapper.getStringOption(RenOptions.Fumen.optName());
            if (!tetfuData.isPresent())
                throw new FinderParseException("Should specify option value: --" + RenOptions.Fumen.optName());

            FieldData fieldData = fumenLoader.load(tetfuData.get(), page);
            return Optional.of(fieldData);
        } else if (wrapper.hasOption(RenOptions.FieldPath.optName())) {
            // フィールドファイルから
            Optional<String> fieldPathOption = wrapper.getStringOption(RenOptions.FieldPath.optName());
            String fieldPath = fieldPathOption.orElse(DEFAULT_FIELD_TXT);
            Path path = Paths.get(fieldPath);
            Charset charset = Charset.forName(CHARSET_NAME);

            FieldTextLoader textLoader = new FieldTextLoader(commandLineFactory);

            try {
                Stream<String> lines = Files.lines(path, charset);
                FieldData fieldData = textLoader.load(lines, fumen -> fumenLoader.load(fumen, page));
                return Optional.of(fieldData);
            } catch (IOException e) {
                throw new FinderParseException(e);
            }
        }

        return Optional.empty();
    }

    private List<String> loadPatterns(CommandLineWrapper wrapper) throws FinderParseException {
        // 探索パターンの設定
        if (wrapper.hasOption(RenOptions.Patterns.optName())) {
            // パターン定義から
            Optional<String> patternOption = wrapper.getStringOption(RenOptions.Patterns.optName());
            assert patternOption.isPresent();
            String patternValue = patternOption.get();
            return Arrays.stream(patternValue.split(PATTERN_DELIMITER)).collect(Collectors.toList());
        } else {
            // パターンファイルから
            Optional<String> patternPathOption = wrapper.getStringOption(RenOptions.PatternsPath.optName());
            String patternPath = patternPathOption.orElse(DEFAULT_PATTERNS_TXT);
            Path path = Paths.get(patternPath);
            Charset charset = Charset.forName(CHARSET_NAME);

            try {
                return Files.lines(path, charset).collect(Collectors.toList());
            } catch (IOException e) {
                throw new FinderParseException("Cannot open patterns file", e);
            }
        }
    }
}
