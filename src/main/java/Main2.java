import core.mino.Block;

import java.util.Arrays;
import java.util.List;

public class Main2 {
    public static void main(String[] args) {
        List<Block> blocks = Arrays.asList(Block.T, Block.J, Block.S, Block.I);
        List<Block> blocks2 = Arrays.asList(Block.T, Block.J, Block.L, Block.I);
        System.out.println(blocks.equals(blocks2));

    }
}
