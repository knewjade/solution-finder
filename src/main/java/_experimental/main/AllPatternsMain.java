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
        switch (args[0]) {
            case "use":
                runOnHold(Integer.valueOf(args[1]));
                break;
            case "avoid":
                runWithoutHold(Integer.valueOf(args[1]));
                break;
        }
    }

    private static void runOnHold(int index) throws IOException {
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

        String pattern = patternsOnHold.get(index);
        createOnHold(pattern, index);
    }

    private static void createOnHold(String pattern, int index) throws IOException {
        String path = String.format("output/order%donhold.csv", index + 1);
        output(pattern, path);
    }

    private static void runWithoutHold(int index) throws IOException {
        // 4line without hold
        List<String> patternsWithoutHold = Arrays.asList(
                "*p7, *p3",
                "*p4, *p6",
                "*, *p7, *p2",
                "*p5, *p5",
                "*p2, *p7, *",
                "*p6, *p4",
                "*p3, *p7"
        );

        String pattern = patternsWithoutHold.get(index);
        createWithoutHold(pattern, index);
    }

    private static void createWithoutHold(String pattern, int index) throws IOException {
        String path = String.format("output/order%davoid.csv", index + 1);
        output(pattern, path);
    }

    private static void output(String pattern, String path) throws IOException {
        File outputFile = new File(path);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            PiecesGenerator generator = new PiecesGenerator(pattern);
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
