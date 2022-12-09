package entry.cover;

import common.datastore.*;
import common.parser.OperationTransform;
import common.tetfu.Tetfu;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoTransform;
import core.mino.Piece;
import entry.CommandLineWrapper;
import entry.common.Loader;
import entry.common.SettingParser;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoverSettingParser extends SettingParser<CoverSettings> {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_PATTERNS_TXT = "input/patterns.txt";
    private static final String DEFAULT_FIELD_TXT = "input/field.txt";
    private static final String PATTERN_DELIMITER = ";";

    public CoverSettingParser(Options options, CommandLineParser parser) {
        super(options, parser, "cover");
    }

    @Override
    protected Optional<CoverSettings> parse(CommandLineWrapper wrapper) throws FinderParseException {
        CoverSettings settings = new CoverSettings();

        MinoFactory minoFactory = new MinoFactory();
        MinoTransform minoTransform = new MinoTransform();
        ColorConverter colorConverter = new ColorConverter();

        // テト譜の読み込み
        List<String> fumens = new ArrayList<>(loadFumenData(
                wrapper,
                CoverOptions.Fumen.optName(),
                CoverOptions.FieldPath.optName(),
                DEFAULT_FIELD_TXT,
                Charset.forName(CHARSET_NAME)
        ));

        if (fumens.isEmpty()) {
            throw new FinderParseException("Cannot load fumen" + fumens);
        }

        // ミラーの設定
        boolean isMirror = wrapper.getBoolOption(CoverOptions.Mirror.optName()).orElse(false);

        List<CoverParameter> parameters = new ArrayList<>();

        for (String raw : fumens) {
            String input = Tetfu.removeDomainData(raw);
            if (!Tetfu.isDataLater115(input)) {
                continue;
            }

            String prefix = input.substring(0, 4);

            int start = 1;
            int end = -1;

            assert Tetfu.isDataLater115(input);
            String data = Tetfu.removePrefixData(input);

            if (data == null) {
                throw new FinderParseException("Not found data: " + input);
            }

            // ページ指定を取り出す
            String[] split = raw.split(prefix);
            assert 2 <= split.length;
            String[] dataPage = split[1].split("#");

            if (2 <= dataPage.length) {
                String[] startEnd = dataPage[1].split(":");
                if (0 < startEnd.length && !startEnd[0].isEmpty()) {
                    start = Integer.parseInt(startEnd[0]);
                }

                if (1 < startEnd.length && !startEnd[1].isEmpty()) {
                    end = Integer.parseInt(startEnd[1]);
                }
            }

            Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
            List<TetfuPage> pages = tetfu.decode(data);

            if (start < 1) {
                throw new FinderParseException("Start page should be greater than 0");
            }

            if (pages.size() < start) {
                throw new FinderParseException("Start page should be less than or equal to " + pages.size());
            }

            if (end == -1) {
                end = pages.size();
            }

            if (end < start) {
                throw new FinderParseException("End page should be greater than or equal to start page");
            }

            if (pages.size() < end) {
                throw new FinderParseException("End page should be less than or equal to " + pages.size());
            }

            pages = pages.subList(start - 1, end);

            if (pages.isEmpty()) {
                throw new FinderParseException("Specified page is empty");
            }

            List<SimpleMinoOperation> operationList = pages.stream()
                    .filter(TetfuPage::isPutMino)
                    .map(page -> {
                        Piece piece = colorConverter.parseToBlock(page.getColorType());
                        Mino mino = minoFactory.create(piece, page.getRotate());
                        return new SimpleMinoOperation(mino, page.getX(), page.getY());
                    })
                    .collect(Collectors.toList());

            int height = 24;

            Field field = toField(pages.get(0).getField(), height);

            List<MinoOperationWithKey> operationsWithKey = OperationTransform.parseToOperationWithKeys(
                    field, new Operations(operationList), minoFactory, height
            );

            {
                parameters.add(new CoverParameter(field, operationsWithKey, input, false));
            }

            if (isMirror) {
                Field freeze = field.freeze();
                freeze.mirror();

                List<MinoOperationWithKey> operationsWithKeyMirror = operationsWithKey.stream().map(m -> {
                    MinoOperation mirror = minoTransform.mirror(minoFactory, m.getPiece(), m.getRotate(), m.getX(), m.getY());
                    return new MinimalOperationWithKey(mirror.getMino(), mirror.getX(), mirror.getY(), m.getNeedDeletedKey());
                }).collect(Collectors.toList());

                parameters.add(new CoverParameter(freeze, operationsWithKeyMirror, input, true));
            }
        }

        settings.setParameters(parameters);

        // パターンの読み込み
        List<String> patterns = Loader.loadPatterns(
                wrapper,
                CoverOptions.Patterns.optName(),
                PATTERN_DELIMITER,
                CoverOptions.PatternsPath.optName(),
                DEFAULT_PATTERNS_TXT,
                Charset.forName(CHARSET_NAME)
        );
        settings.setPatterns(patterns);

        // Load kicks
        Optional<String> kicks = wrapper.getStringOption(CoverOptions.Kicks.optName());
        kicks.ifPresent(settings::setKicks);

        // ドロップの設定
        Optional<String> dropType = wrapper.getStringOption(CoverOptions.Drop.optName());
        if (dropType.isPresent()) {
            settings.setDropType(dropType.get());
        }

        // モードの設定
        Optional<String> modeType = wrapper.getStringOption(CoverOptions.Mode.optName());
        if (modeType.isPresent()) {
            settings.setCoverModes(modeType.get());
        }

        // N-Linesモードの設定
        if (CoverModes.isNLinesMode(settings.getCoverModes())) {
            // max softdrop count
            Optional<Integer> maxSoftdropCount = wrapper.getIntegerOption(CoverOptions.MaxSoftdropTimes.optName());
            maxSoftdropCount.ifPresent(settings::setMaxSoftdropTimes);

            // max clear line count
            Optional<Integer> maxClearLineCount = wrapper.getIntegerOption(CoverOptions.MaxClearLineTimes.optName());
            maxClearLineCount.ifPresent(settings::setMaxClearLineTimes);
        }

        // アウトプットファイルの設定
        Optional<Integer> lastSoftdrop = wrapper.getIntegerOption(CoverOptions.LastSoftdrop.optName());
        lastSoftdrop.ifPresent(settings::setLastSoftdrop);

        // ホールドの設定
        Optional<Boolean> isUsingHold = wrapper.getBoolOption(CoverOptions.Hold.optName());
        isUsingHold.ifPresent(settings::setUsingHold);

        // 優先度の設定
        Optional<Boolean> isUsingPriority = wrapper.getBoolOption(CoverOptions.Priority.optName());
        isUsingPriority.ifPresent(settings::setUsingPriority);

        // ログファイルの設定
        Optional<String> logFilePath = wrapper.getStringOption(CoverOptions.LogPath.optName());
        logFilePath.ifPresent(settings::setLogFilePath);

        // アウトプットファイルの設定
        Optional<String> outputBaseFilePath = wrapper.getStringOption(CoverOptions.OutputBase.optName());
        outputBaseFilePath.ifPresent(settings::setOutputBaseFilePath);

        // 開始直後で必要なB2B継続回数の設定
        Optional<Integer> startingB2B = wrapper.getIntegerOption(CoverOptions.StartingB2B.optName());
        startingB2B.ifPresent(settings::setStartingB2B);

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

    private Field toField(ColoredField coloredField, int height) {
        Field field = FieldFactory.createField(height);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < 10; x++)
                if (coloredField.getColorType(x, y) != ColorType.Empty)
                    field.setBlock(x, y);
        return field;
    }
}
