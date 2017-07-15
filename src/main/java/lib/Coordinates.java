package lib;

import common.datastore.Coordinate;
import core.mino.Mino;

import java.util.stream.Stream;

public class Coordinates {
    // ミノがmaxY以下に全て収まる座標をすべて挙げる
    public static Stream<Coordinate> walk(Mino mino, int maxY) {
        Stream.Builder<Coordinate> builder = Stream.builder();
        for (int y = -mino.getMinY(); y < maxY - mino.getMaxY(); y++) {
            for (int x = -mino.getMinX(); x < 10 - mino.getMaxX(); x++) {
                builder.accept(Coordinate.create(x, y));
            }
        }
        return builder.build();
    }
}
