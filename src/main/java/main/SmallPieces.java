package main;

import core.mino.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// ミノ数は10まで
public class SmallPieces implements Pieces {
    private static final int MAX_KIND_OF_BLOCKS = Block.values().length + 1;
    private static final int EMPTY_BLOCK_NUMBER = MAX_KIND_OF_BLOCKS - 1;

    private static final int POWERS_MAP[] = new int[10];
    private static final Block BLOCKS_MAP[] = new Block[MAX_KIND_OF_BLOCKS];

    static {
        POWERS_MAP[0] = 1;
        for (int index = 1; index < POWERS_MAP.length; index++)
            POWERS_MAP[index] = POWERS_MAP[index - 1] * BLOCKS_MAP.length;

        for (Block block : Block.values())
            BLOCKS_MAP[block.getNumber()] = block;
        BLOCKS_MAP[EMPTY_BLOCK_NUMBER] = null;
    }

    private int blocks;
    private int lastIndex;
    private int stockIndex;

    SmallPieces() {
        this(0, 1, 0);
    }

    private SmallPieces(int blocks, int lastIndex, int stockIndex) {
        this.blocks = blocks;
        this.lastIndex = lastIndex;
        this.stockIndex = stockIndex;
    }

    @Override
    public void addLast(Block block) {
        addBlock(lastIndex, block);
        lastIndex += 1;
    }

    private void addBlock(int index, Block block) {
        int number = block != null ? block.getNumber() : EMPTY_BLOCK_NUMBER;
        blocks += number * POWERS_MAP[index];
    }

    @Override
    public void stock(Block block) {
        addBlock(stockIndex, block);
        stockIndex = lastIndex;
        lastIndex += 1;
    }

    @Override
    public List<Block> getBlocks() {
        Block[] blockArray = new Block[lastIndex - 1];
        int current = blocks;
        int arrayIndex = blockArray.length - 1;
        for (int index = lastIndex - 1; index >= 0; index--) {
            int number = current / POWERS_MAP[index];
            if (stockIndex != index) {
                blockArray[arrayIndex] = BLOCKS_MAP[number];
                arrayIndex -= 1;
            }
            current -= POWERS_MAP[index] * number;
        }
        return Arrays.asList(blockArray);
    }

    @Override
    public Pieces freeze() {
        if (lastIndex < 10)
            return new SmallPieces(blocks, lastIndex, stockIndex);
        else
            return new ListPieces(new ArrayList<>(getBlocks()), stockIndex);
    }

    @Override
    public String toString() {
        return "ListPieces{" +
                "blocks=" + getBlocks() +
                '}';
    }
}
