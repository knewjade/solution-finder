package _experimental.square4x10;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

// 白紙のページを追加する
public class SquareFigureStep5 {
    private static final int BLOCK_SIZE = 8;
    private static final int BLOCK_WIDTH_COUNT = 4;  // フィールドの横ブロック数
    private static final int BLOCK_HEIGHT_COUNT = 4;
    private static final int BLOCK_BOARDER = 1;
    private static final int FIELD_WIDTH_SIZE = BLOCK_SIZE * BLOCK_WIDTH_COUNT + BLOCK_BOARDER * (BLOCK_WIDTH_COUNT - 1);
    private static final int FIELD_HEIGHT_SIZE = BLOCK_SIZE * BLOCK_HEIGHT_COUNT + BLOCK_BOARDER * (BLOCK_HEIGHT_COUNT - 1);

    private static final int FIELD_ROW_COUNT = 4;  // 縦のフィールド数
    private static final int FIELD_COLUMN_COUNT = 12;  // 横のフィールド数
    private static final int FIELD_WIDTH_MARGIN = 8;
    private static final int FIELD_HEIGHT_MARGIN = 8;

    public static void main(String[] args) throws IOException {
        String inputDirectory = "output/img";

        // fileIndex -> y -> x
        HashMap<Integer, Map<Integer, Set<Integer>>> map = new HashMap<>();

        // 最大幅を取得
        OptionalInt maxXOptional = Files.walk(Paths.get(inputDirectory))
                .map(Path::toFile)
                .filter(File::isFile)
                .map(File::getName)
                .mapToInt(name -> {
                    String[] split = name.substring(0, name.length() - 4).split("_");
                    return Integer.valueOf(split[2]);
                })
                .max();
        assert maxXOptional.isPresent();
        int maxX = maxXOptional.getAsInt();
        System.out.println("max width:");
        System.out.println(maxX + 1);

        // mapの作成
        Files.walk(Paths.get(inputDirectory))
                .map(Path::toFile)
                .filter(File::isFile)
                .map(File::getName)
                .forEach(name -> {
                    String[] split = name.substring(0, name.length() - 4).split("_");
                    int fileIndex = Integer.valueOf(split[0]);
                    int y = Integer.valueOf(split[1]);
                    int x = Integer.valueOf(split[2]);

                    Map<Integer, Set<Integer>> eachHeight = map.computeIfAbsent(fileIndex, v -> new HashMap<>());
                    Set<Integer> eachWidth = eachHeight.computeIfAbsent(y, v -> new HashSet<>());
                    eachWidth.add(x);
                });

        int s = 0;
        for (Map<Integer, Set<Integer>> values : map.values()) {
            s += values.keySet().size() + 1;
        }

        System.out.println("max height:");
        System.out.println(s + 1);

        //　白紙のページを作成
        int widthSize = (FIELD_WIDTH_SIZE + FIELD_WIDTH_MARGIN) * FIELD_COLUMN_COUNT;
        int heightSize = (FIELD_HEIGHT_SIZE + FIELD_HEIGHT_MARGIN) * FIELD_ROW_COUNT;

        BufferedImage image = new BufferedImage(widthSize, heightSize, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, widthSize, heightSize);

        // 白紙のページを書き出す
        int emptyCounter = 0;
        for (Map.Entry<Integer, Map<Integer, Set<Integer>>> entry : map.entrySet()) {
            System.out.println(entry);
            int fileIndex = entry.getKey();
            Map<Integer, Set<Integer>> eachHeight = entry.getValue();

            OptionalInt maxOptional = eachHeight.keySet().stream()
                    .mapToInt(v -> v)
                    .max();

            assert maxOptional.isPresent();
            int maxY = maxOptional.getAsInt();

            int startY = fileIndex == 0 ? 0 : 1;
            for (int y = startY; y <= maxY + 1; y++) {
                for (int x = 0; x <= maxX + 1; x++) {
                    String filePath = String.format("%s/%d_%03d_%03d.png", inputDirectory, fileIndex, y, x);
                    Path path = Paths.get(filePath);
                    File file = path.toFile();
                    if (!file.exists()) {
                        //　書き出し
                        ImageIO.write(image, "png", file);
                        emptyCounter++;
                    }
                }
            }
        }
        System.out.println(emptyCounter);
    }
}
