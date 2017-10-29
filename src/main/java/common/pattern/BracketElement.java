package common.pattern;

import common.SyntaxException;
import common.datastore.BlockCounter;
import common.datastore.blocks.Blocks;
import common.datastore.blocks.LongBlocks;
import common.iterable.CombinationIterable;
import common.iterable.PermutationIterable;
import core.mino.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BracketElement implements Element {
    private final int size;
    private final List<Blocks> permutationBlocks;
    private final List<BlockCounter> blockCounters;

    BracketElement(Token token, int size) throws SyntaxException {
        HashSet<Block> blockSet = parseBlockSet(token);

        if (blockSet.isEmpty())
            throw new SyntaxException("Empty in []", token.getLastIndex());

        if (size <= 0)
            throw new SyntaxException("no pop", token.getLastIndex());
        else if (blockSet.size() < size)
            throw new SyntaxException("over pop", token.getLastIndex());

        this.size = size;
        this.permutationBlocks = createPermutationBlocks(blockSet, size);
        this.blockCounters = createBlockCounters(blockSet, size);
    }

    private HashSet<Block> parseBlockSet(Token token) throws SyntaxException {
        HashSet<Block> blocks = new HashSet<>();
        while (token.isContinue()) {
            Block block = token.nextBlock();
            if (blocks.contains(block))
                throw new SyntaxException(String.format("Duplicate '%s' blocks in []", block.getName()), token.getLastIndex());
            blocks.add(block);
        }
        return blocks;
    }

    private ArrayList<Blocks> createPermutationBlocks(HashSet<Block> blockList, int size) {
        PermutationIterable<Block> iterable = new PermutationIterable<>(blockList, size);
        ArrayList<Blocks> blocksList = new ArrayList<>();
        for (List<Block> permutation : iterable)
            blocksList.add(new LongBlocks(permutation));
        return blocksList;
    }

    private ArrayList<BlockCounter> createBlockCounters(HashSet<Block> blockList, int size) {
        CombinationIterable<Block> iterable = new CombinationIterable<>(blockList, size);
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
