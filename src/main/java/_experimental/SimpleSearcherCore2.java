package _experimental;

import common.datastore.action.Action;
import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import searcher.common.validator.Validator;

import java.util.Set;

public class SimpleSearcherCore2<T extends Action> {
    private final MinoFactory minoFactory;
    private final Validator validator;
    private final UsingHoldPerfectTreeVisitor dataPool;
    private final Candidate<T> candidate;

    public SimpleSearcherCore2(MinoFactory minoFactory, Validator validator, UsingHoldPerfectTreeVisitor dataPool, Candidate<T> candidate) {
        this.minoFactory = minoFactory;
        this.validator = validator;
        this.dataPool = dataPool;
        this.candidate = candidate;
    }

    public boolean stepWithNext(Block drawn, SequenceElement element, boolean isLast) {
        Block hold = element.getHold();
        boolean stepWithoutHold = step(drawn, hold, element, isLast);

        if (drawn == hold)
            return stepWithoutHold;

        // Holdの探索
        boolean stepUsingHold = step(hold, drawn, element, isLast);
        return stepWithoutHold | stepUsingHold;
    }

    public boolean stepWithNextNoHold(Block drawn, SequenceElement element, boolean isLast) {
        return step(drawn, element.getHold(), element, isLast);
    }

    public boolean stepWhenNoNext(SequenceElement element, boolean isLast) {
        Block hold = element.getHold();
        return step(hold, null, element, isLast);
    }

    private boolean step(Block drawn, Block nextHold, SequenceElement element, boolean isLast) {
        Field currentField = element.getField();
        int max = element.getMaxClearLine();
        int nextDepth = element.getDepth() + 1;
        Set<T> candidateList = candidate.search(currentField, drawn, max);

        boolean isSucceed = false;
        for (T action : candidateList) {
            Field field = currentField.freeze(max);
            Mino mino = minoFactory.create(drawn, action.getRotate());
            field.put(mino, action.getX(), action.getY());
            int clearLine = field.clearLine();
            int maxClearLine = max - clearLine;

            if (!validator.validate(field, maxClearLine))
                continue;

            if (validator.satisfies(field, maxClearLine)) {
                isSucceed = true;
                continue;
            }

            if (isLast)
                continue;

            SequenceElement nextElement = new SequenceElement(field, nextHold, maxClearLine, nextDepth);
            dataPool.register(nextElement);
        }

        return isSucceed;
    }
}
