package tetfu.encorder;

import tetfu.TetfuTable;

import static tetfu.TetfuTable.COMMENT_TABLE_SIZE;

public class CommentEncoder extends Encoder {
    private final String escapedComment;

    public CommentEncoder(String escapedComment) {
        this.escapedComment = escapedComment;
    }

    public void encod() {
        int commentLength = escapedComment.length();

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
