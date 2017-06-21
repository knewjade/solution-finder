package common.order;

import common.comparator.PiecesNumberComparator;
import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import core.mino.Block;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ForwardOrderLookUpTest {
    @Test
    public void parse() throws Exception {
        List<Block> blockList = Arrays.asList(Block.I, Block.T, Block.Z, Block.O, Block.I, Block.L);
        int toDepth = blockList.size() - 1;

        PiecesNumberComparator comparator = new PiecesNumberComparator();
        List<Pieces> forward1 = OrderLookup.forwardBlocks(blockList, toDepth).stream()
                .map(StackOrder::toList)
                .map(LongPieces::new)
                .sorted(comparator)
                .collect(Collectors.toList());

        ForwardOrderLookUp lookUp = new ForwardOrderLookUp(toDepth);
        List<Pieces> forward2 = lookUp.parse(blockList)
                .map(blockStream -> blockStream.collect(Collectors.toList()))
                .map(LongPieces::new)
                .sorted(comparator)
                .collect(Collectors.toList());

        assertThat(forward2, is(forward1));
    }
}