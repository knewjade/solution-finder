package misc.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InsertMapGenerator {
    public static void main(String[] args) {
        run();
    }

    private static void run() {
        Map<Integer, String> bitOperationMap = createBitOperationMap();

        // switch文に変換
        System.out.println("switch (key) {");
        for (Map.Entry<Integer, String> entry : bitOperationMap.entrySet()) {
            System.out.println("    case " + entry.getKey() + ":");
            System.out.println("        return " + entry.getValue() + ";");
        }
        System.out.println("    default:");
        System.out.println("        throw new IllegalArgumentException(\"No reachable\");");
        System.out.println("}");
    }

    private static Map<Integer, String> createBitOperationMap() {
        Map<Integer, String> map = new HashMap<>();

        for (int pattern = 0; pattern < 64; pattern++) {
            List<Boolean> leftFlags = createLeftFlags(pattern);

            // ブロックで残し始めるインデックスと行数
            List<Integer> leftStart = new ArrayList<>();
            List<Integer> leftLines = new ArrayList<>();
            int count = 0;
            for (int index = 0; index < leftFlags.size(); index++) {
                Boolean flag = leftFlags.get(index);
                if (flag) {
                    if (count == 0)
                        leftStart.add(index);
                    count += 1;
                } else {
                    if (count != 0)
                        leftLines.add(count);
                    count = 0;
                }
            }
            if (count != 0)
                leftLines.add(count);

            // ビット操作に変換する
            List<String> operation = createOperation(leftStart, leftLines, leftFlags);

//            System.out.println(leftFlags);
//            System.out.println(leftStart);
//            System.out.println(leftLines);
//            System.out.println(operation);

            // ()で囲う
            List<String> brancketOperation = operation.stream().map(s -> "(" + s + ")").collect(Collectors.toList());

            // flagsからkeyに変換
            int key = parseToKey(leftFlags);

            // mapに登録
            map.put(key, String.join(" | ", brancketOperation));
        }

        return map;
    }

    private static ArrayList<Boolean> createLeftFlags(int pattern) {
        ArrayList<Boolean> booleans = new ArrayList<>();
        int value = pattern;
        for (int i = 0; i < 6; i++) {
            booleans.add((value & 1) != 0);
            value >>>= 1;
        }
        return booleans;
    }

    private static List<String> createOperation(List<Integer> leftStart, List<Integer> leftLines, List<Boolean> leftFlags) {
        List<String> operation = new ArrayList<>();

        // 残ったブロックを移動させる
        for (int index = 0; index < leftStart.size(); index++) {
            Integer newStart = leftStart.get(index);
            Integer line = leftLines.get(index);

            int srcStart = 0;
            for (int i = 0; i < index; i++) {
                srcStart += leftLines.get(i);
            }

            int slide = (newStart - srcStart) * 10;
            long mask = (1L << line * 10) - 1 << (srcStart * 10);

            if ((newStart + line) == 6 && newStart == 0) {
                operation.add("x");
            } else if (slide == 0) {
                operation.add(String.format("x & %dL", mask));
            } else {
                operation.add(String.format("(x & %dL) << %d", mask, slide));
            }
        }

        // 消えたブロックを復元させる
        long block = 0L;
        for (int index = 0; index < leftFlags.size(); index++) {
            if (!leftFlags.get(index))
                block |= 0x3FFL << (10 * index);
        }

        if (block != 0L)
            operation.add(String.format("%dL", block));

        if (operation.isEmpty())
            operation.add("0L");

        return operation;
    }

    private static int parseToKey(List<Boolean> leftFlags) {
        long key = 0;
        for (int index = leftFlags.size() - 1; 0 <= index; index--) {
            key <<= 10;
            if (!leftFlags.get(index))
                key |= 1;
        }

        int lowerMask = (1 << 30) - 1;
        return (int) ((key >> 29) | (key & lowerMask));
    }
}
