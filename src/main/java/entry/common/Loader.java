package entry.common;

import common.tetfu.Tetfu;
import entry.CommandLineWrapper;
import entry.common.field.FieldData;
import entry.common.field.FumenLoader;
import exceptions.FinderParseException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Loader {
    // フィールドの情報を読み込む
    public static Optional<FieldData> loadFieldData(
            CommandLineWrapper wrapper, FumenLoader fumenLoader,
            String pageOptName, String fumenOptName, String fieldPathOptName,
            String defaultFieldText, Charset charset,
            FunctionParseException<FieldData, Optional<FieldData>> callbackWithFumen,
            FunctionParseException<LinkedList<String>, Optional<FieldData>> callbackWithText
    ) throws FinderParseException {
        // 指定されたページを抽出
        int page = wrapper.getIntegerOption(pageOptName).orElse(1);

        if (wrapper.hasOption(fumenOptName)) {
            // テト譜から
            Optional<String> tetfuData = wrapper.getStringOption(fumenOptName);
            if (!tetfuData.isPresent())
                throw new FinderParseException("Should specify option value: --" + fumenOptName);

            FieldData fieldData = fumenLoader.load(tetfuData.get(), page);

            return callbackWithFumen.apply(fieldData);
        } else {
            // フィールドファイルから
            Optional<String> fieldPathOption = wrapper.getStringOption(fieldPathOptName);
            String fieldPath = fieldPathOption.orElse(defaultFieldText);
            Path path = Paths.get(fieldPath);

            Stream<String> lines;
            try {
                lines = Files.lines(path, charset);
            } catch (IOException e) {
                throw new FinderParseException("Cannot open field file");
            }

            LinkedList<String> fieldLines = lines
                    .map(str -> {
                        if (str.contains("#"))
                            return str.substring(0, str.indexOf('#'));
                        return str;
                    })
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(LinkedList::new));

            if (fieldLines.isEmpty())
                throw new FinderParseException("Should specify clear-line & field-definition in field file");

            String removeDomainData = Tetfu.removeDomainData(fieldLines.get(0));
            if (Tetfu.isDataLater115(removeDomainData)) {
                // テト譜から
                FieldData fieldData = fumenLoader.load(removeDomainData, page);
                return callbackWithFumen.apply(fieldData);
            } else {
                return callbackWithText.apply(fieldLines);
            }
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
