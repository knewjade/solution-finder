package core.field;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LongBoardMapTest {
    private static final int FIELD_WIDTH = 10;

    @Test
    public void testDeleteAll() throws Exception {
        for (int pattern = 0; pattern < 64; pattern++) {
            List<Boolean> leftFlags = createLeftFlags(pattern);
            long mask = 0L;
            for (int index = 0; index < leftFlags.size(); index++) {
                if (!leftFlags.get(index))
                    mask |= 1L << (index * 10);
            }
//            System.out.println(leftFlags);
//            System.out.println((int) ((mask >> 29) | (mask & 1073741823)));

            SmallField field = FieldFactory.createSmallField();
            Random random = new Random();
            for (int index = 0; index < leftFlags.size(); index++) {
                for (int x = 0; x < FIELD_WIDTH; x++)
                    field.setBlock(x, index);

                if (leftFlags.get(index))
                    field.removeBlock(random.nextInt(10), index);
            }

//            System.out.println(FieldView.toString(field));

            long board = field.getBoard(0);
            long deleted = LongBoardMap.deleteLine(board, mask);

//            System.out.println(FieldView.toString(new SmallField(deleted)));

            long inserted = LongBoardMap.insertBlackLine(deleted, mask);

//            System.out.println(FieldView.toString(new SmallField(inserted)));

            assertThat(inserted, is(board));
        }
    }

    private static ArrayList<Boolean> createLeftFlags(int pattern) {
        ArrayList<Boolean> booleans = new ArrayList<>();
        int value = pattern;
        for (int i = 0; i < 6; i++) {
            booleans.add((value & 1) != 0);
            value >>>= 1;
        }
        return booleans;
    }
}
