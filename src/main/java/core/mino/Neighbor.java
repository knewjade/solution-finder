package core.mino;

import core.srs.Rotate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Neighbor {
    public static final Neighbor EMPTY_NEIGHBOR = new Neighbor(Piece.EMPTY_PIECE);
    public static final List<Rotate> ROTATES_PRIORITY = Collections.unmodifiableList(Rotate.valueList());

    private boolean isValid(Neighbor neighbor) {
        return !EMPTY_NEIGHBOR.equals(neighbor);
    }

    private final Piece piece;
    private final List<Neighbor> nextMovesSources = new ArrayList<>();
    private final List<Neighbor> nextLeftRotateSources = new ArrayList<>();
    private final List<Neighbor> nextRightRotateSources = new ArrayList<>();
    private final List<Neighbor> nextLeftRotateDestinations = new ArrayList<>();
    private final List<Neighbor> nextRightRotateDestinations = new ArrayList<>();

    Neighbor(Piece piece) {
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }

    void updateUp(Neighbor neighbor) {
        if (isValid(neighbor))
            nextMovesSources.add(neighbor);
    }

    void updateDown(Neighbor neighbor) {
    }

    void updateLeft(Neighbor neighbor) {
        if (isValid(neighbor))
            nextMovesSources.add(neighbor);
    }

    void updateRight(Neighbor neighbor) {
        if (isValid(neighbor))
            nextMovesSources.add(neighbor);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Neighbor neighbor = (Neighbor) o;
        return piece.equals(neighbor.piece);
    }

    @Override
    public int hashCode() {
        return piece.hashCode();
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
}
