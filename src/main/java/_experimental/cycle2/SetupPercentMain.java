package _experimental.cycle2;

import _experimental.cycle1.EasyPath;
import _experimental.cycle1.EasyPool;
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
import java.util.stream.Stream;

public class SetupPercentMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int width = 3;
        int height = 4;

        EasyPool easyPool = new EasyPool();
        EasyPath easyPath = new EasyPath(easyPool);
        Field emptyField = FieldFactory.createField(height);

        HashSet<LongBlocks> allBlocksSet = new HashSet<>();
        BlockCounter allBlockCounter = new BlockCounter(Stream.of(Block.I, Block.O, Block.S, Block.Z, Block.L, Block.J));

        String marksRight = "" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____" +
                "XXXXXX____";
        Field rightField = FieldFactory.createField(marksRight);

        for (int x = 0; x <= 6; x++) {
            String slidedField = FieldView.toString(rightField, 4, "");
            System.out.println(FieldView.toString(FieldFactory.createField(slidedField)));

            Set<LongBlocks> results = easyPath.buildUp(slidedField, emptyField, width, height).stream()
                    .filter(longBlocks -> new BlockCounter(longBlocks.blockStream()).equals(allBlockCounter))
                    .collect(Collectors.toSet());
            System.out.println(results.size());

            allBlocksSet.addAll(results);

            rightField.slideLeft(1);
            for (int y = 0; y < height; y++) {
                rightField.setBlock(9, y);
            }
        }

        AnalyzeTree tree = new AnalyzeTree();
        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(7, 7);
        new BlocksGenerator("*!").blocksStream()
                .forEach(blocks -> {
                    boolean canBuildUp = lookUp.parse(blocks.getBlocks())
                            .map(LongBlocks::new)
                            .filter(longBlocks -> longBlocks.getLastBlock() == Block.T)
                            .anyMatch(longBlocks -> allBlocksSet.contains(new LongBlocks(longBlocks.blockStream().limit(6))));
                    tree.set(canBuildUp, blocks);
                    System.out.println(blocks.blockStream().map(Block::getName).collect(Collectors.joining()) + "," + (canBuildUp ? "O" : "X"));
                });
//        System.out.println(tree.show());
//        System.out.println(tree.tree(2));
    }
}
