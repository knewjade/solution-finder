package _experimental.main;

import common.datastore.BlockCounter;
import common.parser.BlockInterpreter;
import core.mino.Block;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CombAll11Main {
    public static void main(String[] args) throws IOException {
        HashSet<BlockCounter> collect = Files.lines(Paths.get("output/all11onhold.csv"))
                .map(BlockInterpreter::parse11)
                .map(BlockCounter::new)
                .collect(Collectors.toCollection(HashSet::new));
        System.out.println(collect.size());

        File outputFile = new File("output/comball11onhold.csv");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            collect.stream()
                    .map(BlockCounter::getBlockStream)
                    .map(blockStream -> blockStream.map(Block::getName).collect(Collectors.joining()))
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
