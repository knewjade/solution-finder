package _usecase.sequence;

import _usecase.ConfigFileHelper;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

abstract class SequenceUseCaseBaseTest {
    @BeforeEach
    void setUp() throws IOException {
        ConfigFileHelper.deleteFieldFile();
        ConfigFileHelper.deletePatternFile();
    }
}
