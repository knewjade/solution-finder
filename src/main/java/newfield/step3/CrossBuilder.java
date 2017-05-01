package newfield.step3;

import core.field.Field;
import newfield.step2.FullLimitedMino;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * マルチスレッド非対応
 */
public class CrossBuilder {
    private final List<List<FullLimitedMino>> sets;
    private final FullLimitedMino[] fullLimitedMinos;
    private final LineCounterField lineCounterField;
    private final List<List<FullLimitedMino>> results = new ArrayList<>();

    public CrossBuilder(List<List<FullLimitedMino>> sets, Field field, int maxClearLine) {
        this.sets = sets;
        this.fullLimitedMinos = new FullLimitedMino[sets.size()];
        this.lineCounterField = new LineCounterField(field, maxClearLine);
    }

    public List<List<FullLimitedMino>> create() {
        assert results.isEmpty();
        createList(0);
        return Collections.unmodifiableList(results);
    }

    private void createList(int depth) {
        if (fullLimitedMinos.length == depth) {
            ArrayList<FullLimitedMino> result = new ArrayList<>();
            Collections.addAll(result, fullLimitedMinos);
            results.add(result);
        } else {
            for (FullLimitedMino mino : sets.get(depth)) {
                int[][] blockCountEachLines = mino.getBlockCountEachLines();
                lineCounterField.decrease(blockCountEachLines);
                if (lineCounterField.isValid()) {
                    fullLimitedMinos[depth] = mino;
                    createList(depth + 1);
                }
                lineCounterField.increase(blockCountEachLines);
            }
        }
    }
}
