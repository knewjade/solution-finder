package main;

import a.Depend;
import a.Estimate;
import a.MinoPivot;
import action.reachable.LockedReachable;
import core.mino.Block;
import core.field.Field;
import core.srs.MinoRotation;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.field.FieldFactory;
import misc.Stopwatch;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static core.mino.Block.*;

public class Experiment {
    public static final int FIELD_WIDTH = 10;

    public static void main(String[] args) {
//        main1();

        String marks = "" +
                "XXXXXXXXXX" +
                "XX_XX_XXXX" +
                "XXXXXXXXXX" +
                "XXXXXXXXXX" +
                "";
        Field field = FieldFactory.createField(marks);


//        System.out.println(0b000000010000000001000000000100000000010000000001000000000100L);

        Stopwatch stopwatch = Stopwatch.createStoppedStopwatch();

        for (int count = 0; count < 1000000; count++) {
            Field freeze = field.freeze(field.getMaxFieldHeight());
            freeze.clearLine();
        }

        for (int count = 0; count < 1000000; count++) {
            Field freeze = field.freeze(field.getMaxFieldHeight());
            stopwatch.start();
            freeze.clearLine();
            stopwatch.stop();
        }

        System.out.println(stopwatch.toMessage(TimeUnit.NANOSECONDS));
    }

    private static void main1() {
//        String marks = "" +
//                "____6_____" +
//                "___6663___" +
//                "____55322_" +
//                "00_5543122" +
//                "00_4443111" +
//                "";
//
//        List<Block> blocks = Arrays.asList(O, J, Z, I, L, S, T);

        String marks = "" +
                "__________" +
                "___22____5" +
                "0___224445" +
                "00_1133345" +
                "_0_113___5" +
                "";

        List<Block> blocks = Arrays.asList(S, O, Z, L, J, I);

        // フィールド番号 -> 各ミノの回転方向と回転軸
        int[][] numberField = createNumberField(marks);
        MinoFactory minoFactory = new MinoFactory();
        Estimate estimate = new Estimate(minoFactory);
        List<MinoPivot> minoPivots = estimate.create(numberField, blocks);

        System.out.println(minoPivots);
        System.out.println(minoPivots.stream().map(MinoPivot::getMino).map(Mino::getRotate).collect(Collectors.toList()));

        //
        int maxY = 5;
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, maxY);
        Depend depend = new Depend(reachable);

        Field field = FieldFactory.createField(marks);
        Map<Integer, Set<Integer>> results = depend.extract(field, numberField, minoPivots);
        System.out.println(results);
    }

    private static int[][] createNumberField(String marks) {
        if (marks.length() % FIELD_WIDTH != 0)
            throw new IllegalArgumentException("length of marks should be 'mod 10'");

        int maxY = marks.length() / FIELD_WIDTH;

        int[][] field = new int[maxY][FIELD_WIDTH];

        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < FIELD_WIDTH; x++) {
                char mark = marks.charAt((maxY - y - 1) * FIELD_WIDTH + x);
                if (mark != ' ' && mark != '_')
                    field[y][x] = Integer.valueOf(String.valueOf(mark));
                else
                    field[y][x] = -1;
            }
        }

        return field;
    }
}
