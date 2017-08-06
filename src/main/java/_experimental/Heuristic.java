package _experimental;

import core.field.Field;

public class Heuristic {
    public static int c(Field field, int maxClearLine) {
        int linesCleared = 4 - maxClearLine;
        int depth = (linesCleared * 10 + field.getNumOfAllBlocks()) / 4;
        return c(field, maxClearLine, linesCleared, depth);
    }

    public static int c(Field field, int maxClearLine, int linesCleared, int depth) {
        long matrix = field.getBoard(0);
        return rowTransitions(matrix, maxClearLine)
                + (columnTransitions(matrix, maxClearLine) >> 2)
                - (depth >> 1)
                - (linesCleared << 1);
    }

    private static int columnTransitions(long matrix, int maxClearLine) {
        int columnTransitions = 0;
        int y = 10 * 0;
        int prevRow = (int) (matrix >> y & 0b1111111111);
        y += 10;
        while (y < 10 * maxClearLine) {
            int row = (int) (matrix >> y & 0b1111111111);
            int transitions = row ^ prevRow;
            prevRow = row;
            int x = 0;
            while (x < 10) {
                if ((transitions >> x & 1) == 1)
                    columnTransitions += 1;
                x += 1;
            }
            y += 10;
        }
        return columnTransitions;
    }

    static long COLUMN = 0x40100401L;

    private static int rowTransitions(long matrix, int maxClearLine) {
        long totalDifferences = 0;
        int smoothness = 0;
        long prev = matrix & COLUMN;
        int x = 1;
        while (x < 10) {
            long cur = matrix >> x & COLUMN;
            totalDifferences += cur ^ prev;
            prev = cur;
            x += 1;
        }
        int yStart = 10 * 0;
        int y = yStart;
        while (y < 10 * maxClearLine) {
            smoothness += (int) (totalDifferences >> y & 15);
            y += 10;
        }
        return smoothness;
    }
}
