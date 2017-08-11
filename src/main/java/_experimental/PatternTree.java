package _experimental;

import common.comparator.FieldComparator;
import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import searcher.common.validator.Validator;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class PatternTree implements IPatternTree {
    private final EnumMap<Block, IPatternTree> map = new EnumMap<>(Block.class);
    private final AtomicBoolean isPossible = new AtomicBoolean(false);

    public void build(List<Block> blocks, Function<List<Block>, IPatternTree> terminate) {
        build(blocks, 0, terminate);
    }

    @Override
    public void build(List<Block> blocks, int depth, Function<List<Block>, IPatternTree> terminate) {
        assert depth < blocks.size() : depth;
        Block block = blocks.get(depth);

        if (depth == blocks.size() - 1) {
            map.computeIfAbsent(block, key -> terminate.apply(blocks));
        } else {
            IPatternTree tree = map.computeIfAbsent(block, key -> new PatternTree());
            tree.build(blocks, depth + 1, terminate);
        }
    }

    public boolean get(List<Block> blocks) {
        return get(blocks, 0);
    }

    @Override
    public boolean get(List<Block> blocks, int depth) {
        assert depth < blocks.size() : depth;
        Block block = blocks.get(depth);

        assert map.containsKey(block) : map;
        IPatternTree tree = map.get(block);

        if (depth == blocks.size() - 1) {
            return tree.isPossible();
        } else {
            return tree.get(blocks, depth + 1);
        }
    }

    @Override
    public boolean isPossible() {
        return isPossible.get();
    }

    @Override
    public void success() {
        boolean oldValue = isPossible.getAndSet(true);
        if (!oldValue)
            for (IPatternTree tree : map.values())
                tree.success();
    }

    public boolean run(TreeVisitor visitor) {
        return run(visitor, 0);
    }

        // すべての探索が成功したときtrueを返す
    @Override
    public boolean run(TreeVisitor visitor, int depth) {
        boolean result = true;
        for (Map.Entry<Block, IPatternTree> entry : map.entrySet()) {
            Block block = entry.getKey();
            visitor.visit(depth, block);

            IPatternTree tree = entry.getValue();
            result &= tree.run(visitor, depth + 1);
        }

        if (result)
            this.success();

        return result;
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