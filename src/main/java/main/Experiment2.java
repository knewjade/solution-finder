package main;

import core.mino.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static core.mino.Block.*;

public class Experiment2 {
    public static void main(String[] args) {
        List<Block> blocks = Arrays.asList(T, I, S);
        ArrayList<Pieces> reverses = reverse(blocks, blocks.size() + 1);
        for (Pieces reverse : reverses) {
            System.out.println(reverse);
        }
    }

    public static ArrayList<Pieces> reverse(List<Block> blocks, int maxDepth) {
        ArrayList<Pieces> candidates = new ArrayList<>();
        candidates.add(new SmallPieces());

        for (int depth = 0; depth < maxDepth; depth++) {
            Block block = depth < blocks.size() ? blocks.get(depth) : null;
            int size = candidates.size();
            if (depth < maxDepth - 1) {
                for (int index = 0; index < size; index++) {
                    Pieces pieces = candidates.get(index);
                    Pieces freeze = pieces.freeze();

                    pieces.addLast(block);
                    freeze.stock(block);

                    candidates.add(freeze);
                }
            } else {
                for (Pieces pieces : candidates)
                    pieces.stock(block);
            }
        }

        return candidates;
    }

    private static class ListPieces implements Pieces {
        private final List<Block> blocks;
        private int stockIndex;

        ListPieces() {
            this(new ArrayList<>(), 0);
        }

        private ListPieces(List<Block> blocks, int stockIndex) {
            this.blocks = blocks;
            this.stockIndex = stockIndex;
        }

        @Override
        public void addLast(Block block) {
            blocks.add(block);
        }

        @Override
        public void stock(Block block) {
            blocks.add(stockIndex, block);
            stockIndex = blocks.size();
        }

        @Override
        public List<Block> getBlocks() {
            return blocks;
        }

        @Override
        public Pieces freeze() {
            return new ListPieces(new ArrayList<>(blocks), stockIndex);
        }

        @Override
        public String toString() {
            return "ListPieces{" +
                    "blocks=" + this.blocks +
                    '}';
        }
    }

    // ミノ数は10まで
    private static class SmallPieces implements Pieces {
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
}

