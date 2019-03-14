package entry.setup.filters;

import core.field.Field;

public class SimpleHolesWithFreeFilter implements SetupSolutionFilter {
    private final int maxHeight;
    private final Field freeField;

    public SimpleHolesWithFreeFilter(int maxHeight, Field freeField) {
        this.maxHeight = maxHeight;
        this.freeField = freeField;
    }

    @Override
    public boolean test(SetupResult result) {
        Field field = result.getTestField();
        Field freeze = field.freeze(maxHeight);

        // freezeをホールがない状態にする
        for (int y = 1; y < maxHeight; y *= 2) {
            Field down = freeze.freeze(maxHeight);
            down.slideDown(y);
            freeze.merge(down);
        }

        // freezeから本来の地形を引くことでホールだけが抽出される
        freeze.reduce(field);

        // ホールになっても良い部分を引く
        freeze.reduce(freeField);

        // ホールがまだ残っていたら除外
        return freeze.isPerfect();
    }
}
