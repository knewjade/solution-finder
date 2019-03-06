package searcher.spins;

import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import common.datastore.PieceCounter;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.BlockFieldView;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.Piece;
import entry.path.output.OneFumenParser;
import exceptions.FinderParseException;
import module.LongTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import searcher.spins.results.Result;
import searcher.spins.roof.results.RoofResult;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class MainRunnerTest {
    @Test
    void case1_h4() {
        int fieldHeight = 8;
        Field initField = FieldFactory.createField("" +
                        "XXXX______" +
                        "XXXXXX____" +
                        "XXXXXXX___" +
                        "XXXXXXXX__" +
                        "XXXXXXXXX_" +
                        ""
                , fieldHeight);
        MainRunner runner = new MainRunner(4, fieldHeight);
        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        List<RoofResult> results = runner.search(initField, pieceCounter, 1).parallel().collect(Collectors.toList());
        assertThat(results).hasSize(350);

        verify(results);

        showTetfu(fieldHeight, initField, results);
    }

    private void showTetfu(int fieldHeight, Field initField, List<RoofResult> results) {
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        OneFumenParser oneFumenParser = new OneFumenParser(minoFactory, colorConverter);
        List<TetfuElement> elements = results.stream()
                .map(roofResult -> {
                    Result result = roofResult.getLastResult();
                    List<MinoOperationWithKey> operations = result.operationStream().collect(Collectors.toList());
                    ColoredField coloredField = oneFumenParser.parseToColoredField(operations, initField, fieldHeight);
                    return new TetfuElement(coloredField, "");
                })
                .collect(Collectors.toList());

        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        String encode = tetfu.encode(elements);
        System.out.println("https://knewjade.github.io/fumen-for-mobile/#?d=v115@" + encode);
    }

    private void verify(List<RoofResult> results) {
        TreeSet<BlockField> blockFields = new TreeSet<>(BlockField::compareTo);
        for (RoofResult result : results) {
            BlockField e = result.getLastResult().parseToBlockField();
            boolean add = blockFields.add(e);
            if (!add) {
                System.out.println(BlockFieldView.toString(e));
            }
        }
        assertThat(blockFields).hasSize(results.size());
    }

    @Test
    @LongTest
    void case1_h5() {
        int fieldHeight = 8;
        Field initField = FieldFactory.createField("" +
                        "XXXX______" +
                        "XXXXXX____" +
                        "XXXXXXX___" +
                        "XXXXXXXX__" +
                        "XXXXXXXXX_" +
                        ""
                , fieldHeight);
        MainRunner runner = new MainRunner(5, fieldHeight);
        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        List<RoofResult> results = runner.search(initField, pieceCounter, 1).parallel().collect(Collectors.toList());

        assertThat(results).hasSize(6827);

        verify(results);

        showTetfu(fieldHeight, initField, results);
    }

    @Test
    @LongTest
    void caseTSDOpening() {
        int fieldHeight = 8;
        Field initField = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "__________" +
                        ""
                , fieldHeight);
        MainRunner runner = new MainRunner(4, fieldHeight);
        PieceCounter pieceCounter = new PieceCounter(Piece.valueList());
        List<RoofResult> results = runner.search(initField, pieceCounter, 2).parallel().collect(Collectors.toList());
        assertThat(results).hasSize(272);
    }

    @Test
    @Disabled
    void name() throws FinderParseException {
        String data105 = "wgi0D8Eeg0F8BewwwhG8xwwhH8wwwhI8whJeAgHwgi?0D8AeywAeg0F8wwhlwhG8gHAPQLDeD8glQLFeB8QpKeAAAw?gBtAeD8AeywBtF8wwhlwhG8gHAPQLDeA8SpglQLGeA8g0Ke?AAAugilBeD8AeglAeywF8BtwwwhG8BewhDeA8SpAPwSGeA8?g0KeAAAvgR4BeD8AeR4ywF8BtwwwhE8h0glAtwhDeA8g0A8?QpQLAPHeA8KeAAAwgi0D8AeywAeg0F8wwRpwhF8whAPQpwh?DeA8xhQpQLAPHeA8KeAAAwgBtAeD8AeywBtF8wwRpwhG8xS?QLDeA8SpAeQLHeA8KeAAAugilBeD8AeglQ4ywF8R4wwwhG8?QpAPwhDeA8SpAPwSHeA8KeAAAwgi0D8AeywAtg0E8g0Qpgl?AtwhDeA8g0whQaQLAPGewhJeA8KeAAAxgQ4AeD8DeR4F8ww?RawhDeA8RpAegWAPGeAtAewhHeA8whJeAAAtgg0EeD8i0Ce?F8ywAeG8wwQLglDeD8wDQLHeA8KeAAAvgR4BeD8AeR4CeD8?glA8ywEeilwwhlFeRpQLglHeA8KeAAAtgBtDeD8AeBtCeF8?hWwwEeA8xhwwhlFeRpQLglHeA8KeAAAugRpCeD8AeRpCeD8?BtywEeA8BtwwhlFeRpQLglHeA8KeAAAxgQ4AeD8CewwR4E8?wwglwwAeQ4DeA8ywhlFeRpQLglHeA8KeAAAtgg0EeD8i0ww?BeF8xwwDEeC8AeRLFeRpAeQLHeA8KeAAAvgR4BeD8AeR4ww?BeD8glA8xwFeilAehlFeRpAeglHeA8KeAAAtgBtDeD8AeBt?wwBeF8hWFeA8xhAehlFeRpAeglHeA8KeAAAugRpCeD8AeRp?wwBeD8BtxwFeA8BtAehlFeRpAeglHeA8KeAAAxgQ4AeD8Ce?wwR4E8wwwSxwQ4DeA8xwAehlFeRpAeglHeA8KeAAAzgD8i0?wwBeF8g0wwgWEeC8AeRLFeA8QpRLHeA8KeAAAtgg0EeD8i0?wwBeF8AexwEeilAehlFeglQpQLglHeA8KeAAAvgh0BeD8Be?g0wwBeD8glA8g0xwEeilAehlFeA8QpQLglHeA8KeAAAvgR4?BeD8AeR4wwBeF8gHQawwEeB8glAehlFeglQpQLglHeA8KeA?AAugQ4DeD8AeR4wwBeF8AegWwwEeA8xhAehlFeA8QpQLglH?eA8KeAAAtgBtDeD8AeBtwwBeE8whAexwEeA8xhAehlFewhQ?pQLglHeA8KeAAAugRpCeD8AeRpwwBeD8BtAexwEeA8BtAeh?lFeA8QpQLglHeA8KeAAAzgD8DehlE8wwglxwglDeA8xwAeQ?4glFeA8QpglQ4HeA8wwJeAAAzgD8CewwhlF8xwAeglG8wwQ?4glH8R4DeD8g0wwJeAAAzgD8CewwhlF8AexwglG8wwQ4glD?eC8QpxwFeRpA8wwJeAAAzgD8CewwhlF8ywglG8AeQ4glDeC?8QpxwFeA8RpwwJeAAAwgi0D8AeywAeg0F8wwilG8glQ4EeC?8QpxwGeA8whwwJeAAAwgBtAeD8AeywBtF8wwilG8APwhgHD?eA8SpQ4whGeg0whQ4JeAAArgzhDeD8AeywBeF8wwilG8gWQ?pEeA8UpGeg0whQ4JeAAArgzhDeD8AeywAeglF8wwilB8T4A?8AeQ4EeA8SpR4Geg0whQ4JeAAAwgBtAeD8AeywBtF8wwi0B?8T4A8AeQ4g0DeA8SpQ4wwGeA8whQ4JeAAArgzhDeD8AeywB?eF8wwi0G8APQpg0DeA8UpGeA8xhJeAAAwgi0D8AeywAeg0F?8wwQ4hlB8T4A8R4glDeA8SpQ4glGeA8whgHJeAAAwgBtAeD?8AeywBtF8wwQ4hlG8xhAPDeA8SpQ4APTeAAArgzhDeD8Aey?wBeF8wwQ4hlG8RpglDeA8TpgWTeAAAugilBeD8AeglAeywF?8Btwwg0B8T4A8Btg0DeA8Sph0TeAAAvgR4BeD8AeR4ywF8B?twwg0E8h0glAtg0DeA8g0A8QpxhTeAAAwgBtAeD8AeywBtF?8wwRpg0F8whAPQpg0DeA8xhQpxhTeAAArgzhDeD8AeywBeF?8wwRpg0G8Rag0DeA8SphlTeAAArgzhDeD8AeywR4F8wwR4g?0B8T4A8Beg0DeA8Sph0TeAAAtgzhBeD8BeQ4ywB8V4wwg0D?eA8RpglwDQaGeA8g0UeAAAugilBeD8AeglQ4ywD8R4h0wwg?0DeB8whglQLwhGewhg0UeAAArgzhDeD8AeywAtAeE8g0Qpg?lAtg0DeA8g0whQaQLwhGewhg0UeAAAtgzhBeD8AeBtywB8T?4Btwwg0DeA8RpQLAPg0GeAtg0UeAAAzgD8EewwD8R4RaxwD?eA8BtRaHeA8AtgWTeAAAsgi0DeD8Aeg0AewwhlF8ywglDeC?8BtwSFeB8RaTeAAAtgRpDeD8RpAewwhlF8ywglC8ilA8Btg?lDeA8glA8QphlTeAAArgzhDeD8AeywBeF8wwi0D8xwA8Btg?0DexwA8QphlTeAAArgzhDeD8AeywAeglF8wwilB8T4A8BtE?eA8SpBtTeAAAxgQ4AeD8DeR4B8T4ywQ4DeA8RpAeQpwhGeA?tQaQpTeAAAvgglCeD8ilCeF8ywAeG8wwAPQpDeD8BPTeAAA?tgg0EeD8i0CeF8QpxwEei0wwRpFeRpgWQpTeAAAvgR4BeD8?AeR4CeD8glA8ywEeilwwRpFeRpgWQpTeAAAtgBtDeD8AeBt?CeF8hWwwEeA8xhwwRpFeRpgWQpTeAAAxgQ4AeD8CewwR4D8?BtxwAeQ4DeA8BtwwRpFeRpgWQpTeAAAvgglCeD8ilwwBeF8?xwwDEeC8AeBPFeSpAPTeAAAtgg0EeD8i0wwBeF8QpwwFei0?AeRpFeTpTeAAAvgR4BeD8AeR4wwBeD8glA8xwFeilAeRpFe?TpTeAAAtgBtDeD8AeBtwwBeF8hWFeA8xhAeRpFeTpTeAAAx?gQ4AeD8CewwR4D8BtAexwQ4DeA8BtAeRpFeTpTeAAAvgglC?eD8ilwwBeF8AewwgWEeC8AeBPFeA8QpgWAPTeAAAughlCeD?8BeglwwBeF8AexwEei0AeRpFeA8QpgWQpTeAAAzgD8i0wwB?eE8g0AtxwEeB8g0AeRpFeg0QpgWQpTeAAAtgg0EeD8i0wwB?eF8AexwEeilAeRpFeglQpgWQpTeAAAvgh0BeD8Beg0wwBeD?8glA8g0xwEeilAeRpFeA8QpgWQpTeAAAvgR4BeD8AeR4wwB?eF8gHQawwEeB8glAeRpFeglQpgWQpTeAAAugQ4DeD8AeR4w?wBeF8AegWwwEeA8xhAeRpFeA8QpgWQpTeAAAtgBtDeD8AeB?twwBeE8whAexwEeA8xhAeRpFewhQpgWQpTeAAAwgi0D8Aey?wAeg0D8BtwwilDeA8BtwSRpFeA8QpgWQpTeAAAwgBtAeD8A?eywBtF8wwilG8APxSDeA8TpwSTeAAArgzhDeD8AeywBeF8w?wilG8gWQaQpDeA8SpRaTeAAArgzhDeD8AeywAeglF8wwilB?8T4A8AeRpDeA8UpTeAAAxgRpD8DeRpB8T4ywAtDeA8RpAeA?tglGeA8whwSTeAAAvgglCeD8ilCeF8xwglwhDeC8wwxhdeA?AAtgg0EeD8i0CeF8QpxwAtDei0wwBtdeAAAugRpCeD8AeRp?CeD8glA8ywAtDeilwwBtdeAAAxgRpD8CewwRpE8wwglwwAe?AtDeA8ywBtdeAAAvgglCeD8ilwwxSDeB8wwAewSwhFeQpAe?AteeAAAtgg0EeD8h0AtwwFeh0QpBeAtFeQpAeAteeAAAugR?pCeD8gHRpwwFehlQaBeAtFeQpAeAteeAAAxgRpD8AexSwwR?pDeA8wwwSAewwAtFeQpAeAteeAAAvgglCeD8ilwwxSDeB8B?eglwhFeA8AeQaeeAAAtgg0EeD8h0AtwwFeh0gWAewwAtFeA?8AeQaeeAAAugRpCeD8gHRpwwFehlgHAewwAtFeA8AeQaeeA?AArgzhDeD8AehlwwhlDeA8wwglgWQpglFeA8iWHeAPUeAAA?rgzhDeD8AeywR4B8T4wwR4glDeA8RpwSfeAAAtgzhBeD8Ae?BtywB8T4BtwwglDeA8RpwSRLdeAAArgzhDeD8AeywAeglD8?R4AtwhhlDeA8BtyhIeg0TeAAArgzhDeD8AeywRpB8T4wwg0?RpDeA8Rpwhg0AtIegHTeAAAtgzhBeD8BeQ4ywB8V4wwAtDe?A8RpglxhHeAtUeAAAugilBeD8AeglQ4ywD8R4h0wwAtDeB8?whglRadeAAAxgRpD8CewwRpE8g0RpwwAtDeA8g0whQLRade?AAAvgglCeD8ilwwxSDeB8wwAeglwhneAAAtgg0EeD8h0Atw?wFeh0QpAewwAtneAAAugRpCeD8gHRpwwFehlQaAewwAtneA?AAwgi0D8AehlwwAeg0DeA8wwglwShlGeglBPHeAPUeAAAwg?BtAeD8AexwQahWDeA8QpAewSglAPneAAArgzhDeD8Aexwwh?APEeA8QpAewShWneAAArgzhDeB8R4QaAtxwR4DeA8QpAegl?Q4AtGegWBtHeAtUeAAArgzhDeB8R4QaAtxwRpDeA8QpAegl?BPGeQ4QpAPHeQpUeAAA";
        String data103 = "wgi0D8Eeg0F8BewwwhG8xwwhH8wwwhI8whJeAgHwgi?0D8AeywAeg0F8wwhlwhG8gHAPQLDeD8glQLFeB8QpKeAAAw?gBtAeD8AeywBtF8wwhlwhG8gHAPQLDeA8SpglQLGeA8g0Ke?AAAugilBeD8AeglAeywF8BtwwwhG8BewhDeA8SpAPwSGeA8?g0KeAAAvgR4BeD8AeR4ywF8BtwwwhE8h0glAtwhDeA8g0A8?QpQLAPHeA8KeAAAwgi0D8AeywAeg0F8wwRpwhF8whAPQpwh?DeA8xhQpQLAPHeA8KeAAAwgBtAeD8AeywBtF8wwRpwhG8xS?QLDeA8SpAeQLHeA8KeAAAugilBeD8AeglQ4ywF8R4wwwhG8?QpAPwhDeA8SpAPwSHeA8KeAAAwgi0D8AeywAtg0E8g0Qpgl?AtwhDeA8g0whQaQLAPGewhJeA8KeAAAxgQ4AeD8CewwR4F8?AeRawhDeA8RpAegWAPGeAtAewhHeA8whJeAAAtgg0EeD8i0?wwBeF8AewwgWEeC8AeRLFeA8QpRLHeA8KeAAAvgh0BeD8Be?g0wwBeD8glA8g0xwEeilAehlFeA8QpQLglHeA8KeAAAzgD8?i0wwBeF8AeQawwEeB8glAehlFeglQpQLglHeA8KeAAAvgR4?BeD8AeR4wwBeF8AexwEeilAehlFeglQpQLglHeA8KeAAAug?Q4DeD8AeR4wwBeF8AegWwwEeA8xhAehlFeA8QpQLglHeA8K?eAAAtgBtDeD8AeBtwwBeE8whAexwEeA8xhAehlFewhQpQLg?lHeA8KeAAAugRpCeD8AeRpwwBeD8BtAexwEeA8BtAehlFeA?8QpQLglHeA8KeAAAtgg0EeD8i0CeE8wwglxwEeA8xwAehlF?eA8QpQLglHeA8KeAAAvgR4BeD8AeR4CeD8glA8ywEeilwwh?lFeRpQLglHeA8KeAAAtgBtDeD8AeBtCeF8hWwwEeA8xhwwh?lFeRpQLglHeA8KeAAAugRpCeD8AeRpCeD8BtywEeA8Btwwh?lFeRpQLglHeA8KeAAAxgQ4AeD8CewwR4E8wwglwwAeQ4DeA?8ywhlFeRpQLglHeA8KeAAAtgg0EeD8i0wwBeF8xwwDEeC8A?eRLFeRpAeQLHeA8KeAAAvgR4BeD8AeR4wwBeD8glA8xwFei?lAehlFeRpAeglHeA8KeAAAtgBtDeD8AeBtwwBeF8hWFeA8x?hAehlFeRpAeglHeA8KeAAAugRpCeD8AeRpwwBeD8BtxwFeA?8BtAehlFeRpAeglHeA8KeAAAzgD8CewwhlE8wwwSxwglDeA?8xwAeQ4glFeRpR4HeA8wwJeAAAzgD8DehlF8ywglG8wwQ4g?lDeC8QpxwFeA8RpwwJeAAAzgD8CewwhlF8xwAeglG8wwQ4g?lH8R4DeD8g0wwJeAAAzgD8CewwhlF8ywglG8AeQ4glDeC8Q?pxwFeRpA8wwJeAAArgzhDeD8AeywAeglF8wwilG8AeQ4EeC?8QpxwGeA8whwwJeAAAwgi0D8AeywAeg0F8wwilB8T4A8glQ?4EeA8SpQ4wwGeA8whQ4JeAAAwgBtAeD8AeywBtF8wwilG8A?PwhgHDeA8SpQ4whGeg0whQ4JeAAArgzhDeD8AeywBeF8wwi?lG8gWQpEeA8UpGeg0whQ4JeAAAwgBtAeD8AeywBtF8wwi0B?8T4A8AeQ4g0DeA8SpR4Geg0whQ4JeAAArgzhDeD8AeywBeF?8wwi0G8APQpg0DeA8UpGeA8xhJeAAAwgi0D8AeywAeg0F8w?wQ4hlB8T4A8R4glDeA8SpQ4glGeA8whgHJeAAAwgBtAeD8A?eywBtF8wwQ4hlG8xhAPDeA8SpQ4APTeAAArgzhDeD8AeywB?eF8wwQ4hlG8RpglDeA8TpgWTeAAAugilBeD8AeglAeywF8B?twwg0B8T4A8Btg0DeA8Sph0TeAAAvgR4BeD8AeR4ywF8Btw?wg0E8h0glAtg0DeA8g0A8QpxhTeAAAwgBtAeD8AeywBtF8w?wRpg0F8whAPQpg0DeA8xhQpxhTeAAArgzhDeD8AeywBeF8w?wRpg0G8Rag0DeA8SphlTeAAArgzhDeD8AeywR4F8wwR4g0B?8T4A8Beg0DeA8Sph0TeAAAtgzhBeD8BeQ4ywB8V4wwg0DeA?8RpglwDQaGeA8g0UeAAAugilBeD8AeglQ4ywD8R4h0wwg0D?eB8whglQLwhGewhg0UeAAArgzhDeD8AeywAtAeE8g0QpglA?tg0DeA8g0whQaQLwhGewhg0UeAAAtgzhBeD8AeBtywB8T4B?twwg0DeA8RpQLAPg0GeAtg0UeAAAzgD8EewwD8R4RaxwDeA?8BtRaHeA8AtgWTeAAAsgi0DeD8Aeg0AewwhlF8ywglDeC8B?twSFeB8RaTeAAAtgRpDeD8RpAewwhlF8ywglC8ilA8BtglD?eA8glA8QphlTeAAArgzhDeD8AeywBeF8wwi0D8xwA8Btg0D?exwA8QphlTeAAArgzhDeD8AeywAeglF8wwilB8T4A8BtEeA?8SpBtTeAAAxgQ4AeD8CewwR4B8T4AexwQ4DeA8RpAeQpwhG?eAtQaQpTeAAAvgglCeD8ilwwBeF8AewwgWEeC8AeBPFeA8Q?pgWAPTeAAAughlCeD8BeglwwBeF8AexwEei0AeRpFeA8Qpg?WQpTeAAAtgg0EeD8i0wwBeE8g0gWxwEeB8g0AeRpFeg0Qpg?WQpTeAAAvgh0BeD8Beg0wwBeD8glA8g0xwEeilAeRpFeA8Q?pgWQpTeAAAzgD8i0wwBeF8AeQawwEeB8glAeRpFeglQpgWQ?pTeAAAvgR4BeD8AeR4wwBeF8AexwEeilAeRpFeglQpgWQpT?eAAAugQ4DeD8AeR4wwBeF8AegWwwEeA8xhAeRpFeA8QpgWQ?pTeAAAtgBtDeD8AeBtwwBeE8whAexwEeA8xhAeRpFewhQpg?WQpTeAAAvgglCeD8ilCeD8BtywEeA8BtAeRpFeA8QpgWQpT?eAAAtgg0EeD8i0CeF8QpxwEei0wwRpFeRpgWQpTeAAAvgR4?BeD8AeR4CeD8glA8ywEeilwwRpFeRpgWQpTeAAAtgBtDeD8?AeBtCeF8hWwwEeA8xhwwRpFeRpgWQpTeAAAxgQ4AeD8Ceww?R4D8BtxwAeQ4DeA8BtwwRpFeRpgWQpTeAAAvgglCeD8ilww?BeF8xwwDEeC8AeBPFeSpAPTeAAAtgg0EeD8i0wwBeF8Qpww?Fei0AeRpFeTpTeAAAvgR4BeD8AeR4wwBeD8glA8xwFeilAe?RpFeTpTeAAAtgBtDeD8AeBtwwBeF8hWFeA8xhAeRpFeTpTe?AAAwgi0D8AeywAeg0D8BtwwilDeA8BtwSRpFeTpTeAAAwgB?tAeD8AeywBtF8wwilG8APxSDeA8TpwSTeAAArgzhDeD8Aey?wBeF8wwilG8gWQaQpDeA8SpRaTeAAArgzhDeD8AeywAeglF?8wwilB8T4A8AeRpDeA8UpTeAAAxgRpD8CewwRpB8T4AexwA?tDeA8RpAeAtglGeA8whwSTeAAAvgglCeD8ilwwxSDeB8Beg?lwhFeA8AeQaeeAAAtgg0EeD8h0AtwwFeh0gWAewwAtFeA8A?eQaeeAAAugRpCeD8gHRpwwFehlgHAewwAtFeA8AeQaeeAAA?xgRpD8AexSAeRpDeA8wwglAewwAtFeA8AeQaeeAAAvgglCe?D8ilCeF8xwglwhDeC8wwxhdeAAAtgg0EeD8i0CeF8QpxwAt?Dei0wwBtdeAAAugRpCeD8AeRpCeD8glA8ywAtDeilwwBtde?AAAxgRpD8CewwRpE8wwglwwAeAtDeA8ywBtdeAAAvgglCeD?8ilwwxSDeB8wwAewSwhFeQpAeAteeAAAtgg0EeD8h0AtwwF?eh0QpBeAtFeQpAeAteeAAAugRpCeD8gHRpwwFehlQaBeAtF?eQpAeAteeAAArgzhDeD8AehlwwR4DeA8wwhlQ4glFeQpwSg?lgWHeAPUeAAAtgzhBeD8AeBtywB8T4BtwwglDeA8RpwSRLd?eAAArgzhDeD8AeywhlD8R4AtglQpglDeA8BthWwSdeAAArg?zhDeD8AeywRpB8T4wwg0RpDeA8RpwhBtdeAAArgzhDeD8Ae?ywAeglB8T4wwilDeA8RpwhRpIeg0TeAAAtgzhBeD8BeQ4yw?B8V4wwAtDeA8RpglAtglHeAtgHTeAAAugilBeD8AeglQ4yw?D8R4h0wwAtDeB8whglRadeAAAxgRpD8CewwRpE8g0RpwwAt?DeA8g0whQLRadeAAAvgglCeD8ilwwxSDeB8wwAeglwhneAA?Atgg0EeD8h0AtwwFeh0QpAewwAtneAAAugRpCeD8gHRpwwF?ehlQaAewwAtneAAAwgi0D8AehlwwAeg0DeA8wwglwShlGeg?lBPHeAPUeAAAwgBtAeD8AexwQahWDeA8QpAewSglAPneAAA?rgzhDeD8AexwwhAPEeA8QpAewShWneAAArgzhDeB8R4QaAt?xwRpDeA8QpAeglRpGewwQ4IeQ4UeAAArgzhDeB8R4QaAtxw?R4DeA8QpAeglAtwhGewDwSAtHewSUeAAA";

        TreeSet<BlockField> blockFields103 = createBlockFields(data103);
        TreeSet<BlockField> blockFields105 = createBlockFields(data105);

        blockFields105.removeAll(blockFields103);
        for (BlockField blockField : blockFields105) {
            System.out.println(BlockFieldView.toString(blockField));
            System.out.println();
        }
    }

    private TreeSet<BlockField> createBlockFields(String data103) throws FinderParseException {
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        List<TetfuPage> pages = tetfu.decode(data103);
        TreeSet<BlockField> blockFields = new TreeSet<>(BlockField::compareTo);
        int height = 10;
        for (TetfuPage page : pages) {
            ColoredField coloredField = page.getField();
            BlockField blockField = new BlockField(height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < 10; x++) {
                    ColorType colorType = coloredField.getColorType(x, y);
                    if (ColorType.isMinoBlock(colorType)) {
                        blockField.setBlock(colorConverter.parseToBlock(colorType), x, y);
                    }
                }
            }
            blockFields.add(blockField);
        }
        return blockFields;
    }
}