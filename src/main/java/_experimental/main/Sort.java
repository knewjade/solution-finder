package _experimental.main;

import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import core.mino.Block;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Sort {
    public static void main(String[] args) throws IOException {
        // read
        HashSet<Pieces> allSet = new HashSet<>();
        for (Integer index : Arrays.asList(4, 5, 6, 8, 1, 2, 3, 7)) {
            System.out.println(index);
            Path input = Paths.get(String.format("output/onhold/order%d.txt", index));
            Set<Pieces> set = Files.lines(input, Charset.forName("UTF-8"))
                    .map(s -> s.split(""))
                    .map(array -> {
                        Pieces pieces = new LongPieces();
                        for (String name : array) {
                            Block block = Block.valueOf(name);
                            pieces = pieces.addAndReturnNew(block);
                        }
                        return pieces;
                    })
                    .collect(Collectors.toSet());
            System.out.println(set.size());

            allSet.addAll(set);
            System.out.println("all: " + allSet.size());
        }

        System.out.println("done: read");

        // output
        File outputFile = new File("output/allonhold.txt");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            for (Pieces pieces : allSet) {
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
}
