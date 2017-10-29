package _implements.parity_based_pack.step1;

import common.datastore.PieceCounter;
import core.mino.Piece;

import java.util.EnumMap;
import java.util.List;

import static _implements.parity_based_pack.ListHelper.crossConcatList;
import static core.mino.Piece.*;

// 偶奇数列の変化量から、各ブロックの方向と接着座標の制限をすべて列挙
public class EstimateBuilder {
    private final PieceCounter pieceCounter;
    private final int oddCountLJ;
    private final int oddCountT;
    private final int oddCountI;

    EstimateBuilder(PieceCounter pieceCounter, int oddCountLJ, int oddCountT, int oddCountI) {
        this.pieceCounter = pieceCounter;
        this.oddCountLJ = oddCountLJ;
        this.oddCountT = oddCountT;
        this.oddCountI = oddCountI;
    }

    public List<List<DeltaLimitedMino>> create() {
        EnumMap<Piece, Integer> map = pieceCounter.getEnumMap();
        List<List<DeltaLimitedMino>> szo = EstimateFactory.createSZO(map.getOrDefault(S, 0), map.getOrDefault(Z, 0), map.getOrDefault(O, 0));
        List<List<DeltaLimitedMino>> lj = EstimateFactory.createLJ(map.getOrDefault(L, 0), map.getOrDefault(J, 0), oddCountLJ);
        List<List<DeltaLimitedMino>> t = EstimateFactory.createT(map.getOrDefault(T, 0), oddCountT);
        List<List<DeltaLimitedMino>> i = EstimateFactory.createI(map.getOrDefault(I, 0), oddCountI);

        List<List<DeltaLimitedMino>> szolj = crossConcatList(szo, lj);
        List<List<DeltaLimitedMino>> ti = crossConcatList(t, i);

        return crossConcatList(szolj, ti);
    }

    @Override
    public String toString() {
        return "EstimateBuilder{" +
                "pieceCounter=" + pieceCounter +
                ", oddCountLJ=" + oddCountLJ +
                ", oddCountT=" + oddCountT +
                ", oddCountI=" + oddCountI +
                '}';
    }
}
