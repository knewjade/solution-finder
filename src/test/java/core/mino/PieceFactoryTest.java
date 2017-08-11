package core.mino;

import common.datastore.Pair;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.field.MiddleField;
import core.srs.MinoRotation;
import core.srs.Rotate;
import lib.BooleanWalker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

class PieceFactoryTest {
    // TODO: write unittest
    @Test
    void create() {
        PieceFactory pieceFactory = new PieceFactory(4);
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        Neighbors neighbors = new Neighbors(minoFactory, minoShifter, minoRotation, pieceFactory);
        Neighbor neighbor = neighbors.get(Block.I, Rotate.Spawn, 1, 0);
        System.out.println(neighbor);
    }

    @Test
    void name() {
        MinoRotation minoRotation = new MinoRotation();
        int x = 5;
        int y = 4;
        ArrayList<Integer> lines = new ArrayList<>();
        for (Block block : Block.values()) {
            for (Rotate rotate : Rotate.values()) {
                Mino mino = new Mino(block, rotate);
                int[][] from = minoRotation.getLeftPatternsFrom(mino);
                Rotate left = rotate.getLeftRotate();

                Field field = FieldFactory.createMiddleField();
                for (int[] ints : from) {
                    System.out.println(Arrays.toString(ints));
                    field.put(new Mino(block, left), x + ints[0], y + ints[1]);
                }
                System.out.println(FieldView.toString(field));
                int blocks = field.getNumOfAllBlocks();
                System.out.println(blocks);
                lines.add(blocks);
            }
        }
        System.out.println(lines.stream().mapToInt(value -> value).max());
    }

    @Test
    void name2() {
        Block block = Block.I;
        Rotate rotate = Rotate.Spawn;
        MinoRotation minoRotation = new MinoRotation();
        Mino mino = new Mino(block, rotate);
        Mino nextMino = new Mino(block, rotate.getLeftRotate());
        int[][] patterns = minoRotation.getLeftPatternsFrom(mino);
        Field field = FieldFactory.createMiddleField();
        int startX = 5;
        int startY = 3;
        HashMap<Integer, Integer> contents = new HashMap<>();
        for (int index = 0; index < patterns.length; index++) {
            int[] ints = patterns[index];
            field.put(nextMino, startX + ints[0], startY + ints[1]);
            contents.put(ints[0] + ints[1] * 10, index);
        }

        ArrayList<Integer> integers = new ArrayList<>();
        for (int yIndex = 0; yIndex < 10; yIndex++) {
            for (int xIndex = 0; xIndex < 10; xIndex++) {
                if (!field.isEmpty(xIndex, yIndex))
                    integers.add(yIndex * 10 + xIndex);
            }
        }

        assert integers.size() == field.getNumOfAllBlocks();

        System.out.println(integers);
        System.out.println(FieldView.toString(field));

        List<Pair<MiddleField, Integer>> fields = BooleanWalker.walk(integers.size())
                .map(booleans -> {
                    MiddleField middleField = FieldFactory.createMiddleField();
                    for (int index = 0; index < booleans.size(); index++) {
                        Integer integer = integers.get(index);
                        Boolean flag = booleans.get(index);
                        if (flag) {
                            middleField.setBlock(integer % 10, integer / 10);
                        }
                    }

                    int[] r = minoRotation.getKicksWithLeftRotation(middleField, mino, nextMino, startX, startY);
                    int height = r[0] + r[1] * 10;

                    return new Pair<>(middleField, contents.get(height));
                })
                .collect(Collectors.toList());

        for (Pair<MiddleField, Integer> pair : fields) {
            MiddleField key = pair.getKey();
        }

    }
}