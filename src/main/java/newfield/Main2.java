package newfield;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main2 {
    public static void main(String[] args) {
        List<Block> usedBlocks = Arrays.asList(Block.T, Block.I, Block.S, Block.Z, Block.O, Block.L, Block.J);
        BlockCounter blockCounter = new BlockCounter(usedBlocks);
        System.out.println(blockCounter);

        Field field = FieldFactory.createField("" +
                "XXX_______" +
                "XXX_______" +
                "XXX_______" +
                "XXX_______" +
                ""
        );
        ParityField parityField = new ParityField(field);
        System.out.println(parityField);

        int maxClearLine = 4;
        NewValidate newValidate = new NewValidate(blockCounter, parityField, maxClearLine);
        newValidate.run();
    }

    private static class NewValidate {
        private final BlockCounter blockCounter;
        private final ParityField parityField;
        private final int maxClearLine;

        NewValidate(BlockCounter blockCounter, ParityField parityField, int maxClearLine) {
            this.blockCounter = blockCounter;
            this.parityField = parityField;
            this.maxClearLine = maxClearLine;
        }

        void run() {
            int evenParity = maxClearLine * 5 - parityField.calculateEvenLineParity();  // 奇数列
            int oddParity = maxClearLine * 5 - parityField.calculateOddLineParity();   // 偶数列

            // SZO対応: どの置き方でも 2:2 で減少
            int SZOCount = blockCounter.getCount(Block.S) + blockCounter.getCount(Block.Z) + blockCounter.getCount(Block.O);
            evenParity -= 2 * SZOCount;
            oddParity -= 2 * SZOCount;

            assert 0 <= evenParity && 0 <= oddParity;

            // LJ対応: どの置き方でも 3:1 で減少
            // 最低でも LJCount　減少し、最大で LJCount + LJCount * 2 減少
            int LJCount = blockCounter.getCount(Block.L) + blockCounter.getCount(Block.J);
            for (int count = 0; count <= LJCount; count++) {
                int oddLJ = LJCount + 2 * count;
                int evenLJ = LJCount + 2 * (LJCount - count);
                validateAfterSZOLJ(evenParity - oddLJ, oddParity - evenLJ, count);
            }
        }

        private void validateAfterSZOLJ(int evenParity, int oddParity, int oddCountLJ) {
            // Tは必ず1以上減少するため、0の場合は終了
            if (oddParity <= 0 || evenParity <= 0)
                return;

            // T対応: 横3:1,1:3 と 縦:2:2 の3種
            int TCount = blockCounter.getCount(Block.T);
            for (int count = -TCount; count <= TCount; count++) {
                int oddT = 2 * TCount + count;
                int evenT = 2 * TCount - count;
                validateAfterSZOLJT(evenParity - oddT, oddParity - evenT, oddCountLJ, count);
            }
        }

        private void validateAfterSZOLJT(int evenParity, int oddParity, int oddCountLJ, int oddCountT) {
            // Iは2 or 4ずつ減少するので、奇数の場合は終了
            // 縦置きの場合は減少しないため、0の場合は継続
            if (oddParity < 0 || evenParity < 0 || oddParity % 2 != 0 || evenParity % 2 != 0)
                return;

            // I対応: 横2:2 と 縦:4:0,0:4 の3種
            int ICount = blockCounter.getCount(Block.I);
            for (int count = -ICount; count <= ICount; count++) {
                int oddI = 2 * (ICount + count);
                int evenI = 2 * (ICount - count);

                // パリティが正しいときは記録する
                if (evenParity - oddI == 0 && oddParity - evenI == 0)
                    validateAfterAll(oddCountLJ, oddCountT, count);
            }
        }

        private void validateAfterAll(int oddCountLJ, int oddCountT, int oddCountI) {
            // 偶奇数列の差を相殺する置き方に注意
            // oddCountLJ: 偶数列を3にするLJの置き方の総数
            // oddCountT : 偶数列を3にするTの置き方の総数
            // oddCountI : 偶数列を4にするIの置き方の総数
            EstimateBuilder estimateBuilder = new EstimateBuilder(blockCounter);
            estimateBuilder.run(oddCountLJ, oddCountT, oddCountI);

            for (List<EstimateMino> estimateMinos : estimateBuilder.estimateMinos) {
                for (EstimateMino estimateMino : estimateMinos)
                    System.out.println(estimateMino);
                System.out.println("---");
            }
        }
    }

    private static class EstimateBuilder {
        private ArrayList<List<EstimateMino>> estimateMinos = new ArrayList<>();
        private ArrayList<List<EstimateMino>> nextEstimateMinos = new ArrayList<>();

        private final BlockCounter blockCounter;

        EstimateBuilder(BlockCounter blockCounter) {
            this.blockCounter = blockCounter;
        }

        public void run(int oddCountLJ, int oddCountT, int oddCountI) {
            addSZO();
            addLJAndSZO(oddCountLJ);
            estimateMinos = nextEstimateMinos;
            addLJSZOAndT(oddCountT);
            estimateMinos = nextEstimateMinos;
        }

        private void addSZO() {
            ArrayList<EstimateMino> listSZO = new ArrayList<>();

            // S
            for (int count = 0; count < blockCounter.getCount(Block.S); count++)
                listSZO.add(new EstimateMino(Block.S, RotateLimit.NoLimit, Delta.Flat));

            // Z
            for (int count = 0; count < blockCounter.getCount(Block.Z); count++)
                listSZO.add(new EstimateMino(Block.Z, RotateLimit.NoLimit, Delta.Flat));

            // O
            for (int count = 0; count < blockCounter.getCount(Block.O); count++)
                listSZO.add(new EstimateMino(Block.O, RotateLimit.NoLimit, Delta.Flat));

            estimateMinos.add(listSZO);
        }

        private void addLJAndSZO(int oddCountLJ) {
            int countL = blockCounter.getCount(Block.L);
            int countJ = blockCounter.getCount(Block.J);
            int countLJ = countL + countJ;

            if (countLJ == oddCountLJ) {
                // すべて偶数列を3にするLJ
                for (int oddCountL = 0; oddCountL <= countL; oddCountL++) {
                    // 偶数列を3にするJの数を決定
                    int oddCountJ = oddCountLJ - oddCountL;
                    // Jを使いすぎているときは次へ
                    if (countJ < oddCountJ)
                        continue;

                    ArrayList<EstimateMino> ljList = createLJ(oddCountL, oddCountJ, 0, 0);
                    concat(ljList);
                }
            } else {
                // 偶数列を3にするLの数を決定
                for (int oddCountL = 0; oddCountL <= oddCountLJ; oddCountL++) {
                    // Lを使いすぎているときは次へ
                    if (countL < oddCountL)
                        continue;

                    // 偶数列を3にするJの数を決定
                    int oddCountJ = oddCountLJ - oddCountL;
                    // Jを使いすぎているときは次へ
                    if (countJ < oddCountJ)
                        continue;

                    // 奇数列を3にするLJの数を決定
                    int evenCountLJ = countLJ - oddCountLJ;
                    assert 0 < evenCountLJ;

                    int leastL = countL - oddCountL;
                    int leastJ = countJ - oddCountJ;
                    if (leastL < leastJ) {
                        // Lから埋めていく
                        for (int evenCountL = 0; evenCountL <= leastL; evenCountL++) {
                            int evenCountJ = leastJ - evenCountL;
                            ArrayList<EstimateMino> ljList = createLJ(oddCountL, oddCountJ, evenCountL, evenCountJ);
                            concat(ljList);
                        }
                    } else {
                        // Jから埋めていく
                        for (int evenCountJ = 0; evenCountJ < leastJ; evenCountJ++) {
                            int evenCountL = leastL - evenCountJ;
                            ArrayList<EstimateMino> ljList = createLJ(oddCountL, oddCountJ, evenCountL, evenCountJ);
                            concat(ljList);
                        }
                    }
                }
            }
        }

        private ArrayList<EstimateMino> createLJ(int oddCountL, int oddCountJ, int evenCountL, int evenCountJ) {
            ArrayList<EstimateMino> minos = new ArrayList<>();
            for (int count = 0; count < oddCountL; count++)
                minos.add(new EstimateMino(Block.L, RotateLimit.NoLimit, Delta.OddUp));
            for (int count = 0; count < oddCountJ; count++)
                minos.add(new EstimateMino(Block.J, RotateLimit.NoLimit, Delta.OddUp));
            for (int count = 0; count < evenCountL; count++)
                minos.add(new EstimateMino(Block.L, RotateLimit.NoLimit, Delta.EvenUp));
            for (int count = 0; count < evenCountJ; count++)
                minos.add(new EstimateMino(Block.J, RotateLimit.NoLimit, Delta.EvenUp));
            return minos;
        }

        private void concat(ArrayList<EstimateMino> values) {
            for (List<EstimateMino> estimateMino : estimateMinos) {
                ArrayList<EstimateMino> newList = new ArrayList<>(estimateMino);
                newList.addAll(values);
                nextEstimateMinos.add(newList);
            }
        }

        private ArrayList<EstimateMino> addLJSZOAndT(int oddCountT) {
            int countT = blockCounter.getCount(Block.T);
            
        }
    }

    private static class EstimateMino {
        private final Block block;
        private final RotateLimit rotateLimit;
        private final Delta delta;

        private EstimateMino(Block block, RotateLimit rotateLimit, Delta delta) {
            this.block = block;
            this.rotateLimit = rotateLimit;
            this.delta = delta;
        }

        @Override
        public String toString() {
            return "EstimateMino{" +
                    "block=" + block +
                    ", rotateLimit=" + rotateLimit +
                    ", delta=" + delta +
                    '}';
        }
    }

    private enum RotateLimit {
        NoLimit,
        LeftOrRight,
        SpawnOrReverse,;
    }

    private enum Delta {
        Flat,
        OddUp,
        EvenUp,;
    }
}
