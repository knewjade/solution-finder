package searcher.pack.separable_mino;

import common.datastore.MinoOperationWithKey;
import core.column_field.ColumnField;
import core.field.Field;
import core.mino.Mino;
import searcher.pack.SlideXOperationWithKey;

public class SlideXSeparableMino implements SeparableMino {
    private final SeparableMino separableMino;
    private final int slideX;

    public SlideXSeparableMino(SeparableMino separableMino, int slideX) {
        this.separableMino = separableMino;
        this.slideX = slideX;
    }

    @Override
    public int getLowerY() {
        return separableMino.getLowerY();
    }

    @Override
    public ColumnField getColumnField() {
        throw new UnsupportedOperationException("Cannot get slided column field");
    }

    @Override
    public Field getField() {
        throw new UnsupportedOperationException("Cannot get slided field");
    }

    @Override
    public MinoOperationWithKey toMinoOperationWithKey() {
        return new SlideXOperationWithKey(separableMino.toMinoOperationWithKey(), slideX);
    }
}
