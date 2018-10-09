package _usecase.ren;

import _usecase.ConfigFileHelper;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

abstract class RenUseCaseBaseTest {
    @BeforeEach
    void setUp() throws IOException {
        ConfigFileHelper.deleteFieldFile();
        ConfigFileHelper.deletePatternFile();
    }
}
