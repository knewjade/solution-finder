package searcher;

import common.OperationHistory;
import common.datastore.Result;
import common.datastore.action.Action;
import common.datastore.order.NormalOrder;
import common.datastore.order.Order;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.action.candidate.RotateCandidate;
import core.field.BlockCounter;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;
import searcher.checkmate.CheckmateDataPool;
import searcher.common.DataPool;
import searcher.common.From;
import searcher.core.SearcherCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

class Temp {
    public Field field;  // 接着後のフィールド
    public long deletedKey;  // 接着後の消去されたライン
    public int maxClearLine;  // 残りのライン消去数
    public boolean isLast;
    public Mino mino;
    public int x;
    public int y;
    public Order order;
}

enum ValidationState {
    Valid,
    Result,
    Prune,
}

class SampleSearcherCore<T extends Action, O extends Order> implements SearcherCore<T, O> {
    private final MinoFactory minoFactory;
    private final Validator2 validator;
    private final DataPool<Order, Result> dataPool;
    private final Temp temp = new Temp();

    public SampleSearcherCore(MinoFactory minoFactory, Validator2 validator, DataPool<Order, Result> dataPool) {
        this.minoFactory = minoFactory;
        this.validator = validator;
        this.dataPool = dataPool;
    }

    public void stepWithNext(Candidate<T> candidate, Piece drawn, O order, boolean isLast) {
        Piece hold = order.getHold();
        step(candidate, drawn, hold, order, isLast);

        if (drawn != hold) {
            // Holdの探索
            step(candidate, hold, drawn, order, isLast);
        }
    }

    public void stepWithNextNoHold(Candidate<T> candidate, Piece drawn, O order, boolean isLast) {
        step(candidate, drawn, order.getHold(), order, isLast);
    }

    public void stepWhenNoNext(Candidate<T> candidate, O order, boolean isLast) {
        Piece hold = order.getHold();
        step(candidate, hold, null, order, isLast);
    }

    private void step(Candidate<T> candidate, Piece drawn, Piece nextHold, Order order, boolean isLast) {
        Field currentField = order.getField();
        int max = order.getMaxClearLine();
        Set<T> candidateList = candidate.search(currentField, drawn, max);

        OperationHistory history = order.getHistory();
        temp.order = order;
        temp.isLast = isLast;
        for (T action : candidateList) {
            Field field = currentField.freeze(max);
            Mino mino = minoFactory.create(drawn, action.getRotate());
            int x = action.getX();
            int y = action.getY();
            field.put(mino, x, y);
            long deletedKey = field.clearLineReturnKey();
            int maxClearLine = max - Long.bitCount(deletedKey);

            temp.field = field;
            temp.deletedKey = deletedKey;
            temp.maxClearLine = maxClearLine;
            temp.mino = mino;
            temp.x = x;
            temp.y = y;

            switch (validator.check(temp)) {
                case Result: {
                    Result result = new Result(order, drawn, action, nextHold);
                    dataPool.addResult(result);
                    continue;
                }
                case Prune: {
                    continue;
                }
            }

            if (isLast)
                continue;

            OperationHistory nextHistory = history.recordAndReturnNew(drawn, action);
            Order nextOrder = new NormalOrder(field, nextHold, maxClearLine, nextHistory);
            dataPool.addOrder(nextOrder);
        }
    }
}

interface Validator2 {
    // 最終条件を満たしたフィールドなら Result を返却
    // 最終的な解ではないが、有効なフィールドなら Valid を返却
    // Prune なら枝刈り対象
    ValidationState check(Temp temp);
}

class SampleValidator implements Validator2 {
    private final int maxHeight;
    private final int maxBoardCount;
    private final RotateCandidate candidate;
    private final int maxPiece;
    private final MinoFactory minoFactory;

    SampleValidator(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxHeight, int maxPiece) {
        this.minoFactory = minoFactory;
        this.maxHeight = maxHeight;
        this.maxBoardCount = (maxHeight / 6) + 1;
        this.maxPiece = maxPiece;
        this.candidate = new RotateCandidate(minoFactory, minoShifter, minoRotation, maxHeight);
    }

    @Override
    public ValidationState check(Temp temp) {
        if (!validate(temp)) {
            return ValidationState.Prune;
        }

        if (!temp.isLast) {
            return ValidationState.Valid;
        }

        if (satisfies(temp)) {
            return ValidationState.Result;
        }

        return ValidationState.Prune;
    }

    private boolean validate(Temp temp) {
        StrictHoles holes = new StrictHoles(maxHeight);
        Field originalField = temp.field;
        Field testField = holes.test(originalField);
        int userPieceNum = temp.order.getHistory().getNextIndex() + 1;
        return getField(originalField, testField, maxHeight, maxPiece - userPieceNum);
    }

    private boolean satisfies(Temp temp) {
        Set<Action> actions = candidate.search(temp.field, Piece.T, temp.maxClearLine);

        for (Action action : actions) {
            int x = action.getX();
            int y = action.getY();
            if (y <8) continue;
            if (isTSpin(temp.field, x, y)) {
                Field freeze = temp.field.freeze(maxHeight);
                freeze.put(minoFactory.create(Piece.T, action.getRotate()), x, y);
                int c = freeze.clearLine();
                if (0 < c) {
                    System.out.println("#");
                    System.out.println(FieldView.toString(temp.field));
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isTSpin(Field field, int x, int y) {
        return 3L <= Stream.of(
                isBlock(field, x - 1, y - 1),
                isBlock(field, x - 1, y + 1),
                isBlock(field, x + 1, y - 1),
                isBlock(field, x + 1, y + 1)
        ).filter(Boolean::booleanValue).count();
    }

    private boolean isBlock(Field field, int x, int y) {
        if (x < 0 || 10 <= x || y < 0) {
            return true;
        }
        return !field.isEmpty(x, y);
    }

    private boolean getField(Field originalField, Field testField, int maxHeight, int restPieceNum) {
        // ミノを置ける領域
        // 列ごとのブロック数
        long columnBits1 = BlockCounter.countColumnBlocks(testField);
        long[] columnBlocks1 = BlockCounter.parseColumnIndexToArray(columnBits1);

        // 行ごとのブロック数
        long[] rowBlocks1 = BlockCounter.countRowBlocksToArray(testField, maxBoardCount);

        // 最大限、ミノを置いたとしたときの領域
        Field freeze = originalField.freeze(maxHeight);
        freeze.merge(testField);

        // 行ごとのブロック数
        long[] rowBlocks2 = BlockCounter.countRowBlocksToArray(freeze, maxBoardCount);

        // 元のフィールド
        // 行ごとのブロック数
        long[] rowBlocks3 = BlockCounter.countRowBlocksToArray(originalField, maxBoardCount);

        for (Rotate rotate : Rotate.values()) {
            Mino mino = minoFactory.create(Piece.T, rotate);

            // 最小値・最大値
            int minY = mino.getMinY();
            int maxY = mino.getMaxY();
            int minX = mino.getMinX();
            int maxX = mino.getMaxX();
            int numColumn = maxX - minX + 1;
            int numRow = maxY - minY + 1;

            // 必要な行ごとのブロック数
            int[] needColumn = new int[numColumn];
            int[] needRow = new int[numRow];
            for (int[] position : mino.getPositions()) {
                needColumn[position[0] - minX] += 1;
                needRow[position[1] - minY] += 1;
            }

            // 行ごと
            LOOP_Y:
            for (int y = -minY; y < maxHeight - maxY; y++) {
                // 行にミノを置くスペースがあるか
                for (int iy = 0; iy < needRow.length; iy++) {
                    if (rowBlocks1[y + minY + iy] < needRow[iy]) {
                        continue LOOP_Y;
                    }
                }

                // 列ごと
                LOOP_X:
                for (int x = -minX; x < 10 - maxX; x++) {
                    // 列にミノを置くスペースがあるか
                    for (int ix = 0; ix < needColumn.length; ix++) {
                        if (columnBlocks1[x + minX + ix] < needColumn[ix]) {
                            continue LOOP_X;
                        }
                    }

                    // その行を最大限、埋められたと仮定したとき
                    // 揃う可能性のある行があるか
                    boolean is = false;
                    for (int iy = 0; iy < needRow.length; iy++) {
                        if (rowBlocks2[y + minY + iy] == 10) {
                            is = true;
                        }
                    }

                    if (!is) {
                        continue;
                    }

                    // 埋めるのに必要なブロック数は？
                    long needMinBlock = 10L;
                    for (int iy = 0; iy < needRow.length; iy++) {
                        long needBlock = 10 - (rowBlocks3[y + minY + iy] + needRow[iy]);
                        if (needBlock < needMinBlock) {
                            needMinBlock = needBlock;
                        }
                    }

                    if (needMinBlock <= restPieceNum * 4) {
                        return true;
                    }
                }
            }

        }

        return false;
    }
}

class StrictHoles {
    private static final int FIELD_WIDTH = 10;

    private final int maxHeight;

    public StrictHoles(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Field test(Field field) {
        Field freeze = field.freeze(maxHeight);
        for (int x = 0; x < FIELD_WIDTH; x++)
            putAndMove(freeze, x, maxHeight - 1, From.None);
        freeze.reduce(field);
        return freeze;
    }

    private void putAndMove(Field field, int x, int y, From from) {
        // 壁なら終了
        if (!field.isEmpty(x, y))
            return;

        // 自分自身を塗りつぶす
        field.setBlock(x, y);

        // 移動する
        if (0 <= y - 1) {
            putAndMove(field, x, y - 1, From.None);
        }

        if (from != From.Right && x + 1 < FIELD_WIDTH) {
            putAndMove(field, x + 1, y, From.Left);
        }

        if (from != From.Left && 0 <= x - 1) {
            putAndMove(field, x - 1, y, From.Right);
        }
    }
}

class PutterUsingHoldTest {
    @Test
    void name() {
        int maxHeight = 12;
        List<Piece> pieces = Arrays.asList(Piece.J, Piece.L, Piece.O,Piece.S, Piece.Z, Piece.I);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        SampleValidator validator = new SampleValidator(minoFactory, minoShifter, minoRotation, maxHeight, pieces.size());
        CheckmateDataPool dataPool = new CheckmateDataPool();
        SampleSearcherCore<Action, Order> core = new SampleSearcherCore<>(minoFactory, validator, dataPool);
        PutterNoHold<Action> putter = new PutterNoHold<>(dataPool, core);

        LockedCandidate candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxHeight);
        putter.first(FieldFactory.createField(maxHeight), pieces, candidate, maxHeight, pieces.size());

        ArrayList<Result> results = putter.getResults();
        System.out.println("# " + results.size());
//        for (Result result : results) {
//            System.out.println(result);
//        }
    }
}