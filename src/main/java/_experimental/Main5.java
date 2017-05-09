package _experimental;

import _experimental.newfield.Main3;
import common.Iterables;
import common.OperationsFactory;
import common.datastore.*;
import common.datastore.action.Action;
import common.pattern.PiecesGenerator;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import common.tree.AnalyzeTree;
import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.CheckerUsingHoldThreadLocal;
import concurrent.checker.invoker.using_hold.ConcurrentCheckerUsingHoldInvoker;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

// 操作手順リストから最もパフェ確率の高い地形を選択
public class Main5 {
    public static void main(String[] args) throws IOException {
        Charset charset = Charset.defaultCharset();
        File file = new File("test");
        List<String> lines = Files.readAllLines(file.toPath(), charset);
        List<Operations> operationsList = lines.stream()
                .map(s -> OperationsFactory.createOperations(s.split(";")))
                .collect(Collectors.toList());

        MinoFactory minoFactory = new MinoFactory();
        List<Block> blocks = Arrays.asList(Block.values());
        int maxClearLine = 4;

        System.out.println(operationsList.size());
        int counter = 0;
        ArrayList<List<List<OperationWithKey>>> results = new ArrayList<>();

        LOOP:
        for (Operations operations : operationsList) {
            System.out.println(counter);
            counter++;
            Field field = FieldFactory.createField(maxClearLine);
            for (Operation operation : operations.getOperations()) {
                Block block = operation.getBlock();
                Rotate rotate = operation.getRotate();
                field.putMino(minoFactory.create(block, rotate), operation.getX(), operation.getY());
                int clearLine = field.clearLine();
                if (0 < clearLine)
                    continue LOOP;
            }
//            System.out.println(FieldView.toString(field));

            Field freeze = field.freeze(maxClearLine);
            freeze.invert(maxClearLine);
            Field verifyField = FieldFactory.createField(maxClearLine);
            List<List<OperationWithKey>> operationsWithKey = Main3.search(blocks, freeze, maxClearLine, verifyField);
            results.add(operationsWithKey);
//            System.out.println(operationsWithKey.size());
        }

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        CheckerUsingHoldThreadLocal<Action> checkerThreadLocal = new CheckerUsingHoldThreadLocal<>();
        ConcurrentCheckerUsingHoldInvoker invoker = new ConcurrentCheckerUsingHoldInvoker(executorService, candidateThreadLocal, checkerThreadLocal);

        long count = results.stream()
//                .sorted((o1, o2) -> -Integer.compare(o1.size(), o2.size()))
                .map(operationsWithKey -> {
                    List<BlockField> blockFields = operationsWithKey.stream()
                            .limit(1)
                            .map(operationWithKeys -> {
                                BlockField blockField = new BlockField(maxClearLine);
                                for (OperationWithKey key : operationWithKeys) {
                                    Field test = FieldFactory.createField(maxClearLine);
                                    Mino mino = key.getMino();
                                    test.putMino(mino, key.getX(), key.getY());
                                    test.insertWhiteLineWithKey(key.getNeedDeletedKey());
                                    blockField.merge(test, mino.getBlock());
                                }
                                return blockField;
                            })
                            .collect(Collectors.toList());

                    assert 1 <= blockFields.size();
                    BlockField sampleBlockField = blockFields.get(0);
                    Field mergedField = sampleBlockField.getMergedField();

                    PiecesGenerator generator = new PiecesGenerator("*p4");
                    List<List<Block>> pieces = Iterables.toList(generator).stream()
                            .map(SafePieces::getBlocks)
                            .collect(Collectors.toList());

                    try {
                        List<Pair<List<Block>, Boolean>> search = invoker.search(mergedField, pieces, maxClearLine, 3);
                        AnalyzeTree tree = new AnalyzeTree();
                        for (Pair<List<Block>, Boolean> pair : search)
                            tree.set(pair.getValue(), pair.getKey());
                        double percent = tree.getSuccessPercent();
                        return new Pair<>(operationsWithKey, percent);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .sorted((o1, o2) -> -Double.compare(o1.getValue(), o2.getValue()))
                .peek(pair -> {
                    List<List<OperationWithKey>> operationsWithKey = pair.getKey();
                    Double percent = pair.getValue();

                    List<BlockField> blockFields = operationsWithKey.stream()
                            .map(operationWithKeys -> {
                                BlockField blockField = new BlockField(maxClearLine);
                                for (OperationWithKey key : operationWithKeys) {
                                    Field test = FieldFactory.createField(maxClearLine);
                                    Mino mino = key.getMino();
                                    test.putMino(mino, key.getX(), key.getY());
                                    test.insertWhiteLineWithKey(key.getNeedDeletedKey());
                                    blockField.merge(test, mino.getBlock());
                                }
                                return blockField;
                            })
                            .collect(Collectors.toList());

                    System.out.println(String.format("%.2f %% : %d", percent, operationsWithKey.size()));

                    ColorConverter colorConverter = new ColorConverter();
                    Field field = FieldFactory.createField(maxClearLine);
                    for (BlockField blockField : blockFields) {
                        ColoredField coloredField = ColoredFieldFactory.createField(24);
                        fillInField(coloredField, ColorType.Gray, field);

                        for (Block block : Block.values()) {
                            Field target = blockField.get(block);
                            ColorType colorType = colorConverter.parseToColorType(block);
                            fillInField(coloredField, colorType, target);
                        }
//            System.out.println(ColoredFieldView.toString(coloredField));
                        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
                        String encode = tetfu.encode(singletonList(TetfuElement.createFieldOnly(coloredField)));
                        System.out.println(String.format("v115@%s", encode));
                    }
                    System.out.println("---");
                })
                .count();
        System.out.println(count);
        executorService.shutdown();

    }

    private static void fillInField(ColoredField coloredField, ColorType colorType, Field target) {
        for (int y = 0; y < target.getMaxFieldHeight(); y++) {
            for (int x = 0; x < 10; x++) {
                if (!target.isEmpty(x, y))
                    coloredField.setColorType(colorType, x, y);
            }
        }
    }
}
