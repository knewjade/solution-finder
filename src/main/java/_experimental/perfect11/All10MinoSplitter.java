package _experimental.perfect11;

import common.datastore.BlockCounter;
import common.datastore.pieces.LongBlocks;
import common.datastore.pieces.Blocks;
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
public class All10MinoSplitter {
    public static void main(String[] args) throws IOException {
        String inputFilePath = "deploy/all10mino.csv";
        Path piecesPath = Paths.get(inputFilePath);
        Files.lines(piecesPath, Charset.forName("UTF-8"))
                .sequential()
                .forEach(s -> {
                    // Piecesに変換
                    String[] array = s.split(";");
                    Blocks blocks = new LongBlocks();
                    for (String operation : array) {
                        String[] split = operation.split(",");
                        Block block = Block.valueOf(split[0]);
                        blocks = blocks.addAndReturnNew(block);
                    }

                    // BlockCounterに変換
                    Stream<Block> stream = blocks.blockStream();
                    BlockCounter counter = new BlockCounter(stream);

                    // 使用ミノ文字列に変換
                    String using = counter.getBlocks().stream()
                            .map(Block::getName)
                            .collect(Collectors.joining());

                    // 書き出し
                    String outputFilePath = String.format("output/sp/%s.csv", using);
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
