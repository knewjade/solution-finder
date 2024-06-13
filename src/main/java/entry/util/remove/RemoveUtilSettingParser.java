package entry.util.remove;

import common.tetfu.Tetfu;
import entry.CommandLineWrapper;
import entry.common.SettingParser;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemoveUtilSettingParser extends SettingParser<RemoveUtilSettings> {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_FIELD_TXT = "input/field.txt";

    public RemoveUtilSettingParser(Options options, CommandLineParser parser) {
        super(options, parser, "util remove");
    }

    @Override
    protected Optional<RemoveUtilSettings> parse(CommandLineWrapper wrapper) throws FinderParseException {
        RemoveUtilSettings settings = new RemoveUtilSettings();

        // テト譜の読み込み
        List<String> fumens = loadFumenData(
                wrapper,
                RemoveUtilOptions.Fumen.optName(),
                RemoveUtilOptions.FieldPath.optName(),
                DEFAULT_FIELD_TXT,
                Charset.forName(CHARSET_NAME)
        ).stream()
                .map(Tetfu::removeDomainData)
                .filter(Tetfu::isDataLater115)
                .map(Tetfu::removePrefixData)
                .collect(Collectors.toList());

        if (fumens.isEmpty()) {
            throw new FinderParseException("Cannot load fumen" + fumens);
        }

        settings.setFumens(fumens);

        // ログファイルの設定
        Optional<String> logFilePath = wrapper.getStringOption(RemoveUtilOptions.LogPath.optName());
        logFilePath.ifPresent(settings::setLogFilePath);

        // アウトプットファイルの設定
        Optional<String> outputBaseFilePath = wrapper.getStringOption(RemoveUtilOptions.OutputBase.optName());
        outputBaseFilePath.ifPresent(settings::setOutputBaseFilePath);

        return Optional.of(settings);
    }

    // フィールドの情報を読み込む
    private static List<String> loadFumenData(
            CommandLineWrapper wrapper,
            String fumenOptName,
            String fieldPathOptName,
            String defaultFieldText,
            Charset charset
    ) throws FinderParseException {
        if (wrapper.hasOption(fumenOptName)) {
            // テト譜から
            return wrapper.getStringOptions(fumenOptName);
        } else {
            // フィールドファイルから
            Optional<String> fieldPathOption = wrapper.getStringOption(fieldPathOptName);
            String fieldPath = fieldPathOption.orElse(defaultFieldText);
            Path path = Paths.get(fieldPath);

            LinkedList<String> fieldLines;
            try (Stream<String> lines = Files.lines(path, charset)) {
                fieldLines = lines
                        .map(str -> {
                            if (str.contains("#"))
                                return str.substring(0, str.indexOf('#'));
                            return str;
                        })
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toCollection(LinkedList::new));
            } catch (IOException e) {
                throw new FinderParseException("Cannot open field file");
            }

            if (fieldLines.isEmpty())
                throw new FinderParseException("Should specify clear-line & field-definition in field file");

            return fieldLines;
        }
    }
}
