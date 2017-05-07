package _experimental;

import common.OperationHelper;
import common.ResultHelper;
import common.comparator.FieldComparator;
import common.datastore.Operation;
import common.datastore.Operations;
import common.datastore.Result;
import common.datastore.action.Action;
import common.iterable.PermutationIterable;
import core.action.candidate.HarddropCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.Rotate;
import searcher.checkmate.Checkmate;
import searcher.checkmate.CheckmateNoHold;
import searcher.common.validator.Validator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

// 7ミノでできる地形を列挙する
// longでファイルへ書き出す
public class Main4 {
    public static void main(String[] args) {
        MinoFactory minoFactory = new MinoFactory();
        int maxClearLine = 4;
        int maxDepth = 7;
        Validator validator = new MinoStackValidator(maxDepth);
        Checkmate<Action> checkmate = new CheckmateNoHold<>(minoFactory, validator);
        List<Block> allBlocks = Arrays.asList(Block.values());
        PermutationIterable<Block> permutationIterable = new PermutationIterable<>(allBlocks, allBlocks.size());
        Field field = FieldFactory.createField(maxClearLine);
        MinoShifter minoShifter = new MinoShifter();
        HarddropCandidate candidate = new HarddropCandidate(minoFactory, minoShifter);
        FieldComparator fieldComparator = new FieldComparator();
        TreeSet<Field> treeSet = new TreeSet<>(fieldComparator);
        int counter = 0;
        for (List<Block> blocks : permutationIterable) {
            counter++;
            assert maxDepth == blocks.size();
            List<Result> results = checkmate.search(field, blocks, candidate, maxClearLine, blocks.size());
            List<Field> operationsList = results.stream()
                    .map(ResultHelper::createOperations)
                    .map(operations -> {
                        Field field1 = FieldFactory.createField(maxClearLine);
                        for (Operation operation : operations) {
                            Block block = operation.getBlock();
                            Rotate rotate = operation.getRotate();
                            field1.putMino(minoFactory.create(block, rotate), operation.getX(), operation.getY());
                        }
                        return field1;
                    })
                    .collect(Collectors.toList());

            treeSet.addAll(operationsList);
            System.out.println("counter = " + counter);
        }

        Charset charset = Charset.defaultCharset();
        File file = new File("test");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), charset))) {
            for (Field field1 : treeSet) {
                long board = field1.getBoard(0);
                writer.write(board + "");
                writer.newLine();
            }
            writer.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to output file", e);
        }
    }

    private static class MinoStackValidator implements Validator {
        private final int maxClearLine;

        private MinoStackValidator(int maxClearLine) {
            this.maxClearLine = maxClearLine;
        }

        @Override
        public boolean satisfies(Field field, int maxY) {
            return field.getAllBlockCount() == maxClearLine * 4;
        }

        @Override
        public boolean validate(Field field, int maxClearLine) {
            if (field.existsAbove(maxClearLine))
                return false;

            int sum = maxClearLine - field.getBlockCountBelowOnX(0, maxClearLine);
            for (int x = 1; x < FIELD_WIDTH; x++) {
                int emptyCountInColumn = maxClearLine - field.getBlockCountBelowOnX(x, maxClearLine);
                if (field.isWallBetweenLeft(x, maxClearLine)) {
                    if (sum % 4 != 0)
                        return false;
                    sum = emptyCountInColumn;
                } else {
                    sum += emptyCountInColumn;
                }
            }

            return sum % 4 == 0;
        }
    }
}
