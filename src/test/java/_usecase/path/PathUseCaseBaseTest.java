package _usecase.path;

import _usecase.ConfigFileHelper;
import _usecase.path.files.OutputFileHelper;
import common.tetfu.Tetfu;
import common.tetfu.common.ColorConverter;
import common.tetfu.field.ColoredField;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import exceptions.FinderParseException;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

abstract class PathUseCaseBaseTest {
    private static final MinoFactory minoFactory = new MinoFactory();
    private static final ColorConverter converter = new ColorConverter();

    @BeforeEach
    void setUp() throws IOException {
        ConfigFileHelper.deleteFieldFile();
        ConfigFileHelper.deletePatternFile();
        OutputFileHelper.deletePathUniqueHTML();
        OutputFileHelper.deletePathMinimalHTML();
        OutputFileHelper.deletePathUniqueCSV();
        OutputFileHelper.deletePathMinimalCSV();
        OutputFileHelper.deleteErrorText();
    }

    List<ColoredField> parseLastPageTetfu(List<String> fumens) {
        return fumens.stream()
                .map(fumen -> {
                    Tetfu tetfu = new Tetfu(minoFactory, converter);
                    try {
                        return tetfu.decode(fumen);
                    } catch (FinderParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(tetfuPages -> {
                    assert 0 < tetfuPages.size();
                    return tetfuPages.get(tetfuPages.size() - 1);
                })
                .map(page -> {
                    ColoredField field = page.getField();

                    if (!page.isPutMino())
                        return field;

                    Piece piece = converter.parseToBlock(page.getColorType());
                    field.putMino(new Mino(piece, page.getRotate()), page.getX(), page.getY());
                    return field;
                })
                .collect(Collectors.toList());
    }

    boolean isFilled(int height, ColoredField coloredField) {
        for (int y = 0; y < height; y++)
            if (!coloredField.isFilledLine(y))
                return false;
        return true;
    }

    boolean isEmpty(int height, ColoredField coloredField) {
        ColoredField freeze = coloredField.freeze(height);
        freeze.clearLine();
        return freeze.isPerfect();
    }
}
