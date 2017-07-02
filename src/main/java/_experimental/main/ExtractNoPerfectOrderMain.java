package _experimental.main;

import common.buildup.BuildUp;
import common.datastore.OperationWithKey;
import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import common.order.OrderLookup;
import common.parser.OperationWithKeyInterpreter;
import concurrent.LockedReachableThreadLocal;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 10ミノでパフェできない組み合わせのうち、できない順序のものだけを抽出
public class ExtractNoPerfectOrderMain {
    public static void main(String[] args) throws IOException {
        Path includeNGPath = Paths.get("output/combIncludeNG.csv");

        int height = 4;
        Field field = FieldFactory.createField(height);
        LockedReachableThreadLocal reachableThreadLocal = new LockedReachableThreadLocal(height);

        List<LongPieces> notPerfectOrders = Files.lines(includeNGPath, StandardCharsets.UTF_8)
                .peek(System.out::println)
                .flatMap(name -> {
                    Path perfectPath = Paths.get(String.format("output/perfect10each/%s.csv", name));
                    List<List<OperationWithKey>> perfects = loadPerfects(perfectPath);

                    Path orderPath = Paths.get(String.format("output/order10each/%s.csv", name));

                    try {
                        return Files.lines(orderPath, StandardCharsets.UTF_8)
                                .map(ExtractNoPerfectOrderMain::parse)
                                .map(LongPieces::new)
                                .filter(longPieces -> {
                                    List<Block> blocks = longPieces.getBlocks();
                                    return perfects.parallelStream()
                                            .noneMatch(operationWithKeys -> BuildUp.existsValidByOrder(field, operationWithKeys.stream(), blocks, 4, reachableThreadLocal.get()));
                                })
                                .filter(longPieces -> {
                                    List<Block> blocks = longPieces.getBlocks();
                                    return OrderLookup.forwardBlocks(blocks, 10).stream()
                                            .noneMatch(forward -> perfects.parallelStream()
                                                    .anyMatch(operationWithKeys -> BuildUp.existsValidByOrder(field, operationWithKeys.stream(), forward.toList(), 4, reachableThreadLocal.get())));
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                    throw new IllegalStateException("No reachable");
                })
                .collect(Collectors.toList());

        // output
        File outputFile = new File("output/order10noperfect.csv");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            for (Pieces pieces : notPerfectOrders) {
                String blocks = pieces.getBlockStream().map(Block::getName).collect(Collectors.joining());
                try {
                    writer.write(blocks);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            writer.flush();
        }
    }

    private static List<List<OperationWithKey>> loadPerfects(Path perfectPath) {
        MinoFactory minoFactory = new MinoFactory();
        try {
            return Files.lines(perfectPath, Charset.forName("UTF-8"))
                    .map(line -> OperationWithKeyInterpreter.parseToList(line, minoFactory))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        throw new IllegalStateException("No reachable");
    }

    private static Stream<Block> parse(String a) {
        return Stream.of(
                parse(a.charAt(0)),
                parse(a.charAt(1)),
                parse(a.charAt(2)),
                parse(a.charAt(3)),
                parse(a.charAt(4)),
                parse(a.charAt(5)),
                parse(a.charAt(6)),
                parse(a.charAt(7)),
                parse(a.charAt(8)),
                parse(a.charAt(9))
        );
    }

    private static Block parse(char ch) {
        switch (ch) {
            case 'T':
                return Block.T;
            case 'S':
                return Block.S;
            case 'Z':
                return Block.Z;
            case 'O':
                return Block.O;
            case 'I':
                return Block.I;
            case 'L':
                return Block.L;
            case 'J':
                return Block.J;
        }
        throw new IllegalStateException("No reachable");
    }
}
