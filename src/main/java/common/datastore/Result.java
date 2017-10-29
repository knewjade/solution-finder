package common.datastore;

import common.datastore.action.Action;
import common.datastore.order.Order;
import core.mino.Piece;

public class Result {
    private final Order order;
    private final Piece lastPiece;
    private final Action action;
    private final Piece lastHold;

    public Result(Order order, Piece lastPiece, Action action, Piece lastHold) {
        assert order != null && lastPiece != null && action != null;
        this.order = order;
        this.lastPiece = lastPiece;
        this.action = action;
        this.lastHold = lastHold;
    }

    public Piece getLastPiece() {
        return lastPiece;
    }

    public Action getLastAction() {
        return action;
    }

    public Piece getLastHold() {
        return lastHold;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (!order.equals(result.order)) return false;
        if (lastPiece != result.lastPiece) return false;
        if (!action.equals(result.action)) return false;
        return lastHold == result.lastHold;
    }

    @Override
    public int hashCode() {
        int result = order.hashCode();
        result = 31 * result + lastPiece.hashCode();
        result = 31 * result + action.hashCode();
        result = 31 * result + (lastHold != null ? lastHold.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "order=" + order.getHistory() +
                ", lastPiece=" + lastPiece +
                ", candidate.candidate=" + action +
                ", hold=" + lastHold +
                '}';
    }
}
