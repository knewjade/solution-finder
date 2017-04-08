package core.mino;

import core.srs.Rotate;
import searcher.common.action.Action;
import searcher.common.action.MinimalAction;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class MinoShifter {
    private static class Transform {
        private final int[] offsetsX = new int[4];
        private final int[] offsetsY = new int[4];
        private final Rotate[] rotates = new Rotate[4];
        private final EnumMap<Rotate, List<Rotate>> reverseMap;

        Transform() {
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

        private void set(Rotate rotate, int offsetX, int offsetY, Rotate newRotate) {
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
                    reverseMap.get(rotate).add(newRotate);
                    reverseMap.get(newRotate).add(rotate);
                }
            }
        }

        private Action transform(int x, int y, Rotate rotate) {
            int index = rotate.getNumber();
            return MinimalAction.create(x + offsetsX[index], y + offsetsY[index], rotates[index]);
        }

        private List<Action> enumerateOthers(int x, int y, Rotate rotate) {
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
    }

    private final EnumMap<Block, Transform> transformers = new EnumMap<>(Block.class);

    public MinoShifter() {
        Transform transformI = new Transform();
        transformI.set(Rotate.Right, 0, -1, Rotate.Left);
        transformI.set(Rotate.Reverse, -1, 0, Rotate.Spawn);
        transformers.put(Block.I, transformI);

        Transform transformO = new Transform();
        transformO.set(Rotate.Right, 0, -1, Rotate.Spawn);
        transformO.set(Rotate.Reverse, -1, -1, Rotate.Spawn);
        transformO.set(Rotate.Left, -1, 0, Rotate.Spawn);
        transformers.put(Block.O, transformO);

        Transform transformS = new Transform();
        transformS.set(Rotate.Right, 1, 0, Rotate.Left);
        transformS.set(Rotate.Reverse, 0, -1, Rotate.Spawn);
        transformers.put(Block.S, transformS);

        Transform transformZ = new Transform();
        transformZ.set(Rotate.Left, -1, 0, Rotate.Right);
        transformZ.set(Rotate.Reverse, 0, -1, Rotate.Spawn);
        transformers.put(Block.Z, transformZ);

        transformers.put(Block.T, new Transform());
        transformers.put(Block.L, new Transform());
        transformers.put(Block.J, new Transform());
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
}
