package _usecase.util.fig;

import _usecase.ConfigFileHelper;
import _usecase.FigureFileHelper;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

abstract class UtilFigUseCaseBaseTest {
    @BeforeEach
    void setUp() throws IOException {
        ConfigFileHelper.deleteFieldFile();
        FigureFileHelper.deleteGifFile();
    }
}
