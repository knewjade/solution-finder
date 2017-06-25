package _experimental.main;

import common.datastore.BlockCounter;
import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import core.mino.Block;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// all10mino.csvを使用するミノごとにファイルで分割する
public class Splitter {
    public static void main(String[] args) throws IOException {
        String inputFilePath = "output/all10mino.csv";
        Path piecesPath = Paths.get(inputFilePath);
        Files.lines(piecesPath, Charset.forName("UTF-8"))
                .limit(10L)
                .forEach(s -> {
                    String[] array = s.split(";");
                    Pieces pieces = new LongPieces();
                    for (String operation : array) {
                        String[] split = operation.split(",");
                        Block block = Block.valueOf(split[0]);
                        pieces = pieces.addAndReturnNew(block);
                    }
                    Stream<Block> stream = pieces.getBlockStream();
                    BlockCounter counter = new BlockCounter(stream);
                    String collect = counter.getBlocks().stream()
                            .map(Block::getName)
                            .collect(Collectors.joining());

                    String outputFilePath = String.format("output/sp/%s.csv", collect);
                    File outputFile = new File(outputFilePath);
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, true), StandardCharsets.UTF_8))) {
                        writer.write(s);
                        writer.newLine();
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                });

        System.out.println(inputFilePath);
    }
}
