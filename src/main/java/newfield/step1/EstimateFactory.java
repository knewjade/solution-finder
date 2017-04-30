package newfield.step1;

import core.mino.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static newfield.ListHelper.crossConcatList;

// 偶奇数列の変化量から、各ブロックの方向と接着座標の制限を計算
class EstimateFactory {
    static List<List<DeltaLimitedMino>> createSZO(int countS, int countZ, int countO) {
        List<DeltaLimitedMino> minos = new ArrayList<>();

        // S
        for (int count = 0; count < countS; count++)
            minos.add(DeltaLimitedMino.create(Block.S, DeltaLimit.Flat));

        // Z
        for (int count = 0; count < countZ; count++)
            minos.add(DeltaLimitedMino.create(Block.Z, DeltaLimit.Flat));

        // O
        for (int count = 0; count < countO; count++)
            minos.add(DeltaLimitedMino.create(Block.O, DeltaLimit.Flat));

        return Collections.singletonList(minos);
    }

    static List<List<DeltaLimitedMino>> createLJ(int countL, int countJ, int oddCountLJ) {
        int countLJ = countL + countJ;
        assert -countLJ <= oddCountLJ && oddCountLJ <= countLJ;
        assert (countLJ - (0 <= oddCountLJ ? oddCountLJ : -oddCountLJ)) % 2 == 0;
        if (countLJ == oddCountLJ) {
            return createLJWithAll(countL, countJ, DeltaLimit.OddUp);
        } else if (countLJ == -oddCountLJ) {
            return createLJWithAll(countL, countJ, DeltaLimit.EvenUp);
        } else if (0 <= oddCountLJ) {
            return createLJWithMixed(countL, countJ, oddCountLJ, DeltaLimit.OddUp);
        } else {
            return createLJWithMixed(countL, countJ, -oddCountLJ, DeltaLimit.EvenUp);
        }
    }

    private static List<List<DeltaLimitedMino>> createLJWithAll(int countL, int countJ, DeltaLimit deltaLimit) {
        // すべて偶数列 or すべ　奇数列を3にするLJ
        List<DeltaLimitedMino> minos = new ArrayList<>();

        for (int count = 0; count < countL; count++)
            minos.add(DeltaLimitedMino.create(Block.L, deltaLimit));

        for (int count = 0; count < countJ; count++)
            minos.add(DeltaLimitedMino.create(Block.J, deltaLimit));

        return Collections.singletonList(minos);
    }

    private static List<List<DeltaLimitedMino>> createLJWithMixed(int countL, int countJ, int threeCountLJ, DeltaLimit deltaLimit) {
        assert 0 <= threeCountLJ;

        List<List<DeltaLimitedMino>> list = new ArrayList<>();

        // 3にするLJの各個数を決定
        for (int threeCountL = 0; threeCountL <= threeCountLJ; threeCountL++) {
            // Lを使いすぎているときは次へ
            if (countL < threeCountL)
                continue;

            // 偶数列を3にするJの数を決定
            int threeCountJ = threeCountLJ - threeCountL;
            // Jを使いすぎているときは次へ
            if (countJ < threeCountJ)
                continue;

            // 偶数列のリストを作る
            List<List<DeltaLimitedMino>> threeList = createLJWithAll(threeCountL, threeCountJ, deltaLimit);
            List<List<DeltaLimitedMino>> flatList = createLJWithFlat(countL - threeCountL, countJ - threeCountJ);

            List<List<DeltaLimitedMino>> cross = crossConcatList(threeList, flatList);
            list.addAll(cross);
        }

        return list;
    }

    private static List<List<DeltaLimitedMino>> createLJWithFlat(int countL, int countJ) {
        int countLJ = countL + countJ;
        assert countLJ % 2 == 0;
        int eachCountLJ = countLJ / 2;

        if (countL < countJ) {
            List<List<DeltaLimitedMino>> list = new ArrayList<>();
            for (int evenCountL = 0; evenCountL <= countL; evenCountL++) {
                int oddCountL = countL - evenCountL;
                int evenCountJ = eachCountLJ - evenCountL;
                int oddCountJ = countJ - evenCountJ;
                List<DeltaLimitedMino> minos = createLJWithFlat(evenCountL, oddCountL, evenCountJ, oddCountJ);
                list.add(minos);
            }
            return list;
        } else {
            List<List<DeltaLimitedMino>> list = new ArrayList<>();
            for (int evenCountJ = 0; evenCountJ <= countJ; evenCountJ++) {
                int oddCountJ = countJ - evenCountJ;
                int evenCountL = eachCountLJ - evenCountJ;
                int oddCountL = countL - evenCountL;
                List<DeltaLimitedMino> minos = createLJWithFlat(evenCountL, oddCountL, evenCountJ, oddCountJ);
                list.add(minos);
            }
            return list;
        }
    }

    private static List<DeltaLimitedMino> createLJWithFlat(int evenCountL, int oddCountL, int evenCountJ, int oddCountJ) {
        assert 0 <= evenCountL && 0 <= oddCountL;
        assert 0 <= evenCountJ && 0 <= oddCountJ;
        assert evenCountL + evenCountJ == oddCountL + oddCountJ;

        List<DeltaLimitedMino> minos = new ArrayList<>();

        for (int count = 0; count < evenCountL; count++)
            minos.add(DeltaLimitedMino.create(Block.L, DeltaLimit.EvenUp));

        for (int count = 0; count < oddCountL; count++)
            minos.add(DeltaLimitedMino.create(Block.L, DeltaLimit.OddUp));

        for (int count = 0; count < evenCountJ; count++)
            minos.add(DeltaLimitedMino.create(Block.J, DeltaLimit.EvenUp));

        for (int count = 0; count < oddCountJ; count++)
            minos.add(DeltaLimitedMino.create(Block.J, DeltaLimit.OddUp));

        return minos;
    }

    static List<List<DeltaLimitedMino>> createT(int countT, int oddCountT) {
        if (0 <= oddCountT)
            return createTWithOddUp(countT, oddCountT);
        else
            return createTWithEvenUp(countT, -oddCountT);
    }

    private static List<List<DeltaLimitedMino>> createTWithOddUp(int countT, int oddCountT) {
        assert 0 <= oddCountT && oddCountT <= countT;

        // odd T
        List<DeltaLimitedMino> withOddUp = new ArrayList<>();
        for (int count = 0; count < oddCountT; count++)
            withOddUp.add(DeltaLimitedMino.create(Block.T, DeltaLimit.OddUp));

        // flat
        List<List<DeltaLimitedMino>> withFlat = new ArrayList<>();
        int leastCountT = countT - oddCountT;
        for (int sideCountT = 0; sideCountT <= leastCountT; sideCountT += 2) {
            List<DeltaLimitedMino> minos = createTWithFlat(leastCountT, sideCountT);
            withFlat.add(minos);
        }

        return crossConcatList(Collections.singletonList(withOddUp), withFlat);
    }

    private static List<List<DeltaLimitedMino>> createTWithEvenUp(int countT, int evenCountT) {
        assert 0 <= evenCountT && evenCountT <= countT;

        // even T
        List<DeltaLimitedMino> withEvenUp = new ArrayList<>();
        for (int count = 0; count < evenCountT; count++)
            withEvenUp.add(DeltaLimitedMino.create(Block.T, DeltaLimit.EvenUp));

        // flat
        List<List<DeltaLimitedMino>> withFlat = new ArrayList<>();
        int leastCountT = countT - evenCountT;
        for (int sideCountT = 0; sideCountT <= leastCountT; sideCountT += 2) {
            List<DeltaLimitedMino> minos = createTWithFlat(leastCountT, sideCountT);
            withFlat.add(minos);
        }

        return crossConcatList(Collections.singletonList(withEvenUp), withFlat);
    }

    private static List<DeltaLimitedMino> createTWithFlat(int countT, int sideCountT) {
        assert sideCountT % 2 == 0;

        List<DeltaLimitedMino> minos = new ArrayList<>();

        for (int count = 0; count < sideCountT / 2; count++) {
            minos.add(DeltaLimitedMino.create(Block.T, DeltaLimit.OddUp));
            minos.add(DeltaLimitedMino.create(Block.T, DeltaLimit.EvenUp));
        }

        for (int count = 0; count < countT - sideCountT; count++)
            minos.add(DeltaLimitedMino.create(Block.T, DeltaLimit.Flat));

        return minos;
    }

    static List<List<DeltaLimitedMino>> createI(int countI, int oddCountI) {
        if (0 <= oddCountI)
            return createIWithOddUp(countI, oddCountI);
        else
            return createIWithEvenUp(countI, -oddCountI);
    }

    private static List<List<DeltaLimitedMino>> createIWithOddUp(int countI, int oddCountI) {
        assert 0 <= oddCountI && oddCountI <= countI;

        // odd I
        List<DeltaLimitedMino> withOddUp = new ArrayList<>();
        for (int count = 0; count < oddCountI; count++)
            withOddUp.add(DeltaLimitedMino.create(Block.I, DeltaLimit.OddUp));

        // flat
        List<List<DeltaLimitedMino>> withFlat = new ArrayList<>();
        int leastCountI = countI - oddCountI;
        for (int sideCountI = 0; sideCountI <= leastCountI; sideCountI += 2) {
            List<DeltaLimitedMino> minos = createIWithFlat(leastCountI, sideCountI);
            withFlat.add(minos);
        }

        return crossConcatList(Collections.singletonList(withOddUp), withFlat);
    }

    private static List<List<DeltaLimitedMino>> createIWithEvenUp(int countI, int evenCountI) {
        assert 0 <= evenCountI && evenCountI <= countI;

        // even I
        List<DeltaLimitedMino> withEvenUp = new ArrayList<>();
        for (int count = 0; count < evenCountI; count++)
            withEvenUp.add(DeltaLimitedMino.create(Block.I, DeltaLimit.EvenUp));

        // flat
        List<List<DeltaLimitedMino>> withFlat = new ArrayList<>();
        int leastCountI = countI - evenCountI;
        for (int sideCountI = 0; sideCountI <= leastCountI; sideCountI += 2) {
            List<DeltaLimitedMino> minos = createIWithFlat(leastCountI, sideCountI);
            withFlat.add(minos);
        }

        return crossConcatList(Collections.singletonList(withEvenUp), withFlat);
    }

    private static List<DeltaLimitedMino> createIWithFlat(int countI, int sideCountI) {
        assert sideCountI % 2 == 0;

        List<DeltaLimitedMino> minos = new ArrayList<>();

        for (int count = 0; count < sideCountI / 2; count++) {
            minos.add(DeltaLimitedMino.create(Block.I, DeltaLimit.OddUp));
            minos.add(DeltaLimitedMino.create(Block.I, DeltaLimit.EvenUp));
        }

        for (int count = 0; count < countI - sideCountI; count++)
            minos.add(DeltaLimitedMino.create(Block.I, DeltaLimit.Flat));

        return minos;
    }
}
