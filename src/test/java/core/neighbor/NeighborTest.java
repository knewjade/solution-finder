package core.neighbor;

import core.mino.Mino;
import core.mino.Piece;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class NeighborTest {
    @Test
    void getPiece() {
        OriginalPiece piece = new OriginalPiece(new Mino(Piece.T, Rotate.Spawn), 1, 0, 4);
        Neighbor neighbor = new Neighbor(piece);
        assertThat(piece).isEqualTo(neighbor.getOriginalPiece());
    }

    @Test
    void getNextMovesSources() {
        OriginalPiece piece = new OriginalPiece(new Mino(Piece.T, Rotate.Spawn), 1, 0, 4);
        Neighbor neighbor = new Neighbor(piece);

        OriginalPieceFactory factory = new OriginalPieceFactory(4);
        List<OriginalPiece> pieces = new ArrayList<>(factory.create());
        Randoms randoms = new Randoms();

        ArrayList<Neighbor> all = new ArrayList<>();

        // left
        for (OriginalPiece sample : randoms.sample(pieces, 3)) {
            Neighbor nei = new Neighbor(sample);
            if (!all.contains(nei)) {
                neighbor.updateLeft(nei);
                all.add(nei);
            }
        }

        // right
        for (OriginalPiece sample : randoms.sample(pieces, 3)) {
            Neighbor nei = new Neighbor(sample);
            if (!all.contains(nei)) {
                neighbor.updateRight(nei);
                all.add(nei);
            }
        }

        // up
        for (OriginalPiece sample : randoms.sample(pieces, 3)) {
            Neighbor nei = new Neighbor(sample);
            if (!all.contains(nei)) {
                neighbor.updateUp(nei);
                all.add(nei);
            }
        }

        assertThat(neighbor.getNextMovesSources()).containsAll(all);
    }

    @Test
    void getNextLeftRotateDestinations() {
        OriginalPiece piece = new OriginalPiece(new Mino(Piece.T, Rotate.Spawn), 1, 0, 4);
        Neighbor neighbor = new Neighbor(piece);

        OriginalPieceFactory factory = new OriginalPieceFactory(4);
        List<OriginalPiece> pieces = new ArrayList<>(factory.create());
        Randoms randoms = new Randoms();

        List<Neighbor> samples = randoms.sample(pieces, 4).stream()
                .map(Neighbor::new)
                .collect(Collectors.toList());
        neighbor.updateLeftRotateDestination(samples);
        assertThat(neighbor.getNextLeftRotateDestinations()).containsAll(samples);
    }

    @Test
    void getNextLeftRotateSources() {
        OriginalPiece piece = new OriginalPiece(new Mino(Piece.T, Rotate.Spawn), 1, 0, 4);
        Neighbor neighbor = new Neighbor(piece);

        OriginalPieceFactory factory = new OriginalPieceFactory(4);
        List<OriginalPiece> pieces = new ArrayList<>(factory.create());
        Randoms randoms = new Randoms();

        List<Neighbor> samples = randoms.sample(pieces, 4).stream()
                .map(Neighbor::new)
                .collect(Collectors.toList());
        neighbor.updateLeftRotateSource(samples);
        assertThat(neighbor.getNextLeftRotateSources()).containsAll(samples);
    }

    @Test
    void getNextRightRotateDestinations() {
        OriginalPiece piece = new OriginalPiece(new Mino(Piece.T, Rotate.Spawn), 1, 0, 4);
        Neighbor neighbor = new Neighbor(piece);

        OriginalPieceFactory factory = new OriginalPieceFactory(4);
        List<OriginalPiece> pieces = new ArrayList<>(factory.create());
        Randoms randoms = new Randoms();

        List<Neighbor> samples = randoms.sample(pieces, 4).stream()
                .map(Neighbor::new)
                .collect(Collectors.toList());
        neighbor.updateRightRotateDestination(samples);
        assertThat(neighbor.getNextRightRotateDestinations()).containsAll(samples);
    }

    @Test
    void getNextRightRotateSources() {
        OriginalPiece piece = new OriginalPiece(new Mino(Piece.T, Rotate.Spawn), 1, 0, 4);
        Neighbor neighbor = new Neighbor(piece);

        OriginalPieceFactory factory = new OriginalPieceFactory(4);
        List<OriginalPiece> pieces = new ArrayList<>(factory.create());
        Randoms randoms = new Randoms();

        List<Neighbor> samples = randoms.sample(pieces, 4).stream()
                .map(Neighbor::new)
                .collect(Collectors.toList());
        neighbor.updateRightRotateSource(samples);
        assertThat(neighbor.getNextRightRotateSources()).containsAll(samples);
    }
}