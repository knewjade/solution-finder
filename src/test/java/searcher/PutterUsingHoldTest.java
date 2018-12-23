package searcher;

import common.datastore.Result;
import common.datastore.action.Action;
import common.datastore.order.Order;
import core.action.candidate.LockedCandidate;
import core.field.*;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import org.junit.jupiter.api.Test;
import searcher.checkmate.CheckmateDataPool;
import searcher.core.FullSearcherCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class PutterUsingHoldTest {
    @Test
    void name() {
        int maxHeight = 6;
        List<Piece> pieces = Arrays.asList(Piece.J, Piece.L, Piece.O, Piece.S, Piece.Z);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        TSpinSearchValidator validator = new TSpinSearchValidator(minoFactory, minoShifter, minoRotation, maxHeight, pieces.size());
        CheckmateDataPool dataPool = new CheckmateDataPool();
        FullSearcherCore<Action, Order> core = new FullSearcherCore<>(minoFactory, validator, dataPool);
        PutterNoHold<Action> putter = new PutterNoHold<>(dataPool, core);

        LockedCandidate candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxHeight);
        putter.first(FieldFactory.createField(maxHeight), pieces, candidate, maxHeight, pieces.size());

        ArrayList<Result> results = putter.getResults();
        System.out.println("# " + results.size());
//        for (Result result : results) {
//            System.out.println(result);
//        }
    }
}