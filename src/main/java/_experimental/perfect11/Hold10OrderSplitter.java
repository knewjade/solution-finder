package _experimental.perfect11;

import common.datastore.BlockCounter;
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
public class Hold10OrderSplitter {
    public static void main(String[] args) throws IOException {
        String inputFilePath = "output/orderhold10.csv";
        Path piecesPath = Paths.get(inputFilePath);
        Files.lines(piecesPath, Charset.forName("UTF-8"))
                .sequential()
                .forEach(s -> {
                    // BlockCounterに変換
                    Stream<Block> stream = parse10(s);
                    BlockCounter counter = new BlockCounter(stream);

                    // 使用ミノ文字列に変換
                    String using = counter.getBlocks().stream()
                            .map(Block::getName)
                            .collect(Collectors.joining());

                    // 書き出し
                    String outputFilePath = String.format("output/order/%s.csv", using);
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
