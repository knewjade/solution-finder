package searcher.common;

import core.mino.Block;
import core.srs.Rotate;
import searcher.common.action.Action;

import java.util.*;

public class ResultHelper {
    public static List<Result> uniquify(List<Result> results) {
        HashSet<Result> set = new HashSet<>(results);
        List<Result> unique = new ArrayList<>(set);
        unique.sort((a, b) -> {
            int compare5 = Objects.compare(a.getLastHold(), b.getLastHold(), Comparator.comparingInt(Block::getNumber));
            if (compare5 != 0)
                return compare5;

            int compare4 = Objects.compare(a.getLastBlock(), b.getLastBlock(), Comparator.comparingInt(Block::getNumber));
            if (compare4 != 0)
                return compare4;

            int compare3 = Objects.compare(a.getAction().getRotate(), b.getAction().getRotate(), Comparator.comparingInt(Rotate::getNumber));
            if (compare3 != 0)
                return compare3;

            int compare2 = Objects.compare(a.getAction(), b.getAction(), Comparator.comparingInt(Action::getY));
            if (compare2 != 0)
                return compare2;

            return Objects.compare(a.getAction(), b.getAction(), Comparator.comparingInt(Action::getX));
        });
        return unique;
    }
}
