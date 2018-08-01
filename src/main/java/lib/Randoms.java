package lib;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.Piece;
import core.srs.Rotate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Randoms {
    public static final int FIELD_WIDTH = 10;
    private final Random random;
    private static final String[] STRINGS = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ?/_-^¥=~|[]@:1234567890!\"#$%&'()<>あいうえおかきくけこさしすせそタチツテトナニヌネノハヒフヘホ朝午前後電話時計机携帯光空雨青赤車動力鉄　＿？：；：。＾ー￥：＄”（）".split("");

    public Randoms() {
        this.random = new Random();
    }

    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    public boolean nextBoolean(double truePercent) {
        return random.nextDouble() < truePercent;
    }

    public int nextIntOpen(int bound) {
        assert 0 < bound;
        return random.nextInt(bound);
    }

    // boundは含まない
    public int nextIntOpen(int origin, int bound) {
        assert origin < bound;
        int size = bound - origin;
        return origin + random.nextInt(size);
    }

    public int nextIntClosed(int origin, int boundClosed) {
        return nextIntOpen(origin, boundClosed + 1);
    }

    public Piece block() {
        return Piece.getBlock(random.nextInt(Piece.getSize()));
    }

    public List<Piece> blocks(int size) {
        return random.ints(size, 0, Piece.getSize())
                .mapToObj(Piece::getBlock)
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
                .map(this::nextIntOpen)
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
                int index = nextIntOpen(height);
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
                int index = nextIntOpen(height);
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

                int start = nextIntOpen(min, max);
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

    public long key() {
        return pick(Arrays.asList(
                0L,
                1L,
                1024L,
                1025L,
                1048576L,
                1048577L,
                1049600L,
                1049601L,
                1073741824L,
                1073741825L,
                1073742848L,
                1073742849L,
                1074790400L,
                1074790401L,
                1074791424L,
                1074791425L,
                1099511627776L,
                1099511627777L,
                1099511628800L,
                1099511628801L,
                1099512676352L,
                1099512676353L,
                1099512677376L,
                1099512677377L,
                1100585369600L,
                1100585369601L,
                1100585370624L,
                1100585370625L,
                1100586418176L,
                1100586418177L,
                1100586419200L,
                1100586419201L,
                1125899906842624L,
                1125899906842625L,
                1125899906843648L,
                1125899906843649L,
                1125899907891200L,
                1125899907891201L,
                1125899907892224L,
                1125899907892225L,
                1125900980584448L,
                1125900980584449L,
                1125900980585472L,
                1125900980585473L,
                1125900981633024L,
                1125900981633025L,
                1125900981634048L,
                1125900981634049L,
                1126999418470400L,
                1126999418470401L,
                1126999418471424L,
                1126999418471425L,
                1126999419518976L,
                1126999419518977L,
                1126999419520000L,
                1126999419520001L,
                1127000492212224L,
                1127000492212225L,
                1127000492213248L,
                1127000492213249L,
                1127000493260800L,
                1127000493260801L,
                1127000493261824L,
                1127000493261825L
        ));
    }

    public String string() {
        return STRINGS[nextIntOpen(STRINGS.length)];
    }

    public double nextDouble() {
        return random.nextDouble();
    }

    public List<Piece> block11InCycle(int cycle) {
        assert 0 <= cycle;
        ArrayList<Piece> pieces = new ArrayList<>();
        ArrayList<Piece> allPieces = new ArrayList<>(Piece.valueList());
        List<Integer> cycleCounts = get11CycleCounts(cycle);
        for (int count : cycleCounts) {
            Collections.shuffle(allPieces);
            pieces.addAll(allPieces.subList(0, count));
        }
        return pieces;
    }

    private List<Integer> get11CycleCounts(int cycle) {
        if (cycle == 0)
            return Arrays.asList(7, 4);

        cycle %= 7;
        int prevLastUsed = (10 * cycle + 1) % 7;
        int firstLoop = 7 - prevLastUsed;

        if (firstLoop <= 2) {
            return Arrays.asList(1, firstLoop, 7, 3 - firstLoop);
        } else {
            return Arrays.asList(1, firstLoop, 10 - firstLoop);
        }
    }

    public List<Piece> block10InCycle(int cycle) {
        assert 0 <= cycle;
        ArrayList<Piece> pieces = new ArrayList<>();
        ArrayList<Piece> allPieces = new ArrayList<>(Piece.valueList());
        List<Integer> cycleCounts = get10CycleCounts(cycle);
        for (int count : cycleCounts) {
            Collections.shuffle(allPieces);
            pieces.addAll(allPieces.subList(0, count));
        }
        return pieces;
    }

    private List<Integer> get10CycleCounts(int cycle) {
        cycle %= 7;
        int prevLastUsed = (10 * cycle) % 7;
        int firstLoop = 7 - prevLastUsed;

        if (firstLoop <= 2) {
            return Arrays.asList(firstLoop, 7, 3 - firstLoop);
        } else {
            return Arrays.asList(firstLoop, 10 - firstLoop);
        }
    }
}
