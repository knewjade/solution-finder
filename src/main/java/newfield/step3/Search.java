package newfield.step3;

import core.field.Field;
import newfield.step2.FullLimitedMino;
import searcher.common.validator.PerfectValidator;

import java.util.Comparator;
import java.util.List;

public class Search {
    private final List<List<FullLimitedMino>> sets;
    private final Field initField;
    private final int maxDepth;
    private final PerfectValidator perfectValidator;
    private final FullLimitedMino[] fullLimitedMinos;
    private final int[] xs;
    private int maxClearLine = 4;  // TODO

    public Search(Field initField, List<List<FullLimitedMino>> sets) {
        this.initField = initField.freeze(initField.getMaxFieldHeight());
        sets.sort(Comparator.comparingInt(List::size));
        this.sets = sets;
        this.maxDepth = sets.size();
        this.perfectValidator = new PerfectValidator();
        this.fullLimitedMinos = new FullLimitedMino[maxDepth];
        this.xs = new int[maxDepth];
    }

    public void search() {
        search(initField, 0);
    }

    private void search(Field field, int depth) {
        if (depth == maxDepth) {
//            System.out.println("---");
//            System.out.println(Arrays.toString(fullLimitedMinos));
//            System.out.println(Arrays.toString(xs));
            return;
        }
//        System.out.println(FieldView.toString(field));
        List<FullLimitedMino> limitedMinos = sets.get(depth);
        for (FullLimitedMino limitedMino : limitedMinos) {
            fullLimitedMinos[depth] = limitedMino;
            MinoMask minoMask = limitedMino.getMinoMask();
            for (int x : limitedMino.getXs()) {
                xs[depth] = x;
                Field mask = minoMask.getMinoMask(x);
//                System.out.println(FieldView.toString(mask));
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
