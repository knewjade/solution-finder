package core.mino;

import common.datastore.SimpleMinoOperation;
import common.datastore.action.Action;
import common.datastore.action.MinimalAction;
import core.srs.Rotate;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class MinoTransform {
    private final int[] offsetsX = new int[4];
    private final int[] offsetsY = new int[4];
    private final Rotate[] rotates = new Rotate[4];
    private final EnumMap<Rotate, List<Rotate>> reverseMap;

    public MinoTransform() {
        this.reverseMap = createReverseMap();
        for (Rotate rotate : Rotate.values())
            set(rotate, 0, 0, rotate);
        refresh();
    }

    private EnumMap<Rotate, List<Rotate>> createReverseMap() {
        EnumMap<Rotate, List<Rotate>> map = new EnumMap<>(Rotate.class);
        for (Rotate rotate : Rotate.values())
            map.put(rotate, new ArrayList<>());
        return map;
    }

    void set(Rotate rotate, int offsetX, int offsetY, Rotate newRotate) {
        int index = rotate.getNumber();
        offsetsX[index] = offsetX;
        offsetsY[index] = offsetY;
        rotates[index] = newRotate;
        refresh();
    }

    private void refresh() {
        for (List<Rotate> reverse : reverseMap.values())
            reverse.clear();

        for (Rotate rotate : Rotate.values()) {
            int index = rotate.getNumber();
            Rotate newRotate = rotates[index];
            if (newRotate != null && rotate != newRotate) {
                // 変換後の回転が同じになる、他の回転とも関連づける
                for (Rotate r : reverseMap.get(newRotate)) {
                    reverseMap.get(r).add(rotate);
                    reverseMap.get(rotate).add(r);
                }

                // 変換前と変換後を関連づける
                reverseMap.get(newRotate).add(rotate);
                reverseMap.get(rotate).add(newRotate);
            }
        }
    }

    Action transform(int x, int y, Rotate rotate) {
        int index = rotate.getNumber();
        return MinimalAction.create(x + offsetsX[index], y + offsetsY[index], rotates[index]);
    }

    List<Action> enumerateOthers(int x, int y, Rotate rotate) {
        List<Action> actions = new ArrayList<>();
        int currentRotateIndex = rotate.getNumber();
        int newX = x + this.offsetsX[currentRotateIndex];
        int newY = y + offsetsY[currentRotateIndex];
        for (Rotate prevRotate : reverseMap.get(rotate)) {
            int index = prevRotate.getNumber();
            MinimalAction action = MinimalAction.create(newX - this.offsetsX[index], newY - offsetsY[index], prevRotate);
            actions.add(action);
        }
        return actions;
    }

    Rotate transformRotate(Rotate rotate) {
        int index = rotate.getNumber();
        return rotates[index];
    }

    public SimpleMinoOperation mirror(MinoFactory minoFactory, Piece piece, Rotate rotate, int x, int y) {
        Mino mino = minoFactory.create(piece, rotate);
        int rx = x + mino.getMaxX();
        int by = y + mino.getMinY();

        Piece mirrorPiece = mirrorPiece(piece);
        Rotate mirrorRotate = mirrorRotate(rotate);
        Mino mirror = minoFactory.create(mirrorPiece, mirrorRotate);
        int lx = 9 - rx;
        return new SimpleMinoOperation(
                mirror, lx - mirror.getMinX(), by - mirror.getMinY()
        );
    }

    private Piece mirrorPiece(Piece piece) {
        switch (piece) {
            case J:
                return Piece.L;
            case L:
                return Piece.J;
            case S:
                return Piece.Z;
            case Z:
                return Piece.S;
        }
        return piece;
    }

    private Rotate mirrorRotate(Rotate rotate) {
        switch (rotate) {
            case Left:
                return Rotate.Right;
            case Right:
                return Rotate.Left;
        }
        return rotate;
    }
}
