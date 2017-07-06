package _experimental.main;

import common.datastore.BlockCounter;
import common.parser.BlockInterpreter;
import core.mino.Block;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Stream;

public class VerifyAll11MinoFilteringMain {
    public static void main(String[] args) throws IOException {
        long count = Files.lines(Paths.get("output/11sequences_usehold.csv"))
                .filter(line -> {
                    Stream<Block> blockStream = BlockInterpreter.parse11(line);
                    // BlockCounterに変換
                    BlockCounter blockCounter = new BlockCounter(blockStream);

                    // ミノの個数（最も多い・2番めに多い）を取得
                    EnumMap<Block, Integer> map = blockCounter.getEnumMap();
                    List<Integer> values = new ArrayList<>(map.values());
                    values.add(0);  // ミノが2種類以下の場合はこの0を取得する
                    values.add(0);
                    values.sort(Comparator.reverseOrder());

                    int first = values.get(0);
                    int second = values.get(1);
                    int third = values.get(2);

                    boolean valid = (first == 4 && second <= 2 && third <= 1) ||
                            (first == 3 && second == 3 && third <= 1) ||
                            (first == 3 && second <= 2) ||
                            (first <= 2);
                    return !valid;
                })
                .peek(System.out::println)
                .count();
        System.out.println(count);
    }
}
