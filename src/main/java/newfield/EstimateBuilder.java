package newfield;

import core.mino.Block;

import java.util.List;

import static core.mino.Block.*;
import static newfield.EstimateFactory.*;

class EstimateBuilder {
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

    List<List<EstimateMino>> create() {
        List<List<EstimateMino>> szo = createSZO(blockCounter.getCount(S), blockCounter.getCount(Z), blockCounter.getCount(O));
        List<List<EstimateMino>> lj = createLJ(blockCounter.getCount(L), blockCounter.getCount(J), oddCountLJ);
        List<List<EstimateMino>> t = createT(blockCounter.getCount(T), oddCountT);
        List<List<EstimateMino>> i = createI(blockCounter.getCount(I), oddCountI);

        List<List<EstimateMino>> szolj = crossConcatList(szo, lj);
        List<List<EstimateMino>> ti = crossConcatList(t, i);

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
