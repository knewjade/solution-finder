package core.mino;

import common.datastore.action.Action;
import core.srs.Rotate;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MinoShifter {
    private final EnumMap<Piece, MinoTransform> transformers = new EnumMap<>(Piece.class);

    public MinoShifter() {
        MinoTransform transformI = new MinoTransform();
        transformI.set(Rotate.Right, 0, -1, Rotate.Left);
        transformI.set(Rotate.Reverse, -1, 0, Rotate.Spawn);
        transformers.put(Piece.I, transformI);

        MinoTransform transformO = new MinoTransform();
        transformO.set(Rotate.Right, 0, -1, Rotate.Spawn);
        transformO.set(Rotate.Reverse, -1, -1, Rotate.Spawn);
        transformO.set(Rotate.Left, -1, 0, Rotate.Spawn);
        transformers.put(Piece.O, transformO);

        MinoTransform transformS = new MinoTransform();
        transformS.set(Rotate.Right, 1, 0, Rotate.Left);
        transformS.set(Rotate.Reverse, 0, -1, Rotate.Spawn);
        transformers.put(Piece.S, transformS);

        MinoTransform transformZ = new MinoTransform();
        transformZ.set(Rotate.Left, -1, 0, Rotate.Right);
        transformZ.set(Rotate.Reverse, 0, -1, Rotate.Spawn);
        transformers.put(Piece.Z, transformZ);

        transformers.put(Piece.T, new MinoTransform());
        transformers.put(Piece.L, new MinoTransform());
        transformers.put(Piece.J, new MinoTransform());
    }

    public Rotate createTransformedRotate(Piece piece, Rotate rotate) {
        return transformers.get(piece).transformRotate(rotate);
    }

    public Action createTransformedAction(Piece piece, Action action) {
        return createTransformedAction(piece, action.getRotate(), action.getX(), action.getY());
    }

    public Action createTransformedAction(Piece piece, Rotate rotate, int x, int y) {
        return transformers.get(piece).transform(x, y, rotate);
    }

    public List<Action> enumerateSameOtherActions(Piece piece, Rotate rotate, int x, int y) {
        return transformers.get(piece).enumerateOthers(x, y, rotate);
    }

    public Set<Rotate> getUniqueRotates(Piece piece) {
        HashSet<Rotate> uniques = new HashSet<>();
        for (Rotate rotate : Rotate.values()) {
            Rotate newRotate = transformers.get(piece).transformRotate(rotate);
            uniques.add(newRotate);
        }
        return uniques;
    }
}
