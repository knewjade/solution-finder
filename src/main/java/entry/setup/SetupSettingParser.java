package entry.setup;

import common.tetfu.Tetfu;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import entry.CommandLineWrapper;
import entry.NormalCommandLineWrapper;
import entry.PriorityCommandLineWrapper;
import exceptions.FinderParseException;
import org.apache.commons.cli.*;

import javax.activation.UnsupportedDataTypeException;
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

public class SetupSettingParser {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_PATTERNS_TXT = "input/patterns.txt";
    private static final String DEFAULT_FIELD_TXT = "input/field.txt";
    private static final String PATTERN_DELIMITER = ";";

    private final String[] commands;

    public SetupSettingParser(List<String> commands) {
        this.commands = new String[commands.size()];
        commands.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList())
                .toArray(this.commands);
    }

    public Optional<SetupSettings> parse() throws FinderParseException {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parseToCommandLine(options, parser, commands);
        CommandLineWrapper wrapper = new NormalCommandLineWrapper(commandLine);
        SetupSettings settings = new SetupSettings();

        // help
        if (wrapper.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("percent [options]", options);
            return Optional.empty();
        }

        // フィールド・最大削除ラインの設定
        if (wrapper.hasOption("tetfu")) {
            // テト譜から
            Optional<String> tetfuData = wrapper.getStringOption("tetfu");
            if (!tetfuData.isPresent())
                throw new FinderParseException("Should specify option value: --tetfu");

            String encoded = Tetfu.removeDomainData(tetfuData.get());
            wrapper = loadTetfu(encoded, parser, options, wrapper, settings);
        } else {
            // フィールドファイルから
            Optional<String> fieldPathOption = wrapper.getStringOption("field-path");
            String fieldPath = fieldPathOption.orElse(DEFAULT_FIELD_TXT);
            Path path = Paths.get(fieldPath);
            Charset charset = Charset.forName(CHARSET_NAME);

            try {
                LinkedList<String> fieldLines = Files.lines(path, charset)
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
                    wrapper = loadTetfu(removeDomainData, parser, options, wrapper, settings);
                } else {
                    // 固定ピースの指定があるか
                    Optional<Boolean> reservedOption = wrapper.getBoolOption("reserved");
                    reservedOption.ifPresent(settings::setReserved);

                    // 最大削除ラインの設定
                    int maxHeightForce = -1;
                    try {
                        maxHeightForce = Integer.valueOf(fieldLines.peekFirst());
                        fieldLines.pollFirst();  // 読み込みに成功したときだけ進める
                    } catch (Exception ignore) {
                    }

                    // フィールドの設定
                    String fieldMarks = String.join("", fieldLines);
                    parseField(fieldMarks, settings, maxHeightForce);
                }
            } catch (NumberFormatException e) {
                throw new FinderParseException("Cannot read clear-line from " + fieldPath);
            } catch (IOException e) {
                throw new FinderParseException("Cannot open field file", e);
            }
        }

        // ログファイルの設定
        Optional<String> logFilePath = wrapper.getStringOption("log-path");
        logFilePath.ifPresent(settings::setLogFilePath);

        // アウトプットファイルの設定
        Optional<String> outputBaseFilePath = wrapper.getStringOption("output-base");
        outputBaseFilePath.ifPresent(settings::setOutputBaseFilePath);

        // ドロップの設定
        Optional<String> dropType = wrapper.getStringOption("drop");
        try {
            dropType.ifPresent(type -> {
                String key = dropType.orElse("softdrop");
                try {
                    settings.setDropType(key);
                } catch (UnsupportedDataTypeException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new FinderParseException("Unsupported format: format=" + dropType.orElse("<empty>"));
        }

        // 探索パターンの設定
        if (wrapper.hasOption("patterns")) {
            // パターン定義から
            Optional<String> patternOption = wrapper.getStringOption("patterns");
            assert patternOption.isPresent();
            String patternValue = patternOption.get();
            List<String> patterns = Arrays.stream(patternValue.split(PATTERN_DELIMITER)).collect(Collectors.toList());
            settings.setPatterns(patterns);
        } else {
            // パターンファイルから
            Optional<String> patternPathOption = wrapper.getStringOption("patterns-path");
            String patternPath = patternPathOption.orElse(DEFAULT_PATTERNS_TXT);
            Path path = Paths.get(patternPath);
            Charset charset = Charset.forName(CHARSET_NAME);

            try {
                List<String> patterns = Files.lines(path, charset).collect(Collectors.toList());
                settings.setPatterns(patterns);
            } catch (IOException e) {
                throw new FinderParseException("Cannot open patterns file", e);
            }
        }

        return Optional.of(settings);
    }

    private void parseField(String fieldMarks, SetupSettings settings, int maxHeightForce) {
        ColoredField coloredField = ColoredFieldFactory.createColoredField(fieldMarks);
        int maxHeight = maxHeightForce != -1 ? maxHeightForce : coloredField.getUsingHeight();

        // Load init field
        String initFieldMarks = fieldMarks.replace(".", " ").replace("*", " ");
        Field initField = FieldFactory.createField(initFieldMarks);
        for (int y = maxHeight; y < initField.getMaxFieldHeight(); y++)
            for (int x = 0; x < 10; x++)
                initField.removeBlock(x, y);

        // Load need filled field
        String needFilledFieldMarks = filterString(fieldMarks, '*', '_');
        Field needFilledField = FieldFactory.createField(needFilledFieldMarks);
        for (int y = maxHeight; y < needFilledField.getMaxFieldHeight(); y++)
            for (int x = 0; x < 10; x++)
                needFilledField.removeBlock(x, y);

        // Load not filled field
        Field notFilledField = FieldFactory.createInverseField(fieldMarks.replace(".", " "));
        for (int y = maxHeight; y < notFilledField.getMaxFieldHeight(); y++)
            for (int x = 0; x < 10; x++)
                notFilledField.removeBlock(x, y);

        if (settings.isReserved()) {
            settings.setFieldWithReserved(initField, needFilledField, notFilledField, coloredField, maxHeight);
        } else {
            settings.setField(initField, needFilledField, notFilledField, maxHeight);
        }
    }

    private String filterString(String str, char allow, char replace) {
        char[] chars = str.toCharArray();
        for (int index = 0; index < chars.length; index++)
            if (chars[index] != allow)
                chars[index] = replace;
        return String.valueOf(chars);
    }

    private Options createOptions() {
        Options options = new Options();

        Option helpOption = Option.builder("h")
                .optionalArg(true)
                .longOpt("help")
                .desc("Usage")
                .build();
        options.addOption(helpOption);

        Option holdOption = Option.builder("H")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("use or avoid")
                .longOpt("hold")
                .desc("If use hold, set 'use'. If not use hold, set 'avoid'")
                .build();
        options.addOption(holdOption);

        Option tetfuOption = Option.builder("t")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("data-of-tetfu")
                .longOpt("tetfu")
                .desc("Specify tetfu data for s-finder settings")
                .build();
        options.addOption(tetfuOption);

        Option tetfuPageOption = Option.builder("P")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("page-of-tetfu")
                .longOpt("page")
                .desc("Specify pages of tetfu data for s-finder settings")
                .build();
        options.addOption(tetfuPageOption);

        Option fieldFileOption = Option.builder("fp")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("file-path")
                .longOpt("field-path")
                .desc("File path of field definition")
                .build();
        options.addOption(fieldFileOption);

        Option patternOption = Option.builder("p")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("definition")
                .longOpt("patterns")
                .desc("Specify pattern definition, directly")
                .build();
        options.addOption(patternOption);

        Option patternFileOption = Option.builder("pp")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("file-path")
                .longOpt("patterns-path")
                .desc("File path of pattern definition")
                .build();
        options.addOption(patternFileOption);

        Option logFileOption = Option.builder("l")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("file-path")
                .longOpt("log-path")
                .desc("File path of output log")
                .build();
        options.addOption(logFileOption);

        Option outputFileOption = Option.builder("o")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("file-path")
                .longOpt("output-base")
                .desc("Base file path of result to output")
                .build();
        options.addOption(outputFileOption);

        Option clearLineOption = Option.builder("c")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("num-of-line")
                .longOpt("clear-line")
                .desc("Max clear line")
                .build();
        options.addOption(clearLineOption);

        Option reservedOption = Option.builder("r")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("reserved-block")
                .longOpt("reserved")
                .desc("Specify reserved block")
                .build();
        options.addOption(reservedOption);

        Option marginColorOption = Option.builder("m")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("color")
                .longOpt("margin-color")
                .desc("Specify margin color")
                .build();
        options.addOption(marginColorOption);

        Option fillColorOption = Option.builder("f")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("color")
                .longOpt("fill-color")
                .desc("Specify fill color")
                .build();
        options.addOption(fillColorOption);

        Option dropOption = Option.builder("d")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("drop")
                .longOpt("drop")
                .desc("Specify drop")
                .build();
        options.addOption(dropOption);

        return options;
    }

    private CommandLine parseToCommandLine(Options options, CommandLineParser parser, String[] commands) throws FinderParseException {
        try {
            return parser.parse(options, commands);
        } catch (ParseException e) {
            throw new FinderParseException(e);
        }
    }

    private CommandLineWrapper loadTetfu(String data, CommandLineParser parser, Options options, CommandLineWrapper wrapper, SetupSettings settings) throws FinderParseException {
        // テト譜面のエンコード
        List<TetfuPage> decoded = encodeTetfu(data);

        // 指定されたページを抽出
        int page = wrapper.getIntegerOption("page").orElse(1);
        TetfuPage tetfuPage = extractTetfuPage(decoded, page);

        // コメントの抽出
        // 先頭が数字ではない(--clear-line -p *p7のようになる)場合でも、parserはエラーにならない
        // データ取得時にOptional.emptyがかえるだけ
        String comment = "--clear-line " + tetfuPage.getComment();
        List<String> splitComment = Arrays.stream(comment.split(" "))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // コマンド引数を配列に変換
        String[] commentArgs = new String[splitComment.size()];
        splitComment.toArray(commentArgs);

        // オプションとして読み込む
        try {
            CommandLine commandLineTetfu = parseToCommandLine(options, parser, commentArgs);
            CommandLineWrapper newWrapper = new NormalCommandLineWrapper(commandLineTetfu);
            newWrapper.getIntegerOption("clear-line");  // 削除ラインが読み取れればOK
            wrapper = new PriorityCommandLineWrapper(Arrays.asList(wrapper, newWrapper));
        } catch (FinderParseException ignore) {
        }

        // 固定ピースの指定があるか
        Optional<Boolean> reservedOption = wrapper.getBoolOption("reserved");
        reservedOption.ifPresent(settings::setReserved);

        // マージン色の指定があるか
        Optional<String> fillColorOption = wrapper.getStringOption("fill-color");
        if (fillColorOption.isPresent()) {
            try {
                settings.setFillColorType(fillColorOption.get());
            } catch (UnsupportedDataTypeException e) {
                throw new FinderParseException(e);
            }
        }

        // マージン色の指定があるか
        Optional<String> marginColorOption = wrapper.getStringOption("margin-color");
        if (marginColorOption.isPresent()) {
            try {
                settings.setMarginColorType(marginColorOption.get());
            } catch (UnsupportedDataTypeException e) {
                throw new FinderParseException(e);
            }
        }

        // フィールドを設定
        ColoredField coloredField = tetfuPage.getField();
        if (tetfuPage.isPutMino()) {
            ColorType colorType = tetfuPage.getColorType();
            Rotate rotate = tetfuPage.getRotate();
            int x = tetfuPage.getX();
            int y = tetfuPage.getY();

            ColorConverter colorConverter = new ColorConverter();
            Mino mino = new Mino(colorConverter.parseToBlock(colorType), rotate);
            coloredField.putMino(mino, x, y);
            coloredField.clearLine();
        }

        // 最大削除ラインの設定
        Optional<Integer> maxHeightOption = wrapper.getIntegerOption("clear-line");
        int maxHeight = maxHeightOption.orElse(coloredField.getUsingHeight());

        if (settings.isReserved()) {
            Field initField = FieldFactory.createField(maxHeight);
            Field needFilledField = FieldFactory.createField(maxHeight);
            Field notFilledField = FieldFactory.createField(maxHeight);

            ColorType marginColorType = settings.getMarginColorType();
            ColorType fillColorType = settings.getFillColorType();

            for (int y = 0; y < maxHeight; y++) {
                for (int x = 0; x < 10; x++) {
                    ColorType colorType = coloredField.getColorType(x, y);
                    if (colorType.equals(marginColorType)) {
                        coloredField.setColorType(ColorType.Empty, x, y);
                    } else if (colorType.equals(fillColorType)) {
                        coloredField.setColorType(ColorType.Empty, x, y);
                        needFilledField.setBlock(x, y);
                    } else {
                        switch (colorType) {
                            case Gray:
                                initField.setBlock(x, y);
                                notFilledField.setBlock(x, y);
                                coloredField.setColorType(ColorType.Empty, x, y);
                                break;
                            case Empty:
                                notFilledField.setBlock(x, y);
                                break;
                            default:
                                initField.setBlock(x, y);
                                break;
                        }
                    }
                }
            }

            settings.setFieldWithReserved(initField, needFilledField, notFilledField, coloredField, maxHeight);
        } else {
            Field initField = FieldFactory.createField(maxHeight);
            Field needFilledField = FieldFactory.createField(maxHeight);
            Field notFilledField = FieldFactory.createField(maxHeight);

            ColorType marginColorType = settings.getMarginColorType();

            for (int y = 0; y < maxHeight; y++) {
                for (int x = 0; x < 10; x++) {
                    ColorType colorType = coloredField.getColorType(x, y);

                    if (colorType.equals(marginColorType))
                        continue;  // skip

                    switch (colorType) {
                        case Gray:
                            needFilledField.setBlock(x, y);
                            break;
                        case Empty:
                            notFilledField.setBlock(x, y);
                            break;
                        default:
                            initField.setBlock(x, y);
                            notFilledField.setBlock(x, y);
                            break;
                    }
                }
            }

            settings.setField(initField, needFilledField, notFilledField, maxHeight);
        }

        return wrapper;
    }

    private List<TetfuPage> encodeTetfu(String encoded) throws FinderParseException {
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        String data = Tetfu.removePrefixData(encoded);
        if (data == null)
            throw new FinderParseException("Unsupported tetfu: data=" + encoded);
        return tetfu.decode(data);
    }

    private TetfuPage extractTetfuPage(List<TetfuPage> tetfuPages, int page) throws FinderParseException {
        if (page < 1) {
            throw new FinderParseException(String.format("Tetfu-page should be 1 <= page: page=%d", page));
        } else if (page <= tetfuPages.size()) {
            return tetfuPages.get(page - 1);
        } else {
            throw new FinderParseException(String.format("Tetfu-page is over max page: page=%d", page));
        }
    }
}
