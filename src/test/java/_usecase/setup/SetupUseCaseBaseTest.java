package _usecase.setup;

import _usecase.ConfigFileHelper;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

abstract class SetupUseCaseBaseTest {
    @BeforeEach
    void setUp() throws IOException {
        ConfigFileHelper.deleteFieldFile();
        ConfigFileHelper.deletePatternFile();
    }
}
