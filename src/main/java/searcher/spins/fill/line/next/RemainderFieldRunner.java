package searcher.spins.fill.line.next;

import common.datastore.Pair;
import core.field.*;

import java.util.ArrayList;
import java.util.List;

public class RemainderFieldRunner {
    public List<RemainderField> extract(Field initField, int targetY) {
        int maxFieldHeight = initField.getMaxFieldHeight();

        Field remainderBlock = FieldFactory.createField(maxFieldHeight);
        long bitKey = KeyOperators.getBitKey(targetY);
        remainderBlock.insertBlackLineWithKey(bitKey);

        remainderBlock.reduce(initField);

        return extract(remainderBlock);
    }

    // ブロックを抽出
    private List<RemainderField> extract(Field remainderBlock) {
        int maxBoardCount = remainderBlock.getBoardCount();

        assert !remainderBlock.isPerfect();

        List<RemainderField> pairs = new ArrayList<>();

        do {
            Pair<RemainderField, Field> pair = calcRemainderFieldPair(remainderBlock, maxBoardCount);

            RemainderField remainderField = pair.getKey();
            pairs.add(remainderField);

            remainderBlock = pair.getValue();
        } while (!remainderBlock.isPerfect());

        assert 1 <= pairs.size();

        return pairs;
    }

    private Pair<RemainderField, Field> calcRemainderFieldPair(Field restBlock, int boardCount) {
        assert !restBlock.isPerfect();

        switch (boardCount) {
            case 1: {
                long low = restBlock.getBoard(0);

                {
                    long nextBoard = getNextBoard(low);
                    Field nextRestBlock = new SmallField(nextBoard);
                    return toRemainderFieldPair(low, nextBoard, nextRestBlock);
                }
            }
            case 2: {
                long low = restBlock.getBoard(0);
                long high = restBlock.getBoard(1);

                if (low != 0L) {
                    long nextBoard = getNextBoard(low);
                    Field nextRestBlock = new MiddleField(nextBoard, high);
                    return toRemainderFieldPair(low, nextBoard, nextRestBlock);
                }

                {
                    long nextBoard = getNextBoard(high);
                    Field nextRestBlock = new MiddleField(low, nextBoard);
                    return toRemainderFieldPair(high, nextBoard, nextRestBlock);
                }
            }
            case 4: {
                long low = restBlock.getBoard(0);
                long midLow = restBlock.getBoard(1);
                long midHigh = restBlock.getBoard(2);
                long high = restBlock.getBoard(3);

                if (low != 0L) {
                    long nextBoard = getNextBoard(low);
                    Field nextRestBlock = new LargeField(nextBoard, midLow, midHigh, high);
                    return toRemainderFieldPair(low, nextBoard, nextRestBlock);
                }

                if (midLow != 0L) {
                    long nextBoard = getNextBoard(midLow);
                    Field nextRestBlock = new LargeField(low, nextBoard, midHigh, high);
                    return toRemainderFieldPair(midLow, nextBoard, nextRestBlock);
                }

                if (midHigh != 0L) {
                    long nextBoard = getNextBoard(midHigh);
                    Field nextRestBlock = new LargeField(low, midLow, nextBoard, high);
                    return toRemainderFieldPair(midHigh, nextBoard, nextRestBlock);
                }

                {
                    long nextBoard = getNextBoard(high);
                    Field nextRestBlock = new LargeField(low, midLow, midHigh, nextBoard);
                    return toRemainderFieldPair(high, nextBoard, nextRestBlock);
                }
            }
        }

        throw new IllegalStateException();
    }

    private long getNextBoard(long board) {
        return ((board | (board - 1)) + 1) & board;
    }

    private Pair<RemainderField, Field> toRemainderFieldPair(long currentBoard, long nextBoard, Field nextRestBlock) {
        long targetBoard = currentBoard ^ nextBoard;
        int targetBlockCount = Long.bitCount(targetBoard);

        long board = targetBoard;
        board = board | (board >> 20);
        board = board | (board >> 20);
        board = board | (board >> 10);
        long lowerBit = board & (-board);
        int minX = BitOperators.bitToX(lowerBit);

        RemainderField remainderField = new RemainderField(minX, targetBlockCount);
        return new Pair<>(remainderField, nextRestBlock);
    }
}

