package entry.util.remove;

import common.datastore.Pair;
import common.datastore.SimpleOperation;
import common.datastore.action.Action;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.action.reachable.ILockedReachable;
import core.action.reachable.LockedReachable;
import core.action.reachable.ReachableFacade;
import core.action.reachable.RotateReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.field.LargeField;
import core.mino.*;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.EntryPoint;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import entry.path.output.FumenParser;
import entry.path.output.MyFile;
import exceptions.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RemoveUtilEntryPoint implements EntryPoint {
    public static final int HEIGHT = 24;
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final RemoveUtilSettings settings;
    private final BufferedWriter logWriter;

    private final MinoFactory minoFactory = new MinoFactory();
    private final ColorConverter colorConverter = new ColorConverter();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
    private final ILockedReachable lockedReachable = ReachableFacade.createLocked(
            minoFactory, minoShifter, minoRotation, HEIGHT, false
    );

    public RemoveUtilEntryPoint(RemoveUtilSettings settings) throws FinderInitializeException {
        this.settings = settings;

        // ログファイルの出力先を整備
        String logFilePath = settings.getLogFilePath();
        MyFile logFile = new MyFile(logFilePath);

        logFile.mkdirs();
        logFile.verify();

        try {
            this.logWriter = logFile.newBufferedWriter();
        } catch (IOException e) {
            throw new FinderInitializeException(e);
        }
    }

    @Override
    public void run() throws FinderException {
        for (String fumen : settings.getFumens()) {
            generate(fumen);
        }
    }

    private void generate(String fumen) throws FinderParseException, FinderExecuteException {
        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        List<TetfuPage> decoded = tetfu.decode(fumen);

        ArrayList<ColoredField> allResults = new ArrayList<>();
        for (TetfuPage page : decoded) {
//            System.out.println("###");
            List<Pair<ColoredField, SimpleOperation>> results = new ArrayList<>();
            generate(page.getField(), results);
            Collections.reverse(results);
            if (results.isEmpty()) {
                allResults.add(page.getField());
            } else {
                allResults.add(results.get(0).getKey());
                allResults.addAll(results.stream()
                        .map(it -> {
                            ColoredField coloredField = it.getKey().freeze();
                            SimpleOperation operation = it.getValue();
                            int cx = operation.getX();
                            int cy = operation.getY();

                            // cyより下でそろっている行数をカウント
                            Field field = FieldFactory.createField(coloredField, HEIGHT);
                            Field mask = FieldFactory.createField(HEIGHT);
                            for (int y = 0; y < cy; y++) {
                                for (int x = 0; x < 10; x++) {
                                    mask.setBlock(x, y);
                                }
                            }
                            field.mask(mask);
                            int slideY = field.clearLine();

                            // 揃っている行を削除
                            coloredField.clearLine();

                            // ライン消去を考慮したうえでoperationを再配置
                            Piece piece = operation.getPiece();
                            Rotate rotate = operation.getRotate();
                            ColorType colorType = colorConverter.parseToColorType(piece);
                            Mino mino = minoFactory.create(piece, rotate);
                            int[][] positions = mino.getPositions();
                            Arrays.stream(positions).forEach(position -> {
                                int x1 = cx + position[0];
                                int y1 = cy + position[1] - slideY;
                                coloredField.setColorType(colorType, x1, y1);
                            });

                            return coloredField;
                        })
                        .collect(Collectors.toList()));
            }
        }

        Tetfu encoder = new Tetfu(minoFactory, colorConverter);
        String encode = encoder.encode(allResults.stream()
                .map(TetfuElement::createFieldOnly)
                .collect(Collectors.toList()));
        output(String.format("v115@%s", encode));
    }

    private void generate(ColoredField coloredField, List<Pair<ColoredField, SimpleOperation>> results) {
        // Clear linesなければスキップ
        boolean foundClearLine = false;
        for (int y = 0; y < coloredField.getUsingHeight(); y++) {
            if (coloredField.isFilledLine(y)) {
                foundClearLine = true;
                break;
            }
        }

        if (!foundClearLine) {
            return;
        }

        // 配置されたOperationsをすべて取り出す
        HashSet<SimpleOperation> allOperations = new HashSet<>();
        for (int y = coloredField.getUsingHeight() - 1; 0 <= y; y--) {
            for (int x = 0; x < 10; x++) {
                ColorType colorType = coloredField.getColorType(x, y);
                Piece piece = colorConverter.parseToBlock(colorType);

                if (Objects.isNull(piece)) {
                    continue;
                }

                final int cx = x;
                final int cy = y;

                // (cx, cy)を中心として、当てはまる形はあるか？
                for (Rotate rotate : Rotate.values()) {
                    Mino mino = minoFactory.create(piece, rotate);
                    int[][] positions = mino.getPositions();
                    boolean ok = Arrays.stream(positions).allMatch(position -> {
                        int x1 = cx + position[0];
                        int y1 = cy + position[1];

                        if (x1 < 0 || 10 <= x1 || y1 < 0 || coloredField.getMaxHeight() < ~y1) {
                            return false;
                        }

                        return coloredField.getColorType(x1, y1) == colorType;
                    });

                    if (ok) {
                        Action transformed = minoShifter.createTransformedAction(piece, rotate, cx, cy);
                        SimpleOperation operation = new SimpleOperation(
                                piece, transformed.getRotate(), transformed.getX(), transformed.getY()
                        );
                        allOperations.add(operation);
                    }
                }
            }
        }

        // allOperationsの内、Clear Lineに関連するOperationsを取り出す
        HashSet<SimpleOperation> operationsWithClear = new HashSet<>();
        for (int y = coloredField.getUsingHeight() - 1; 0 <= y; y--) {
            if (coloredField.isFilledLine(y)) {
                for (int x = 0; x < 10; x++) {
                    for (SimpleOperation candidate : allOperations) {
                        LargeField blocks = new LargeField();
                        Mino mino = minoFactory.create(candidate.getPiece(), candidate.getRotate());
                        blocks.put(mino, candidate.getX(), candidate.getY());

                        if (!blocks.isEmpty(x, y)) {
                            operationsWithClear.add(candidate);
                        }
                    }
                }
            }
        }

        // operationsWithClearの内、ReachableなOperationsを取り出す
        // 最終候補
        HashSet<SimpleOperation> candidates = new HashSet<>();
        Field field = FieldFactory.createField(coloredField, HEIGHT);
        for (SimpleOperation candidate : operationsWithClear) {
            Field freeze = field.freeze();
            Mino mino = minoFactory.create(candidate.getPiece(), candidate.getRotate());
            int x = candidate.getX();
            int y = candidate.getY();
            freeze.remove(mino, x, y);
            if (lockedReachable.checks(freeze, mino, x, y, HEIGHT)) {
                candidates.add(candidate);
            }
        }

        // 最終候補をT>I>その他の順で取り出す
        // 複数該当する場合は最初に見つかったOperationが選択される
        boolean containsT = candidates.stream().anyMatch(candidate -> candidate.getPiece() == Piece.T);
        boolean containsI = candidates.stream().anyMatch(candidate -> candidate.getPiece() == Piece.I);

        Optional<SimpleOperation> selected;
        if (containsT) {
            selected = candidates.stream().filter(candidate -> candidate.getPiece() == Piece.T).findFirst();
        } else if (containsI) {
            selected = candidates.stream().filter(candidate -> candidate.getPiece() == Piece.I).findFirst();
        } else {
            selected = candidates.stream().findFirst();
        }

        // Operationが選択されていれば再起して探索
        selected.ifPresent(operation -> {
            ColoredField freeze = coloredField.freeze();

            Piece piece = operation.getPiece();
            Rotate rotate = operation.getRotate();
            Mino mino = minoFactory.create(piece, rotate);
            int[][] positions = mino.getPositions();
            int cx = operation.getX();
            int cy = operation.getY();
            Arrays.stream(positions).forEach(position -> {
                int x1 = cx + position[0];
                int y1 = cy + position[1];
                freeze.setColorType(ColorType.Empty, x1, y1);
            });

            results.add(new Pair<>(freeze, operation));

            generate(freeze, results);
        });
    }

    private void output(String str) throws FinderExecuteException {
        try {
            logWriter.append(str).append(LINE_SEPARATOR);
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }

        if (settings.isOutputToConsole())
            System.out.println(str);
    }

    private void flush() throws FinderExecuteException {
        try {
            logWriter.flush();
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }
    }

    @Override
    public void close() throws FinderTerminateException {
        try {
            flush();
            logWriter.close();
        } catch (IOException | FinderExecuteException e) {
            throw new FinderTerminateException(e);
        }
    }
}


