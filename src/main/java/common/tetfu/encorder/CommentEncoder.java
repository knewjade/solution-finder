package common.tetfu.encorder;

import common.tetfu.TetfuTable;

import static common.tetfu.TetfuTable.COMMENT_TABLE_SIZE;

public class CommentEncoder extends Encoder {
    private final String escapedComment;

    public CommentEncoder(String escapedComment) {
        this.escapedComment = escapedComment;
    }

    public void encode() {
        int commentLength = escapedComment.length();
        if (4096 <= commentLength)
            commentLength = 4095;

        pushValues(commentLength, 2);

        // コメントを符号化
        for (int index = 0; index < commentLength; index += 4) {
            int value = 0;
            for (int count = 0; count < 4; count++) {
                int newIndex = index + count;
                if (commentLength <= newIndex)
                    break;
                char c = escapedComment.charAt(newIndex);
                value += TetfuTable.encodeCommentChar(c) * (int) Math.pow(COMMENT_TABLE_SIZE, count);
            }

            pushValues(value, 5);
        }
    }
}
