package entry.spin;

import core.field.Field;
import core.neighbor.SimpleOriginalPiece;

import java.util.stream.Stream;

public interface OutputCandidate {
    SimpleOriginalPiece getOperationT();

    // initField + usingField
    Field getAllMergedField();

    // すべてのミノを取得する
    Stream<SimpleOriginalPiece> operationStream();

    // 最終的なフィールドで消去されているライン
    long getAllMergedFilledLine();

    Field getAllMergedFieldWithoutT();

    long getAllMergedFilledLineWithoutT();
}
