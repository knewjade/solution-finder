package entry.setup.filters;

import core.field.Field;

public class StrictHolesFilter implements SetupSolutionFilter {
    private final int maxHeight;

    public StrictHolesFilter(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public boolean test(SetupResult result) {
        Field testField = result.getTestField();
        Field freeze = StrictHoles.fill(testField, maxHeight);
        return freeze.getNumOfAllBlocks() == maxHeight * 10;
    }
}
