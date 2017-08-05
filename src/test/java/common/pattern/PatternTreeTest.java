package common.pattern;

import com.google.common.collect.Iterables;
import common.comparator.PiecesNameComparator;
import common.datastore.pieces.Pieces;
import concurrent.LockedCandidateThreadLocal;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import searcher.common.validator.PerfectValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class PatternTreeTest {
    @Test
    void start() {
        PatternTree tree = new PatternTree();
        PiecesGenerator piecesGenerator = new PiecesGenerator("I,I,J,L,O,S,Z,T,T,I,*");
        ArrayList<Pieces> piecesList = Lists.newArrayList(piecesGenerator);
        piecesList.forEach(pieces -> tree.build(pieces.getBlocks(), blocks -> new PatternTree()));

        System.out.println(piecesList.size());

        Field field = FieldFactory.createField("" +
                "__________"
//                "XXXX______" +
//                "XXXX______" +
//                "XXXX______"
        );

        MinoFactory minoFactory = new MinoFactory();
        int height = 4;
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(height);
        PerfectValidator validator = new PerfectValidator();
        CommonObj commonObj = new CommonObj(candidateThreadLocal, minoFactory, validator);
        boolean run = tree.run(field, height, commonObj);
        System.out.println(run);

        piecesList.sort(new PiecesNameComparator());
        for (Pieces pieces : piecesList) {
            List<Block> blocks = pieces.getBlocks();
            System.out.println(blocks + " " + tree.get(blocks));
        }
    }
}