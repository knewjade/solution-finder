package common.tetfu;

import common.tetfu.common.ActionFlags;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.common.Coordinate;
import common.tetfu.decorder.ActionDecoder;
import common.tetfu.decorder.CommentDecoder;
import common.tetfu.encorder.ActionEncoder;
import common.tetfu.encorder.CommentEncoder;
import common.tetfu.encorder.FieldEncoder;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.Rotate;
import exceptions.FinderParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static common.tetfu.TetfuTable.ENCODE_TABLE_SIZE;
import static common.tetfu.TetfuTable.decodeData;

public class Tetfu {
    public static final int TETFU_FIELD_TOP = 23;
    public static final int TETFU_MAX_HEIGHT = TETFU_FIELD_TOP + 1;

    public static final int TETFU_FIELD_WIDTH = 10;
    public static final int TETFU_FIELD_BLOCKS = TETFU_MAX_HEIGHT * TETFU_FIELD_WIDTH;
    private static final int FILED_WIDTH = 10;

    public static String removeDomainData(String str) {
        String regex = "[vmd][0-9]{3}@[a-zA-Z0-9?+/]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            return matcher.group(0);
        }
        return str;
    }

    public static boolean isDataLater115(String data) {
        return removePrefixData(data) != null;
    }

    public static String removePrefixData(String data) {
        if (data.startsWith("v115@") || data.startsWith("d115@") || data.startsWith("m115@"))
            return data.substring(5);
        return null;
    }

    public static String encodeForQuiz(List<Piece> orders) {
        return encodeForQuiz(orders, orders.get(0));
    }

    // 入力フィールドの高さは23, 幅は10
    public static String encodeForQuiz(List<Piece> orders, Piece firstPiece) {
        List<Piece> minos = new ArrayList<>(orders);

        // 最初のツモを決める
        Piece hold = null;
        if (minos.get(0) != firstPiece)
            hold = minos.remove(0);

        Piece current = minos.get(0);
        minos = minos.subList(1, minos.size());

        // 文字列に変換
        String names = minos.stream().map(Piece::getName).collect(Collectors.joining());
        return String.format("#Q=[%s](%s)%s", hold != null ? hold.getName() : "", current.getName(), names);
    }

    private final List<Integer> encodedValues = new ArrayList<>();
    private final MinoFactory minoFactory;
    private final ColorConverter converter;

    private int lastRepeatIndex = -1;

    public Tetfu(MinoFactory minoFactory, ColorConverter converter) {
        this.minoFactory = minoFactory;
        this.converter = converter;
    }

    // コメント・フィールドは初期設定のみ設定可能
    public String encode(List<TetfuElement> elements) {
        // 空のページを作成する
        if (elements.isEmpty()) {
            elements = Collections.singletonList(new TetfuElement(""));
        }
        return encodeInner(elements);
    }

    private String encodeInner(List<TetfuElement> elements) {
        ColoredField prevField = ColoredFieldFactory.createField(TETFU_MAX_HEIGHT);
        List<Integer> prevBlockUp = createBlockUp();
        String prevComment = "";

        for (int index = 0; index < elements.size(); index++) {
            TetfuElement element = elements.get(index);
            ColoredField field = element.getField().isPresent()
                    ? element.getField().get().freeze()
                    : prevField;
            List<Integer> blockUp = element.getBlockUpList().orElse(prevBlockUp);

            // field settings
            // prevFieldは、ひとつ前のミノを置いてできたフィールド
            // fieldは次に表示させたいフィールド。今回は、最初をのぞいてひとつ前のミノを置いてできたフィールドをそのまま利用
            encodeField(prevField, prevBlockUp, field, blockUp);

            String comment = element.getEscapedComment();
            ActionFlags flags = new ActionFlags(comment, prevComment, index, element);
            parseAction(element, flags);

            ColorType colorType = element.getColorType();
            if (flags.isLock) {
                if (ColorType.isMinoBlock(colorType)) {
                    Piece piece = converter.parseToBlock(colorType);
                    Mino mino = minoFactory.create(piece, element.getRotate());
                    field.putMino(mino, element.getX(), element.getY());
                }

                field.clearLine();

                if (flags.isBlockUp) {
                    field.blockUp();
                    for (int x = 0; x < TETFU_FIELD_WIDTH; x++)
                        field.setBlockNumber(x, 0, element.getBlockUp(x));
                }

                if (flags.isMirror) {
                    field.mirror();
                }
            }
            // next field
            prevField = field;
            prevBlockUp = blockUp;
            prevComment = comment;
        }

        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < encodedValues.size(); index++) {
            Integer value = encodedValues.get(index);
            String encoded = TetfuTable.encodeData(value);
            builder.append(encoded);
            if (index % 47 == 41)
                builder.append('?');
        }

        return builder.toString();
    }

    private List<Integer> createBlockUp() {
        return IntStream.range(0, 10).mapToObj(i -> 0).collect(Collectors.toList());
    }

    private void encodeField(
            ColoredField prevField, List<Integer> prevBlockUp,
            ColoredField currentField, List<Integer> currentBlockUp
    ) {
        FieldEncoder encoder = new FieldEncoder(prevField, prevBlockUp, currentField, currentBlockUp);
        boolean isChanged = encoder.encode();

        if (isChanged) {
            // フィールドを記録して、リピートを終了する
            encodedValues.addAll(encoder.getEncodedValues());
            lastRepeatIndex = -1;
        } else if (lastRepeatIndex < 0 || encodedValues.get(lastRepeatIndex) == ENCODE_TABLE_SIZE - 1) {
            // フィールドを記録して、リピートを開始する
            encodedValues.addAll(encoder.getEncodedValues());
            encodedValues.add(0);
            lastRepeatIndex = this.encodedValues.size() - 1;
        } else if (encodedValues.get(lastRepeatIndex) < (ENCODE_TABLE_SIZE - 1)) {
            // フィールドは記録せず、リピートを進める
            Integer currentRepeatValue = encodedValues.get(lastRepeatIndex);
            encodedValues.set(lastRepeatIndex, currentRepeatValue + 1);
        }
    }

    private void parseAction(TetfuElement element, ActionFlags flags) {
        ActionEncoder actionEncoder = new ActionEncoder(element, flags);

        boolean isCommented = actionEncoder.encode();

        encodedValues.addAll(actionEncoder.getEncodedValues());

        if (isCommented) {
            CommentEncoder commentEncoder = new CommentEncoder(flags.escapedComment);
            commentEncoder.encode();
            encodedValues.addAll(commentEncoder.getEncodedValues());
        }
    }

    public List<TetfuPage> decode(String str) throws FinderParseException {
        try {
            return decodeMain(str);
        } catch (Exception e) {
            throw new FinderParseException("Cannot parse tetfu: " + str, e);
        }
    }

    private List<TetfuPage> decodeMain(String str) {
        LinkedList<Integer> values = str.replace("?", "").chars().boxed()
                .map(c -> decodeData((char) c.intValue()))
                .collect(Collectors.toCollection(LinkedList::new));

        ArrayList<TetfuPage> pages = new ArrayList<>();

        ColoredField prevField = ColoredFieldFactory.createField(TETFU_MAX_HEIGHT);
        ColoredField currentField = ColoredFieldFactory.createField(TETFU_MAX_HEIGHT);

        int[] blockUp = new int[FILED_WIDTH];
        int repeatCount = -1;
        while (!values.isEmpty()) {
            if (repeatCount <= 0) {
                int index = 0;
                boolean isChange = false;
                while (index < TETFU_FIELD_BLOCKS) {
                    int diffBlock = pollValues(values, 2);
                    int diff = diffBlock / TETFU_FIELD_BLOCKS;
                    int block = diffBlock % TETFU_FIELD_BLOCKS;
                    if (block != TETFU_FIELD_BLOCKS - 1)
                        isChange = true;

                    for (int b = 0; b < block + 1; b++) {
                        int x = index % 10;
                        int y = TETFU_FIELD_TOP - (index / 10) - 1;
                        if (0 <= y) {
                            int prevBlockNumber = prevField.getBlockNumber(x, y);
                            currentField.setBlockNumber(x, y, diff + prevBlockNumber - 8);
                        } else {
                            blockUp[x] += diff - 8;
                        }

                        index += 1;
                    }
                }
                if (!isChange)
                    repeatCount = pollValues(values, 1);
            } else {
                currentField = prevField;
                repeatCount -= 1;
            }

            int action = pollValues(values, 3);
            ActionDecoder actionDecoder = new ActionDecoder(action);

            String escapedComment = "";
            if (actionDecoder.isComment) {
                List<Integer> commentValues = new ArrayList<>();
                int commentLength = pollValues(values, 2);
                for (int commentCounter = 0; commentCounter < (commentLength + 3) / 4; commentCounter++) {
                    int commentValue = pollValues(values, 5);
                    commentValues.add(commentValue);
                }
                CommentDecoder commentDecoder = new CommentDecoder(commentLength, commentValues);
                escapedComment = commentDecoder.getEscapedComment();
            }

            TetfuPage tetfuPage = new DecodedTetfuPage(actionDecoder, escapedComment, currentField, blockUp);
            pages.add(tetfuPage);

            ColorType colorType = actionDecoder.colorType;
            if (actionDecoder.isLock) {
                if (ColorType.isMinoBlock(colorType)) {
                    Rotate rotate = actionDecoder.rotate;
                    Coordinate coordinate = actionDecoder.coordinate;

                    Piece piece = converter.parseToBlock(colorType);
                    Mino mino = minoFactory.create(piece, rotate);

                    currentField.putMino(mino, coordinate.x, coordinate.y);
                }

                currentField.clearLine();

                if (actionDecoder.isBlockUp) {
                    currentField.blockUp();
                    for (int x = 0; x < TETFU_FIELD_WIDTH; x++)
                        currentField.setBlockNumber(x, 0, blockUp[x]);
                }

                if (actionDecoder.isMirror)
                    currentField.mirror();
            }

            prevField = currentField;
        }

        return pages;
    }

    private int pollValues(LinkedList<Integer> values, int splitCount) {
        int value = 0;
        for (int count = 0; count < splitCount; count++) {
            Integer v = values.pollFirst();
            if (v == null) {
                throw new IllegalStateException("Next value does not exist");
            }
            value += v * Math.pow(ENCODE_TABLE_SIZE, count);
        }
        return value;
    }
}


