package tetfu.decorder;

import tetfu.TetfuTable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static tetfu.TetfuTable.COMMENT_TABLE_SIZE;

public class CommentDecoder {
    private final String comment;

    public CommentDecoder(List<Integer> values) {
        String comment = values.stream()
                .flatMap(value -> {
                    ArrayList<Integer> chars = new ArrayList<>();
                    for (int count = 0; count < 4; count++) {
                        int currentChar = value % COMMENT_TABLE_SIZE;
                        chars.add(currentChar);
                        value /= COMMENT_TABLE_SIZE;
                    }
                    return chars.stream();
                })
                .map(TetfuTable::decodeCommentChar)
                .map(String::valueOf)
                .collect(Collectors.joining());

        this.comment = comment;
    }

    public String getComment() {
        return TetfuTable.escape(comment);
    }
}
