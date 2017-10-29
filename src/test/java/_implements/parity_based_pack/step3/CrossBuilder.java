package _implements.parity_based_pack.step3;

import common.datastore.OperationWithKey;
import common.datastore.MinoOperationWithKey;
import core.field.Field;
import core.mino.Mino;
import _implements.parity_based_pack.step2.FullLimitedMino;
import searcher.pack.separable_mino.mask.MinoMask;
import searcher.common.validator.PerfectValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * マルチスレッド非対応
 */
public class CrossBuilder {
    private final List<List<FullLimitedMino>> sets;
    private final int maxClearLine;
    private final LineCounterField lineCounterField;
    private final List<List<OperationWithKey>> results = new ArrayList<>();
    private final int lastIndex;
    private final PerfectValidator perfectValidator;
    private final boolean[] isSame;

    // 結果記録用
    private final FullLimitedMino[] fullLimitedMinos;
    private final FullLimitedMino[] prev;
    private final List<List<XField>> cache = new ArrayList<>();

    // sets: 同じ種類のミノは連続させたほうが探索カットが有効になるため推奨
    public CrossBuilder(List<List<FullLimitedMino>> sets, Field field, int maxClearLine) {
        this.sets = sets;
        this.maxClearLine = maxClearLine;
        this.lineCounterField = new LineCounterField(field, maxClearLine);
        this.lastIndex = sets.size() - 1;
        this.perfectValidator = new PerfectValidator();

        this.isSame = new boolean[sets.size()];
        for (int index = 1; index < isSame.length; index++) {
            TreeSet<FullLimitedMino> current = new TreeSet<>(sets.get(index));
            TreeSet<FullLimitedMino> prev = new TreeSet<>(sets.get(index - 1));
            this.isSame[index] = prev.equals(current);
        }

        this.fullLimitedMinos = new FullLimitedMino[sets.size()];
        this.prev = new FullLimitedMino[sets.size()];
        for (int index = 0; index < sets.size(); index++)
            cache.add(new ArrayList<>());
        this.cache.get(0).add(XField.createFirst(field, sets.size()));
    }

    public List<List<OperationWithKey>> create() {
        assert results.isEmpty();
        createList(0);
        return results;
    }

    private void createList(int depth) {
        for (FullLimitedMino mino : sets.get(depth)) {
            if (isSame[depth] && 0 <= fullLimitedMinos[depth - 1].compareTo(mino))
                continue;

            int[][] blockCountEachLines = mino.getBlockCountEachLines();
            int[] parity = mino.getParity();
            lineCounterField.decrease(blockCountEachLines);
            lineCounterField.decrease(parity);
            if (lineCounterField.isValid()) {
                fullLimitedMinos[depth] = mino;
                if (lastIndex == depth) {
                    recordResult();
                } else {
                    createList(depth + 1);
                }
            }
            lineCounterField.increase(blockCountEachLines);
            lineCounterField.increase(parity);
        }
    }

    private void recordResult() {
        boolean isReuse = true;

        for (int index = 0; index < prev.length; index++) {
            if (!isReuse || fullLimitedMinos[index] != prev[index]) {
                isReuse = false;

                List<XField> xFields = cache.get(index);
                List<XField> nextXFields = new ArrayList<>();

                for (XField xField : xFields) {
                    Field field = xField.getField();
                    XHistory xHistory = xField.getxHistory();
                    FullLimitedMino current = fullLimitedMinos[index];
                    MinoMask minoMask = current.getMinoMask();
                    int[] xs = current.getXs();
                    for (int x : xs) {
                        Field mask = minoMask.getMinoMask(x);
                        if (field.canMerge(mask)) {
                            Field newField = field.freeze(maxClearLine);
                            newField.merge(mask);
                            if (perfectValidator.validate(newField, maxClearLine)) {
                                XHistory newHistory = xHistory.recordAndReturnNew(x);
                                nextXFields.add(new XField(newField, newHistory));
                            }
                        }
                    }
                }
                if (index < prev.length - 1) {
                    cache.set(index + 1, nextXFields);
                } else {
                    for (XField nextXField : nextXFields) {
                        int[] history = nextXField.getxHistory().getHistory();
                        assert fullLimitedMinos.length == history.length;
                        List<OperationWithKey> result = new ArrayList<>();
                        for (int i = 0, length = history.length; i < length; i++) {
                            FullLimitedMino limitedMino = fullLimitedMinos[i];
                            int x = history[i];

                            Mino mino = limitedMino.getMino();
                            long deleteKey = limitedMino.getDeleteKey();
                            long usingKey = limitedMino.getUsingKey();
                            int lowerY = limitedMino.getLowerY();
                            OperationWithKey withKey = new MinoOperationWithKey(mino, x, deleteKey, usingKey, lowerY);
                            result.add(withKey);
                        }
                        results.add(result);
                    }
                }
                prev[index] = fullLimitedMinos[index];
            }
        }
    }

//    private void search(Field field, int depth) {
//        if (depth == sets.getDepth()) {
//            List<OperationWithKey> result = new ArrayList<>();
//            for (int index = 0, length = fullLimitedMinos.length; index < length; index++) {
//                FullLimitedMino limitedMino = fullLimitedMinos[index];
//                int x = xs[index];
//
//                Mino mino = limitedMino.getMino();
//                long deleteKey = limitedMino.getDeleteKey();
//                int lowerY = limitedMino.getLowerY();
//                OperationWithKey withKey = new OperationWithKey(mino, x, deleteKey, lowerY);
//                result.add(withKey);
//            }
//            results.add(result);
//        } else {
//            FullLimitedMino limitedMino = sets.get(depth);
//            fullLimitedMinos[depth] = limitedMino;
//            MinoMask minoMask = limitedMino.getMinoMask();
//            for (int x : limitedMino.getXs()) {
//                xs[depth] = x;
//                Field mask = minoMask.getMinoMask(x);
//                if (field.canMerge(mask)) {
//                    field.merge(mask);
//                    if (perfectValidator.validate(field, maxClearLine))
//                        search(field, depth + 1);
//                    field.reduce(mask);
//                }
//            }
//        }
//    }
}

