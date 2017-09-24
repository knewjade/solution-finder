package _experimental.mino6;

import common.datastore.Pair;
import common.datastore.action.Action;
import common.datastore.pieces.Blocks;
import common.pattern.BlocksGenerator;
import common.pattern.IBlocksGenerator;
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
import core.field.SmallField;
import core.mino.MinoFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

// フィールドからパフェ率順に並び替えてテト譜を作る
public class FieldSortMain {
    public static void main(String[] args) throws IOException {
        // ファイルから読み込む
        Charset charset = Charset.defaultCharset();
        File file = new File("test");
        List<String> lines = Files.readAllLines(file.toPath(), charset);

        // フィールドに変換する
        int maxDepth = 6;
        List<Field> collect = lines.stream()
                .map(s -> new SmallField(Long.valueOf(s)))
                .filter(smallField -> smallField.getNumOfAllBlocks() == maxDepth * 4)
                .collect(Collectors.toList());
        System.out.println(collect.size());

        // 設定
        MinoFactory minoFactory = new MinoFactory();
        int maxClearLine = 4;

        // executorServiceの生成
        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);

        // checkerの生成
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        CheckerUsingHoldThreadLocal<Action> checkerThreadLocal = new CheckerUsingHoldThreadLocal<>();
        ConcurrentCheckerUsingHoldInvoker invoker = new ConcurrentCheckerUsingHoldInvoker(executorService, candidateThreadLocal, checkerThreadLocal);

        // 使用する4ミノのリスト
        String pattern = "L, *p4";
        IBlocksGenerator generator = new BlocksGenerator(pattern);
        List<Blocks> pieces = generator.blocksStream().collect(Collectors.toList());

        // 探索
        AtomicInteger counter = new AtomicInteger(0);
        List<Pair<Field, Double>> pairs = collect.stream()
                .map(field -> {
                    // 個数の表示
                    int c = counter.incrementAndGet();
                    System.out.println(c + "/" + collect.size());

                    try {
                        // 探索
                        List<Pair<Blocks, Boolean>> search = invoker.search(field, pieces, maxClearLine, 10 - maxDepth);

                        // 結果の保存
                        AnalyzeTree tree = new AnalyzeTree();
                        for (Pair<Blocks, Boolean> pair : search)
                            tree.set(pair.getValue(), pair.getKey());

                        // 確率の取得
                        double percent = tree.getSuccessPercent();

                        // 結果を返却
                        return new Pair<>(field, percent);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(pair -> 0.0 < pair.getValue())  // 確率0をカット
                .sorted((o1, o2) -> -Double.compare(o1.getValue(), o2.getValue()))
                .collect(Collectors.toList());

        // executorServiceの終了
        executorService.shutdown();

        // colorConverterの生成
        ColorConverter colorConverter = new ColorConverter();

        // ファイルへの出力
        File outputFile = new File("sorted");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false), charset))) {
            for (Pair<Field, Double> pair : pairs) {
                Field field = pair.getKey();
                Double percent = pair.getValue();

                // フィールドの変換
                ColoredField coloredField = ColoredFieldFactory.createField(24);
                fillInField(coloredField, ColorType.Gray, field);

                // テト譜への変換
                Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
                String encode = tetfu.encode(singletonList(TetfuElement.createFieldOnly(coloredField)));
                writer.write(String.format("%.2f,%s", percent * 100, encode));
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
