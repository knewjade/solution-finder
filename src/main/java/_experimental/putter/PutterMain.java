package _experimental.putter;

import common.datastore.BlockCounter;
import common.datastore.Pair;
import common.datastore.action.Action;
import common.datastore.order.Order;
import common.datastore.pieces.Blocks;
import common.iterable.PermutationIterable;
import common.pattern.BlocksGenerator;
import common.pattern.IBlocksGenerator;
import common.tree.AnalyzeTree;
import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.CheckerUsingHoldThreadLocal;
import concurrent.checker.invoker.using_hold.ConcurrentCheckerUsingHoldInvoker;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import lib.MyFiles;
import searcher.common.validator.PerfectValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PutterMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        MinoFactory minoFactory = new MinoFactory();
        PerfectValidator validator = new PerfectValidator();
        PutterUsingHold<Action> putter = new PutterUsingHold<>(minoFactory, validator);

        IBlocksGenerator generator = new BlocksGenerator("*p4");
        Set<BlockCounter> blockCounters = generator.blocksStream()
                .map(pieces -> new BlockCounter(pieces.blockStream()))
                .collect(Collectors.toSet());

        int maxClearLine = 4;
        int maxDepth = 10;
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedCandidate candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CheckerUsingHoldThreadLocal<Action> checkerThreadLocal = new CheckerUsingHoldThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        ConcurrentCheckerUsingHoldInvoker invoker = new ConcurrentCheckerUsingHoldInvoker(executorService, candidateThreadLocal, checkerThreadLocal);

        IBlocksGenerator blocksGenerator = new BlocksGenerator("*p7");
        List<Blocks> searchingPieces = blocksGenerator.blocksStream()
                .collect(Collectors.toList());

        HashMap<Field, Connect> map = new HashMap<>();
        Comparator<Connect> connectComparator = Comparator.<Connect>comparingDouble(o -> o.percent).reversed();

        Path outputDirectoryPath = Paths.get("output/cycle2");
        if (!Files.exists(outputDirectoryPath)) {
            Files.createDirectories(outputDirectoryPath);
        }

        for (BlockCounter counter : blockCounters) {
            List<Block> blocks = counter.getBlocks();
            System.out.println(blocks);

            TreeSet<Order> orders = new TreeSet<>();
            PermutationIterable<Block> iterable = new PermutationIterable<>(blocks, blocks.size());
            for (List<Block> permutation : iterable) {
                Field initField = FieldFactory.createField("");
                orders.addAll(putter.search(initField, permutation, candidate, maxClearLine, maxDepth));
            }

            System.out.println(orders.size());

            ArrayList<Connect> results = new ArrayList<>();

            int i = 0;
            for (Order order : orders) {
                i++;
                if (order.getMaxClearLine() < maxClearLine)
                    continue;

                Field field = order.getField();

                Connect connect = map.getOrDefault(field, null);
                if (connect != null) {
                    connect.add();
                    results.add(connect);
                    continue;
                }

//                System.out.println(i);

                List<Pair<Blocks, Boolean>> search = invoker.search(field, searchingPieces, maxClearLine, maxDepth);
                AnalyzeTree tree = new AnalyzeTree();
                for (Pair<Blocks, Boolean> pair : search) {
                    tree.set(pair.getValue(), pair.getKey());
                }

//                System.out.println(FieldView.toString(field));
                double percent = tree.getSuccessPercent();
//                System.out.println(percent);
//                System.out.println("===");

                Connect value = new Connect(field, percent);
                map.put(field, value);
                results.add(value);
            }

            results.sort(connectComparator);

            List<String> lines = results.stream()
                    .filter(connect -> 0.0 < connect.percent)
                    .map(connect -> String.format("%d,%.5f", connect.field.getBoard(0), connect.percent))
                    .collect(Collectors.toList());

            String name = blocks.stream().map(Block::getName).collect(Collectors.joining());
            MyFiles.write("output/cycle2/" + name + ".csv", lines);
        }

        List<Connect> values = new ArrayList<>(map.values());
        values.sort(connectComparator);
        List<String> lines = values.stream()
                .filter(connect -> 0.0 < connect.percent)
                .map(connect -> String.format("%d,%.5f,%d", connect.field.getBoard(0), connect.percent, connect.count))
                .collect(Collectors.toList());

        MyFiles.write("output/cycle2/all.csv", lines);

        executorService.shutdown();
    }

    static class Connect {
        private final Field field;
        private final double percent;
        private int count = 1;

        public Connect(Field field, double percent) {
            this.field = field;
            this.percent = percent;
        }

        public void add() {
            count += 1;
        }
    }
}
