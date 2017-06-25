package _experimental.main;

import common.datastore.pieces.FrozenLongPieces;
import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import core.mino.Block;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        String inputFilePath = "output/allonhold.txt";
        Path piecesPath = Paths.get(inputFilePath);

        HashSet<FrozenLongPieces> collect = Files.lines(piecesPath, Charset.forName("UTF-8"))
                .parallel()
                .map(s -> new LongPieces(Main.parse(s)).fix())
                .collect(Collectors.toCollection(HashSet::new));
        System.out.println(collect.size());

        File outputFile = new File("output/cut.txt");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            for (Pieces pieces : collect) {
                writer.write(pieces.getBlockStream().map(Block::getName).collect(Collectors.joining()));
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


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
