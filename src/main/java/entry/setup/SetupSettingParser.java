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
                    throw new FinderParseException("Should specify field-definition in field file");

                String removeDomainData = Tetfu.removeDomainData(fieldLines.get(0));
                if (Tetfu.isDataLater115(removeDomainData)) {
                    // テト譜から
                    wrapper = loadTetfu(removeDomainData, parser, options, wrapper, settings);
                } else {
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
            } catch (IOException e) {
                throw new FinderParseException("Cannot open field file", e);
            }
        }

        // ホールドの設定
        Optional<Boolean> isUsingHold = wrapper.getBoolOption("hold");
        isUsingHold.ifPresent(settings::setUsingHold);

        // ミノの組み合わせ
        Optional<Boolean> combination = wrapper.getBoolOption("combination");
        combination.ifPresent(settings::setCombination);

        // 除外の設定
        Optional<String> excludeType = wrapper.getStringOption("exclude");
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
        List<String> addOperations = wrapper.getStringOptions("operate");
        settings.setAddOperations(addOperations);

        // 必ず使用するミノの数
        Optional<Integer> numOfPieces = wrapper.getIntegerOption("n-pieces");
        numOfPieces.ifPresent(settings::setNumOfPieces);

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
                } catch (FinderParseException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new FinderParseException("Unsupported format: drop=" + dropType.orElse("<empty>"));
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
        String initFieldMarks = fieldMarks
                .replace(".", "_")
                .replace("+", "_")
                .replace("*", "_");
        Field initField = FieldFactory.createField(initFieldMarks);
        for (int y = maxHeight; y < initField.getMaxFieldHeight(); y++)
            for (int x = 0; x < 10; x++)
                initField.removeBlock(x, y);

        // Load margin field
        String marginFieldMarks = filterString(fieldMarks, '.', '_');
        Field marginField = FieldFactory.createField(marginFieldMarks);
        for (int y = maxHeight; y < marginField.getMaxFieldHeight(); y++)
            for (int x = 0; x < 10; x++)
                marginField.removeBlock(x, y);

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

        settings.setField(initField, needFilledField, notFilledField, marginField, maxHeight);
    }

    private String filterString(String str, char allow, char notAllowTo) {
        char[] chars = str.toCharArray();
        for (int index = 0; index < chars.length; index++)
            if (chars[index] != allow)
                chars[index] = notAllowTo;
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

        Option logFileOption = Option.builder("lp")
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

        Option maxHeightOption = Option.builder("l")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("num-of-line")
                .longOpt("line")
                .desc("Max height line")
                .build();
        options.addOption(maxHeightOption);

        Option combinationOption = Option.builder("co")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("enable/disable")
                .longOpt("combination")
                .desc("If yes, `patterns` is interpreted as combinations of piece")
                .build();
        options.addOption(combinationOption);

        Option holdOption = Option.builder("H")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("use or avoid")
                .longOpt("hold")
                .desc("If use hold, set 'use'. If not use hold, set 'avoid'")
                .build();
        options.addOption(holdOption);

        Option marginColorOption = Option.builder("m")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("color")
                .longOpt("margin")
                .desc("Specify margin color")
                .build();
        options.addOption(marginColorOption);

        Option noHolesColorOption = Option.builder("nh")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("color")
                .longOpt("no-holes")
                .desc("Specify no-holes color")
                .build();
        options.addOption(noHolesColorOption);

        Option fillColorOption = Option.builder("f")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("color")
                .longOpt("fill")
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

        Option excludeOption = Option.builder("e")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("type")
                .longOpt("exclude")
                .desc("If specify holes, exclude solutions containing holes")
                .build();
        options.addOption(excludeOption);

        Option addPieceOption = Option.builder("op")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(Integer.MAX_VALUE)
                .valueSeparator(' ')
                .argName("operations...")
                .longOpt("operate")
                .desc("Operate field before determining to exclude solutions")
                .build();
        options.addOption(addPieceOption);

        Option numOfPiecesOption = Option.builder("np")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("num")
                .longOpt("n-pieces")
                .desc("If specify N, must use N pieces")
                .build();
        options.addOption(numOfPiecesOption);

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
        String comment = tetfuPage.getComment();
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
            wrapper = new PriorityCommandLineWrapper(Arrays.asList(wrapper, newWrapper));
        } catch (FinderParseException ignore) {
        }

        // 固定色の指定があるか
        Optional<String> fillColorOption = wrapper.getStringOption("fill");
        if (fillColorOption.isPresent()) {
            settings.setFillColorType(fillColorOption.get());
        }

        // マージン色（穴あり）の指定があるか
        Optional<String> marginColorOption = wrapper.getStringOption("margin");
        if (marginColorOption.isPresent()) {
            settings.setMarginColorType(marginColorOption.get());
        }

        // マージン色（穴なし）の指定があるか
        Optional<String> noHolesColorOption = wrapper.getStringOption("no-holes");
        if (noHolesColorOption.isPresent()) {
            settings.setNoHolesColorType(noHolesColorOption.get());
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
        }

        // 最大削除ラインの設定
        Optional<Integer> maxHeightOption = wrapper.getIntegerOption("line");
        int maxHeight = maxHeightOption.orElse(coloredField.getUsingHeight());

        // フィールドの設定
        Field initField = FieldFactory.createField(maxHeight);
        Field needFilledField = FieldFactory.createField(maxHeight);
        Field notFilledField = FieldFactory.createField(maxHeight);
        Field marginField = FieldFactory.createField(maxHeight);

        ColorType marginColorType = settings.getMarginColorType();
        ColorType fillColorType = settings.getFillColorType();
        ColorType noHolesColorType = settings.getNoHolesColorType();

        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < 10; x++) {
                ColorType colorType = coloredField.getColorType(x, y);

                if (colorType.equals(marginColorType)) {
                    marginField.setBlock(x, y);
                } else if (colorType.equals(fillColorType)) {
                    needFilledField.setBlock(x, y);
                } else if (colorType.equals(noHolesColorType)) {
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

        settings.setField(initField, needFilledField, notFilledField, marginField, maxHeight);

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
