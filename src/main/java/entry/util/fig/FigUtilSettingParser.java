package entry.util.fig;

import common.UnsupportedDataTypeException;
import common.tetfu.Tetfu;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import core.mino.MinoFactory;
import entry.CommandLineWrapper;
import entry.NormalCommandLineWrapper;
import exceptions.FinderParseException;
import org.apache.commons.cli.*;
import util.fig.FrameType;

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

public class FigUtilSettingParser {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_FIELD_TXT = "input/field.txt";

    private final String[] commands;

    public FigUtilSettingParser(List<String> commands) {
        this.commands = new String[commands.size()];
        commands.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList())
                .toArray(this.commands);
    }

    FigUtilSettingParser(String commands) {
        this(commands.split(" "));
    }

    private FigUtilSettingParser(String[] commands) {
        this.commands = commands;
    }

    public Optional<FigUtilSettings> parse() throws FinderParseException {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parseToCommandLine(options, parser);
        CommandLineWrapper wrapper = new NormalCommandLineWrapper(commandLine);
        FigUtilSettings settings = new FigUtilSettings();

        // help
        if (wrapper.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("util fig [options]", options);
            return Optional.empty();
        }

        // ホールドの設定
        Optional<Boolean> isUsingHold = wrapper.getBoolOption("hold");
        isUsingHold.ifPresent(settings::setUsingHold);

        // ループの設定
        Optional<Boolean> isInfiniteLoop = wrapper.getBoolOption("loop");
        isInfiniteLoop.ifPresent(settings::setInfiniteLoop);

        // アウトプットファイルの設定
        Optional<String> outputPath = wrapper.getStringOption("output");
        outputPath.ifPresent(settings::setOutputFilePath);

        // ディレイタイムの設定
        Optional<Integer> delay = wrapper.getIntegerOption("delay");
        try {
            delay.ifPresent(value -> {
                if (value <= 0)
                    throw new RuntimeException("Delay should be positive: delay=" + value);
                settings.setDelay(value);
            });
        } catch (RuntimeException e) {
            throw new FinderParseException(e.getMessage());
        }

        // フレームの設定
        Optional<String> frameTypeName = wrapper.getStringOption("frame");
        try {
            Optional<FrameType> frameType = frameTypeName.map(frame -> {
                try {
                    return parseFrameType(frame);
                } catch (UnsupportedDataTypeException e) {
                    throw new RuntimeException(e);
                }
            });
            frameType.ifPresent(settings::setFrameType);
        } catch (RuntimeException e) {
            throw new FinderParseException("Unsupported frame: frame=" + frameTypeName.orElse("<empty>"));
        }

        // フォーマットの設定
        Optional<String> formatName = wrapper.getStringOption("format");
        try {
            Optional<FigFormat> figFormat = formatName.map(format -> {
                try {
                    return parseFigFormat(format);
                } catch (UnsupportedDataTypeException e) {
                    throw new RuntimeException(e);
                }
            });
            figFormat.ifPresent(settings::setFigFormat);
        } catch (RuntimeException e) {
            throw new FinderParseException("Unsupported format: format=" + formatName.orElse("<empty>"));
        }

        // 高さの設定
        Optional<Integer> height = wrapper.getIntegerOption("line");
        try {
            height.ifPresent(value -> {
                if (value == 0)
                    throw new RuntimeException("Line should be positive or -1: line=" + value);
                settings.setHeight(value);
            });
        } catch (RuntimeException e) {
            throw new FinderParseException(e.getMessage());
        }

        // テト譜の設定
        if (wrapper.hasOption("tetfu")) {
            // パラメータから
            Optional<String> tetfuData = wrapper.getStringOption("tetfu");
            if (!tetfuData.isPresent())
                throw new FinderParseException("Should specify option value: --tetfu");

            String encoded = Tetfu.removeDomainData(tetfuData.get());
            wrapper = loadTetfu(encoded, wrapper, settings);
        } else {
            // フィールドファイルから
            Optional<String> fieldPathOption = wrapper.getStringOption("field-path");
            String fieldPath = fieldPathOption.orElse(DEFAULT_FIELD_TXT);
            Path path = Paths.get(fieldPath);
            Charset charset = Charset.forName(CHARSET_NAME);

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

                if (fieldLines.isEmpty())
                    throw new FinderParseException("Should specify field-definition in field file");

                String encoded = fieldLines.get(0);
                String removeDomainData = Tetfu.removeDomainData(encoded);
                wrapper = loadTetfu(removeDomainData, wrapper, settings);
            } catch (IOException e) {
                throw new FinderParseException("Cannot open field file", e);
            }
        }

        // ネクストの設定
        Optional<Integer> next = wrapper.getIntegerOption("next");
        Optional<Integer> positiveNext = next.map(integer -> 0 < integer ? integer : 0);
        positiveNext.ifPresent(settings::setNextBoxCount);

        // ログファイルの設定
        Optional<String> logFilePath = wrapper.getStringOption("log-path");
        logFilePath.ifPresent(settings::setLogFilePath);

        // カラーテーマの設定
        Optional<String> colorTheme = wrapper.getStringOption("color");
        colorTheme.ifPresent(settings::setColorTheme);

        return Optional.of(settings);
    }

    private CommandLine parseToCommandLine(Options options, CommandLineParser parser) throws FinderParseException {
        try {
            return parser.parse(options, commands);
        } catch (ParseException e) {
            throw new FinderParseException(e);
        }
    }

    private FrameType parseFrameType(String frameTypeName) throws UnsupportedDataTypeException {
        switch (frameTypeName.toLowerCase()) {
            case "basic":
                return FrameType.Basic;
            case "no":
                return FrameType.NoFrame;
            case "right":
                return FrameType.Right;
        }
        throw new UnsupportedDataTypeException("Not found frame type: " + frameTypeName);
    }

    private FigFormat parseFigFormat(String formatName) throws UnsupportedDataTypeException {
        switch (formatName.toLowerCase()) {
            case "png":
                return FigFormat.Png;
            case "gif":
                return FigFormat.Gif;
        }
        throw new UnsupportedDataTypeException("Not found format name: " + formatName);
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
                .argName("visible or hidden")
                .longOpt("hold")
                .desc("If show hold, set 'visible'. If not show hold, set 'hidden'")
                .build();
        options.addOption(holdOption);

        Option delayOption = Option.builder("d")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("delay-time")
                .longOpt("delay")
                .desc("Specify delay time [unit: 10ms] ex) 15 -> 150ms")
                .build();
        options.addOption(delayOption);

        Option nextOption = Option.builder("n")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("next-box-count")
                .longOpt("next")
                .desc("Specify next box count")
                .build();
        options.addOption(nextOption);

        Option tetfuOption = Option.builder("t")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("data-of-tetfu")
                .longOpt("tetfu")
                .desc("Specify tetfu data for s-finder settings")
                .build();
        options.addOption(tetfuOption);

        Option startPageOption = Option.builder("s")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("page-of-tetfu")
                .longOpt("start")
                .desc("Specify start pages of tetfu")
                .build();
        options.addOption(startPageOption);

        Option endPageOption = Option.builder("e")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("page-of-tetfu")
                .longOpt("end")
                .desc("Specify end pages of tetfu")
                .build();
        options.addOption(endPageOption);

        Option heightLineOption = Option.builder("l")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("num-of-line")
                .longOpt("line")
                .desc("Max show line")
                .build();
        options.addOption(heightLineOption);

        Option loopOption = Option.builder("L")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("true or false")
                .longOpt("loop")
                .desc("Infinite loop flag")
                .build();
        options.addOption(loopOption);

        Option frameTypeOption = Option.builder("f")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("frame-type")
                .longOpt("frame")
                .desc("Frame type of figure")
                .build();
        options.addOption(frameTypeOption);

        Option formatOption = Option.builder("F")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("format-name")
                .longOpt("format")
                .desc("Format of figure")
                .build();
        options.addOption(formatOption);

        Option colorThemeOption = Option.builder("c")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("string")
                .longOpt("color")
                .desc("Color theme name")
                .build();
        options.addOption(colorThemeOption);

        Option outputFileOption = Option.builder("o")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("file-path")
                .longOpt("output")
                .desc("Figure file path of result to output")
                .build();
        options.addOption(outputFileOption);

        Option logFileOption = Option.builder("lp")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("path")
                .longOpt("log-path")
                .desc("File path of output log")
                .build();
        options.addOption(logFileOption);

        Option fieldFileOption = Option.builder("fp")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("file-path")
                .longOpt("field-path")
                .desc("File path of field definition")
                .build();
        options.addOption(fieldFileOption);

        return options;
    }

    private CommandLineWrapper loadTetfu(String data, CommandLineWrapper wrapper, FigUtilSettings settings) throws FinderParseException {
        // テト譜面のエンコード
        List<TetfuPage> tetfuPages = encodeTetfu(data);

        // 指定されたページを抽出
        //// 開始ページ
        int startPage = wrapper.getIntegerOption("start").orElse(1);
        if (startPage <= 0)
            throw new FinderParseException(String.format("Tetfu-start-page should be 1 <= page: StartPage=%d", startPage));

        if (tetfuPages.size() < startPage)
            throw new FinderParseException(String.format("Tetfu-start-page is over max page: StartPage=%d", startPage));

        //// 終了ページ
        int endPage = wrapper.getIntegerOption("end").orElse(-1);

        if (endPage == -1)
            endPage = tetfuPages.size();

        if (endPage < startPage)
            throw new FinderParseException(String.format("Tetfu-end-page should be %d <= page: EndPage=%d", startPage, endPage));

        if (tetfuPages.size() < endPage)
            throw new FinderParseException(String.format("Tetfu-end-page is over max page: EndPage=%d", endPage));

        settings.setTetfuPages(tetfuPages, startPage, endPage);

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
}
