package common.pattern;

import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.field.Field;
import core.field.FieldView;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import searcher.common.validator.Validator;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PatternTree {
    private static int c(int maxClearLine, Field field) {
        int depth = field.getNumOfAllBlocks() / 4;
        int linesCleared = 4 - maxClearLine;
        long matrix = field.getBoard(0);
        return rowTransitions(matrix, linesCleared) << 2
                + (columnTransitions(matrix, linesCleared))
                - (depth >> 1)
                - (linesCleared << 1);
    }

    private static int columnTransitions(long matrix, int linesCleared) {
        int columnTransitions = 0;
        int y = 10 * linesCleared;
        int prevRow = (int) (matrix >> y & 0b1111111111);
        y += 10;
        while (y < CELLAMOUNT) {
            int row = (int) (matrix >> y & 0b1111111111);
            int transitions = row ^ prevRow;
            prevRow = row;
            int x = 0;
            while (x < 10) {
                if ((transitions >> x & 1) == 1)
                    columnTransitions += 1;
                x += 1;
            }
            y += 10;
        }
        return columnTransitions;
    }

    static long COLUMN = 1074791425L;
    static int CELLAMOUNT = 40;

    private static int rowTransitions(long matrix, int linesCleared) {
        long totalDifferences = 0;
        int smoothness = 0;
        long prev = matrix & COLUMN;
        int x = 1;
        while (x < 10) {
            long cur = matrix >> x & COLUMN;
            totalDifferences += cur ^ prev;
            prev = cur;
            x += 1;
        }
        int yStart = 10 * linesCleared;
        int y = yStart;
        while (y < CELLAMOUNT) {
            smoothness += (int) (totalDifferences >> y & 15);
            y += 10;
        }
        return smoothness;
    }


    private final EnumMap<Block, PatternTree> map = new EnumMap<>(Block.class);
    private AtomicBoolean isPossible = new AtomicBoolean();

    public void build(List<Block> blocks, Function<List<Block>, PatternTree> terminate) {
        build(blocks, 0, terminate);
    }

    private void build(List<Block> blocks, int index, Function<List<Block>, PatternTree> terminate) {
        assert index < blocks.size() : index;
        Block block = blocks.get(index);

        if (index == blocks.size() - 1) {
            map.computeIfAbsent(block, key -> terminate.apply(blocks));
        } else {
            PatternTree tree = map.computeIfAbsent(block, key -> new PatternTree());
            tree.build(blocks, index + 1, terminate);
        }
    }

    public boolean get(List<Block> blocks) {
        return get(blocks, 0);
    }

    private boolean get(List<Block> blocks, int index) {
        assert index < blocks.size() : index;
        Block block = blocks.get(index);

        assert map.containsKey(block) : map;
        PatternTree tree = map.get(block);

        if (index == blocks.size() - 1) {
            return tree.isPossible();
        } else {
            return tree.get(blocks, index + 1);
        }
    }

    public boolean run(Field field, int maxClearLine, CommonObj common) {
        return run(field, null, maxClearLine, common);
    }

    private boolean isPossible() {
        return isPossible.get();
    }

    public void success() {
        boolean oldValue = isPossible.getAndSet(true);
        if (!oldValue)
            for (PatternTree tree : map.values())
                tree.success();
    }

    // 探索の必要がなくなったときtrueを返す
    private boolean run(Field field, Block hold, int maxClearLine, CommonObj common) {
//        System.out.println(FieldView.toString(field));
//        System.out.println();

        List<Map.Entry<Block, PatternTree>> entries = map.entrySet().parallelStream()
                .filter(entry -> {
                    PatternTree tree = entry.getValue();
//                    if (tree.isPossible())
//                        return false;

                    // ホールドなし
                    Block block = entry.getKey();

                    MinoFactory minoFactory = common.getMinoFactory();
                    Validator validator = common.getValidator();

                    Candidate<Action> candidate = common.getCandidate();
                    Set<Action> actions = candidate.search(field, block, maxClearLine);
                    HashMap<Integer, ArrayList<Field>> nextFieldsMap = new HashMap<>();  // 0-4の配列でも代用可能
                    for (Action action : actions) {
                        Mino mino = minoFactory.create(block, action.getRotate());
                        int x = action.getX();
                        int y = action.getY();

                        Field nextField = field.freeze(maxClearLine);
                        nextField.putMino(mino, x, y);
                        int clearLine = nextField.clearLine();

                        int nextMaxClearLine = maxClearLine - clearLine;

                        if (validator.satisfies(nextField, nextMaxClearLine)) {
                            tree.success();
                            return true;
                        }

                        if (validator.validate(nextField, nextMaxClearLine)) {
                            ArrayList<Field> nextFields = nextFieldsMap.computeIfAbsent(clearLine, integer -> new ArrayList<>());
                            nextFields.add(nextField);
                        }
                    }

//                    System.out.println(nextFieldsMap.size());

                    boolean allFailed = nextFieldsMap.entrySet().stream()
                            .noneMatch(nextFieldsEntry -> {
                                int clearLine = nextFieldsEntry.getKey();
                                int nextMaxClearLine = maxClearLine - clearLine;
                                ArrayList<Field> nextFields = nextFieldsEntry.getValue();

                                Comparator<Field> comparator = Comparator.comparingInt(o -> c(nextMaxClearLine, o));
                                nextFields.sort(comparator);

                                return nextFields.stream()
                                        .anyMatch(nextField -> tree.run(nextField, hold, nextMaxClearLine, common));
                            });

                    if (!allFailed)
                        tree.success();

                    return allFailed;
                })
                .filter(entry -> {
                    PatternTree tree = entry.getValue();

//                    if (tree.isPossible())
//                        return false;

                    Block block = entry.getKey();
                    if (hold == null)
                        return !tree.run(field, block, maxClearLine, common);

                    // ホールドしたあと
                    MinoFactory minoFactory = common.getMinoFactory();
                    Validator validator = common.getValidator();

                    Candidate<Action> candidate = common.getCandidate();
                    Set<Action> actions = candidate.search(field, hold, maxClearLine);
                    HashMap<Integer, ArrayList<Field>> nextFieldsMap = new HashMap<>();  // 0-4の配列でも代用可能
                    for (Action action : actions) {
                        Mino mino = minoFactory.create(hold, action.getRotate());
                        int x = action.getX();
                        int y = action.getY();

                        Field nextField = field.freeze(maxClearLine);
                        nextField.putMino(mino, x, y);
                        int clearLine = nextField.clearLine();

                        int nextMaxClearLine = maxClearLine - clearLine;

                        if (validator.satisfies(nextField, nextMaxClearLine)) {
                            tree.success();
                            return true;
                        }

                        if (validator.validate(nextField, nextMaxClearLine)) {
                            ArrayList<Field> nextFields = nextFieldsMap.computeIfAbsent(clearLine, integer -> new ArrayList<>());
                            nextFields.add(nextField);
                        }
                    }

                    boolean allFailed = nextFieldsMap.entrySet().stream()
                            .noneMatch(nextFieldsEntry -> {
                                int clearLine = nextFieldsEntry.getKey();
                                int nextMaxClearLine = maxClearLine - clearLine;
                                ArrayList<Field> nextFields = nextFieldsEntry.getValue();
                                Comparator<Field> comparator = (o1, o2) -> Integer.compare(c(nextMaxClearLine, o1), c(nextMaxClearLine, o1));
                                nextFields.sort(comparator);
                                return nextFields.stream()
                                        .anyMatch(nextField -> tree.run(nextField, block, nextMaxClearLine, common));
                            });

                    if (!allFailed)
                        tree.success();

                    return allFailed;
                })
                .collect(Collectors.toList());

//        try {
//            Thread.sleep(200L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        boolean empty = entries.isEmpty();

        if (empty)
            success();

        return empty;
    }
}

class CommonObj {
    CommonObj(ThreadLocal<Candidate<Action>> candidateThreadLocal, MinoFactory minoFactory, Validator validator) {
        this.candidateThreadLocal = candidateThreadLocal;
        this.minoFactory = minoFactory;
        this.validator = validator;
    }

    private ThreadLocal<Candidate<Action>> candidateThreadLocal;
    private MinoFactory minoFactory;
    private Validator validator;

    public Candidate<Action> getCandidate() {
        return candidateThreadLocal.get();
    }

    public MinoFactory getMinoFactory() {
        return minoFactory;
    }

    public Validator getValidator() {
        return validator;
    }
}