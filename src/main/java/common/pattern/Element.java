package common.pattern;

import common.datastore.PieceCounter;
import common.datastore.blocks.Pieces;

import java.util.List;

public interface Element {
    int getPopCount();

    List<Pieces> getPermutationBlocks();

    List<PieceCounter> getPieceCounters();
}
