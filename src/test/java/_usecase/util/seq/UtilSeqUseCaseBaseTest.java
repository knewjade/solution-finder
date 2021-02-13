package _usecase.util.seq;

import _usecase.ConfigFileHelper;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

abstract class UtilSeqUseCaseBaseTest {
    @BeforeEach
    void setUp() throws IOException {
        ConfigFileHelper.deleteFieldFile();
        ConfigFileHelper.deletePatternFile();
    }
}
