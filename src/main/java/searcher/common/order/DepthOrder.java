package searcher.common.order;

import core.mino.Block;
import core.field.Field;
import searcher.common.History;

public class DepthOrder extends Order {
    public DepthOrder(Field field, Block hold, int maxClearLine, int maxDepth) {
        super(field, hold, maxClearLine, maxDepth);
    }

    DepthOrder(Field field, Block hold, int maxClearLine, History history) {
        super(field, hold, maxClearLine, history);
    }

    @Override
    public int compareTo(Order o) {
        int compare = Integer.compare(this.getHistory().getIndex(), o.getHistory().getIndex());
        if (compare == 0)
            return super.compareTo(o);
        return compare;
    }
}
