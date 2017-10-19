package common.pattern;

import common.datastore.BlockCounter;
import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.iterable.CombinationIterable;
import common.iterable.PermutationIterable;
import core.mino.Block;

import java.util.ArrayList;
import java.util.List;

public class WildcardElement implements Element {
    private final int size;
    private final List<Blocks> permutationBlocks;
    private final List<BlockCounter> blockCounters;

    WildcardElement(int size) {
        this.size = size;
        this.permutationBlocks = createPermutationBlocks(size);
        this.blockCounters = createBlockCounters(size);
    }

    private ArrayList<Blocks> createPermutationBlocks(int size) {
        PermutationIterable<Block> iterable = new PermutationIterable<>(Block.valueList(), size);
        ArrayList<Blocks> blocksList = new ArrayList<>();
        for (List<Block> permutation : iterable)
            blocksList.add(new LongBlocks(permutation));
        return blocksList;
    }

    private ArrayList<BlockCounter> createBlockCounters(int size) {
        CombinationIterable<Block> iterable = new CombinationIterable<>(Block.valueList(), size);
        ArrayList<BlockCounter> blockCounterList = new ArrayList<>();
        for (List<Block> combination : iterable)
            blockCounterList.add(new BlockCounter(combination));
        return blockCounterList;
    }

    @Override
    public int getPopCount() {
        return size;
    }

    @Override
    public List<Blocks> getPermutationBlocks() {
        return permutationBlocks;
    }

    @Override
    public List<BlockCounter> getBlockCounters() {
        return blockCounters;
    }
}
