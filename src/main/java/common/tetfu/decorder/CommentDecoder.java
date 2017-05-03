package common.tetfu.decorder;

import common.tetfu.TetfuTable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static common.tetfu.TetfuTable.COMMENT_TABLE_SIZE;

public class CommentDecoder {
    private final String escapedComment;

    public CommentDecoder(int commentLength, List<Integer> values) {
        String escapedComment = values.stream()
                .flatMap(value -> {
                    ArrayList<Integer> chars = new ArrayList<>();
                    for (int count = 0; count < 4; count++) {
                        int currentChar = value % COMMENT_TABLE_SIZE;
                        chars.add(currentChar);
                        value /= COMMENT_TABLE_SIZE;
                    }
                    return chars.stream();
                })
                .limit(commentLength)
                .map(TetfuTable::decodeCommentChar)
                .map(String::valueOf)
                .collect(Collectors.joining());

        this.escapedComment = escapedComment;
    }

    public String getEscapedComment() {
        return escapedComment;
    }
}
