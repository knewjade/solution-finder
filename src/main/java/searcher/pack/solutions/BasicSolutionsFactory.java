package searcher.pack.solutions;

import common.datastore.OperationWithKey;
import common.datastore.Pair;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import searcher.pack.MinoField;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.ListMinoField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.SolutionFilter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BasicSolutionsFactory {
    public static BasicSolutions createAndWriteSolutions(File cacheFile, SolutionFilter solutionFilter, SeparableMinos separableMinos, SizedBit sizedBit) {
        // 基本パターンを計算する
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, List<MinoField>> calculate = calculator.calculate();

        // ファイルに出力する
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cacheFile), StandardCharsets.UTF_8))) {
            // 高さを出力する
            writer.write("" + sizedBit.getHeight());
            writer.newLine();

            // 各基本パターンを出力する
            for (Map.Entry<ColumnField, List<MinoField>> entry : calculate.entrySet()) {
                List<MinoField> value = entry.getValue();
                // 空のときは出力しない
                if (!value.isEmpty()) {
                    // 1行ごとに "キーとなる地形#対応する操作" となる
                    // キーとなる地形を出力
                    ColumnField columnField = entry.getKey();
                    String board = encodeColumnField(columnField);
                    writer.write(board);

                    writer.write('#');

                    // キーに対応する操作結果を出力
                    String operations = value.parallelStream()
                            .map(minoField -> {
                                String collect = minoField.getOperationsStream()
                                        .map(separableMinos::toIndex)
                                        .map(String::valueOf)
                                        .collect(Collectors.joining(","));
                                String outer = encodeColumnField(minoField.getOuterField());
                                return collect + "/" + outer;
                            })
                            .collect(Collectors.joining(";"));
                    writer.write(operations);

                    writer.newLine();
                }
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new BasicSolutions(calculate, solutionFilter);
    }

    private static String encodeColumnField(ColumnField columnField) {
        return IntStream.range(0, columnField.getBoardCount())
                .boxed()
                .map(columnField::getBoard)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public static BasicSolutions readAndCreateSolutions(File cacheFile, SolutionFilter solutionFilter, SeparableMinos separableMinos, SizedBit sizedBit) {
        // ファイルから読み込む
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cacheFile), StandardCharsets.UTF_8))) {
            // 高さを読み込む
            int height = Integer.valueOf(reader.readLine());
            if (height != sizedBit.getHeight())
                return null;

            HashMap<ColumnField, List<MinoField>> map = reader.lines()
                    .parallel()
                    .map(line -> {
                        // 地形と操作結果に分割する
                        String[] sharpSplit = line.split("#");
                        assert sharpSplit.length == 2;

                        // 地形に変換
                        ColumnField field = decodeToColumnField(sharpSplit[0]);

                        // 操作結果に変換
                        ArrayList<MinoField> minoFields = new ArrayList<>();
                        for (String operationBoard : sharpSplit[1].split(";")) {
                            String[] slashSplit = operationBoard.split("/");
                            assert slashSplit.length == 2;

                            // 操作に変換
                            String operation = slashSplit[0];
                            String[] split1 = operation.split(",");
                            List<OperationWithKey> operations = new ArrayList<>();
                            for (String s2 : split1) {
                                Integer index = Integer.valueOf(s2);
                                SeparableMino mino = separableMinos.getAt(index);
                                operations.add(mino.toOperation());
                            }

                            // 結果に変換
                            ColumnField outer = decodeToColumnField(slashSplit[1]);

                            minoFields.add(new ListMinoField(operations, outer, height, separableMinos));
                        }

                        return new Pair<>(field, minoFields);
                    })
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (u, u2) -> {
                        // 通常、同じキーの結果は複数個存在しない
                        throw new IllegalStateException("No reachable");
                    }, HashMap::new));

            return new BasicSolutions(map, solutionFilter);
        } catch (Exception e) {
            return null;
        }
    }

    private static ColumnField decodeToColumnField(String s) {
        String[] split = s.split(",");
        switch (split.length) {
            case 1:
                return new ColumnSmallField(Long.valueOf(split[0]));
        }
        throw new IllegalStateException("No reachable");
    }
}
