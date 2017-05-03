package _experimental.newfield.step1;

import _experimental.newfield.BlockCounter;

import java.util.List;

import static core.mino.Block.*;
import static _experimental.newfield.ListHelper.crossConcatList;

// 偶奇数列の変化量から、各ブロックの方向と接着座標の制限をすべて列挙
public class EstimateBuilder {
    private final BlockCounter blockCounter;
    private final int oddCountLJ;
    private final int oddCountT;
    private final int oddCountI;

    EstimateBuilder(BlockCounter blockCounter, int oddCountLJ, int oddCountT, int oddCountI) {
        this.blockCounter = blockCounter;
        this.oddCountLJ = oddCountLJ;
        this.oddCountT = oddCountT;
        this.oddCountI = oddCountI;
    }

    public List<List<DeltaLimitedMino>> create() {
        List<List<DeltaLimitedMino>> szo = EstimateFactory.createSZO(blockCounter.getCount(S), blockCounter.getCount(Z), blockCounter.getCount(O));
        List<List<DeltaLimitedMino>> lj = EstimateFactory.createLJ(blockCounter.getCount(L), blockCounter.getCount(J), oddCountLJ);
        List<List<DeltaLimitedMino>> t = EstimateFactory.createT(blockCounter.getCount(T), oddCountT);
        List<List<DeltaLimitedMino>> i = EstimateFactory.createI(blockCounter.getCount(I), oddCountI);

        List<List<DeltaLimitedMino>> szolj = crossConcatList(szo, lj);
        List<List<DeltaLimitedMino>> ti = crossConcatList(t, i);

        return crossConcatList(szolj, ti);
    }

    @Override
    public String toString() {
        return "EstimateBuilder{" +
                "blockCounter=" + blockCounter +
                ", oddCountLJ=" + oddCountLJ +
                ", oddCountT=" + oddCountT +
                ", oddCountI=" + oddCountI +
                '}';
    }
}
