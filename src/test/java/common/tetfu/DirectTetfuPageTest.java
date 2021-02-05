package common.tetfu;

import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class DirectTetfuPageTest {
    @Test
    void test1() {
        ColoredField coloredField = ColoredFieldFactory.createColoredField("______IIII");
        TetfuPage page = new DirectTetfuPage(
                ColorType.I, 3, 0, Rotate.Left, "test", coloredField, true, true, false, false, Collections.emptyList()
        );
        assertThat(page)
                .returns(ColorType.I, TetfuPage::getColorType)
                .returns(3, TetfuPage::getX)
                .returns(0, TetfuPage::getY)
                .returns("test", TetfuPage::getComment)
                .returns(coloredField, TetfuPage::getField)
                .returns(true, TetfuPage::isPutMino)
                .returns(true, TetfuPage::isLock);
    }
}