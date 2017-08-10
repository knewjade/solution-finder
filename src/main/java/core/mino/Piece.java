package core.mino;

import core.field.Field;
import core.field.FieldFactory;

public class Piece {
    private final Mino mino;
    private final int x;
    private final int y;
    private final Field minoField;
    private final Field harddropCollider;

    public Piece(Mino mino, int x, int y, int fieldHeight) {
        this.mino = mino;
        this.x = x;
        this.y = y;
        this.minoField = createMinoField();
        this.harddropCollider = createHarddropCollider(mino, x, y, fieldHeight);
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
}
