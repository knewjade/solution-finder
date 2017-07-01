package _experimental.main;

import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import common.pattern.PiecesGenerator;
import core.mino.Block;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class All {
    public static void main(String[] args) throws IOException {
        // 7種1巡で可能性のあるツモ順
        // 4line on hold
        List<String> patternsOnHold = Arrays.asList(
                "*p7, *p4",
                "*, *p3, *p7",
                "*, *p7, *p3",
                "*, *p4, *p6",
                "*, *, *p7, *p2",
                "*, *p5, *p5",
                "*, *p2, *p7, *",
                "*, *p6, *p4"
        );

        HashSet<Pieces> allPieces = new HashSet<>();

        for (String pattern : patternsOnHold) {
            PiecesGenerator generator = new PiecesGenerator(pattern);
            generator.stream()
                    .sequential()
                    .map(pieces -> new LongPieces(pieces.getBlockStream()))
                    .forEach(allPieces::add);
        }

        System.out.println(allPieces.size());

        File outputFile = new File("output/all11.csv");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            allPieces.stream()
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
}
