package lib;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BooleanWalker {
    public static Stream<List<Boolean>> walk(int size) {
        return IntStream.range(0, 1 << size)
                .mapToObj(value -> {
                    ArrayList<Boolean> booleans = new ArrayList<>();
                    for (int index = 0; index < size; index++)
                        booleans.add((value & (1 << index)) == 0);
                    return booleans;
                });
    }
}
