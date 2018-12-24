package searcher;

import common.OperationHistory;
import common.datastore.action.Action;
import core.action.candidate.RotateCandidate;
import core.field.BlockCounter;
import core.field.Field;
import core.field.FieldHelper;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.setup.filters.StrictHoles;
import searcher.core.ValidationParameter;
import searcher.core.ValidationResultState;

import java.util.Set;

// マルチスレッド非対応
public class TSpinSearchValidator implements SearchValidator {
    private final int maxBoardCount;
    private final RotateCandidate candidate;
    private final int maxPieceNum;  // 利用できるミノの最大個数 (Tスピンに利用するTは除く)
    private final MinoFactory minoFactory;

    public TSpinSearchValidator(MinoFactory minoFactory, MinoShifter minoShifter, MinoRotation minoRotation, int maxHeight, int maxPieceNum) {
        this.minoFactory = minoFactory;
        this.maxBoardCount = (maxHeight / 6) + 1;
        this.maxPieceNum = maxPieceNum;
        this.candidate = new RotateCandidate(minoFactory, minoShifter, minoRotation, maxHeight);
    }

    @Override
    public ValidationResultState check(ValidationParameter parameter) {
        Field originalField = parameter.freezeField();
        OperationHistory history = parameter.getOrder().getHistory();
        int maxClearLine = parameter.getMaxClearLine();

        int usingPieceNum = history.getNextIndex() + 1;
        int leftPieceNum = maxPieceNum - usingPieceNum;

        if (leftPieceNum != 0) {
            // すべてのミノを使い切っていない
            if (!validate(originalField, maxClearLine, leftPieceNum)) {
                // この後もTスピンできない地形
                return ValidationResultState.Prune;
            }

            if (canSpinT(originalField, maxClearLine)) {
//                 解である
                return ValidationResultState.Result;
            }

            // 解ではないが、可能性はある地形
            return ValidationResultState.Valid;
        } else {
            // T以外のすべてのミノを使い切った
            // 解である
            if (canSpinT(originalField, maxClearLine)) {
                return ValidationResultState.Result;
            }

            // Tスピンできない地形
            return ValidationResultState.Prune;
        }
    }

    private boolean validate(Field field, int maxClearLine, int leftPieceNum) {
        Field testField = StrictHoles.fill(field, maxClearLine);
        return checksSpannableValidField(field, testField, maxClearLine, leftPieceNum);
    }

    private boolean checksSpannableValidField(Field originalField, Field testField, int maxClearLine, int leftPieceNum) {
        // ミノを置ける領域
        // 行ごとのブロック数
        long[] rowBlocks1 = BlockCounter.countRowBlocksToArray(testField, maxBoardCount);

        // 最大限、ミノを置いたとしたときの領域
        Field freeze = originalField.freeze(maxClearLine);
        freeze.merge(testField);

        // 行ごとのブロック数
        long[] rowBlocks2 = BlockCounter.countRowBlocksToArray(freeze, maxBoardCount);

        // 元のフィールド
        // 行ごとのブロック数
        long[] rowBlocks3 = BlockCounter.countRowBlocksToArray(originalField, maxBoardCount);

        // ミノを置ける領域 (置ける領域が空ブロック)
        Field inverseTestField = testField.freeze();
        inverseTestField.inverse();
        inverseTestField.merge(originalField);

        for (Rotate rotate : Rotate.values()) {
            Mino mino = minoFactory.create(Piece.T, rotate);

            // 最小値・最大値
            int minY = mino.getMinY();
            int maxY = mino.getMaxY();
            int minX = mino.getMinX();
            int maxX = mino.getMaxX();
            int numRow = maxY - minY + 1;

            // 必要な行ごとのブロック数
            int[] needRow = new int[numRow];
            for (int[] position : mino.getPositions()) {
                needRow[position[1] - minY] += 1;
            }

            // 行ごと
            LOOP_Y:
            for (int y = -minY; y < maxClearLine - maxY; y++) {
                int lowerY = y + minY;

                // 行にミノを置くスペースがあるか
                for (int iy = 0; iy < needRow.length; iy++) {
                    if (rowBlocks1[lowerY + iy] < needRow[iy]) {
                        continue LOOP_Y;
                    }
                }

                // 列ごと
                for (int x = -minX; x < 10 - maxX; x++) {
                    // これから置ける領域にミノを置くスペースがあるか
                    if (!inverseTestField.canPut(mino, x, y)) {
                        continue;
                    }

                    // その行を最大限、埋められたと仮定したとき
                    // 揃う可能性のある行があるか
                    boolean filled = isFilled(rowBlocks2, lowerY);
                    if (!filled) {
                        continue LOOP_Y;
                    }

                    // 埋めるのに必要なブロック数は
                    long leastNeedBlock = getLeastNeedBlock(rowBlocks3, needRow, lowerY);
                    if (leastNeedBlock <= leftPieceNum * 4) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    // すでにあるフィールド + 置ける可能性のある場所 で、行が揃うとき true
    private boolean isFilled(long[] rowBlocks2, int lowerY) {
        for (int iy = 0; iy < rowBlocks2.length; iy++) {
            if (rowBlocks2[lowerY + iy] == 10) {
                return true;
            }
        }
        return false;
    }

    // Tスピンできる地形であるか
    private boolean canSpinT(Field field, int maxClearLine) {
        Set<Action> actions = candidate.search(field, Piece.T, maxClearLine);

        for (Action action : actions) {
            int x = action.getX();
            int y = action.getY();
            if (FieldHelper.isTSpin(field, x, y)) {
                Field freeze = field.freeze(maxClearLine);
                freeze.put(minoFactory.create(Piece.T, action.getRotate()), x, y);
                int clearedLine = freeze.clearLine();
                if (0 < clearedLine) {
                    return true;
                }
            }
        }

        return false;
    }

    // さらに必要なブロック数を計算
    // 1行揃うまでに一番少ないブロック数
    private long getLeastNeedBlock(long[] rowBlocks3, int[] needRow, int lowerY) {
        long needMinBlock = 10L;
        for (int indexY = 0; indexY < needRow.length; indexY++) {
            long needBlock = 10 - (rowBlocks3[lowerY + indexY] + needRow[indexY]);
            if (needBlock < needMinBlock) {
                needMinBlock = needBlock;
            }
        }
        return needMinBlock;
    }
}
