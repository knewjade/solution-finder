package lib;

import core.mino.Block;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Randoms {
    private final Random random;

    public Randoms() {
        this.random = new Random();
    }

    public int nextInt(int origin, int bound) {
        int size = bound - origin;
        return origin + random.nextInt(size);
    }

    public List<Block> blocks(int size) {
        return random.ints(0, Block.getSize())
                .limit(size)
                .mapToObj(Block::getBlock)
                .collect(Collectors.toList());
    }
}
