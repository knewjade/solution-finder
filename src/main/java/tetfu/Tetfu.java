package tetfu;

import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import tetfu.common.ActionFlags;
import tetfu.common.ColorConverter;
import tetfu.common.ColorType;
import tetfu.common.Coordinate;
import tetfu.decorder.ActionDecoder;
import tetfu.decorder.CommentDecoder;
import tetfu.encorder.ActionEncoder;
import tetfu.encorder.CommentEncoder;
import tetfu.encorder.FieldEncoder;
import tetfu.field.ColoredField;
import tetfu.field.ColoredFieldFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static tetfu.TetfuTable.ENCODE_TABLE_SIZE;
import static tetfu.TetfuTable.decodeData;

public class Tetfu {
    public static final int TETFU_FIELD_TOP = 23;
    public static final int TETFU_MAX_HEIGHT = TETFU_FIELD_TOP + 1;

    public static final int TETFU_FIELD_WIDTH = 10;
    public static final int TETFU_FIELD_BLOCKS = TETFU_MAX_HEIGHT * TETFU_FIELD_WIDTH;
    private static final int FILED_WIDTH = 10;

    public static String encodeForQuiz(List<Block> orders) {
        return encodeForQuiz(orders, orders.get(0));
    }

    // 入力フィールドの高さは23, 幅は10
    public static String encodeForQuiz(List<Block> orders, Block firstBlock) {
        List<Block> minos = new ArrayList<>(orders);

        // 最初のツモを決める
        Block hold = null;
        if (minos.get(0) != firstBlock)
            hold = minos.remove(0);

        Block current = minos.get(0);
        minos = minos.subList(1, minos.size());

        // 文字列に変換
        String names = minos.stream().map(Block::getName).collect(Collectors.joining());
        return String.format("#Q=[%s](%s)%s", hold != null ? hold.getName() : "", current.getName(), names);
    }

    private static String escapeComment(String comment) {
        String escape = TetfuTable.escape(comment);
        if (4095 <= escape.length())
            throw new UnsupportedOperationException("Escaped comment is less than 4095");
        return escape;
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
    public String encode(ColoredField initField, List<TetfuElement> elements) {
        ColoredField field = initField.freeze(TETFU_MAX_HEIGHT);
        ColoredField prevField = ColoredFieldFactory.createField(TETFU_MAX_HEIGHT);
        String prevComment = "";

        for (int index = 0; index < elements.size(); index++) {
            // field settings
            // prevFieldは、ひとつ前のミノを置いてできたフィールド
            // fieldは次に表示させたいフィールド。今回は、最初をのぞいてひとつ前のミノを置いてできたフィールドをそのまま利用
            encodeField(prevField, field);

            TetfuElement element = elements.get(index);
            String comment = element.getEscapedComment();
            ActionFlags flags = new ActionFlags(comment, prevComment, index);
            parseAction(element, flags);

            ColorType colorType = element.getColorType();
            if (flags.isLock && ColorType.isBlock(colorType)) {
                Block block = converter.parseToBlock(colorType);
                Mino mino = minoFactory.create(block, element.getRotate());

                field.putMino(mino, element.getX(), element.getY());
                field.clearLine();

                if (flags.isBlockUp) {
                    throw new UnsupportedOperationException();
//                    currentField.blockUp();
//                    for (int x = 0; x < TETFU_FIELD_WIDTH; x++)
//                        currentField.setBlockNumber(x, 0, blockUp[x]);
                }

                if (flags.isMirror) {
                    throw new UnsupportedOperationException();
//                    currentField.mirror();
                }
            }
            // next field
            prevField = field;
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

    private void encodeField(ColoredField prevField, ColoredField currentField) {
        FieldEncoder encoder = new FieldEncoder(prevField, currentField);
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
            commentEncoder.encod();
            encodedValues.addAll(commentEncoder.getEncodedValues());
        }
    }

    List<TetfuPage> decode(String str) {
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

            TetfuPage tetfuPage = new TetfuPage(actionDecoder, escapedComment, currentField);
            pages.add(tetfuPage);

            ColorType colorType = actionDecoder.colorType;
            if (actionDecoder.isLock && ColorType.isBlock(colorType)) {
                Rotate rotate = actionDecoder.rotate;
                Coordinate coordinate = actionDecoder.coordinate;

                Block block = converter.parseToBlock(colorType);
                Mino mino = minoFactory.create(block, rotate);

                currentField.putMino(mino, coordinate.x, coordinate.y);
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
            int v = values.pollFirst();
            value += v * Math.pow(ENCODE_TABLE_SIZE, count);
        }
        return value;
    }
}


