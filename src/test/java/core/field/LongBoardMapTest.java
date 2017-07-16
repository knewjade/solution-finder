package core.field;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: unittest: LongBoardのinsertWhiteLineのテスト
class LongBoardMapTest {
    private static final int FIELD_WIDTH = 10;

    @Test
    void testDeleteAll() throws Exception {
        for (int pattern = 0; pattern < 64; pattern++) {
            List<Boolean> leftFlags = createLeftFlags(pattern);
            long mask = 0L;
            for (int index = 0; index < leftFlags.size(); index++) {
                if (!leftFlags.get(index))
                    mask |= 1L << (index * 10);
            }

            SmallField field = FieldFactory.createSmallField();
            Random random = new Random();
            for (int index = 0; index < leftFlags.size(); index++) {
                for (int x = 0; x < FIELD_WIDTH; x++)
                    field.setBlock(x, index);

                if (leftFlags.get(index))
                    field.removeBlock(random.nextInt(10), index);
            }

            long board = field.getBoard(0);
            long deleted = LongBoardMap.deleteLine(board, mask);

            long inserted = LongBoardMap.insertBlackLine(deleted, mask);

            assertThat(inserted).isEqualTo(board);
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
