package _experimental.cycle2;

import _experimental.cycle1.EasyPath;
import _experimental.cycle1.EasyPool;
import _experimental.cycle1.EasyTetfu;
import common.datastore.BlockCounter;
import common.datastore.pieces.LongBlocks;
import common.order.ForwardOrderLookUp;
import common.pattern.BlocksGenerator;
import common.tree.AnalyzeTree;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String marks = "" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "XXXXXXXX__" +
                "XXXXXXX___";
        Field field = FieldFactory.createField(marks);

        int width = 3;
        int height = 4;

        EasyPool easyPool = new EasyPool();
        EasyPath easyPath = new EasyPath(easyPool);
        EasyTetfu easyTetfu = new EasyTetfu();
        Field emptyField = FieldFactory.createField(height);

        HashSet<LongBlocks> allBlocksSet = new HashSet<>();
        BlockCounter allBlockCounter = new BlockCounter(Block.valueList());
        for (int x = 0; x <= 6; x++) {
            String slidedField = FieldView.toString(field, 4, "");
            System.out.println(FieldView.toString(FieldFactory.createField(slidedField)));

            Set<LongBlocks> results = easyPath.buildUp(slidedField, emptyField, width, height).stream()
                    .filter(longBlocks -> new BlockCounter(longBlocks.blockStream()).equals(allBlockCounter))
                    .collect(Collectors.toSet());
            System.out.println(results.size());

            allBlocksSet.addAll(results);

            field.slideLeft(1);
            for (int y = 0; y < height; y++) {
                field.setBlock(9, y);
            }
        }

        AnalyzeTree tree = new AnalyzeTree();
        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(7, 7);
        new BlocksGenerator("*!").blocksStream()
                .forEach(blocks -> {
                    boolean canBuildUp = lookUp.parse(blocks.getBlocks())
                            .anyMatch(blockStream -> allBlocksSet.contains(new LongBlocks(blockStream)));
                    tree.set(canBuildUp, blocks);
                });
        System.out.println(tree.getSuccessPercent());
        System.out.println(tree.tree(7));
    }


    /*
    public static void main(String[] args) {
        LongBlocks blocks = new LongBlocks(Stream.of(Block.I, Block.T, Block.O, Block.S));
        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(4, 4);
        lookUp.parse(blocks.getBlocks())
                .map(LongBlocks::new)
                .filter(longBlocks -> longBlocks.getLastBlock() == Block.I)
    }

    public static void main2(String[] args) throws ExecutionException, InterruptedException {
// ある特定の形に組める組み合わせを列挙




        String goalFieldMarks = "" +
                "XXXX______" +
                "XXXXX_____" +
                "XXXXX_____" +
                "XXXX______";
        Set<LongBlocks> results = easyPath.buildUp(goalFieldMarks, emptyField, width, height);

        for (LongBlocks result : results) {
            System.out.println(result.getBlocks());
        }
    }

    */
}
