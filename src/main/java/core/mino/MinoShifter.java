package core.mino;

import common.datastore.action.Action;
import core.srs.Rotate;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MinoShifter {
    private final EnumMap<Block, MinoTransform> transformers = new EnumMap<>(Block.class);

    public MinoShifter() {
        MinoTransform transformI = new MinoTransform();
        transformI.set(Rotate.Right, 0, -1, Rotate.Left);
        transformI.set(Rotate.Reverse, -1, 0, Rotate.Spawn);
        transformers.put(Block.I, transformI);

        MinoTransform transformO = new MinoTransform();
        transformO.set(Rotate.Right, 0, -1, Rotate.Spawn);
        transformO.set(Rotate.Reverse, -1, -1, Rotate.Spawn);
        transformO.set(Rotate.Left, -1, 0, Rotate.Spawn);
        transformers.put(Block.O, transformO);

        MinoTransform transformS = new MinoTransform();
        transformS.set(Rotate.Right, 1, 0, Rotate.Left);
        transformS.set(Rotate.Reverse, 0, -1, Rotate.Spawn);
        transformers.put(Block.S, transformS);

        MinoTransform transformZ = new MinoTransform();
        transformZ.set(Rotate.Left, -1, 0, Rotate.Right);
        transformZ.set(Rotate.Reverse, 0, -1, Rotate.Spawn);
        transformers.put(Block.Z, transformZ);

        transformers.put(Block.T, new MinoTransform());
        transformers.put(Block.L, new MinoTransform());
        transformers.put(Block.J, new MinoTransform());
    }

    public Rotate createTransformedRotate(Block block, Rotate rotate) {
        return transformers.get(block).transformRotate(rotate);
    }

    public Action createTransformedAction(Block block, Action action) {
        return createTransformedAction(block, action.getX(), action.getY(), action.getRotate());
    }

    public Action createTransformedAction(Block block, int x, int y, Rotate rotate) {
        return transformers.get(block).transform(x, y, rotate);
    }

    public List<Action> enumerateSameOtherActions(Block block, int x, int y, Rotate rotate) {
        return transformers.get(block).enumerateOthers(x, y, rotate);
    }

    public Set<Rotate> getUniqueRotates(Block block) {
        HashSet<Rotate> uniques = new HashSet<>();
        for (Rotate rotate : Rotate.values()) {
            Rotate newRotate = transformers.get(block).transformRotate(rotate);
            uniques.add(newRotate);
        }
        return uniques;
    }
}
