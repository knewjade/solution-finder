package newfield;

import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
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
        int maxClearLine = 4;
        System.out.println(FieldView.toString(field, maxClearLine));

        ParityField parityField = new ParityField(field);
        System.out.println(parityField);

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
                if ((LJCount - count) % 2 == 0)
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
            // oddCountLJ: 偶数列を3にするLJの置き方の総数 // 必ず正となる
            // oddCountT : 偶数列を3にするTの置き方の総数  // マイナスの場合は、奇数列を3にするTの置き方の総数
            // oddCountI : 偶数列を4にするIの置き方の総数  // マイナスの場合は、奇数列を4にするIの置き方の総数

            EstimateBuilder estimateBuilder = new EstimateBuilder(blockCounter, oddCountLJ, oddCountT, oddCountI);
            List<List<EstimateMino>> lists = estimateBuilder.create();

            System.out.println(estimateBuilder);
            System.out.println(lists.size());
            System.out.println(lists);
        }
    }
}
