package a;

import core.mino.Block;
import core.srs.Rotate;
import core.mino.Mino;
import core.mino.MinoFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Estimate {
    private final MinoFactory minoFactory;

    public Estimate(MinoFactory minoFactory) {
        this.minoFactory = minoFactory;
    }

    public List<MinoPivot> create(int[][] field, List<Block> blocks) {
        // 番号から座標リストへ変更
        List<List<Coordinate>> blockCoordinates = getBlockCoordinates(field, blocks);

        // 座標リストをもとにブロックの向きと回転軸を推定する
        List<MinoPivot> minoPivots = new ArrayList<>();
        BLOCK_LOOP:
        for (int index = 0; index < blocks.size(); index++) {
            Block block = blocks.get(index);
            List<Coordinate> coordinates = blockCoordinates.get(index);

            // 各回転ごとに照らし合わせる
            for (Rotate rotate : Rotate.values()) {
                Mino mino = minoFactory.create(block, rotate);
                Coordinate pivot = matchCoordinatesAndMino(coordinates, mino.getPositions());
                if (pivot != null) {
                    minoPivots.add(new MinoPivot(mino, pivot));
                    continue BLOCK_LOOP;
                }
            }

            throw new IllegalArgumentException("No match all rotate and coordinate");
        }

        return minoPivots;
    }

    private List<List<Coordinate>> getBlockCoordinates(int[][] field, List<Block> blocks) {
        List<List<Coordinate>> blockCoordinates = new ArrayList<>(blocks.size());
        for (int index = 0; index < blocks.size(); index++)
            blockCoordinates.add(new ArrayList<>(4));

        // 番号ごとに座標へ変換する
        for (int y = 0; y < field.length; y++) {
            for (int x = 0; x < field[y].length; x++) {
                int index = field[y][x];
                if (0 <= index)
                    blockCoordinates.get(index).add(new Coordinate(x, y));
            }
        }

        // 本来のミノの形になるよう修正する
        for (List<Coordinate> blockCoordinate : blockCoordinates) {
            TreeSet<Integer> setY = extractYFromCoordinate(blockCoordinate);
            List<Integer> listY = new ArrayList<>(setY);

            // 抜けている行がないときはそのまま
            if (!existsSkipY(setY))
                continue;

            // 抜けている行を詰める
            int minY = listY.get(0);
            for (int index = 0; index < blockCoordinate.size(); index++) {
                Coordinate coordinate = blockCoordinate.get(index);
                Coordinate newCoordinate = new Coordinate(coordinate.x, minY + listY.indexOf(coordinate.y));
                blockCoordinate.set(index, newCoordinate);
            }
        }

        return blockCoordinates;
    }

    private boolean existsSkipY(TreeSet<Integer> setY) {
        boolean existsSkipY = false;
        Integer prev = setY.pollFirst();
        while (!setY.isEmpty()) {
            Integer current = setY.pollFirst();
            if (prev != current - 1)
                existsSkipY = true;
        }
        return existsSkipY;
    }

    private TreeSet<Integer> extractYFromCoordinate(List<Coordinate> blockCoordinate) {
        TreeSet<Integer> usedY = new TreeSet<>();
        for (Coordinate coordinate : blockCoordinate)
            usedY.add(coordinate.y);
        return usedY;
    }

    private Coordinate matchCoordinatesAndMino(List<Coordinate> coordinates, int[][] blockPosition) {
        COORDINATE_LOOP:
        for (Coordinate coordinate : coordinates) {
            int x = coordinate.x;
            int y = coordinate.y;
            for (int[] positions : blockPosition) {
                Coordinate target = new Coordinate(x + positions[0], y + positions[1]);
                if (!coordinates.contains(target))
                    continue COORDINATE_LOOP;
            }
            return coordinate;
        }
        return null;
    }
}
