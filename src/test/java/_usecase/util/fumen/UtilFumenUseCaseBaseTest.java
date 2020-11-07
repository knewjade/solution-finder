package _usecase.util.fumen;

import _usecase.ConfigFileHelper;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

abstract class UtilFumenUseCaseBaseTest {
    @BeforeEach
    void setUp() throws IOException {
        ConfigFileHelper.deleteFieldFile();
        ConfigFileHelper.deletePatternFile();
    }
}
