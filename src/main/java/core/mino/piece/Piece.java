package core.mino.piece;

import common.ActionParser;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;

// TODO: write unittest
public class Piece {
    static final Piece EMPTY_PIECE = new Piece();

    private final Mino mino;
    private final int x;
    private final int y;
    private final Field minoField;
    private final Field harddropCollider;
    private final int hash;

    public Piece(Mino mino, int x, int y, int fieldHeight) {
        this.mino = mino;
        this.x = x;
        this.y = y;
        this.minoField = createMinoField();
        this.harddropCollider = createHarddropCollider(mino, x, y, fieldHeight);
        this.hash = ActionParser.parseToInt(mino.getBlock(), mino.getRotate(), x, y);
    }

    private Piece() {
        this.mino = new Mino(Block.I, Rotate.Spawn);
        this.x = -1;
        this.y = -1;
        this.minoField = FieldFactory.createField(1);
        this.harddropCollider = FieldFactory.createField(1);
        this.hash = -1;
    }

    private Field createMinoField() {
        Field field = FieldFactory.createField(y + mino.getMaxY() + 1);
        field.put(mino, x, y);
        return field;
    }

    private Field createHarddropCollider(Mino mino, int x, int y, int fieldHeight) {
        Field field = FieldFactory.createField(fieldHeight);
        for (int yIndex = y; yIndex < fieldHeight - mino.getMinY(); yIndex++)
            field.put(mino, x, yIndex);
        for (int yIndex = fieldHeight; yIndex < field.getMaxFieldHeight(); yIndex++)
            for (int xIndex = 0; xIndex < 10; xIndex++)
                field.removeBlock(xIndex, yIndex);
        return field;
    }

    public Field getMinoField() {
        return minoField;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "mino=" + mino +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public Mino getMino() {
        return mino;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Field getHarddropCollider() {
        return harddropCollider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return x == piece.x & y == piece.y & mino.equals(piece.mino);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
