package common.pattern;

import common.comparator.FieldComparator;
import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import searcher.common.validator.Validator;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class PatternTree {
    private static final ObjComparator COMPARATOR = new ObjComparator();

    private final EnumMap<Block, PatternTree> map = new EnumMap<>(Block.class);
    private final AtomicBoolean isPossible = new AtomicBoolean();

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
        long failedCount = map.entrySet().stream()
                .filter(entry -> {
                    Block block = entry.getKey();
                    PatternTree tree = entry.getValue();
                    boolean run = tree.run(field, block, maxClearLine, common);
                    return !run;
                })
                .count();

        boolean isAllSucceed = failedCount == 0;

        if (isAllSucceed)
            success();

        return isAllSucceed;
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
        if (isPossible())
            return true;
//        System.out.println(FieldView.toString(field));
//        System.out.println();

//        int depth = field.getNumOfAllBlocks() / 4;
//        System.out.println(depth);

        long failedCount = map.entrySet().stream()
                .filter(entry -> {
                    Block block = entry.getKey();
                    PatternTree tree = entry.getValue();

                    // If possible, skip
                    if (tree.isPossible())
                        return false;

                    // Initialize
                    MinoFactory minoFactory = common.getMinoFactory();
                    Validator validator = common.getValidator();
                    Candidate<Action> candidate = common.getCandidate();

                    // Enumerate next fields
                    HashSet<Obj> nextFields = new HashSet<>();

                    // Put next piece, without Hold
                    Set<Action> actions = candidate.search(field, block, maxClearLine);
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
                            return false;
                        }

                        if (validator.validate(nextField, nextMaxClearLine)) {
                            nextFields.add(new Obj(nextField, nextMaxClearLine, hold));
                        }
                    }

                    // Put hold piece, Using hold
                    Set<Action> actions2 = candidate.search(field, hold, maxClearLine);
                    for (Action action : actions2) {
                        Mino mino = minoFactory.create(hold, action.getRotate());
                        int x = action.getX();
                        int y = action.getY();

                        Field nextField = field.freeze(maxClearLine);
                        nextField.putMino(mino, x, y);
                        int clearLine = nextField.clearLine();

                        int nextMaxClearLine = maxClearLine - clearLine;

                        if (validator.satisfies(nextField, nextMaxClearLine)) {
                            tree.success();
                            return false;
                        }

                        if (validator.validate(nextField, nextMaxClearLine)) {
                            nextFields.add(new Obj(nextField, nextMaxClearLine, block));
                        }
                    }

                    // Decide priority
                    PriorityQueue<Obj> nextFieldsQueue = new PriorityQueue<>(COMPARATOR);
                    nextFieldsQueue.addAll(nextFields);

                    // Search next fields
                    while (!nextFieldsQueue.isEmpty()) {
                        Obj poll = nextFieldsQueue.poll();
                        int nextMaxClearLine = poll.getMaxClearLine();
                        Field nextField = poll.getField();
                        Block nextHold = poll.getHold();

                        boolean run = tree.run(nextField, nextHold, nextMaxClearLine, common);
                        if (run)
                            return false;
                    }

                    return true;
                })
                .count();

        boolean isAllSucceed = failedCount == 0;

        if (isAllSucceed)
            success();

        return isAllSucceed;
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

class Obj implements Comparable<Obj> {
    private final Field field;
    private final int maxClearLine;
    private final Block hold;

    public Obj(Field field, int maxClearLine, Block hold) {
        this.field = field;
        this.maxClearLine = maxClearLine;
        this.hold = hold;
    }

    public int getPriority() {
        return Heuristic.c(field, maxClearLine);
    }

    public int getMaxClearLine() {
        return maxClearLine;
    }

    public Field getField() {
        return field;
    }

    public Block getHold() {
        return hold;
    }

    @Override
    public boolean equals(Object o) {
        assert o != null;
        assert o instanceof Obj;
        Obj obj = (Obj) o;
        return hold == obj.hold && FieldComparator.compareField(this.field, obj.field) == 0;
    }

    @Override
    public int hashCode() {
        int number = hold != null ? hold.getNumber() : 7;
        return number * 31 + field.hashCode();
    }

    @Override
    public int compareTo(Obj o) {
        Block hold1 = this.hold;
        Block hold2 = o.hold;
        if (hold1 == hold2) {
            return FieldComparator.compareField(this.field, o.field);
        } else {
            int number1 = hold1 != null ? hold1.getNumber() : 7;
            int number2 = hold2 != null ? hold2.getNumber() : 7;
            return number1 - number2;
        }
    }
}

class ObjComparator implements Comparator<Obj> {
    @Override
    public int compare(Obj o1, Obj o2) {
        return Integer.compare(o1.getPriority(), o2.getPriority());
    }
}