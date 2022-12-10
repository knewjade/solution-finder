package core.srs;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.Piece;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinoRotationDetailTest {
    @Test
    void caseDouble() {
        MinoFactory minoFactory = new MinoFactory();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        MinoRotationDetail detail = new MinoRotationDetail(minoFactory, minoRotation);

        Field field = FieldFactory.createField("" +
                "X__XXXXXXX" +
                "X___XXXXXX" +
                "XX_XXXXXXX"
        );
        SpinResult result = detail.getKicks(field, RotateDirection.Right, minoFactory.create(Piece.T, Rotate.Right), 1, 2);

        assertThat(result)
                .returns(2, SpinResult::getToX)
                .returns(1, SpinResult::getToY)
                .returns(RotateDirection.Right, SpinResult::getDirection)
                .returns(Rotate.Reverse, SpinResult::getToRotate)
                .returns(2, SpinResult::getTestPatternIndex);
    }

    @Test
    void caseMini() {
        MinoFactory minoFactory = new MinoFactory();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        MinoRotationDetail detail = new MinoRotationDetail(minoFactory, minoRotation);

        Field field = FieldFactory.createField("" +
                "__________" +
                "XXXXXXXXX_" +
                "XXXXXXXXX_"
        );
        SpinResult result = detail.getKicks(field, RotateDirection.Left, minoFactory.create(Piece.T, Rotate.Spawn), 8, 2);

        assertThat(result)
                .returns(9, SpinResult::getToX)
                .returns(2, SpinResult::getToY)
                .returns(RotateDirection.Left, SpinResult::getDirection)
                .returns(Rotate.Left, SpinResult::getToRotate)
                .returns(1, SpinResult::getTestPatternIndex);
    }

    @Test
    void caseTripe() {
        MinoFactory minoFactory = new MinoFactory();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        MinoRotationDetail detail = new MinoRotationDetail(minoFactory, minoRotation);

        Field field = FieldFactory.createField("" +
                "XXXXX_____" +
                "XXXX______" +
                "XXXX_XXXXX" +
                "XXXX__XXXX" +
                "XXXX_XXXXX"
        );
        SpinResult result = detail.getKicks(field, RotateDirection.Right, minoFactory.create(Piece.T, Rotate.Spawn), 5, 3);

        assertThat(result)
                .returns(4, SpinResult::getToX)
                .returns(1, SpinResult::getToY)
                .returns(RotateDirection.Right, SpinResult::getDirection)
                .returns(Rotate.Right, SpinResult::getToRotate)
                .returns(4, SpinResult::getTestPatternIndex);
    }

    @Test
    void caseNeo() {
        MinoFactory minoFactory = new MinoFactory();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        MinoRotationDetail detail = new MinoRotationDetail(minoFactory, minoRotation);

        Field field = FieldFactory.createField("" +
                "___XXXXXXX" +
                "_____XXXXX" +
                "____XXXXXX" +
                "XX__XXXXXX" +
                "XXX_XXXXXX"
        );
        SpinResult result = detail.getKicks(field, RotateDirection.Right, minoFactory.create(Piece.T, Rotate.Reverse), 3, 3);

        assertThat(result)
                .returns(3, SpinResult::getToX)
                .returns(1, SpinResult::getToY)
                .returns(RotateDirection.Right, SpinResult::getDirection)
                .returns(Rotate.Left, SpinResult::getToRotate)
                .returns(3, SpinResult::getTestPatternIndex);
    }

    @Test
    void caseFin() {
        MinoFactory minoFactory = new MinoFactory();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        MinoRotationDetail detail = new MinoRotationDetail(minoFactory, minoRotation);

        Field field = FieldFactory.createField("" +
                "XXXXXX____" +
                "XXXX______" +
                "XXXX______" +
                "XXXX__XXXX" +
                "XXXX_XXXXX"
        );
        SpinResult result = detail.getKicks(field, RotateDirection.Left, minoFactory.create(Piece.T, Rotate.Reverse), 5, 3);

        assertThat(result)
                .returns(4, SpinResult::getToX)
                .returns(1, SpinResult::getToY)
                .returns(RotateDirection.Left, SpinResult::getDirection)
                .returns(Rotate.Right, SpinResult::getToRotate)
                .returns(4, SpinResult::getTestPatternIndex);
    }

    @Test
    void caseIso() {
        MinoFactory minoFactory = new MinoFactory();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        MinoRotationDetail detail = new MinoRotationDetail(minoFactory, minoRotation);

        Field field = FieldFactory.createField("" +
                "XXXXXXX___" +
                "XXXX______" +
                "XXXXX_____" +
                "XXXX__XXXX" +
                "XXXXX_XXXX"
        );
        SpinResult result = detail.getKicks(field, RotateDirection.Right, minoFactory.create(Piece.T, Rotate.Reverse), 5, 3);

        assertThat(result)
                .returns(5, SpinResult::getToX)
                .returns(1, SpinResult::getToY)
                .returns(RotateDirection.Right, SpinResult::getDirection)
                .returns(Rotate.Left, SpinResult::getToRotate)
                .returns(3, SpinResult::getTestPatternIndex);
    }
}