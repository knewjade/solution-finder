package entry.common.field;

import common.tetfu.Tetfu;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import entry.CommandLineWrapper;
import entry.NormalCommandLineWrapper;
import entry.common.CommandLineFactory;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLine;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldTextLoader {
    private final CommandLineFactory commandLineFactory;

    public FieldTextLoader(CommandLineFactory commandLineFactory) {
        this.commandLineFactory = commandLineFactory;
    }

    public FieldData load(Stream<String> lines, ParseFunction<String, FieldData> callbackIfFumen) throws FinderParseException {
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
            return callbackIfFumen.apply(removeDomainData);
        } else {
            try {
                // 最大削除ラインの設定
                String firstLine = fieldLines.pollFirst();
                int maxClearLine = Integer.parseInt(firstLine != null ? firstLine : "error");

                // フィールドの設定
                String fieldMarks = String.join("", fieldLines);
                ColoredField coloredField = ColoredFieldFactory.createColoredField(fieldMarks);

                // 最大削除ラインをコマンドラインのオプションに設定
                CommandLine commandLineTetfu = commandLineFactory.parse(Arrays.asList("--clear-line", String.valueOf(maxClearLine)));
                CommandLineWrapper newWrapper = new NormalCommandLineWrapper(commandLineTetfu);
                return new FieldData(coloredField, newWrapper);
            } catch (NumberFormatException e) {
                throw new FinderParseException("Cannot read clear-line from field file");
            }
        }
    }
}
