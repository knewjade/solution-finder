package _usecase.path;

import _usecase.*;
import _usecase.path.files.OutputFileHelper;
import _usecase.path.files.PathCSV;
import common.datastore.Operation;
import common.datastore.Operations;
import common.datastore.SimpleOperation;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Piece;
import core.mino.Mino;
import core.srs.Rotate;
import entry.EntryPointMain;
import helper.CSVStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class PathCSVCaseTest extends PathUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void none1() throws Exception {
        // フォーマットをCSV

            /*
            comment: 4 -p I,*p6
            X_________
            XXXXX_____
            XXXXX_____
            XXXXX_____
             */

        String tetfu = "v115@9gA8IeE8EeE8EeE8OeAgWQA0no2ANI98AwN88AjPEN?B";

        int height = 4;
        ConfigFileHelper.createFieldFile(FieldFactory.createField(height), height);
        ConfigFileHelper.createPatternFile("*p2");

        String command = String.format("path -t %s -f csv -k none", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getOutput())
                .contains("I,*p6")
                .contains(Messages.clearLine(4))
                .contains(Messages.uniqueCount(186))
                .contains(Messages.minimalCount(142))
                .contains(Messages.useHold());

        assertThat(log.getReturnCode()).isEqualTo(0);

        Field field = FieldFactory.createField("" +
                "X_________" +
                "XXXXX_____" +
                "XXXXX_____" +
                "XXXXX_____"
        );

        // unique
        PathCSV uniqueCSV = OutputFileHelper.loadPathUniqueNoneCSV();
        assertThat(uniqueCSV.operations().stream()
                .map(operations -> {
                    Field freeze = field.freeze(height);
                    for (Operation operation : operations.getOperations()) {
                        freeze.put(new Mino(operation.getPiece(), operation.getRotate()), operation.getX(), operation.getY());
                        freeze.clearLine();
                    }
                    return freeze;
                }))
                .hasSize(186)
                .allMatch(Field::isPerfect);

        // minimal
        PathCSV minimalCSV = OutputFileHelper.loadPathMinimalNoneCSV();
        assertThat(minimalCSV.operations().stream()
                .map(operations -> {
                    Field freeze = field.freeze(height);
                    for (Operation operation : operations.getOperations()) {
                        freeze.put(new Mino(operation.getPiece(), operation.getRotate()), operation.getX(), operation.getY());
                        freeze.clearLine();
                    }
                    return freeze;
                }))
                .hasSize(142)
                .allMatch(Field::isPerfect);
    }

    @Test
    void none2() throws Exception {
        // フォーマットをCSV

            /*
            comment: 4 -p *p2
            XXXXXXXXX_
            XXXXXXXXX_
            __XXXXXXX_
            __XXXXXXX_
             */

        String tetfu = "v115@9gI8AeI8CeG8CeG8KeAgWMA0no2ANI98AQPk/A";

        int height = 4;
        ConfigFileHelper.createFieldFile(FieldFactory.createField(height), height);
        ConfigFileHelper.createPatternFile("*p2");

        String command = String.format("path -t %s -f csv -L 1", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p2")
                .contains(Messages.clearLine(4))
                .contains(Messages.uniqueCount(1))
                .doesNotContain(Messages.minimalCount())
                .contains(Messages.useHold());

        // unique
        PathCSV uniqueCSV = OutputFileHelper.loadPathUniqueNoneCSV();
        assertThat(uniqueCSV.operations().stream()
                .map(Operations::getOperations))
                .hasSize(1)
                .element(0)
                .isEqualTo(Arrays.<Operation>asList(
                        new SimpleOperation(Piece.I, Rotate.Left, 9, 1),
                        new SimpleOperation(Piece.O, Rotate.Spawn, 0, 0)
                ));
    }

    @Test
    void solution1() throws Exception {
        String fumen = "v115@9gF8DeG8CeH8BeG8MeAgH";

        String command = String.format("path -t %s -p *p3 -f csv -k solution", fumen);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains(Messages.foundPath(18));

        // column: [fumen, use, num-solutions, num-patterns, solutions, patterns]
        CSVStore csvStore = OutputFileHelper.loadPathSolutionCSV();
        assertThat(csvStore.size()).isEqualTo(18);

        assertThat(csvStore.row("fumen", "http://fumen.zui.jp/?v115@9gF8BthlG8ywH8wwglG8BtglJeAgWDA0iDCA"))
                .contains(entry("use", "TLZ"))
                .contains(entry("num-solutions", "1"))
                .contains(entry("num-patterns", "4"))
                .contains(entry("solutions", "TLZ"))
                .matches(map -> count(map.get("patterns"), 4), "fail patterns");

        assertThat(csvStore.row("fumen", "http://fumen.zui.jp/?v115@9gF8zhG8BtglH8BtG8ilJeAgWDA6SdBA"))
                .contains(entry("use", "ILZ"))
                .contains(entry("num-solutions", "3"))
                .contains(entry("num-patterns", "6"))
                .matches(map -> count(map.get("solutions"), 3), "fail solutions")
                .matches(map -> count(map.get("patterns"), 6), "fail patterns");

        assertThat(csvStore.row("fumen", "http://fumen.zui.jp/?v115@9gF8ywwhG8wwg0whH8g0whG8h0whJeAgWDAK+1BA"))
                .contains(entry("use", "TIJ"))
                .contains(entry("num-solutions", "2"))
                .contains(entry("num-patterns", "4"))
                .matches(map -> count(map.get("solutions"), 2), "fail solutions")
                .matches(map -> count(map.get("patterns"), 4), "fail patterns");
    }

    @Test
    void solution1WithoutHold() throws Exception {
        String fumen = "v115@9gF8DeG8CeH8BeG8MeAgH";

        String command = String.format("path -t %s -p *p3 -f csv -k solution -H no", fumen);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains(Messages.foundPath(18));

        // column: [fumen, use, num-solutions, num-patterns, solutions, patterns]
        CSVStore csvStore = OutputFileHelper.loadPathSolutionCSV();
        assertThat(csvStore.size()).isEqualTo(18);

        assertThat(csvStore.row("fumen", "http://fumen.zui.jp/?v115@9gF8BthlG8ywH8wwglG8BtglJeAgWDA0iDCA"))
                .contains(entry("use", "TLZ"))
                .contains(entry("num-solutions", "1"))
                .contains(entry("num-patterns", "1"))
                .contains(entry("solutions", "TLZ"))
                .contains(entry("patterns", "TLZ"));

        assertThat(csvStore.row("fumen", "http://fumen.zui.jp/?v115@9gF8zhG8RpwwH8xwG8RpwwJeAgWDAUXdBA"))
                .contains(entry("use", "TIO"))
                .contains(entry("num-solutions", "3"))
                .contains(entry("num-patterns", "3"))
                .matches(map -> count(map.get("solutions"), 3), "fail solutions")
                .matches(map -> count(map.get("patterns"), 3), "fail patterns");

        assertThat(csvStore.row("fumen", "http://fumen.zui.jp/?v115@9gF8zhG8ywH8wwglG8ilJeAgWDAsedBA"))
                .contains(entry("use", "TIL"))
                .contains(entry("num-solutions", "6"))
                .contains(entry("num-patterns", "6"))
                .matches(map -> count(map.get("solutions"), 6), "fail solutions")
                .matches(map -> count(map.get("patterns"), 6), "fail patterns");
    }

    @Test
    void use1() throws Exception {
        String fumen = "v115@9gF8DeG8CeH8BeG8MeAgH";

        String command = String.format("path -t %s -p *p3 -f csv -k use", fumen);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains(Messages.foundPath(18))
                .contains(Messages.foundPieceCombinations(35));

        // column: [use, num-solutions, num-patterns, fumens, patterns]
        CSVStore csvStore = OutputFileHelper.loadPathUseCSV();
        assertThat(csvStore.size()).isEqualTo(35);

        assertThat(csvStore.row("use", "JSO"))
                .contains(entry("use", "JSO"))
                .contains(entry("num-solutions", "0"))
                .contains(entry("num-patterns", "0"))
                .contains(entry("fumens", ""))
                .contains(entry("patterns", ""));

        assertThat(csvStore.row("use", "TIO"))
                .contains(entry("use", "TIO"))
                .contains(entry("num-solutions", "1"))
                .contains(entry("num-patterns", "6"))
                .contains(entry("fumens", "v115@9gF8zhG8RpwwH8xwG8RpwwJeAgWDAUXdBA"))
                .matches(map -> count(map.get("patterns"), 6), "fail patterns");

        assertThat(csvStore.row("use", "TSZ"))
                .contains(entry("use", "TSZ"))
                .contains(entry("num-solutions", "2"))
                .contains(entry("num-patterns", "4"))
                .matches(map -> count(map.get("fumens"), 2), "fail fumens")
                .matches(map -> count(map.get("patterns"), 4), "fail patterns");
    }

    @Test
    void use1WithoutHold() throws Exception {
        String fumen = "v115@9gF8DeG8CeH8BeG8MeAgH";

        String command = String.format("path -t %s -p *p3 -f csv -k use -H no", fumen);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains(Messages.foundPath(18))
                .contains(Messages.foundPieceCombinations(35));

        // column: [use, num-solutions, num-patterns, fumens, patterns]
        CSVStore csvStore = OutputFileHelper.loadPathUseCSV();
        assertThat(csvStore.size()).isEqualTo(35);

        assertThat(csvStore.row("use", "TJS"))
                .contains(entry("use", "TJS"))
                .contains(entry("num-solutions", "0"))
                .contains(entry("num-patterns", "0"))
                .contains(entry("fumens", ""))
                .contains(entry("patterns", ""));

        assertThat(csvStore.row("use", "ILZ"))
                .contains(entry("use", "ILZ"))
                .contains(entry("num-solutions", "1"))
                .contains(entry("num-patterns", "3"))
                .contains(entry("fumens", "v115@9gF8zhG8BtglH8BtG8ilJeAgWDA6SdBA"))
                .matches(map -> count(map.get("patterns"), 3), "fail patterns");

        assertThat(csvStore.row("use", "TIJ"))
                .contains(entry("use", "TIJ"))
                .contains(entry("num-solutions", "2"))
                .contains(entry("num-patterns", "5"))
                .matches(map -> count(map.get("fumens"), 2), "fail fumens")
                .matches(map -> count(map.get("patterns"), 5), "fail patterns");
    }

    @Test
    void pattern1() throws Exception {
        String fumen = "v115@9gF8DeG8CeH8BeG8MeAgH";

        String command = String.format("path -t %s -p *p4 -f csv -k pattern", fumen);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains(Messages.foundPath(18))
                .contains(Messages.success(514, 840));

        // column: [pattern, num-solutions, use, nouse, fumens]
        CSVStore csvStore = OutputFileHelper.loadPathPatternCSV();
        assertThat(csvStore.size()).isEqualTo(840);

        assertThat(csvStore.row("pattern", "JZSO"))
                .contains(entry("num-solutions", "0"))
                .contains(entry("use", ""))
                .contains(entry("nouse", ""))
                .contains(entry("fumens", ""));

        assertThat(csvStore.row("pattern", "STOZ"))
                .contains(entry("num-solutions", "1"))
                .contains(entry("use", "TSZ"))
                .contains(entry("nouse", "O"))
                .contains(entry("fumens", "v115@9gF8BtR4G8R4wwH8xwG8BtwwJeAgWDAUtDCA"));

        assertThat(csvStore.row("pattern", "SLZO"))
                .contains(entry("num-solutions", "2"))
                .contains(entry("use", "LSZ"))
                .contains(entry("nouse", "O"))
                .matches(map -> count(map.get("fumens"), 2), "fail fumens");

        assertThat(csvStore.row("pattern", "TSZL"))
                .contains(entry("num-solutions", "4"))
                .matches(map -> count(map.get("use"), 3), "fail use")
                .matches(map -> count(map.get("nouse"), 3), "fail nouse")
                .matches(map -> count(map.get("fumens"), 4), "fail fumens");
    }

    @Test
    void pattern1WithoutHold() throws Exception {
        String fumen = "v115@9gF8DeG8CeH8BeG8MeAgH";

        String command = String.format("path -t %s -p *p4 -f csv -k pattern -H no", fumen);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains(Messages.foundPath(18))
                .contains(Messages.success(144, 840));

        // column: [pattern, num-solutions, use, nouse, fumens]
        CSVStore csvStore = OutputFileHelper.loadPathPatternCSV();
        assertThat(csvStore.size()).isEqualTo(840);

        assertThat(csvStore.row("pattern", "OSJ"))
                .contains(entry("num-solutions", "0"))
                .contains(entry("use", ""))
                .contains(entry("nouse", ""))
                .contains(entry("fumens", ""));

        assertThat(csvStore.row("pattern", "JIT"))
                .contains(entry("num-solutions", "1"))
                .contains(entry("use", "TIJ"))
                .contains(entry("nouse", ""))
                .contains(entry("fumens", "v115@9gF8ywwhG8wwg0whH8g0whG8h0whJeAgWDAK+1BA"));

        assertThat(csvStore.row("pattern", "TLZ"))
                .contains(entry("num-solutions", "2"))
                .contains(entry("use", "TLZ"))
                .contains(entry("nouse", ""))
                .matches(map -> count(map.get("fumens"), 2), "fail fumens");
    }

    @Test
    void pattern2() throws Exception {
        // reservedオプション
        String fumen = "v115@9gE8DewhF8CewhG8BewhF8CewhJeAgH";

        String command = String.format("path -t %s -p I,*p4 -f csv -k pattern -r yes", fumen);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains(Messages.foundPath(18))
                .contains(Messages.success(514, 840));

        // column: [pattern, num-solutions, use, nouse, fumens]
        CSVStore csvStore = OutputFileHelper.loadPathPatternCSV();
        assertThat(csvStore.size()).isEqualTo(840);

        assertThat(csvStore.row("pattern", "ISZJO"))
                .contains(entry("num-solutions", "0"))
                .contains(entry("use", ""))
                .contains(entry("nouse", ""))
                .contains(entry("fumens", ""));

        assertThat(csvStore.row("pattern", "ILZJT"))
                .contains(entry("num-solutions", "1"))
                .contains(entry("use", "TILZ"))
                .contains(entry("nouse", "J"))
                .contains(entry("fumens", "v115@9gE8ywAtwhF8wwBtwhG8AtglwhF8ilwhJeAgWEAMej?xC"));

        assertThat(csvStore.row("pattern", "ITLIZ"))
                .contains(entry("num-solutions", "5"))
                .matches(map -> count(map.get("use"), 3), "fail use")
                .matches(map -> count(map.get("nouse"), 3), "fail nouse")
                .matches(map -> count(map.get("fumens"), 5), "fail fumens");
    }

    @Test
    void pattern3() throws Exception {
        // reservedオプション
        /*
        _____XXXXX
        IIIIXXXXXX
        ___XXXXXXX
        ____XXXXXX
         */
        String fumen = "v115@ChE8DeF8CeG8DeF8JexEJ";

        String command = String.format("path -t %s -p I,*p4 -f csv -k pattern -r yes", fumen);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains(Messages.foundPath(14))
                .contains(Messages.success(400, 840));

        // column: [pattern, num-solutions, use, nouse, fumens]
        CSVStore csvStore = OutputFileHelper.loadPathPatternCSV();
        assertThat(csvStore.size()).isEqualTo(840);

        assertThat(csvStore.row("pattern", "ITJSZ"))
                .contains(entry("num-solutions", "0"))
                .contains(entry("use", ""))
                .contains(entry("nouse", ""))
                .contains(entry("fumens", ""));

        assertThat(csvStore.row("pattern", "ISOJL"))
                .contains(entry("num-solutions", "1"))
                .contains(entry("use", "IJSO"))
                .contains(entry("nouse", "L"))
                .contains(entry("fumens", "v115@9gi0R4E8zhF8Rpg0G8RpR4F8JeAgWEAPentC"));

        assertThat(csvStore.row("pattern", "ITLSI"))
                .contains(entry("num-solutions", "3"))
                .contains(entry("use", "TILS"))
                .contains(entry("nouse", "I"))
                .matches(map -> count(map.get("fumens"), 3), "fail fumens");

        assertThat(csvStore.row("pattern", "ILTSZ"))
                .contains(entry("num-solutions", "4"))
                .matches(map -> count(map.get("use"), 2), "fail use")
                .matches(map -> count(map.get("nouse"), 2), "fail nouse")
                .matches(map -> count(map.get("fumens"), 4), "fail fumens");
    }

    private boolean count(String str, int count) {
        return str.split(";").length == count;
    }
}
