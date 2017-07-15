package lib;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.srs.Rotate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Randoms {
    public static final int FIELD_WIDTH = 10;
    private final Random random;
    public static final String[] STRINGS = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ?/_-^¥=~|[]@:1234567890!\"#$%&'()<>あいうえおかきくけこさしすせそタチツテトナニヌネノハヒフヘホ朝午前後電話時計机携帯光空雨青赤車動力鉄　＿？：；：。＾ー￥：＄”（）".split("");

    public Randoms() {
        this.random = new Random();
    }

    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    // TODO: unittest
    public boolean nextBoolean(double truePercent) {
        return random.nextDouble() < truePercent;
    }

    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public int nextInt(int origin, int bound) {
        int size = bound - origin;
        return origin + random.nextInt(size);
    }

    public int nextIntClosed(int origin, int boundClosed) {
        return nextInt(origin, boundClosed + 1);
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

    public <T> T pick(List<T> bag) {
        int index = random.nextInt(bag.size());
        return bag.get(index);
    }

    public <T> List<T> sample(List<T> bag, int size) {
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

    public Field field(int height, int numOfEmptyMinos) {
        assert numOfEmptyMinos <= (10 * height / 4);

        int numOfEmpty = numOfEmptyMinos * 4;

        int[] emptyEachLine = new int[height];
        int numOfBlocks = 10 * height - numOfEmpty;
        if (numOfEmpty < numOfBlocks) {
            // 空白のほうが少ないとき
            int count = 0;
            while (count < numOfEmpty) {
                int index = nextInt(height);
                if (emptyEachLine[index] < 10) {
                    emptyEachLine[index] += 1;
                    count += 1;
                }
            }
        } else {
            // ブロックのほうが少ないとき
            Arrays.fill(emptyEachLine, 10);

            int count = 0;
            while (count < numOfBlocks) {
                int index = nextInt(height);
                if (0 < emptyEachLine[index]) {
                    emptyEachLine[index] -= 1;
                    count += 1;
                }
            }
        }

        Field field = FieldFactory.createField(height);
        int prevStart = 0;
        int prevEnd = 10;
        for (int y = height - 1; 0 <= y; y--) {
            int count = emptyEachLine[y];
            if (count == 0) {
                // すべてのブロックを埋める
                for (int x = 0; x < FIELD_WIDTH; x++)
                    field.setBlock(x, y);
            } else if (count != FIELD_WIDTH) {
                // 一部に空白をつくる
                int min = count <= prevStart ? prevStart - count + 1 : 0;
                int max = prevEnd <= FIELD_WIDTH - count ? prevEnd : FIELD_WIDTH - count;

                int start = nextInt(min, max);
                assert 0 <= start && start < FIELD_WIDTH : Arrays.toString(emptyEachLine);
                int end = start + count;
                assert 0 <= end && end < FIELD_WIDTH : Arrays.toString(emptyEachLine);

                for (int x = 0; x < start; x++)
                    field.setBlock(x, y);

                for (int x = end; x < 10; x++)
                    field.setBlock(x, y);

                prevStart = start;
                prevEnd = end;
            }
        }

        assert height * FIELD_WIDTH - field.getNumOfAllBlocks() == numOfEmpty;

        return field;
    }

    // TODO: add key candidate
    public long keys() {
        return pick(Arrays.asList(0L, 1024L, 1025L, 1049601L, 1048576L, 1074790400L));
    }

    public String string() {
        return STRINGS[nextInt(STRINGS.length)];
    }
}
