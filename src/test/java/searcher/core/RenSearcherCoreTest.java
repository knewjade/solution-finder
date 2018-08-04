package searcher.core;

import common.datastore.RenResult;
import common.datastore.action.Action;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import org.junit.jupiter.api.Test;
import searcher.ren.RenUsingHold;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

class RenSearcherCoreTest {
    @Test
    void test() {
        System.out.println("hello");
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();

        RenUsingHold<Action> renUsingHold = new RenUsingHold<>(minoFactory);
        Field field = FieldFactory.createLargeField("" +
                "____XXXXXX" +
                "____XXXXXX" +
                "X___XXXXXX" +
                "XX__XXXXXX" +
                "XXXXXXXXXX"
        );

        LockedCandidate candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, 24);
        List<Piece> pieces = Arrays.asList(Piece.Z, Piece.O, Piece.L);
        List<RenResult> results = renUsingHold.check(field, pieces, candidate, pieces.size());

        System.out.println(results.size());
        results.sort(Comparator.comparingInt(RenResult::getRenCount).reversed());

        for (RenResult result : results) {
            System.out.println(result.getRenOrder().getRenCount());
            System.out.println(FieldView.toString(result.getRenOrder().getField()));
        }
    }
}