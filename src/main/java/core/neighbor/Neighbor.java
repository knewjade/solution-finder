package core.neighbor;

import common.datastore.action.Action;
import core.srs.Rotate;

import java.util.ArrayList;
import java.util.List;

public class Neighbor implements Action {
    public static final Neighbor EMPTY_NEIGHBOR = new Neighbor(OriginalPiece.EMPTY_COLLIDER_PIECE);

    private boolean isValid(Neighbor neighbor) {
        return !EMPTY_NEIGHBOR.equals(neighbor);
    }

    private final OriginalPiece piece;
    private final List<Neighbor> nextMovesSources = new ArrayList<>();
    private final List<Neighbor> nextLeftRotateSources = new ArrayList<>();
    private final List<Neighbor> nextRightRotateSources = new ArrayList<>();
    private final List<Neighbor> nextLeftRotateDestinations = new ArrayList<>();
    private final List<Neighbor> nextRightRotateDestinations = new ArrayList<>();

    private Neighbor up = EMPTY_NEIGHBOR;
    private Neighbor left = EMPTY_NEIGHBOR;
    private Neighbor right = EMPTY_NEIGHBOR;

    Neighbor(OriginalPiece piece) {
        this.piece = piece;
    }

    public OriginalPiece getOriginalPiece() {
        return piece;
    }

    void updateUp(Neighbor neighbor) {
        if (isValid(neighbor)) {
            nextMovesSources.add(neighbor);
            this.up = neighbor;
        }
    }

    void updateLeft(Neighbor neighbor) {
        if (isValid(neighbor)) {
            nextMovesSources.add(neighbor);
            this.left = neighbor;
        }
    }

    void updateRight(Neighbor neighbor) {
        if (isValid(neighbor)) {
            nextMovesSources.add(neighbor);
            this.right = neighbor;
        }
    }

    void updateLeftRotateDestination(List<Neighbor> destinations) {
        for (Neighbor neighbor : destinations)
            if (isValid(neighbor))
                nextLeftRotateDestinations.add(neighbor);
    }

    void updateRightRotateDestination(List<Neighbor> destinations) {
        for (Neighbor neighbor : destinations)
            if (isValid(neighbor))
                nextRightRotateDestinations.add(neighbor);
    }

    void updateLeftRotateSource(List<Neighbor> sources) {
        for (Neighbor neighbor : sources)
            if (isValid(neighbor))
                nextLeftRotateSources.add(neighbor);
    }

    void updateRightRotateSource(List<Neighbor> sources) {
        for (Neighbor neighbor : sources)
            if (isValid(neighbor))
                nextRightRotateSources.add(neighbor);
    }

    public Neighbor getLeft() {
        return left;
    }

    public Neighbor getRight() {
        return right;
    }

    public Neighbor getUp() {
        return up;
    }

    public List<Neighbor> getNextMovesSources() {
        return nextMovesSources;
    }

    public List<Neighbor> getNextLeftRotateSources() {
        return nextLeftRotateSources;
    }

    public List<Neighbor> getNextRightRotateSources() {
        return nextRightRotateSources;
    }

    public List<Neighbor> getNextLeftRotateDestinations() {
        return nextLeftRotateDestinations;
    }

    public List<Neighbor> getNextRightRotateDestinations() {
        return nextRightRotateDestinations;
    }

    @Override
    public int getX() {
        return piece.getX();
    }

    @Override
    public int getY() {
        return piece.getY();
    }

    @Override
    public Rotate getRotate() {
        return piece.getRotate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Action)) return false;
        return Action.defaultEquals(this, (Action) o);
    }

    @Override
    public int hashCode() {
        return Action.defaultHashCode(getX(), getY(), getRotate());
    }

    @Override
    public String toString() {
        return "Neighbor{" +
                "x=" + getX() +
                ", y=" + getY() +
                ", rotate=" + getRotate() +
                '}';
    }
}
