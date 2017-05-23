package _experimental.newfield.step4;

import common.datastore.OperationWithKey;
import core.field.Field;
import core.mino.Mino;
import common.datastore.SimpleOperationWithKey;
import _experimental.newfield.step2.FullLimitedMino;
import _experimental.newfield.step2.FullLimitedMinos;
import searcher.common.validator.PerfectValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * マルチスレッド非対応
 */
public class Search {
    private final Field initField;
    private final FullLimitedMinos sets;
    private final int maxClearLine;
    private final PerfectValidator perfectValidator;
    private final List<List<OperationWithKey>> results = new ArrayList<>();

    // 結果を一時的に保存
    private final FullLimitedMino[] fullLimitedMinos;
    private final int[] xs;

    public Search(Field initField, FullLimitedMinos sets, int maxClearLine) {
        this.initField = initField.freeze(initField.getMaxFieldHeight());
        this.sets = sets;
        this.maxClearLine = maxClearLine;
        this.perfectValidator = new PerfectValidator();

        int maxDepth = sets.getDepth();
        this.fullLimitedMinos = new FullLimitedMino[maxDepth];
        this.xs = new int[maxDepth];
    }

    public List<List<OperationWithKey>> search() {
        assert results.isEmpty();
        search(initField, 0);
        return Collections.unmodifiableList(results);
    }

    private void search(Field field, int depth) {
        if (depth == sets.getDepth()) {
            List<OperationWithKey> result = new ArrayList<>();
            for (int index = 0, length = fullLimitedMinos.length; index < length; index++) {
                FullLimitedMino limitedMino = fullLimitedMinos[index];
                int x = xs[index];

                Mino mino = limitedMino.getMino();
                long deleteKey = limitedMino.getDeleteKey();
                long usingKey = limitedMino.getUsingKey();
                int lowerY = limitedMino.getLowerY();
                OperationWithKey withKey = new SimpleOperationWithKey(mino, x, deleteKey, usingKey, lowerY);
                result.add(withKey);
            }
            results.add(result);
        } else {
            FullLimitedMino limitedMino = sets.get(depth);
            fullLimitedMinos[depth] = limitedMino;
            MinoMask minoMask = limitedMino.getMinoMask();
            for (int x : limitedMino.getXs()) {
                xs[depth] = x;
                Field mask = minoMask.getMinoMask(x);
                if (field.canMerge(mask)) {
                    field.merge(mask);
                    if (perfectValidator.validate(field, maxClearLine))
                        search(field, depth + 1);
                    field.reduce(mask);
                }
            }
        }
    }
}
