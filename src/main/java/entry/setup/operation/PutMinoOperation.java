package entry.setup.operation;

import core.field.Field;
import core.mino.Mino;
import core.mino.Piece;
import core.srs.Rotate;

public class PutMinoOperation implements FieldOperation {
    private final Piece piece;
    private final Rotate rotate;
    private final int x;
    private final int y;

    public PutMinoOperation(Piece piece, Rotate rotate, int x, int y) {
        this.piece = piece;
        this.rotate = rotate;
        this.x = x;
        this.y = y;
    }

    @Override
    public void operate(Field field) {
        field.put(new Mino(piece, rotate), x, y);
    }
}
