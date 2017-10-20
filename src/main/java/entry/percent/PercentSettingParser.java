package entry.percent;

import common.tetfu.Tetfu;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
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

public class PercentSettingParser {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_PATTERNS_TXT = "input/patterns.txt";
    private static final String DEFAULT_FIELD_TXT = "input/field.txt";
    private static final String PATTERN_DELIMITER = ";";

    private final String[] commands;

    public PercentSettingParser(List<String> commands) {
        this.commands = new String[commands.size()];
        commands.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList())
                .toArray(this.commands);
    }

    PercentSettingParser(String commands) {
        this(commands.split(" "));
    }

    private PercentSettingParser(String[] commands) {
        this.commands = commands;
    }

    public Optional<PercentSettings> parse() throws FinderParseException {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parseToCommandLine(options, parser, commands);
        CommandLineWrapper wrapper = new NormalCommandLineWrapper(commandLine);
        PercentSettings settings = new PercentSettings();

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
                    // 最大削除ラインの設定
                    int maxClearLine = Integer.valueOf(fieldLines.pollFirst());
                    settings.setMaxClearLine(maxClearLine);

                    // フィールドの設定
                    String fieldMarks = String.join("", fieldLines);
                    Field field = FieldFactory.createField(fieldMarks);
                    settings.setFieldFilePath(field);
                }
            } catch (NumberFormatException e) {
                throw new FinderParseException("Cannot read clear-line from " + fieldPath);
            } catch (IOException e) {
                throw new FinderParseException("Cannot open field file", e);
            }
        }

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

        // パフェ成功確率ツリーの深さの設定
        Optional<Integer> treeDepth = wrapper.getIntegerOption("tree-depth");
        treeDepth.ifPresent(settings::setTreeDepth);

        // パフェ失敗パターンの表示個数の設定
        Optional<Integer> failedCount = wrapper.getIntegerOption("failed-count");
        failedCount.ifPresent(settings::setFailedCount);

        // ホールドの設定
        Optional<Boolean> isUsingHold = wrapper.getBoolOption("hold");
        isUsingHold.ifPresent(settings::setUsingHold);

        // ログファイルの設定
        Optional<String> logFilePath = wrapper.getStringOption("log-path");
        logFilePath.ifPresent(settings::setLogFilePath);

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

        Option clearLineOption = Option.builder("c")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("num-of-line")
                .longOpt("clear-line")
                .desc("Max clear line")
                .build();
        options.addOption(clearLineOption);

        Option treeDepthOption = Option.builder("td")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("depth")
                .longOpt("tree-depth")
                .desc("Depth of percent tree each head pieces")
                .build();
        options.addOption(treeDepthOption);

        Option failedCountOption = Option.builder("fc")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("max-count")
                .longOpt("failed-count")
                .desc("Max count of failed patterns when output")
                .build();
        options.addOption(failedCountOption);

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

    private CommandLineWrapper loadTetfu(String data, CommandLineParser parser, Options options, CommandLineWrapper wrapper, PercentSettings settings) throws FinderParseException {
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

        // 最大削除ラインの設定
        Optional<Integer> maxClearLineOption = wrapper.getIntegerOption("clear-line");
        maxClearLineOption.ifPresent(settings::setMaxClearLine);

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
        settings.setField(coloredField, settings.getMaxClearLine());

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
