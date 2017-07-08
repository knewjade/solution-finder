package lib;

import core.mino.Block;
import core.srs.Rotate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Randoms {
    private final Random random;

    public Randoms() {
        this.random = new Random();
    }

    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public int nextInt(int origin, int bound) {
        int size = bound - origin;
        return origin + random.nextInt(size);
    }

    public Block block() {
        return Block.getBlock(random.nextInt(Block.getSize()));
    }

    public List<Block> blocks(int size) {
        return random.ints(size, 0, Block.getSize())
                .mapToObj(Block::getBlock)
                .collect(Collectors.toList());
    }

    public Rotate rotate() {
        return Rotate.getRotate(random.nextInt(Rotate.getSize()));
    }

    public <T> T choose(List<T> bag) {
        int index = random.nextInt(bag.size());
        return bag.get(index);
    }

    public <T> List<T> combinations(List<T> bag, int size) {
        int[] indexes = IntStream.range(0, size)
                .map(value -> bag.size() - value)
                .map(this::nextInt)
                .toArray();

        for (int i = indexes.length - 2; 0 <= i; i--) {
            int index = indexes[i];
            for (int j = i + 1; j < indexes.length; j++) {
                if (index <= indexes[j])
                    indexes[j] += 1;
            }
        }

        return Arrays.stream(indexes)
                .mapToObj(bag::get)
                .collect(Collectors.toList());
    }
}
