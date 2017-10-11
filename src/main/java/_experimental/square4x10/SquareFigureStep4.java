package _experimental.square4x10;

import common.comparator.OperationWithKeyComparator;
import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import common.datastore.Pair;
import common.parser.OperationWithKeyInterpreter;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.SmallField;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.Rotate;
import lib.ListComparator;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.separable_mino.SeparableMino;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// 各ブロックに分割したcsvから画像に変換
public class SquareFigureStep4 {
    private static final int BLOCK_SIZE = 8;
    private static final int BLOCK_WIDTH_COUNT = 9;  // フィールドの横ブロック数
    private static final int BLOCK_HEIGHT_COUNT = 4;
    private static final int BLOCK_BOARDER = 1;
    private static final int FIELD_WIDTH_SIZE = BLOCK_SIZE * BLOCK_WIDTH_COUNT + BLOCK_BOARDER * (BLOCK_WIDTH_COUNT - 1);
    private static final int FIELD_HEIGHT_SIZE = BLOCK_SIZE * BLOCK_HEIGHT_COUNT + BLOCK_BOARDER * (BLOCK_HEIGHT_COUNT - 1);

    private static final int FIELD_ROW_COUNT = 8;  // 縦のフィールド数
    private static final int FIELD_COLUMN_COUNT = 12;  // 横のフィールド数
    private static final int FIELD_WIDTH_MARGIN = 8;
    private static final int FIELD_HEIGHT_MARGIN = 8;

    private static final int MAX_IMG_COLUMN = 104;  // 許可する画像の最大横数
    private static final int MAX_IMG_ROW = 400;  // 許可する画像の最大縦数

    private static final boolean IS_INDEX_NAME = true;  // 出力ファイル名をインデックスにする
    private static final boolean IS_EMPTY_RUN = false;  // 出力ファイルを空にする

    private static final ListComparator<OperationWithKey> OPERATION_WITH_KEY_LIST_COMPARATOR = new ListComparator<>(new OperationWithKeyComparator());

    private static final List<Color> COLORS = Arrays.asList(
            new Color(0xEDFBFF),
            new Color(0xFFF8E6),
            new Color(0xEDF0FF),
            new Color(0xFFF2F6),
            new Color(0xFFF2E6),
            new Color(0xE6EEFF),
            new Color(0xF6FFE3),
            new Color(0xFFEBEF),
            new Color(0xFFF5FE),
            new Color(0xF3FFFD)
    );

    // 画像化
    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File("output/img/");
        assert !file.exists();
        file.mkdirs();

        String inputDirectory = "input/9x4";
        List<PatternFile> patterns = premain(inputDirectory);
        main(patterns);

        createEmptyPages();
    }

    private static List<PatternFile> premain(String inputDirectory) throws IOException {
        List<Path> paths = Files.walk(Paths.get(inputDirectory))
                .filter(path -> path.toFile().isFile())
                .sorted(Comparator.comparing(Path::getFileName))
                .collect(Collectors.toList());

        return IntStream.range(0, paths.size())
                .boxed()
                .map(index -> new PatternFile(paths.get(index), index, COLORS.get(index)))
                .collect(Collectors.toList());
    }

    private static void main(List<PatternFile> patterns) throws IOException {
        // ファイル別パターン数
        /*
        for (PatternFile pattern : patterns) {
            long count = Files.lines(Paths.get(pattern.path)).count();
            System.out.printf("%s %d%n", pattern.path, count);
        }
        */

        // Block -> Rotate -> 接着y座標 -> 消去ライン
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        EnumMap<Block, EnumMap<Rotate, ArrayList<HashMap<Long, List<Delta>>>>> minoMap = calcMinoMap(minoFactory, minoShifter, BLOCK_HEIGHT_COUNT);

        for (PatternFile pattern : patterns) {
            System.out.println(pattern);
            main3(pattern, minoMap);
        }
    }

    private static void main3(PatternFile patternFile, EnumMap<Block, EnumMap<Rotate, ArrayList<HashMap<Long, List<Delta>>>>> minoMap) throws IOException {
        MinoFactory minoFactory = new MinoFactory();

        // パターンを使用ミノ別に分類する
        Map<BlockCounter, List<List<OperationWithKey>>> eachBlockCounter = Files.lines(patternFile.path)
                .map(s -> OperationWithKeyInterpreter.parseToStream(s, minoFactory))
                .map(stream -> stream.collect(Collectors.toList()))
                .collect(Collectors.groupingBy(o -> new BlockCounter(o.stream().map(OperationWithKey::getMino).map(Mino::getBlock))));

        // パターン数に変換する
        ArrayList<Pair<BlockCounter, Integer>> counters = new ArrayList<>();
        for (Map.Entry<BlockCounter, List<List<OperationWithKey>>> entry : eachBlockCounter.entrySet()) {
            Pair<BlockCounter, Integer> pair = new Pair<>(entry.getKey(), entry.getValue().size());
            counters.add(pair);
        }
        Comparator<Pair<BlockCounter, Integer>> comparing = Comparator.comparing(Pair::getValue);
        Collections.sort(counters, comparing.reversed());

        // 四角形に変換する
        ArrayList<Square> squares = new ArrayList<>();
        for (Pair<BlockCounter, Integer> pair : counters) {
            int w;
            int h;
            Integer value = pair.getValue();

            int fieldOnPage = size(1, 1);
            double quoter = MAX_IMG_COLUMN / 4.0;
            int unit = (int) (quoter * fieldOnPage);
            if (value <= unit) {
                w = (int) quoter + 1;
                h = 1;
            } else if (value <= unit * 2) {
                w = (int) quoter + 1;
                h = 2;
            } else {
                w = (int) quoter + 1;
                h = 300;
            }

            while (value < size(w, h - 1)) {
                h -= 1;
            }

            while (value < size(w - 1, h)) {
                w -= 1;
            }

            assert value < size(w, h) : w + " " + h;

//            System.out.printf("%d, %d, %d%n", pair.getValue(), w, h);
            squares.add(new Square(pair.getKey(), pair.getValue(), w, h));
        }

        int targetSquareSize = squares.size();

        // 四角形の位置を決める
        System.out.println("decide position");
        boolean[][] flags = new boolean[MAX_IMG_ROW][MAX_IMG_COLUMN];
        ArrayList<FixSquare> fixSquares = new ArrayList<>();
        int maxHeight = 0;
        LOOP2:
        while (!squares.isEmpty()) {
            // 左上を取得
            int currentX = 0;
            int currentY = 0;
            while (currentY < flags.length && currentX < flags[currentY].length) {
                LOOP:
                for (; currentY < flags.length; currentY++) {
                    for (; currentX < flags[currentY].length; currentX++) {
                        if (!flags[currentY][currentX])
                            break LOOP;
                    }
                    currentX = 0;
                }

                // 残りの幅から長方形を決める
                int leastWidth = MAX_IMG_COLUMN - currentX;
                for (int x = 0; x < leastWidth; x++) {
                    if (flags[currentY][currentX + x]) {
                        leastWidth = x;
                        break;
                    }
                }

                Square current = null;
                LOOP:
                for (int index = 0; index < squares.size(); index++) {
                    Square square = squares.get(index);
                    if (square.width <= leastWidth) {
                        if (currentX != 0 && maxHeight < currentY + square.height)
                            continue;

                        for (int y = 0; y < square.height; y++)
                            if (currentY + y < flags.length && flags[currentY + y][currentX])
                                continue LOOP;
                        current = squares.remove(index);
                        break;
                    }
                }

                // 埋める
                if (current != null) {
                    fixSquares.add(new FixSquare(current, currentX, currentY));
                    for (int yIndex = 0; yIndex < current.height; yIndex++) {
                        for (int xIndex = 0; xIndex < current.width; xIndex++) {
                            assert !flags[currentY + yIndex][currentX + xIndex] : (currentY + yIndex) + " " + (currentX + xIndex);
                            flags[currentY + yIndex][currentX + xIndex] = true;
                        }
                    }
                    maxHeight = maxHeight < currentY + current.height ? currentY + current.height : maxHeight;
                    continue LOOP2;
                } else {
                    // 埋められないので適当にスキップ
                    currentY += 1;
                    currentX = 0;
                }
            }
            assert false;
        }

        assert fixSquares.size() == targetSquareSize;

        // 表示
//        char[][] chars = new char[flags.length][flags[0].length];
//        char c = 'A';
//        for (FixSquare square : fixSquares) {
//            for (int y = 0; y < square.square.height; y++) {
//                for (int x = 0; x < square.square.width; x++) {
//                    chars[y + square.y][x + square.x] = c;
//                }
//            }
//            c += 1;
//        }
//        for (int y = 0; y < chars.length; y++) {
//            for (int x = 0; x < chars[y].length; x++) {
//                System.out.print(chars[y][x]);
//            }
//            System.out.println();
//        }
//        System.out.println("===");

        // 画像化
        for (int index = 0, size = fixSquares.size(); index < size; index++) {
            System.out.printf("%d / %d%n", index + 1, size);
            FixSquare fixSquare = fixSquares.get(index);
            main3(patternFile, fixSquare, eachBlockCounter.get(fixSquare.square.blockCounter), minoMap);
        }
    }

    public static int size(int w, int h) {
        int count = FIELD_COLUMN_COUNT * w * FIELD_ROW_COUNT * h;
        count -= (FIELD_COLUMN_COUNT * w) * 2;
        count -= (FIELD_ROW_COUNT * h) * 2;
        count += 4;
        return count;
    }

    private static void main3(PatternFile patternFile, FixSquare fixSquare, List<List<OperationWithKey>> lists, EnumMap<Block, EnumMap<Rotate, ArrayList<HashMap<Long, List<Delta>>>>> minoMap) throws IOException {
        if (!IS_EMPTY_RUN) {
            // 全ての操作を並び替える
            for (List<OperationWithKey> list : lists) {
                list.sort((o1, o2) -> {
                    int compareBlock = o1.getMino().getBlock().compareTo(o2.getMino().getBlock());
                    if (compareBlock != 0)
                        return compareBlock;

                    int compareX = Integer.compare(o1.getX(), o2.getX());
                    if (compareX != 0)
                        return compareX;

                    return Integer.compare(o1.getY(), o2.getY());
                });
            }

            // 操作リスト自体を並び替える
            lists.sort(OPERATION_WITH_KEY_LIST_COMPARATOR);
        }

        for (int yIndex = 0; yIndex < fixSquare.square.height; yIndex++) {
            for (int xIndex = 0; xIndex < fixSquare.square.width; xIndex++) {
                String path = toName(patternFile, fixSquare, yIndex, xIndex);
                main3(path, fixSquare, lists, xIndex, yIndex, minoMap, patternFile.background);
            }
        }
    }

    private static String toName(PatternFile patternFile, FixSquare fixSquare, int yIndex, int xIndex) {
        // 名前を取得 (インデックス)
        if (IS_INDEX_NAME) {
            return String.format("output/img/%d_%03d_%03d.png", patternFile.index, fixSquare.y + yIndex + 1, fixSquare.x + xIndex + 1);
        } else {
            // 名前を取得 (ブロック名)
            EnumMap<Block, Integer> map = fixSquare.square.blockCounter.getEnumMap();
            String delimiter = "-";
            String name = getName(map, delimiter);
            int index = yIndex * fixSquare.square.width + xIndex + 1;
            return String.format("output/img/%s_%d.png", name, index);
        }
    }

    private static void main3(String path, FixSquare fixSquare, List<List<OperationWithKey>> lists, int xIndex, int yIndex, EnumMap<Block, EnumMap<Rotate, ArrayList<HashMap<Long, List<Delta>>>>> minoMap, Color background) throws IOException {
//        System.out.printf("generate: %s (%d, %d)%n", fixSquare, xIndex, yIndex);

        // リストの準備
        EnumMap<Block, List<TaskData>> normalColorTasks = new EnumMap<>(Block.class);
        EnumMap<Block, List<TaskData>> strongColorTasks = new EnumMap<>(Block.class);
        for (Block block : Block.values()) {
            normalColorTasks.put(block, new ArrayList<>());
            strongColorTasks.put(block, new ArrayList<>());
        }

        ArrayList<TaskData> blackColorTasks = new ArrayList<>();

        if (!IS_EMPTY_RUN) {
            int leftXIndex = xIndex * FIELD_COLUMN_COUNT - 1;
            int topYIndex = yIndex * FIELD_ROW_COUNT - 1;
            int maxColumnFieldCount = (FIELD_COLUMN_COUNT * fixSquare.square.width) - 2;

            // 塗りつぶす場所を全て列挙
            for (int patternY = 0; patternY < FIELD_ROW_COUNT; patternY++) {
                if (patternY == 0 && yIndex == 0) {
                    // 四角全体の一番上の行
                    continue;
                }

                if (patternY == FIELD_ROW_COUNT - 1 && yIndex == fixSquare.square.height - 1) {
                    // 四角全体の一番下の行
                    continue;
                }

                for (int patternX = 0; patternX < FIELD_COLUMN_COUNT; patternX++) {
                    if (patternX == 0 && xIndex == 0) {
                        // 四角全体の一番左の列
                        continue;
                    }

                    if (patternX == FIELD_COLUMN_COUNT - 1 && xIndex == fixSquare.square.width - 1) {
                        // 四角全体の一番右の列
                        continue;
                    }

                    // フィールドの左上の座標
                    int left = patternX * (FIELD_WIDTH_SIZE + FIELD_WIDTH_MARGIN) + FIELD_WIDTH_MARGIN;
                    int top = patternY * (FIELD_HEIGHT_SIZE + FIELD_HEIGHT_MARGIN) + FIELD_HEIGHT_MARGIN;

                    // フィールドの番号を計算
                    int fieldIndex = (topYIndex + patternY) * maxColumnFieldCount + (leftXIndex + patternX);
                    if (lists.size() <= fieldIndex)
                        continue;

                    List<OperationWithKey> sample = lists.get(fieldIndex);

                    // フィールドを黒で塗る 線の色
                    blackColorTasks.add(new TaskData(left, top));

                    // 色を決定する
                    EnumMap<Block, Prev> prev = new EnumMap<>(Block.class);
                    for (OperationWithKey operationWithKey : sample) {
                        Block block = operationWithKey.getMino().getBlock();
                        Mino mino = operationWithKey.getMino();
                        List<Delta> deltas = minoMap.get(mino.getBlock()).get(mino.getRotate()).get(operationWithKey.getY()).get(operationWithKey.getNeedDeletedKey());
//                        System.out.println(block);
//                        System.out.println(deltas);
                        int x = operationWithKey.getX();
                        int y = operationWithKey.getY();

                        Prev prevKey = prev.getOrDefault(block, null);

                        SmallField field = new SmallField();
                        field.put(operationWithKey.getMino(), operationWithKey.getX(), operationWithKey.getY());
                        field.insertWhiteLineWithKey(operationWithKey.getNeedDeletedKey());

                        List<TaskData> list;
                        if (prevKey == null) {
                            list = normalColorTasks.get(block);
                            prev.put(block, new Prev(field));
                        } else {
                            if (!prevKey.flag) {
                                boolean isNoDuplicate = prevKey.getRange().canMerge(field);
                                if (isNoDuplicate) {
                                    list = normalColorTasks.get(block);
                                    prev.put(block, new Prev(field));
                                } else {
                                    list = strongColorTasks.get(block);
                                    prev.put(block, new Prev(field, true));
                                }
                            } else {
                                list = normalColorTasks.get(block);
                                prev.put(block, new Prev(field));
                            }
                        }

                        for (Delta delta : deltas) {
                            list.add(new TaskData(left, top, x + delta.x, y + delta.y));
                        }
                    }
                }
            }
        }

        // 画像を生成
        generateFigure(path, background, fixSquare, xIndex, yIndex, normalColorTasks, strongColorTasks, blackColorTasks);
    }

    private static void generateFigure(String path, Color background, FixSquare fixSquare, int xIndex, int yIndex, EnumMap<Block, List<TaskData>> normalColorTasks, EnumMap<Block, List<TaskData>> strongColorTasks, ArrayList<TaskData> blackColorTasks) throws IOException {
        int widthSize = (FIELD_WIDTH_SIZE + FIELD_WIDTH_MARGIN) * FIELD_COLUMN_COUNT;
        int heightSize = (FIELD_HEIGHT_SIZE + FIELD_HEIGHT_MARGIN) * FIELD_ROW_COUNT;

        BufferedImage image = new BufferedImage(widthSize, heightSize, BufferedImage.TYPE_INT_RGB);
        if (!IS_EMPTY_RUN) {
            // 白色で背景を塗る
            Graphics graphics = image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, widthSize, heightSize);

            // 背景色を塗る
            int backX = 0;
            int backWidth = widthSize;
            int backY = 0;
            int backHeight = heightSize;

            if (xIndex == 0) {
                backX += FIELD_WIDTH_SIZE / 2;
                backWidth -= FIELD_WIDTH_SIZE / 2;
            }
            if (xIndex == fixSquare.square.width - 1) {
                backWidth -= FIELD_WIDTH_SIZE / 2;
            }
            if (yIndex == 0) {
                backY += FIELD_HEIGHT_SIZE / 4;
                backHeight -= FIELD_HEIGHT_SIZE / 4;
            }
            if (yIndex == fixSquare.square.height - 1) {
                backHeight -= FIELD_HEIGHT_SIZE / 2;
            }
            graphics.setColor(background);
            graphics.fillRect(backX, backY, backWidth, backHeight);

            // テキストを入力
            graphics.setColor(new Color(0x4E4E4E));
            if (xIndex == 0 && yIndex == 0) {
                EnumMap<Block, Integer> map = fixSquare.square.blockCounter.getEnumMap();
                ArrayList<Map.Entry<Block, Integer>> entries = new ArrayList<>(map.entrySet());
//            entries.sort((o1, o2) -> {
//                int compare = Integer.compare(o1.getValue(), o2.getValue());
//                if (compare != 0)
//                    return -compare;  // 逆順。多い順
//                return o1.getKey().compareTo(o2.getKey());
//            });

                // 名前を取得
                String delimiter = " ";
                String name = getName(map, delimiter);

                // アンチエイリアス
                Graphics2D g2 = (Graphics2D) graphics;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // piecesの表示
                Font font = new Font("Verdana", Font.BOLD, 35);
                graphics.setFont(font);

                FontMetrics fontmetrics = graphics.getFontMetrics();
                int ascent = fontmetrics.getAscent();
                int x = FIELD_WIDTH_SIZE / 2 + 15;
                int y = FIELD_HEIGHT_SIZE / 4 + ascent;

                // 詳細を表示
                String str1 = String.format("%s / %d", name, fixSquare.square.solutionCount);
                graphics.drawString(str1, x, y);
                int stringWidth = fontmetrics.stringWidth(str1);

                Font font2 = new Font("Verdana", Font.BOLD, 30);
                graphics.setFont(font2);
                graphics.drawString(" solutions", x + stringWidth, y);
            }

            // フィールドの線を塗る
            for (TaskData data : blackColorTasks) {
                graphics.fillRect(data.x, data.y, FIELD_WIDTH_SIZE, FIELD_HEIGHT_SIZE);
            }

            // ブロックを塗る
            for (Block key : normalColorTasks.keySet()) {
                List<TaskData> normalColor = normalColorTasks.get(key);
                List<TaskData> strongColor = strongColorTasks.get(key);

                Color color = getNormalColor(key);
                graphics.setColor(color);

                for (TaskData data : normalColor) {
                    graphics.fillRect(data.x, data.y, BLOCK_SIZE, BLOCK_SIZE);
                }

                for (TaskData data : strongColor) {
                    graphics.fillRect(data.x, data.y, BLOCK_SIZE, BLOCK_SIZE);
                }

                Color strong = getStrongColor(key);
                graphics.setColor(strong);

                for (TaskData data : strongColor) {
                    graphics.fillRect(data.x + 2, data.y + 2, BLOCK_SIZE - 4, BLOCK_SIZE - 4);
                }
            }
        }

        // 書き出し
        ImageIO.write(image, "png", new File(path));
    }

    private static String getName(EnumMap<Block, Integer> map, String delimiter) {
        // 使用個数ごとのマップに代入
        Map<Integer, List<Map.Entry<Block, Integer>>> eachCount = new ArrayList<>(map.entrySet()).stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue));

        // キーを多い順にソート
        ArrayList<Integer> keys = new ArrayList<>(eachCount.keySet());
        keys.sort(Comparator.reverseOrder());

        // 文字列に変換
        ArrayList<String> strings = new ArrayList<>();
        for (int count : keys) {
            List<Map.Entry<Block, Integer>> list = eachCount.get(count);
            String blockNames = list.stream()
                    .map(Map.Entry::getKey)
                    .map(Block::getName)
                    .collect(Collectors.joining());
            strings.add(blockNames + "x" + count);
        }

        // 文字列を連結する
        return strings.stream()
                .collect(Collectors.joining(delimiter));
    }

    private static EnumMap<Block, EnumMap<Rotate, ArrayList<HashMap<Long, List<Delta>>>>> calcMinoMap(MinoFactory minoFactory, MinoShifter minoShifter, int height) {
        // 分割されたミノ一覧
        SizedBit sizedBit = new SizedBit(1, height);
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);

        // マップにする
        EnumMap<Block, EnumMap<Rotate, ArrayList<HashMap<Long, List<Delta>>>>> minoMap = new EnumMap<>(Block.class);
        for (SeparableMino separableMino : separableMinos.getMinos()) {
            ColumnField field = separableMino.getField();
            Mino mino = separableMino.getMino();
            int x = separableMino.getX();
            int y = separableMino.getLowerY() - mino.getMinY();

            // 4x4の範囲にあるブロックをみつけて、回転軸からの差分を計算
            ArrayList<Delta> deltas = new ArrayList<>();
            for (int yIndex = 0; yIndex < height; yIndex++)
                for (int xIndex = 0; xIndex < 4; xIndex++)
                    if (!field.isEmpty(xIndex, yIndex, height))
                        deltas.add(new Delta(xIndex - x, yIndex - y));
            assert deltas.size() == 4 : deltas;

            // Block -> Rotate
            Block block = mino.getBlock();
            EnumMap<Rotate, ArrayList<HashMap<Long, List<Delta>>>> rotateMap = minoMap.computeIfAbsent(block, (key -> new EnumMap<>(Rotate.class)));

            // Rotate -> height
            Rotate rotate = mino.getRotate();
            ArrayList<HashMap<Long, List<Delta>>> heightList = rotateMap.computeIfAbsent(rotate, key -> {
                ArrayList<HashMap<Long, List<Delta>>> list = new ArrayList<>();
                for (int yIndex = 0; yIndex < height; yIndex++)
                    list.add(new HashMap<>());
                return list;
            });

            // height -> deleteKey
            HashMap<Long, List<Delta>> deleteMap = heightList.get(y);

            // 追加する
            deleteMap.put(separableMino.getDeleteKey(), deltas);
        }
        return minoMap;
    }

    private static void createEmptyPages() throws IOException {
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
        System.out.println(s);

        System.out.println("-> " + ((double) s / (maxX + 1)));

        //　白紙のページを作成
        int widthSize = (FIELD_WIDTH_SIZE + FIELD_WIDTH_MARGIN) * FIELD_COLUMN_COUNT;
        int heightSize = (FIELD_HEIGHT_SIZE + FIELD_HEIGHT_MARGIN) * FIELD_ROW_COUNT;

        System.out.printf("%dx%d: %.3f%n", widthSize, heightSize, (double) widthSize / heightSize);

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

    private static Color getNormalColor(Block key) {
        switch (key) {
            case I:
                return new Color(0x01BDFF);
            case L:
                return new Color(0xFC9826);
            case J:
                return new Color(0x366CE8);
            case S:
                return new Color(0x7CCF00);
            case Z:
                return new Color(0xFF3863);
            case O:
                return new Color(0xFFCF33);
            case T:
                return new Color(0xD43FBD);
            default:
                return Color.gray;
        }
    }


    private static Color getStrongColor(Block key) {
        switch (key) {
            case I:
                return new Color(0x02ADE6);
            case L:
                return new Color(0xE88B27);
            case J:
                return new Color(0x3063C9);
            case S:
                return new Color(0x73BF00);
            case Z:
                return new Color(0xED345F);
            case O:
                return new Color(0xEDBE2F);
            case T:
                return new Color(0xC43DB0);
            default:
                return Color.gray;
        }
    }

    private static class PatternFile {
        private final Path path;
        private final int index;
        private final Color background;

        private PatternFile(String path, int index, Color background) {
            this(Paths.get(path), index, background);
        }

        private PatternFile(Path path, int index, Color background) {
            this.path = path;
            this.index = index;
            this.background = background;
        }

        @Override
        public String toString() {
            return "PatternFile{" +
                    "path='" + path + '\'' +
                    ", index=" + index +
                    '}';
        }
    }

    private static class TaskData {
        private final int x;
        private final int y;

        private TaskData(int left, int top, int x, int y) {
            this.x = left + x * (BLOCK_SIZE + BLOCK_BOARDER);
            this.y = top + (BLOCK_HEIGHT_COUNT - y - 1) * (BLOCK_SIZE + BLOCK_BOARDER);
        }

        private TaskData(int left, int top) {
            this.x = left;
            this.y = top;
        }
    }

    private static class Delta {
        private final int x;
        private final int y;

        private Delta(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return String.format("(%d, %d)", x, y);
        }
    }

    private static class Prev {
        private final Field current;
        private final boolean flag;

        private Prev(Field current) {
            this(current, false);
        }

        private Prev(Field current, boolean flag) {
            this.current = current;
            this.flag = flag;
        }

        public SmallField getRange() {
            long currentBoard = current.getBoard(0);
            long board = currentBoard << 10 | currentBoard >>> 10;

            Field left1 = current.freeze(4);
            left1.slideLeft(1);
            board |= left1.getBoard(0);

            Field right1 = current.freeze(4);
            right1.slideRight(1);
            board |= right1.getBoard(0);

            return new SmallField(board);
        }
    }

    private static class Square {
        private final BlockCounter blockCounter;
        private final int solutionCount;
        private final int width;
        private final int height;

        public Square(BlockCounter blockCounter, int solutionCount, int width, int height) {
            this.blockCounter = blockCounter;
            this.solutionCount = solutionCount;
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "Square{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    private static class FixSquare {
        private final Square square;
        private final int x;
        private final int y;

        public FixSquare(Square square, int x, int y) {
            this.square = square;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "FixSquare{" +
                    "x=" + x +
                    ", y=" + y +
                    ", square=" + square +
                    '}';
        }
    }
}
