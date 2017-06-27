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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 10ミノでパフェできない組み合わせのうち、できない順序のものだけを抽出
public class ExtractNoPerfectOrderMain {
    public static void main(String[] args) throws IOException {
        String inputFilePath = "output/minosp";
        Path path = Paths.get(inputFilePath);

        // ミノの組み合わせを決める
        LinkedList<File> targets = Files.walk(path)
                .map(Path::toFile)
                .filter(File::isFile)
                .collect(Collectors.toCollection(LinkedList::new));

        ArrayList<LongPieces> noPerfects = new ArrayList<>();
        while (!targets.isEmpty()) {
            // ミノの組みあわせを決定
            File targetFile = targets.pop();
            String name = targetFile.getName().substring(0, 10);

            // ツモ順一覧
            List<LongPieces> pieces = Files.lines(targetFile.toPath(), Charset.forName("UTF-8"))
                    .map(ExtractNoPerfectOrderMain::parse)
                    .map(LongPieces::new)
                    .collect(Collectors.toList());

            /// パフェ地形一覧
            File perfectsFile = new File(String.format("output/pfsp/%s.csv", name));
            if (!perfectsFile.exists()) {
                continue;
            }

            MinoFactory minoFactory = new MinoFactory();
            List<List<OperationWithKey>> perfects = Files.lines(perfectsFile.toPath(), Charset.forName("UTF-8"))
                    .map(line -> OperationWithKeyInterpreter.parseToList(line, minoFactory))
                    .collect(Collectors.toList());

            System.out.println(String.format("%s => %s: %d orders in %d fields", targetFile, name, pieces.size(), perfects.size()));


            // 一部ができないものに対して、パフェできない順序だけを抽出
            int height = 4;
            LockedReachableThreadLocal reachableThreadLocal = new LockedReachableThreadLocal(height);
            Field field = FieldFactory.createField(height);
            List<LongPieces> noList = pieces.parallelStream()
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
                    })
                    .collect(Collectors.toList());

            System.out.println(noList.size());

            noPerfects.addAll(noList);

            Files.move(targetFile.toPath(), Paths.get(String.format("output/ngexistmino/%s.txt", name)));
        }

        // output
        File outputFile = new File("output/nonono.txt");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            for (Pieces pieces : noPerfects) {
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



//        String inputFilePath = "output/cut.txt";
//        Path piecesPath = Paths.get(inputFilePath);
//        Map<BlockCounter, List<LongPieces>> map = Files.lines(piecesPath, Charset.forName("UTF-8"))
//                .map(s -> new LongPieces(Filter10MinoPerfectMain.parse(s)))
//                .collect(Collectors.groupingBy(o -> new BlockCounter(o.getBlockStream())));
//        System.out.println(map.size());
//
//
//        for (Map.Entry<BlockCounter, List<LongPieces>> entry : map.entrySet()) {
//            String blocks = entry.getKey().getBlockStream()
//                    .map(Block::getName)
//                    .collect(Collectors.joining());
//            File outputFile = new File(String.format("output/minosp/%s.txt", blocks));
//            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
//                for (Pieces pieces : entry.getValue()) {
//                    writer.write(pieces.getBlockStream().map(Block::getName).collect(Collectors.joining()));
//                    writer.newLine();
//                }
//                writer.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.exit(1);
//            }
//        }
//
//        HashSet<ReduceLongPieces> collect = Files.lines(piecesPath, Charset.forName("UTF-8"))
//                .parallel()
//                .map(s -> new LongPieces(Main.parse(s)).fix())
//                .collect(Collectors.toCollection(HashSet::new));
//        System.out.println(collect.size());
//


//        String inputFilePath = "output/sp/TIILLLJJJO.csv";
//        Path piecesPath = Paths.get(inputFilePath);
//        Files.lines(piecesPath, Charset.forName("UTF-8"))
//                .forEach(s -> {
//                    String[] array = s.split(";");
//                    Arrays.stream(array)
//                            .forEach(s1 -> {
//                                String[] split = s1.split(",");
//                                if (split[0].equals("T")) {
//                                    if (split[1].equals("L") || split[1].equals("R")) {
//                                        System.out.println(s1);
//                                    }
//                                }
//                            });
//                });
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
