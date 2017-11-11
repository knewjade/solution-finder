package _implements.parity_based_pack.step3;

import core.field.Field;

public  class XField {
    static XField createFirst(Field field, int max) {
        return new XField(field, new XHistory(max));
    }

    private final Field field;

    private final XHistory xHistory;

    XField(Field field, XHistory xHistory) {
        this.field = field;
        this.xHistory = xHistory;
    }

    public Field getField() {
        return field;
    }

    public XHistory getxHistory() {
        return xHistory;
    }
}
