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
import core.field.SmallField;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

// フィールドからパフェ率順に並び替えてテト譜を作る
public class Main6 {
    public static void main(String[] args) throws IOException {
        Charset charset = Charset.defaultCharset();
        File file = new File("test");
        List<String> lines = Files.readAllLines(file.toPath(), charset);
        List<Field> collect = lines.stream()
                .map(s -> new SmallField(Long.valueOf(s)))
                .filter(smallField -> smallField.getAllBlockCount() == 28)
                .collect(Collectors.toList());

        MinoFactory minoFactory = new MinoFactory();
        List<Block> blocks = Arrays.asList(Block.values());
        int maxClearLine = 4;


        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        CheckerUsingHoldThreadLocal<Action> checkerThreadLocal = new CheckerUsingHoldThreadLocal<>();
        ConcurrentCheckerUsingHoldInvoker invoker = new ConcurrentCheckerUsingHoldInvoker(executorService, candidateThreadLocal, checkerThreadLocal);

        System.out.println(collect.size());
        AtomicInteger counter = new AtomicInteger(0);
        List<Pair<Field, Double>> pairs = collect.stream()
                .map(field -> {
                    int c = counter.incrementAndGet();
                    System.out.println(c + "/" + collect.size());

                    PiecesGenerator generator = new PiecesGenerator("*p4");
                    List<List<Block>> pieces = Iterables.toList(generator).stream()
                            .map(SafePieces::getBlocks)
                            .collect(Collectors.toList());

                    try {
                        List<Pair<List<Block>, Boolean>> search = invoker.search(field, pieces, maxClearLine, 3);
                        AnalyzeTree tree = new AnalyzeTree();
                        for (Pair<List<Block>, Boolean> pair : search)
                            tree.set(pair.getValue(), pair.getKey());
                        double percent = tree.getSuccessPercent();
                        return new Pair<>(field, percent);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .sorted((o1, o2) -> -Double.compare(o1.getValue(), o2.getValue()))
                .collect(Collectors.toList());

        executorService.shutdown();

        File outputFile = new File("test2");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false), charset))) {
            for (Pair<Field, Double> pair : pairs) {

                Field field = pair.getKey();
                Double percent = pair.getValue();

                ColorConverter colorConverter = new ColorConverter();
                ColoredField coloredField = ColoredFieldFactory.createField(24);
                fillInField(coloredField, ColorType.Gray, field);

                Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
                String encode = tetfu.encode( singletonList(TetfuElement.createFieldOnly(coloredField)));
                writer.write(String.format("%.2f %% => http://fumen.zui.jp/?v115@%s", percent * 100, encode));
                writer.newLine();
            }
            writer.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to output file", e);
        }

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
