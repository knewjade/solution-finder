package newfield;

import core.mino.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class EstimateFactory {
    static List<List<EstimateMino>> createSZO(int countS, int countZ, int countO) {
        List<EstimateMino> minos = new ArrayList<>();

        // S
        for (int count = 0; count < countS; count++)
            minos.add(new EstimateMino(Block.S, RotateLimit.NoLimit, Delta.Flat));

        // Z
        for (int count = 0; count < countZ; count++)
            minos.add(new EstimateMino(Block.Z, RotateLimit.NoLimit, Delta.Flat));

        // O
        for (int count = 0; count < countO; count++)
            minos.add(new EstimateMino(Block.O, RotateLimit.NoLimit, Delta.Flat));

        return Collections.singletonList(minos);
    }

    static List<List<EstimateMino>> createLJ(int countL, int countJ, int oddCountLJ) {
        int countLJ = countL + countJ;
        assert oddCountLJ <= countLJ;
        assert (countLJ - oddCountLJ) % 2 == 0;
        if (countLJ == oddCountLJ) {
            return createLJWithOdd(countL, countJ);
        } else {
            return createLJWithOddAndEven(countL, countJ, oddCountLJ);
        }
    }

    private static List<List<EstimateMino>> createLJWithOdd(int countL, int countJ) {
        // すべて偶数列を3にするLJ
        List<EstimateMino> minos = new ArrayList<>();

        for (int count = 0; count < countL; count++)
            minos.add(new EstimateMino(Block.L, RotateLimit.NoLimit, Delta.OddUp));

        for (int count = 0; count < countJ; count++)
            minos.add(new EstimateMino(Block.J, RotateLimit.NoLimit, Delta.OddUp));

        return Collections.singletonList(minos);
    }

    private static List<List<EstimateMino>> createLJWithOddAndEven(int countL, int countJ, int oddCountLJ) {
        List<List<EstimateMino>> list = new ArrayList<>();

        // 偶数列を3にするLJの各個数を決定
        for (int oddCountL = 0; oddCountL <= oddCountLJ; oddCountL++) {
            // Lを使いすぎているときは次へ
            if (countL < oddCountL)
                continue;

            // 偶数列を3にするJの数を決定
            int oddCountJ = oddCountLJ - oddCountL;
            // Jを使いすぎているときは次へ
            if (countJ < oddCountJ)
                continue;

            // 偶数列のリストを作る
            List<List<EstimateMino>> oddList = createLJWithOdd(oddCountL, oddCountJ);
            List<List<EstimateMino>> evenList = createLJWithEven(countL - oddCountL, countJ - oddCountJ);

            List<List<EstimateMino>> cross = crossConcatList(oddList, evenList);
            list.addAll(cross);
        }

        return list;
    }

    private static List<List<EstimateMino>> createLJWithEven(int countL, int countJ) {
        int countLJ = countL + countJ;
        assert countLJ % 2 == 0;
        int eachCountLJ = countLJ / 2;

        if (countL < countJ) {
            List<List<EstimateMino>> list = new ArrayList<>();
            for (int evenCountL = 0; evenCountL <= countL; evenCountL++) {
                int oddCountL = countL - evenCountL;
                int evenCountJ = eachCountLJ - evenCountL;
                int oddCountJ = countJ - evenCountJ;
                List<EstimateMino> minos = createLJWithEven(evenCountL, oddCountL, evenCountJ, oddCountJ);
                list.add(minos);
            }
            return list;
        } else {
            List<List<EstimateMino>> list = new ArrayList<>();
            for (int evenCountJ = 0; evenCountJ <= countJ; evenCountJ++) {
                int oddCountJ = countJ - evenCountJ;
                int evenCountL = eachCountLJ - evenCountJ;
                int oddCountL = countL - evenCountL;
                List<EstimateMino> minos = createLJWithEven(evenCountL, oddCountL, evenCountJ, oddCountJ);
                list.add(minos);
            }
            return list;
        }
    }

    private static List<EstimateMino> createLJWithEven(int evenCountL, int oddCountL, int evenCountJ, int oddCountJ) {
        List<EstimateMino> minos = new ArrayList<>();

        for (int count = 0; count < evenCountL; count++)
            minos.add(new EstimateMino(Block.L, RotateLimit.NoLimit, Delta.EvenUp));

        for (int count = 0; count < oddCountL; count++)
            minos.add(new EstimateMino(Block.L, RotateLimit.NoLimit, Delta.OddUp));

        for (int count = 0; count < evenCountJ; count++)
            minos.add(new EstimateMino(Block.J, RotateLimit.NoLimit, Delta.EvenUp));

        for (int count = 0; count < oddCountJ; count++)
            minos.add(new EstimateMino(Block.J, RotateLimit.NoLimit, Delta.OddUp));

        return minos;
    }

    static List<List<EstimateMino>> crossConcatList(List<List<EstimateMino>> oddList, List<List<EstimateMino>> evenList) {
        List<List<EstimateMino>> list = new ArrayList<>();
        for (List<EstimateMino> odd : oddList) {
            for (List<EstimateMino> even : evenList) {
                List<EstimateMino> minos = new ArrayList<>(odd);
                minos.addAll(even);
                list.add(minos);
            }
        }
        return list;
    }

    static List<List<EstimateMino>> createT(int countT, int oddCountT) {
        if (0 <= oddCountT)
            return createTWithOddUp(countT, oddCountT);
        else
            return createTWithEvenUp(countT, -oddCountT);
    }

    private static List<List<EstimateMino>> createTWithOddUp(int countT, int oddCountT) {
        assert 0 <= oddCountT && oddCountT <= countT;

        // odd T
        List<EstimateMino> withOddUp = new ArrayList<>();
        for (int count = 0; count < oddCountT; count++)
            withOddUp.add(new EstimateMino(Block.T, RotateLimit.LeftOrRight, Delta.OddUp));

        // flat
        List<List<EstimateMino>> withFlat = new ArrayList<>();
        int leastCountT = countT - oddCountT;
        for (int sideCountT = 0; sideCountT <= leastCountT; sideCountT += 2) {
            List<EstimateMino> minos = createTWithFlat(leastCountT, sideCountT);
            withFlat.add(minos);
        }

        return crossConcatList(Collections.singletonList(withOddUp), withFlat);
    }

    private static List<List<EstimateMino>> createTWithEvenUp(int countT, int evenCountT) {
        assert 0 <= evenCountT && evenCountT <= countT;

        // even T
        List<EstimateMino> withEvenUp = new ArrayList<>();
        for (int count = 0; count < evenCountT; count++)
            withEvenUp.add(new EstimateMino(Block.T, RotateLimit.LeftOrRight, Delta.EvenUp));

        // flat
        List<List<EstimateMino>> withFlat = new ArrayList<>();
        int leastCountT = countT - evenCountT;
        for (int sideCountT = 0; sideCountT <= leastCountT; sideCountT += 2) {
            List<EstimateMino> minos = createTWithFlat(leastCountT, sideCountT);
            withFlat.add(minos);
        }

        return crossConcatList(Collections.singletonList(withEvenUp), withFlat);
    }

    private static List<EstimateMino> createTWithFlat(int countT, int sideCountT) {
        assert sideCountT % 2 == 0;

        List<EstimateMino> minos = new ArrayList<>();

        for (int count = 0; count < sideCountT / 2; count++) {
            minos.add(new EstimateMino(Block.T, RotateLimit.LeftOrRight, Delta.OddUp));
            minos.add(new EstimateMino(Block.T, RotateLimit.LeftOrRight, Delta.EvenUp));
        }

        for (int count = 0; count < countT - sideCountT; count++)
            minos.add(new EstimateMino(Block.T, RotateLimit.SpawnOrReverse, Delta.Flat));

        return minos;
    }

    static List<List<EstimateMino>> createI(int countI, int oddCountI) {
        if (0 <= oddCountI)
            return createIWithOddUp(countI, oddCountI);
        else
            return createIWithEvenUp(countI, -oddCountI);
    }

    private static List<List<EstimateMino>> createIWithOddUp(int countI, int oddCountI) {
        assert 0 <= oddCountI && oddCountI <= countI;

        // odd I
        List<EstimateMino> withOddUp = new ArrayList<>();
        for (int count = 0; count < countI; count++)
            withOddUp.add(new EstimateMino(Block.I, RotateLimit.LeftOrRight, Delta.OddUp));

        // flat
        List<List<EstimateMino>> withFlat = new ArrayList<>();
        int leastCountI = countI - oddCountI;
        for (int sideCountI = 0; sideCountI <= leastCountI; sideCountI += 2) {
            List<EstimateMino> minos = createIWithFlat(leastCountI, sideCountI);
            withFlat.add(minos);
        }

        return crossConcatList(Collections.singletonList(withOddUp), withFlat);
    }

    private static List<List<EstimateMino>> createIWithEvenUp(int countI, int evenCountI) {
        assert 0 <= evenCountI && evenCountI <= countI;

        // even I
        List<EstimateMino> withEvenUp = new ArrayList<>();
        for (int count = 0; count < countI; count++)
            withEvenUp.add(new EstimateMino(Block.I, RotateLimit.LeftOrRight, Delta.EvenUp));

        // flat
        List<List<EstimateMino>> withFlat = new ArrayList<>();
        int leastCountI = countI - evenCountI;
        for (int sideCountI = 0; sideCountI <= leastCountI; sideCountI += 2) {
            List<EstimateMino> minos = createIWithFlat(leastCountI, sideCountI);
            withFlat.add(minos);
        }

        return crossConcatList(Collections.singletonList(withEvenUp), withFlat);
    }

    private static List<EstimateMino> createIWithFlat(int countI, int sideCountI) {
        assert sideCountI % 2 == 0;

        List<EstimateMino> minos = new ArrayList<>();

        for (int count = 0; count < sideCountI / 2; count++) {
            minos.add(new EstimateMino(Block.I, RotateLimit.LeftOrRight, Delta.OddUp));
            minos.add(new EstimateMino(Block.I, RotateLimit.LeftOrRight, Delta.EvenUp));
        }

        for (int count = 0; count < countI - sideCountI; count++)
            minos.add(new EstimateMino(Block.I, RotateLimit.SpawnOrReverse, Delta.Flat));

        return minos;
    }
}
