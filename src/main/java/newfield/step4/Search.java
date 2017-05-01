package newfield.step4;

import core.field.Field;
import core.mino.Mino;
import misc.OperationWithKey;
import newfield.step2.FullLimitedMino;
import searcher.common.validator.PerfectValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * マルチスレッド非対応
 */
public class Search {
    private final Field initField;
    private final List<FullLimitedMino> sets;
    private final int maxClearLine;
    private final PerfectValidator perfectValidator;
    private final List<List<OperationWithKey>> results = new ArrayList<>();

    // 結果を一時的に保存
    private final FullLimitedMino[] fullLimitedMinos;
    private final int[] xs;

    public Search(Field initField, List<FullLimitedMino> sets, int maxClearLine) {
        this.initField = initField.freeze(initField.getMaxFieldHeight());
        this.sets = sets;
        this.maxClearLine = maxClearLine;
        this.perfectValidator = new PerfectValidator();

        int maxDepth = sets.size();
        this.fullLimitedMinos = new FullLimitedMino[maxDepth];
        this.xs = new int[maxDepth];
    }

    public List<List<OperationWithKey>> search() {
        assert results.isEmpty();
        search(initField, 0);
        return Collections.unmodifiableList(results);
    }

    private void search(Field field, int depth) {
        if (depth == sets.size()) {
            List<OperationWithKey> result = new ArrayList<>();
            for (int index = 0, length = fullLimitedMinos.length; index < length; index++) {
                FullLimitedMino limitedMino = fullLimitedMinos[index];
                int x = xs[index];

                Mino mino = limitedMino.getMino();
                long deleteKey = limitedMino.getDeleteKey();
                int lowerY = limitedMino.getLowerY();
                OperationWithKey withKey = new OperationWithKey(mino, x, deleteKey, lowerY);
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
