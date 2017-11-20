package common.pattern;

import common.datastore.PieceCounter;
import common.datastore.blocks.Pieces;

import java.util.stream.Stream;

public interface PatternGenerator {
    int getDepth();

    Stream<Pieces> blocksStream();

    Stream<PieceCounter> blockCountersStream();
}
