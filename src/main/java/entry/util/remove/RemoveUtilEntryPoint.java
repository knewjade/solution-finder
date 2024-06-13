package entry.util.remove;

import common.SpinChecker;
import common.datastore.Pair;
import common.datastore.SimpleOperation;
import common.datastore.action.Action;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldView;
import core.action.reachable.ILockedReachable;
import core.action.reachable.ReachableFacade;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.field.LargeField;
import core.mino.*;
import core.srs.MinoRotation;
import core.srs.MinoRotationDetail;
import core.srs.Rotate;
import entry.EntryPoint;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import entry.path.output.MyFile;
import exceptions.*;
import searcher.spins.spin.Spin;

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
    private final SpinChecker spinChecker = new SpinChecker(
            minoFactory, new MinoRotationDetail(minoFactory, minoRotation), lockedReachable
    );

    static class Record {
        private final MinoFactory minoFactory;
        private final SpinChecker spinChecker;

        List<Pair<ColoredField, SimpleOperation>> bestOperationPairs = new ArrayList<>();
        int bestScore = -1;

        Record(MinoFactory minoFactory, SpinChecker spinChecker) {
            this.minoFactory = minoFactory;
            this.spinChecker = spinChecker;
        }

        public void updateIfBest(LinkedList<Pair<ColoredField, SimpleOperation>> operationPairs) {
            if (operationPairs.isEmpty()) {
                return;
            }

            if (bestScore < 0) {
                bestOperationPairs = new ArrayList<>(operationPairs);
                bestScore = calcScore(operationPairs);
                return;
            }

            int score = calcScore(operationPairs);
            if (bestScore < score) {
                // 同じスコアは探索順を優先する
                bestOperationPairs = new ArrayList<>(operationPairs);
                bestScore = score;
            }
        }

        private int calcScore(LinkedList<Pair<ColoredField, SimpleOperation>> operationPairs) {
            boolean b2b = true;
            int score = 0;

            for (Pair<ColoredField, SimpleOperation> pair : operationPairs) {
                ColoredField coloredField = pair.getKey();
                Field field = FieldFactory.createField(coloredField, HEIGHT);

                SimpleOperation operation = pair.getValue();
                Piece piece = operation.getPiece();
                Rotate rotate = operation.getRotate();
                Mino mino = minoFactory.create(piece, rotate);

                int clearedLines;
                {
                    Field freezeAfter = field.freeze();
                    freezeAfter.put(mino, operation.getX(), operation.getY());
                    int clearedLinesAfter = freezeAfter.clearLine();

                    Field freezeBefore = field.freeze();
                    int clearedLinesBefore = freezeBefore.clearLine();

                    clearedLines = clearedLinesAfter - clearedLinesBefore;
                }

                if (0 < clearedLines) {
                    Optional<Spin> spin = Optional.empty();
                    if (piece == Piece.T) {
                        spin = spinChecker.check(field, operation, HEIGHT, clearedLines);
                    }

                    if (spin.isPresent()) {
                        switch (spin.get().getSpin()) {
                            case Regular: {
                                score += clearedLines * 2;
                                break;
                            }
                            case Mini: {
                                score += clearedLines - 1;
                                break;
                            }
                        }
                        score += b2b ? 1 : 0;
                        b2b = true;
                    } else {
                        if (clearedLines == 4) {
                            score += 4;
                            score += b2b ? 1 : 0;
                            b2b = true;
                        } else {
                            score += clearedLines - 1;
                            b2b = false;
                        }
                    }
                }
            }

            return score;
        }

        public List<Pair<ColoredField, SimpleOperation>> get() {
//            System.out.println(bestOperationPairs);
            return bestOperationPairs;
        }
    }

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
            List<Pair<ColoredField, SimpleOperation>> results = generate(page.getField());
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

    private List<Pair<ColoredField, SimpleOperation>> generate(ColoredField coloredField) {
        LinkedList<Pair<ColoredField, SimpleOperation>> temps = new LinkedList<>();
        Record record = new Record(minoFactory, spinChecker);
        generate(coloredField, temps, record::updateIfBest);
        return record.get();
    }

    private void generate(
            ColoredField coloredField,
            LinkedList<Pair<ColoredField, SimpleOperation>> temps,
            Consumer<LinkedList<Pair<ColoredField, SimpleOperation>>> consumer
    ) {
        // Clear linesなければスキップ
        boolean foundClearLine = false;
        for (int y = 0; y < coloredField.getUsingHeight(); y++) {
            if (coloredField.isFilledLine(y)) {
                foundClearLine = true;
                break;
            }
        }

        if (!foundClearLine) {
            consumer.accept(temps);
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

        if (candidates.isEmpty()) {
            consumer.accept(temps);
            return;
        }

        // candidatesをT > I > その他の順にソート
        List<SimpleOperation> sortedCandidates = candidates.stream()
                .sorted(Comparator.comparing((SimpleOperation operation) -> {
                    Piece piece = operation.getPiece();
                    if (piece == Piece.T) {
                        return 0;
                    } else if (piece == Piece.I) {
                        return 1;
                    } else {
                        return 2;
                    }
                }))
                .collect(Collectors.toList());

//        System.out.println(sortedCandidates);

        // 最終候補を再帰的に探索
        for (SimpleOperation operation : sortedCandidates) {
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

            temps.addFirst(new Pair<>(freeze.freeze(), operation));
            generate(freeze, temps, consumer);
            temps.removeFirst();
        }
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


