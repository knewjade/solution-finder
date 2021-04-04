package entry.util.seq;

import java.util.HashSet;

interface PieceOutputForBackward {
    void output(String str);
}

class SimplePieceOutputForBackward implements PieceOutputForBackward {
    @Override
    public void output(String str) {
        System.out.println(str);
    }
}

class DistinctPieceOutputForBackward implements PieceOutputForBackward {
    private final HashSet<String> map = new HashSet<>();

    @Override
    public void output(String str) {
        boolean success = map.add(str);
        if (success) {
            System.out.println(str);
        }
    }
}
