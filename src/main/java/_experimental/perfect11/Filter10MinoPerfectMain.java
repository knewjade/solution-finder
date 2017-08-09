package _experimental.perfect11;

import common.buildup.BuildUp;
import common.datastore.OperationWithKey;
import common.datastore.pieces.LongBlocks;
import common.order.OrderLookup;
import common.parser.BlockInterpreter;
import common.parser.OperationWithKeyInterpreter;
import concurrent.LockedReachableThreadLocal;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

// 10ミノでパフェできるものをフィルタする
public class Filter10MinoPerfectMain {
    public static void main(String[] args) throws IOException {
        // ツモ順ファイル一覧を読み込む
        String inputFilePath = "deploy/order10each/";
        Path path = Paths.get(inputFilePath);

        // ミノの組み合わせを決める
        LinkedList<File> targets = Files.walk(path)
                .map(Path::toFile)
                .filter(File::isFile)
                .filter(file -> !file.isHidden())
                .collect(Collectors.toCollection(LinkedList::new));

        List<String> successList = new ArrayList<>();
        List<String> failedList = new ArrayList<>();
        List<String> skipList = new ArrayList<>();

        while (!targets.isEmpty()) {
            // ミノの組みあわせを決定
            File targetFile = targets.pop();
            String name = targetFile.getName().substring(0, 10);

            // パフェ地形のファイルが存在するか
            File perfectsFile = new File(String.format("deploy/perfect10each/%s.csv", name));
            if (!perfectsFile.exists()) {
                System.out.println("skip: no file # " + name);
                skipList.add(name);
                continue;
            }

            // ツモ順一覧
            List<LongBlocks> pieces = Files.lines(targetFile.toPath(), Charset.forName("UTF-8"))
                    .map(BlockInterpreter::parse10)
                    .map(LongBlocks::new)
                    .collect(Collectors.toList());

            // パフェ地形一覧
            MinoFactory minoFactory = new MinoFactory();
            List<List<OperationWithKey>> perfects = Files.lines(perfectsFile.toPath(), Charset.forName("UTF-8"))
                    .map(line -> OperationWithKeyInterpreter.parseToList(line, minoFactory))
                    .collect(Collectors.toList());

            System.out.println(String.format("%s => %s: %d orders in %d fields", targetFile, name, pieces.size(), perfects.size()));

            // 最初10ミノをホールドなしでパフェできるか確認
            int height = 4;
            LockedReachableThreadLocal reachableThreadLocal = new LockedReachableThreadLocal(height);
            Field field = FieldFactory.createField(height);
            boolean noPerfect = pieces.parallelStream()
                    .map(LongBlocks::getBlocks)
                    .filter(blocks -> {
                        return perfects.parallelStream()
                                .noneMatch(operationWithKeys -> BuildUp.existsValidByOrder(field, operationWithKeys.stream(), blocks, 4, reachableThreadLocal.get()));
                    })
                    .anyMatch(blocks -> {
                        return OrderLookup.forwardBlocks(blocks, 10).stream()
                                .noneMatch(forward -> perfects.parallelStream()
                                        .anyMatch(operationWithKeys -> BuildUp.existsValidByOrder(field, operationWithKeys.stream(), forward.toList(), 4, reachableThreadLocal.get())));
                    });

            // できないパターンがあるときはストップ
            if (noPerfect) {
                System.out.println("    => skip: no all building");
                failedList.add(name);
            } else {
                System.out.println("    => success");
                successList.add(name);
            }
        }

        // ファイルに出力
        String successOutputPath = "output/success.csv";
        File successOutputFile = new File(successOutputPath);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(successOutputFile), StandardCharsets.UTF_8))) {
            successList.stream()
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

        String failedOutputPath = "output/failed.csv";
        File failedOutputFile = new File(failedOutputPath);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(failedOutputFile), StandardCharsets.UTF_8))) {
            failedList.stream()
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

        String skipOutputPath = "output/skip.csv";
        File skipOutputFile = new File(skipOutputPath);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(skipOutputFile), StandardCharsets.UTF_8))) {
            skipList.stream()
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
