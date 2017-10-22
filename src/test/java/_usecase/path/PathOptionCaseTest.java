package _usecase.path;

import _usecase.*;
import common.datastore.Operation;
import common.datastore.Operations;
import common.datastore.SimpleOperation;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

// オプションが正しく反映されているかを確認する
class PathOptionCaseTest extends PathUseCaseBaseTest {
    @Override
    @BeforeEach
    void setUp() throws IOException {
        super.setUp();
    }

    @Test
    void help() throws Exception {
        // ヘルプ
        String command = "path -h";
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);
        assertThat(log.getOutput()).contains("usage");
        assertThat(log.getError()).isEmpty();
    }

    @Test
    void maxLayer() throws Exception {
        // 計算するレイヤー数を指定

            /*
            comment: 4 -p *p7 -L 1
            _____X____
            _____XX___
            ___XXXXXX_
            ___XXXXXXX
             */

        String tetfu = "v115@ChA8IeB8FeF8DeG8JeAgWVA0no2ANI98AQPcQBFbcs?AMoo2ARAAAA";

        ConfigFileHelper.createPatternFile("*p4");

        String command = String.format("path -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.clearLine(4))
                .contains(Messages.uniqueCount(4))
                .doesNotContain(Messages.minimalCount())
                .contains(Messages.useHold());

        assertThat(OutputFileHelper.existsPathUniqueHTML()).isTrue();
        assertThat(OutputFileHelper.existsPathMinimalHTML()).isFalse();
    }

    @Test
    void page() throws Exception {
        // テト譜のページを指定

            /*
            page: 5
            comment: 4 -p S,[TIOJLZ]p6
            X____XX__X
            X____X__XX
            X____XXXXX
            X____XXXXX
             */

        String tetfu = "v115@9gA8IeA8IeA8IeA8SeSSYMA0no2ANI98AQPk/AvhDM?oBHsBumBAAPbA0no2ANI98Awc88ADYfzBUuaPCsnEHBkYzA?A";

        ConfigFileHelper.createPatternFile("*p2");

        String command = String.format("path --page 5 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("S,[TIOJLZ]p6")
                .contains(Messages.clearLine(4))
                .contains(Messages.uniqueCount(95))
                .contains(Messages.minimalCount(47))
                .contains(Messages.useHold());
    }

    @Test
    void noClearLineOptionValue() throws Exception {
        // オプションの指定値がない: クリアライン
        //    -> デフォルト値が使用される

            /*
            comment: <Empty>
            ZZ________
            LZZ_T_____
            LZZTT_____
            LLZZT_____
             */

        String tetfu = "v115@vhDKJJUqB0fBdrB";

        String command = String.format("path -p *p7 -c -P 4 -t %s", tetfu);
        Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

        assertThat(log.getReturnCode()).isEqualTo(0);

        assertThat(log.getOutput())
                .contains("*p7")
                .contains(Messages.clearLine(4))
                .contains(Messages.uniqueCount(68))
                .contains(Messages.minimalCount(45))
                .contains(Messages.useHold());
    }
}
