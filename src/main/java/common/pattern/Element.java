package common.pattern;

import common.datastore.BlockCounter;
import common.datastore.blocks.Blocks;

import java.util.List;

public interface Element {
    int getPopCount();

    List<Blocks> getPermutationBlocks();

    List<BlockCounter> getBlockCounters();
}
