package lib;

import core.field.Field;
import core.mino.Block;
import core.srs.Rotate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class RandomsTest {
    @Test
    void nextInt() throws Exception {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            int next = randoms.nextInt(3, 19);
            assertThat(next)
                    .isGreaterThanOrEqualTo(3)
                    .isLessThan(19);
        }
    }

    @Test
    void nextIntClosed() throws Exception {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            int next = randoms.nextIntClosed(3, 19);
            assertThat(next)
                    .isGreaterThanOrEqualTo(3)
                    .isLessThanOrEqualTo(19);
        }
    }

    @Test
    void nextDouble() {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            double next = randoms.nextDouble();
            assertThat(next)
                    .isGreaterThanOrEqualTo(0.0)
                    .isLessThan(1.0);
        }
    }

    @Test
    void nextBoolean() {
        Randoms randoms = new Randoms();
        int trueCount = 0;
        int maxCount = 2000000;
        for (int count = 0; count < maxCount; count++) {
            if (randoms.nextBoolean())
                trueCount += 1;
        }
        assertThat((double) trueCount / maxCount).isCloseTo(0.5, offset(0.001));
    }

    @Test
    void nextBooleanWithPercent() {
        Randoms randoms = new Randoms();
        double percent = randoms.nextDouble();
        int trueCount = 0;
        int maxCount = 2000000;
        for (int count = 0; count < maxCount; count++) {
            if (randoms.nextBoolean(percent))
                trueCount += 1;
        }
        assertThat((double) trueCount / maxCount).isCloseTo(percent, offset(0.001));
    }

    @Test
    void block() throws Exception {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            Block block = randoms.block();
            assertThat(block)
                    .isIn(Block.valueList());
        }
    }

    @Test
    void blocks() throws Exception {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(3, 19);
            List<Block> blocks = randoms.blocks(size);
            assertThat(blocks)
                    .hasSize(size)
                    .doesNotContainNull();
        }
    }

    @Test
    void rotate() throws Exception {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            Rotate rotate = randoms.rotate();
            assertThat(rotate)
                    .isIn(Rotate.valueList());
        }
    }

    @Test
    void pick() throws Exception {
        Randoms randoms = new Randoms();
        List<Integer> bag = IntStream.range(1, 100).boxed().collect(Collectors.toList());
        for (int count = 0; count < 10000; count++) {
            Integer choose = randoms.pick(bag);
            assertThat(choose).isIn(bag);
        }
    }

    @Test
    void sample() throws Exception {
        Randoms randoms = new Randoms();
        List<Integer> bag = IntStream.range(1, 100).boxed().collect(Collectors.toList());
        for (int count = 0; count < 10000; count++) {
            int size = randoms.nextInt(1, 15);
            List<Integer> sample = randoms.sample(bag, size);
            assertThat(sample).hasSize(size);
            assertThat(bag).containsAll(sample);
            assertThat(new HashSet<>(sample)).hasSize(size);
        }
    }

    @Test
    void field() throws Exception {
        Randoms randoms = new Randoms();
        for (int count = 0; count < 10000; count++) {
            int height = randoms.nextIntClosed(1, 12);
            int numOfMinos = randoms.nextInt(1, height * 10 / 4);
            Field randomField = randoms.field(height, numOfMinos);
            assertThat(randomField)
                    .matches(field -> field.getNumOfAllBlocks() == 10 * height - numOfMinos * 4);
        }
    }

    @Nested
    class Blocks11 {
        boolean isUnique(List<Block> list) {
            return new HashSet<>(list).size() == list.size();
        }

        @Test
        void first() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block11InCycle(0);
                assertThat(blocks).hasSize(11);
                assertThat(isUnique(blocks.subList(0, 7))).isTrue();
                assertThat(isUnique(blocks.subList(7, 11))).isTrue();
            }
        }

        @Test
        void second() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block11InCycle(1);
                assertThat(blocks).hasSize(11);
                assertThat(isUnique(blocks.subList(0, 1))).isTrue();
                assertThat(isUnique(blocks.subList(1, 4))).isTrue();
                assertThat(isUnique(blocks.subList(4, 11))).isTrue();
            }
        }

        @Test
        void third() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block11InCycle(2);
                assertThat(blocks).hasSize(11);
                assertThat(isUnique(blocks.subList(0, 1))).isTrue();
                assertThat(isUnique(blocks.subList(1, 8))).isTrue();
                assertThat(isUnique(blocks.subList(8, 11))).isTrue();
            }
        }

        @Test
        void forth() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block11InCycle(3);
                assertThat(blocks).hasSize(11);
                assertThat(isUnique(blocks.subList(0, 1))).isTrue();
                assertThat(isUnique(blocks.subList(1, 5))).isTrue();
                assertThat(isUnique(blocks.subList(5, 11))).isTrue();
            }
        }

        @Test
        void fifth() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block11InCycle(4);
                assertThat(blocks).hasSize(11);
                assertThat(isUnique(blocks.subList(0, 1))).isTrue();
                assertThat(isUnique(blocks.subList(1, 2))).isTrue();
                assertThat(isUnique(blocks.subList(2, 9))).isTrue();
                assertThat(isUnique(blocks.subList(9, 11))).isTrue();
            }
        }

        @Test
        void sixth() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block11InCycle(5);
                assertThat(blocks).hasSize(11);
                assertThat(isUnique(blocks.subList(0, 1))).isTrue();
                assertThat(isUnique(blocks.subList(1, 6))).isTrue();
                assertThat(isUnique(blocks.subList(6, 11))).isTrue();
            }
        }

        @Test
        void seventh() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block11InCycle(6);
                assertThat(blocks).hasSize(11);
                assertThat(isUnique(blocks.subList(0, 1))).isTrue();
                assertThat(isUnique(blocks.subList(1, 3))).isTrue();
                assertThat(isUnique(blocks.subList(3, 10))).isTrue();
                assertThat(isUnique(blocks.subList(10, 11))).isTrue();
            }
        }

        @Test
        void eighth() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block11InCycle(7);
                assertThat(blocks).hasSize(11);
                assertThat(isUnique(blocks.subList(0, 1))).isTrue();
                assertThat(isUnique(blocks.subList(1, 7))).isTrue();
                assertThat(isUnique(blocks.subList(7, 11))).isTrue();
            }
        }
    }

    @Nested
    class Blocks10 {
        boolean isUnique(List<Block> list) {
            return new HashSet<>(list).size() == list.size();
        }

        @Test
        void first() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block10InCycle(0);
                assertThat(blocks).hasSize(10);
                assertThat(isUnique(blocks.subList(0, 7))).isTrue();
                assertThat(isUnique(blocks.subList(7, 10))).isTrue();
            }
        }

        @Test
        void second() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block10InCycle(1);
                assertThat(blocks).hasSize(10);
                assertThat(isUnique(blocks.subList(0, 4))).isTrue();
                assertThat(isUnique(blocks.subList(4, 10))).isTrue();
            }
        }

        @Test
        void third() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block10InCycle(2);
                assertThat(blocks).hasSize(10);
                assertThat(isUnique(blocks.subList(0, 1))).isTrue();
                assertThat(isUnique(blocks.subList(1, 8))).isTrue();
                assertThat(isUnique(blocks.subList(8, 10))).isTrue();
            }
        }

        @Test
        void forth() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block10InCycle(3);
                assertThat(blocks).hasSize(10);
                assertThat(isUnique(blocks.subList(0, 5))).isTrue();
                assertThat(isUnique(blocks.subList(5, 10))).isTrue();
            }
        }

        @Test
        void fifth() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block10InCycle(4);
                assertThat(blocks).hasSize(10);
                assertThat(isUnique(blocks.subList(0, 2))).isTrue();
                assertThat(isUnique(blocks.subList(2, 9))).isTrue();
                assertThat(isUnique(blocks.subList(9, 10))).isTrue();
            }
        }

        @Test
        void sixth() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block10InCycle(5);
                assertThat(blocks).hasSize(10);
                assertThat(isUnique(blocks.subList(0, 6))).isTrue();
                assertThat(isUnique(blocks.subList(6, 10))).isTrue();
            }
        }

        @Test
        void seventh() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block10InCycle(6);
                assertThat(blocks).hasSize(10);
                assertThat(isUnique(blocks.subList(0, 3))).isTrue();
                assertThat(isUnique(blocks.subList(3, 10))).isTrue();
            }
        }

        @Test
        void eighth() {
            Randoms randoms = new Randoms();
            for (int count = 0; count < 100000; count++) {
                List<Block> blocks = randoms.block10InCycle(7);
                assertThat(blocks).hasSize(10);
                assertThat(isUnique(blocks.subList(0, 7))).isTrue();
                assertThat(isUnique(blocks.subList(7, 10))).isTrue();
            }
        }
    }
}