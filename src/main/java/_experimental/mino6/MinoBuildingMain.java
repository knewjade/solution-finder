package _experimental.mino6;

import common.ResultHelper;
import common.datastore.Operation;
import common.datastore.Result;
import common.datastore.action.Action;
import common.iterable.PermutationIterable;
import core.action.candidate.Candidate;
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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

// 7ミノでできる地形を列挙する
// longでファイルへ書き出す
public class MinoBuildingMain {
    public static void main(String[] args) {
        // 設定
        int maxClearLine = 4;
        int maxDepth = 6;

        // checkmateの作成
        MinoFactory minoFactory = new MinoFactory();
        Validator validator = new MinoStackValidator(maxDepth);
        Checkmate<Action> checkmate = new CheckmateNoHold<>(minoFactory, validator);

        // フィールドの設定
        Field field = FieldFactory.createField(maxClearLine);

        // candidateの作成
        MinoShifter minoShifter = new MinoShifter();
//        MinoRotation minoRotation = new MinoRotation();
//        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        Candidate<Action> candidate = new HarddropCandidate(minoFactory, minoShifter);

        // treesetの作成
        HashSet<Field> fieldSet = new HashSet<>();

        // すべてのブロックの組み合わせを生成
        List<Block> allBlocks = Arrays.asList(Block.O, Block.Z, Block.I, Block.T, Block.S, Block.J);
        PermutationIterable<Block> permutationIterable = new PermutationIterable<>(allBlocks, allBlocks.size());

        // 探索
        int counter = 0;
        for (List<Block> blocks : permutationIterable) {
            System.out.println("counter = " + counter);
            counter++;

            assert maxDepth == blocks.size();

            // 地形を探索
            List<Result> results = checkmate.search(field, blocks, candidate, maxClearLine, blocks.size());

            // 操作からfieldに変換
            List<Field> operationsList = results.stream()
                    .map(result -> {
                        Field field1 = FieldFactory.createField(maxClearLine);
                        List<Operation> operations = ResultHelper.createOperationStream(result).collect(Collectors.toList());
                        for (Operation operation : operations) {
                            Block block = operation.getBlock();
                            Rotate rotate = operation.getRotate();
                            field1.put(minoFactory.create(block, rotate), operation.getX(), operation.getY());
                        }
                        return field1;
                    })
                    .collect(Collectors.toList());

            // 重複を取り除く
            fieldSet.addAll(operationsList);
        }

        // できた地形をファイルを出力
        Charset charset = Charset.defaultCharset();
        File file = new File("test");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), charset))) {
            for (Field field1 : fieldSet) {
                long board = field1.getBoard(0);
                writer.write(board + "");
                writer.newLine();
            }
            writer.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to output file", e);
        }
    }

    // パフェへの可能性があるもののみを残す
    private static class MinoStackValidator implements Validator {
        private final int maxDepth;

        private MinoStackValidator(int maxDepth) {
            this.maxDepth = maxDepth;
        }

        @Override
        public boolean satisfies(Field field, int maxY) {
            return field.getNumOfAllBlocks() == maxDepth * 4;
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
