package common.order;

import core.mino.Block;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReverseOrderLookUpTest {
    @Test
    public void parse() throws Exception {
        List<Block> blockList = Arrays.asList(Block.I, Block.T, Block.Z, Block.O, Block.I, Block.L);
        int toDepth = blockList.size() + 1;

        Comparator<List<Block>> comparator = (o1, o2) -> {
            int size1 = o1.size();
            int size2 = o2.size();
            int compareSize = Integer.compare(size1, size2);
            if (compareSize != 0)
                return compareSize;

            Comparator<Block> blockComparator = Comparator.nullsLast(Enum::compareTo);
            for (int index = 0; index < size1; index++) {
                int compare = blockComparator.compare(o1.get(index), o2.get(index));
                if (compare != 0)
                    return compare;
            }

            return 0;
        };

        List<List<Block>> reverse1 = OrderLookup.reverseBlocks(blockList, toDepth).stream()
                .map(StackOrder::toList)
                .sorted(comparator)
                .collect(Collectors.toList());

        ReverseOrderLookUp lookUp = new ReverseOrderLookUp(blockList.size(), toDepth);
        List<List<Block>> reverse2 = lookUp.parse(blockList)
                .map(blockStream -> blockStream.collect(Collectors.toList()))
                .sorted(comparator)
                .collect(Collectors.toList());

        assertThat(reverse2, is(reverse1));
    }
}