package entry.common;

import common.tetfu.common.ColorConverter;
import common.tetfu.field.ColoredFieldView;
import core.mino.MinoFactory;
import entry.CommandLineWrapper;
import entry.common.field.FieldData;
import entry.common.field.FieldTextLoader;
import entry.common.field.FumenLoader;
import exceptions.FinderParseException;

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

public class Loader {
    // フィールドの情報を読み込む
    public static Optional<FieldData> loadFieldData(
            CommandLineWrapper wrapper, CommandLineFactory commandLineFactory, String pageOptName,
            String fumenOptName, String fieldPathOptName, String defaultFieldText, Charset charset
    ) throws FinderParseException {
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();

        FumenLoader fumenLoader = new FumenLoader(commandLineFactory, minoFactory, colorConverter);

        // 指定されたページを抽出
        int page = wrapper.getIntegerOption(pageOptName).orElse(1);

        if (wrapper.hasOption(fumenOptName)) {
            // テト譜から
            Optional<String> tetfuData = wrapper.getStringOption(fumenOptName);
            if (!tetfuData.isPresent())
                throw new FinderParseException("Should specify option value: --" + fumenOptName);

            FieldData fieldData = fumenLoader.load(tetfuData.get(), page);
            return Optional.of(fieldData);
        } else {
            // フィールドファイルから
            Optional<String> fieldPathOption = wrapper.getStringOption(fieldPathOptName);
            String fieldPath = fieldPathOption.orElse(defaultFieldText);
            Path path = Paths.get(fieldPath);

            FieldTextLoader textLoader = new FieldTextLoader(commandLineFactory);

            Stream<String> lines;
            try {
                lines = Files.lines(path, charset);
            } catch (IOException e) {
                throw new FinderParseException("Cannot open field file");
            }

            FieldData fieldData = textLoader.load(lines, fumen -> fumenLoader.load(fumen, page));
            return Optional.of(fieldData);
        }
    }

    public static List<String> loadPatterns(
            CommandLineWrapper wrapper, String patternsOptName, String patternDelimiter, String patternsPathOptName,
            String defaultPatternsText, Charset charset
    ) throws FinderParseException {
        // 探索パターンの設定
        if (wrapper.hasOption(patternsOptName)) {
            // パターン定義から
            Optional<String> patternOption = wrapper.getStringOption(patternsOptName);
            assert patternOption.isPresent();
            String patternValue = patternOption.get();
            return Arrays.stream(patternValue.split(patternDelimiter)).collect(Collectors.toList());
        } else {
            // パターンファイルから
            Optional<String> patternPathOption = wrapper.getStringOption(patternsPathOptName);
            String patternPath = patternPathOption.orElse(defaultPatternsText);
            Path path = Paths.get(patternPath);

            try {
                return Files.lines(path, charset).collect(Collectors.toList());
            } catch (IOException e) {
                throw new FinderParseException("Cannot open patterns file", e);
            }
        }
    }
}
