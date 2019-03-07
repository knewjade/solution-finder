package searcher.spins.wall;

import core.field.Field;

public class MaskField {
    private final Field rest;
    private final Field notAllowed;

    MaskField(Field rest, Field notAllowed) {
        this.rest = rest;
        this.notAllowed = notAllowed;
    }

    public Field getRemain() {
        return rest;
    }

    public Field getNotAllowed() {
        return notAllowed;
    }
}
