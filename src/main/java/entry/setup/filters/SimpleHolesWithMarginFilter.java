package entry.setup.filters;

import core.field.Field;

public class SimpleHolesWithMarginFilter implements SetupSolutionFilter {
    private final int maxHeight;
    private final Field marginField;

    public SimpleHolesWithMarginFilter(int maxHeight, Field marginField) {
        this.maxHeight = maxHeight;
        this.marginField = marginField;
    }

    @Override
    public boolean test(SetupResult result) {
        Field field = result.getTestField();
        Field freeze = field.freeze(maxHeight);

        // freezeをホールがない状態にする
        for (int y = 0; y < maxHeight; y++) {
            Field down = freeze.freeze(maxHeight);
            down.slideDown();
            freeze.merge(down);
        }

        // freezeから本来の地形を引くことでホールだけが抽出される
        freeze.reduce(field);

        // ホールになっても良い部分を引く
        freeze.reduce(marginField);

        // ホールがまだ残っていたら除外
        return freeze.isPerfect();
    }
}
