package core.field;

import lib.BooleanWalker;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LongBoardMapTest {
    @Test
    void deleteLine() {
        Randoms randoms = new Randoms();
        BooleanWalker.walk(6)
                .forEach(booleans -> {
                    SmallField field = new SmallField();
                    SmallField expect = new SmallField();

                    int expectY = 0;
                    for (int y = 0; y < booleans.size(); y++) {
                        if (booleans.get(y)) {
                            // ラインを全て埋める
                            for (int x = 0; x < 10; x++)
                                field.setBlock(x, y);
                        } else {
                            // ラインを全て埋めない
                            for (int x = 0; x < 10; x++) {
                                if (randoms.nextBoolean(0.8)) {
                                    field.setBlock(x, y);
                                    expect.setBlock(x, expectY);
                                }
                            }

                            int removeX = randoms.nextIntOpen(0, 10);
                            field.removeBlock(removeX, y);
                            expect.removeBlock(removeX, expectY);

                            expectY += 1;
                        }
                    }

                    long board = field.getXBoard();
                    long deleteKey = KeyOperators.getDeleteKey(board);
                    assertThat(LongBoardMap.deleteLine(board, deleteKey)).isEqualTo(expect.getXBoard());
                });
    }

    @Test
    void insertBlackLine() {
        Randoms randoms = new Randoms();
        BooleanWalker.walk(6)
                .forEach(booleans -> {
                    SmallField expect = new SmallField();
                    SmallField field = new SmallField();
                    long deleteKey = 0L;

                    int expectY = 0;
                    for (int y = 0; y < booleans.size(); y++) {
                        if (booleans.get(y)) {
                            // ラインを全て埋める
                            for (int x = 0; x < 10; x++)
                                expect.setBlock(x, y);
                            deleteKey += KeyOperators.getDeleteBitKey(y);
                        } else {
                            // ラインを全て埋めない
                            for (int x = 0; x < 10; x++) {
                                if (randoms.nextBoolean(0.8)) {
                                    expect.setBlock(x, y);
                                    field.setBlock(x, expectY);
                                }
                            }

                            int removeX = randoms.nextIntOpen(0, 10);
                            expect.removeBlock(removeX, y);
                            field.removeBlock(removeX, expectY);

                            expectY += 1;
                        }
                    }

                    long board = field.getXBoard();
                    assertThat(LongBoardMap.insertBlackLine(board, deleteKey)).isEqualTo(expect.getXBoard());
                });
    }

    @Test
    void insertWhiteLine() {
        Randoms randoms = new Randoms();
        BooleanWalker.walk(6)
                .forEach(booleans -> {
                    SmallField expect = new SmallField();
                    SmallField field = new SmallField();
                    long deleteKey = 0L;

                    int expectY = 0;
                    for (int y = 0; y < booleans.size(); y++) {
                        if (booleans.get(y)) {
                            // ラインを空白にする
                            deleteKey += KeyOperators.getDeleteBitKey(y);
                        } else {
                            // ラインを全て埋めない
                            for (int x = 0; x < 10; x++) {
                                if (randoms.nextBoolean(0.8)) {
                                    expect.setBlock(x, y);
                                    field.setBlock(x, expectY);
                                }
                            }

                            int removeX = randoms.nextIntOpen(0, 10);
                            expect.removeBlock(removeX, y);
                            field.removeBlock(removeX, expectY);

                            expectY += 1;
                        }
                    }

                    long board = field.getXBoard();
                    assertThat(LongBoardMap.insertWhiteLine(board, deleteKey)).isEqualTo(expect.getXBoard());
                });
    }
}
