package _experimental.main;

import common.datastore.pieces.Pieces;
import common.pattern.PiecesGenerator;
import core.mino.Block;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AllPatternsMain {
    public static void main(String[] args) throws IOException {
        // 7種1巡で可能性のあるツモ順
        // 4line on hold
//        List<String> patterns = Arrays.asList(
//                "*p7, *p4"
//                "*, *p3, *p7"
//                "*, *p7, *p3"
//                "*, *p4, *p6"
//                "*, *, *p7, *p2"
//                "*, *p5, *p5"
//                "*, *p2, *p7, *"
//                "*, *p6, *p4"
//        );

        // 4line without hold
        List<String> patterns = Arrays.asList(
//                "*p7, *p3"
//                "*p4, *p6"
//                "*, *p7, *p2"
//                "*p5, *p5"
//                "*p2, *p7, *"
//                "*p6, *p4"
                "*p3, *p7"
        );

        File outputFile = new File("output/order7avoid.csv");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            PiecesGenerator generator = new PiecesGenerator(patterns);
            generator.stream()
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
