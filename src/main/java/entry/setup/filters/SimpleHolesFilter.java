package entry.setup.filters;

import core.field.Field;

public class SimpleHolesFilter implements SetupSolutionFilter {
    private final int maxHeight;

    public SimpleHolesFilter(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public boolean test(SetupResult result) {
        Field field = result.getTestField();
        Field freeze = field.freeze(maxHeight);
        freeze.slideDown();
        return field.contains(freeze);
    }
}
