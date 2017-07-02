package _experimental.perfect11;

import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import core.mino.Block;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Hold10OrderMergeMain {
    public static void main(String[] args) throws IOException {
        Path src = Paths.get("output/hold10list/");

        // すべての手順ファイルを列挙
        List<File> files = Files.walk(src)
                .map(Path::toFile)
                .filter(File::isFile)
                .filter(file -> !file.isHidden())
                .collect(Collectors.toList());

        System.out.println(files.size());

        // すべての手順
        HashSet<LongPieces> allOrders = new HashSet<>();

        // すべての手順を追加
        for (File file : files) {
            System.out.println(file);
            Files.lines(file.toPath())
                    .sequential()
                    .map(Hold10OrderMergeMain::parse10)
                    .map(LongPieces::new)
                    .forEach(allOrders::add);
            System.out.println(allOrders.size());
        }

        // ファイルに出力
        String outputPath = "output/allorderhold10.csv";
        File outputFile = new File(outputPath);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            allOrders.stream()
                    .map(Pieces::getBlocks)
                    .map(blocks -> blocks.stream().map(Block::getName).collect(Collectors.joining()))
                    .sorted()
                    .forEach(line -> {
                        try {
                            writer.write(line);
                            writer.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            writer.flush();
        }
    }

    private static Stream<Block> parse10(String a) {
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
