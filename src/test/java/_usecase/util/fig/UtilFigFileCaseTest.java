package _usecase.util.fig;

import _usecase.ConfigFileHelper;
import _usecase.FigureFileHelper;
import _usecase.RunnerHelper;
import com.google.common.io.ByteSource;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class UtilFigFileCaseTest extends UtilFigUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void useFileCase1() throws Exception {
        // オプションなし

        ConfigFileHelper.createFieldFile("v115@vhIKJYzAFLDmClcJSAVDEHBEooRBPoAVBsHMMC0vyt?C6eNPCMe/wCM9aFDzn9wCv/7LCz3fBA0qBvrBxxBlgBOaBT?oBTnBshBBgh0Heg0EeAtCeg0AeywBtCeglBewwQ4AtTpAPg?HBtR4TpAPglAeBtQ4wSyhgHA8SpBtC8gWB8QpwhAtzwgWA8?BtxhzwgWg0A8BtwhT4Je3DBvhDZ5AWPBtQB5MBTfwhHexhH?exhEeh0AexhR4Ceg0BewhR4Atg0BehlAeAtQpQ4zhAeB8R4?E8AeB8R4E8gHglA8R4xhC8gHB8Q4xhAtglB8h0A8BtwhR4Q?aQ4A8GeAAAeA8GeAAAeA8GeAAAeA8GeAAKe+7AvhJq1AdzA?q0ATpAM2AXsAMyAJwANyAT0AwwCeRpwhCexwTpwhAeR4g0B?ewhR4Ati0hlAeBtQ4zhAeI8AeI8AeI8AeQ8AeI8AeA8QpC8?xwQ4A8AeA8RpzwQ4A8wDwhglB8Q4xhAtglgHglh0A8APAtw?hT4A8BeAAFeA8BeAAaeAAFeA8BeAAFeA8BeAAFeA8IeA8AA?HeA8AAJeAAA");

        String command = "util fig -c fumen";
        RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        ByteSource actual = FigureFileHelper.loadGifByteSource();
        ByteSource expect = FigureFileHelper.loadResourceByteSource(UtilFigResources.FileCase1);

        assertThat(actual.contentEquals(expect)).isTrue();
    }

    @Test
    void useFileCase2() throws Exception {
        // オプション: next, delay, frame:no

        ConfigFileHelper.createFieldFile("http://fumen.zui.jp/?v115@vhOKJY5AFLDmClcJSAVDEHBEooRBMoAVBJ3TxC6f3L?CzCmFDv/9tCMnbMCzHLWCp/NMCzXMgC0vTxCpAAAARwBTtB?/sBUmBFqBWyBmnB/pBaqBpjBlrB0lBTfBOjBngh0RpFeg0w?hRpBtEeQLAtQaglBtAeh0BeBtgWAPywg0BewhgWg0BtA8il?Q4whwDg0A8SphlQ4Je/YBvhGCPBlcBTcBUSBPTBpKBzOBYf?whIewhEeQ4AeRpwhCeBtR4RpwhDeBtQ4ilDeRpQ4glg0wwR?pBeRpR4g0AeR4glAewwgWwwRpAeA8AeA8BtxhxwQ4A8AeB8?Btwhi0C8Aexwwhg0hlxwA8AexwxhglQ4xhg0AeSphlQ4AeA?8AeAAGeA8AeAAPeizAvhBcwAOsADfh0GeAtAeQLFeAtEeil?Q4AeQawSDehlAeQpEegWAPAewSAeRawhDeRaBeBtRpEewwA?eQLBtglAehlgWQaBewhwDg0A8SphlQ4KeA8AeAAweA8CeAA?Le9CBvhChBB30A8rAleAtHeBtHeAth0EeR4Atg0whDeR4Bt?glwhBeilQ4AtRawhBeglBtR4QawSQLCewwQpAPAegWAewwA?8AeB8xhBtglQ4A8Aei0whAtxwQ4B8g0APAtxhxwQ4C8QLxw?whg0hlAeA8AeAAGeA8AeAASeA8CeAAEeA8BeAAFeA8BeAAF?eA8BeAAIeAAA8Le6NAGeBtGegWglh0EeR4glg0whDeR4Ath?lwhBeilQ4AtRawhBeglBtR4QawSQLCewwQpAPAegWAewwA8?AeB8xhBtglQ4A8Aei0whAtxwQ4B8g0APAtxhxwQ4C8QLxww?hg0hlAeA8AeAAGeA8AeAASeA8CeAAEeA8BeAAFeA8BeAAFe?A8BeAAIeAAA8BeAAEeA8DeAADeA8DeAADeA8DeAADeA8MeA?AA", "test_field", "input");

        String command = "util fig -c fumen -n 3 -d 24 -f no -fp input/test_field.txt";
        RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        ByteSource actual = FigureFileHelper.loadGifByteSource();
        ByteSource expect = FigureFileHelper.loadResourceByteSource(UtilFigResources.FileCase2);

        assertThat(actual.contentEquals(expect)).isTrue();
    }
}
