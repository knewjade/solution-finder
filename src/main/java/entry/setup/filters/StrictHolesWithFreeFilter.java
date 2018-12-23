package entry.setup.filters;

import core.field.Field;

public class StrictHolesWithFreeFilter implements SetupSolutionFilter {
    private final int maxHeight;
    private final Field freeField;

    public StrictHolesWithFreeFilter(int maxHeight, Field freeField) {
        this.maxHeight = maxHeight;
        this.freeField = freeField;
    }

    @Override
    public boolean test(SetupResult result) {
        Field testField = result.getTestField();
        Field freeze = StrictHoles.fill(testField, maxHeight);

        // マージン上で空白の部分（ホール）があれば塗りつぶす
        freeze.merge(freeField);

        // すべてが塗りつぶされていないときは除外
        return freeze.getNumOfAllBlocks() == maxHeight * 10;
    }
}
