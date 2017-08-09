package _experimental.perfect11;

import common.datastore.pieces.Blocks;
import common.datastore.pieces.LongBlocks;
import common.parser.BlockInterpreter;
import core.mino.Block;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
        HashSet<LongBlocks> allOrders = new HashSet<>();

        // すべての手順を追加
        for (File file : files) {
            System.out.println(file);
            Files.lines(file.toPath())
                    .sequential()
                    .map(BlockInterpreter::parse10)
                    .map(LongBlocks::new)
                    .forEach(allOrders::add);
            System.out.println(allOrders.size());
        }

        // ファイルに出力
        String outputPath = "output/allorderhold10.csv";
        File outputFile = new File(outputPath);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            allOrders.stream()
                    .map(Blocks::getBlocks)
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
