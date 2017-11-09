package common.datastore.action;

import core.srs.Rotate;

public interface Action {
    static int defaultCompareTo(Action self, Action o) {
        int rotateCompare = self.getRotate().compareTo(o.getRotate());
        if (rotateCompare != 0)
            return rotateCompare;

        int xCompare = self.getX() - o.getX();
        if (xCompare != 0)
            return xCompare;

        return self.getY() - o.getY();
    }

    static int defaultHashCode(int x, int y, Rotate rotate) {
        int result = x;
        result = 24 * result + y;
        result = 4 * result + rotate.getNumber();
        return result;
    }

    static boolean defaultEquals(Action self, Action o) {
        return self.getX() == o.getX() && self.getY() == o.getY() && self.getRotate() == o.getRotate();
    }

    int getX();

    int getY();

    Rotate getRotate();
}
