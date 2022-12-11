package common.tetfu;

import common.buildup.BuildUpStream;
import common.datastore.MinoOperationWithKey;
import common.datastore.Operations;
import common.parser.OperationTransform;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ArrayColoredField;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import concurrent.ILockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import entry.common.kicks.factory.SRSMinoRotationFactory;
import exceptions.FinderParseException;
import lib.Randoms;
import module.LongTest;
import org.junit.jupiter.api.Test;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.solutions.OnDemandBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PerfectPackSearcher;
import searcher.pack.task.Result;
import searcher.pack.task.TaskResultHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static core.mino.Piece.J;
import static core.mino.Piece.L;
import static org.assertj.core.api.Assertions.assertThat;

class TetfuTest {
    @Test
    void removeDomainData() {
        // http://fumen.zui.jp/?
        assertThat(Tetfu.removeDomainData("http://fumen.zui.jp/?v115@vhAAgH")).isEqualTo("v115@vhAAgH");
        assertThat(Tetfu.removeDomainData("http://fumen.zui.jp/?m115@vhAAgH")).isEqualTo("m115@vhAAgH");
        assertThat(Tetfu.removeDomainData("http://fumen.zui.jp/?d115@vhAAgH")).isEqualTo("d115@vhAAgH");

        // fumen.zui.jp/?
        assertThat(Tetfu.removeDomainData("fumen.zui.jp/?v114@vhAAgH")).isEqualTo("v114@vhAAgH");
        assertThat(Tetfu.removeDomainData("fumen.zui.jp/?m114@vhAAgH")).isEqualTo("m114@vhAAgH");
        assertThat(Tetfu.removeDomainData("fumen.zui.jp/?d114@vhAAgH")).isEqualTo("d114@vhAAgH");

        // http://harddrop.com/fumen/?
        assertThat(Tetfu.removeDomainData("http://harddrop.com/fumen/?v113@vhAAgH")).isEqualTo("v113@vhAAgH");
        assertThat(Tetfu.removeDomainData("http://harddrop.com/fumen/?m113@vhAAgH")).isEqualTo("m113@vhAAgH");
        assertThat(Tetfu.removeDomainData("http://harddrop.com/fumen/?d113@vhAAgH")).isEqualTo("d113@vhAAgH");

        // harddrop.com/fumen/?
        assertThat(Tetfu.removeDomainData("harddrop.com/fumen/?v115@vhAAgH")).isEqualTo("v115@vhAAgH");
        assertThat(Tetfu.removeDomainData("harddrop.com/fumen/?m115@vhAAgH")).isEqualTo("m115@vhAAgH");
        assertThat(Tetfu.removeDomainData("harddrop.com/fumen/?d115@vhAAgH")).isEqualTo("d115@vhAAgH");

        // https://punsyuko.com/fumen/#
        assertThat(Tetfu.removeDomainData("https://punsyuko.com/fumen/#v115@vhAAgH")).isEqualTo("v115@vhAAgH");
        assertThat(Tetfu.removeDomainData("https://punsyuko.com/fumen/#m115@vhAAgH")).isEqualTo("m115@vhAAgH");
        assertThat(Tetfu.removeDomainData("https://punsyuko.com/fumen/#d115@vhAAgH")).isEqualTo("d115@vhAAgH");

        // punsyuko.com/fumen/#
        assertThat(Tetfu.removeDomainData("punsyuko.com/fumen/#v115@vhAAgH")).isEqualTo("v115@vhAAgH");
        assertThat(Tetfu.removeDomainData("punsyuko.com/fumen/#m115@vhAAgH")).isEqualTo("m115@vhAAgH");
        assertThat(Tetfu.removeDomainData("punsyuko.com/fumen/#d115@vhAAgH")).isEqualTo("d115@vhAAgH");

        // knewjade.github.io/fumen-for-mobile
        assertThat(Tetfu.removeDomainData("https://knewjade.github.io/fumen-for-mobile/#?d=v115@vhAAgH")).isEqualTo("v115@vhAAgH");
        assertThat(Tetfu.removeDomainData("knewjade.github.io/fumen-for-mobile/#?d=v115@vhAAgH")).isEqualTo("v115@vhAAgH");

        // direct
        assertThat(Tetfu.removeDomainData("v115@vhAAgH")).isEqualTo("v115@vhAAgH");
        assertThat(Tetfu.removeDomainData("m115@vhAAgH")).isEqualTo("m115@vhAAgH");
        assertThat(Tetfu.removeDomainData("d115@vhAAgH")).isEqualTo("d115@vhAAgH");
    }

    @Test
    void removePrefixData() {
        // v115
        assertThat(Tetfu.removePrefixData("v115@vhAAgH")).isEqualTo("vhAAgH");
        assertThat(Tetfu.removePrefixData("m115@vhAAgH")).isEqualTo("vhAAgH");
        assertThat(Tetfu.removePrefixData("d115@vhAAgH")).isEqualTo("vhAAgH");

        // v114
        assertThat(Tetfu.removePrefixData("v114@vhAAgH")).isNull();
        assertThat(Tetfu.removePrefixData("m114@vhAAgH")).isNull();
        assertThat(Tetfu.removePrefixData("d114@vhAAgH")).isNull();
    }

    @Test
    void isDataLater115() {
        // v115
        assertThat(Tetfu.isDataLater115("v115@vhAAgH")).isTrue();
        assertThat(Tetfu.isDataLater115("m115@vhAAgH")).isTrue();
        assertThat(Tetfu.isDataLater115("d115@vhAAgH")).isTrue();

        // v114
        assertThat(Tetfu.isDataLater115("v114@vhAAgH")).isFalse();
        assertThat(Tetfu.isDataLater115("m114@vhAAgH")).isFalse();
        assertThat(Tetfu.isDataLater115("d114@vhAAgH")).isFalse();
    }

    private static void assertField(ColoredField actual, ColoredField expected) {
        for (int y = 0; y < 24; y++)
            for (int x = 0; x < 10; x++)
                assertThat(actual.getBlockNumber(x, y)).isEqualTo(expected.getBlockNumber(x, y));
    }

    @Test
    void encode1() {
        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(ColorType.T, Rotate.Spawn, 5, 0)
        );

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhA1QJ");
    }

    @Test
    void encode2() {
        List<TetfuElement> elements = Arrays.asList(
                new TetfuElement(ColorType.L, Rotate.Spawn, 4, 0),
                new TetfuElement(ColorType.J, Rotate.Spawn, 8, 0),
                new TetfuElement(ColorType.I, Rotate.Right, 6, 2),
                new TetfuElement(ColorType.S, Rotate.Spawn, 4, 1),
                new TetfuElement(ColorType.Z, Rotate.Spawn, 8, 1),
                new TetfuElement(ColorType.T, Rotate.Spawn, 4, 3),
                new TetfuElement(ColorType.O, Rotate.Spawn, 0, 0),
                new TetfuElement(ColorType.J, Rotate.Right, 0, 3)
        );

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhHSQJWyBJnBXmBUoBVhBTpBOfB");
    }

    @Test
    void encode3() {
        List<TetfuElement> elements = Arrays.asList(
                new TetfuElement(ColorType.I, Rotate.Reverse, 5, 0, "a"),
                new TetfuElement(ColorType.S, Rotate.Reverse, 5, 2, "b"),
                new TetfuElement(ColorType.J, Rotate.Left, 9, 1, "c"),
                new TetfuElement(ColorType.O, Rotate.Right, 0, 1, "d"),
                new TetfuElement(ColorType.Z, Rotate.Left, 3, 1, "e"),
                new TetfuElement(ColorType.L, Rotate.Right, 0, 3, "日本語"),
                new TetfuElement(ColorType.T, Rotate.Reverse, 7, 1)
        );

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhGBQYBABBAAAnmQBACBAAA+tQBADBAAALpQBAEBAA?AcqQBAFBAAAKfQSAlfrHBFwDfE2Cx2Bl/PwB53AAAlsQAA");
    }

    @Test
    void encode4() {
        MinoFactory factory = new MinoFactory();
        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);
        field.putMino(factory.create(Piece.I, Rotate.Spawn), 1, 0);

        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(field, ColorType.I, Rotate.Spawn, 5, 0, "")
        );

        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("bhzhPexQJ");
    }

    @Test
    void encode5() {
        MinoFactory factory = new MinoFactory();
        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);
        field.putMino(factory.create(Piece.I, Rotate.Spawn), 1, 0);

        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(field, ColorType.I, Rotate.Reverse, 6, 0)
        );

        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("bhzhPehQJ");
    }

    @Test
    void encode6() {
        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(ColorType.Empty, Rotate.Spawn, 6, 0)
        );

        MinoFactory factory = new MinoFactory();

        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhAAgH");
    }

    @Test
    void encode7() {
        List<TetfuElement> elements = Arrays.asList(
                TetfuElement.createFieldOnly(ColoredFieldFactory.createColoredField("XXXXLLXXXX")),
                TetfuElement.createFieldOnly(ColoredFieldFactory.createColoredField("XXXXJJXXXX"))
        );

        MinoFactory factory = new MinoFactory();

        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("bhD8hlD8JeAgHbhD8h0D8JeAAA");
    }

    @Test
    void encode8() {
        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(ColorType.I, Rotate.Left, 0, 1)
        );

        MinoFactory factory = new MinoFactory();

        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhAZEJ");
    }

    @Test
    void encode9() {
        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(ColorType.I, Rotate.Right, 0, 2)
        );

        MinoFactory factory = new MinoFactory();

        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhAJEJ");
    }

    @Test
    void encodeQuiz1() {
        List<Piece> orders = Collections.singletonList(L);
        String quiz = Tetfu.encodeForQuiz(orders);

        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(ColorType.L, Rotate.Right, 0, 1, quiz)
        );

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhAKJYUAFLDmClcJSAVDEHBEooRBMoAVB");
    }

    @Test
    void encodeQuiz2() {
        List<Piece> orders = Arrays.asList(J, L);
        String quiz = Tetfu.encodeForQuiz(orders, L);

        List<TetfuElement> elements = Arrays.asList(
                new TetfuElement(ColorType.L, Rotate.Right, 0, 1, quiz),
                new TetfuElement(ColorType.J, Rotate.Left, 3, 1, quiz)
        );

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhBKJYVAFLDmClcJSAVTXSAVG88AYS88AZAAAA+qB");
    }

    @Test
    void decode1() throws Exception {
        String value = "bhzhPexAN";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(1);
        assertThat(pages.get(0))
                .returns(ColorType.I, TetfuPage::getColorType)
                .returns(Rotate.Spawn, TetfuPage::getRotate)
                .returns(5, TetfuPage::getX)
                .returns(0, TetfuPage::getY)
                .returns("", TetfuPage::getComment);
        assertField(ColoredFieldFactory.createColoredField("IIII______"), pages.get(0).getField());
    }

    @Test
    void decode2() throws Exception {
        String value = "bhzhPexAcFAooMDEPBAAA";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(1);
        assertThat(pages.get(0))
                .returns(ColorType.I, TetfuPage::getColorType)
                .returns(Rotate.Spawn, TetfuPage::getRotate)
                .returns(5, TetfuPage::getX)
                .returns(0, TetfuPage::getY)
                .returns("hello", TetfuPage::getComment);
        assertField(ColoredFieldFactory.createColoredField("IIII______"), pages.get(0).getField());
    }

    @Test
    void decode3() throws Exception {
        // empty
        String value = "vhAAgH";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(1);
        assertThat(pages.get(0))
                .returns(ColorType.Empty, TetfuPage::getColorType)
                .returns(Rotate.Reverse, TetfuPage::getRotate)
                .returns(0, TetfuPage::getX)
                .returns(22, TetfuPage::getY)
                .returns("", TetfuPage::getComment);
        assertField(ColoredFieldFactory.createColoredField(""), pages.get(0).getField());
    }

    @Test
    void decode4() throws Exception {
        List<TetfuElement> elements = Arrays.asList(
                new TetfuElement(ColorType.I, Rotate.Reverse, 5, 0, "a"),
                new TetfuElement(ColorType.S, Rotate.Reverse, 5, 2, "b"),
                new TetfuElement(ColorType.J, Rotate.Left, 9, 1, "c"),
                new TetfuElement(ColorType.O, Rotate.Right, 0, 1, "hello world!"),
                new TetfuElement(ColorType.Z, Rotate.Left, 3, 1, "こんにちは"),
                new TetfuElement(ColorType.L, Rotate.Right, 0, 3, "x ~= 1;"),
                new TetfuElement(ColorType.T, Rotate.Reverse, 7, 1)
        );

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);

        List<TetfuPage> pages = tetfu.decode(encode);

        assertThat(pages).hasSize(elements.size());
        for (int index = 0; index < pages.size(); index++) {
            TetfuElement element = elements.get(index);
            assertThat(pages.get(index))
                    .returns(element.getColorType(), TetfuPage::getColorType)
                    .returns(element.getRotate(), TetfuPage::getRotate)
                    .returns(element.getX(), TetfuPage::getX)
                    .returns(element.getY(), TetfuPage::getY)
                    .returns(element.getComment(), TetfuPage::getComment);
        }
    }

    @Test
    void decode5() throws Exception {
        String value = "bhzhFeH8Bex4OvhAAAA";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(2);

        assertThat(pages.get(0))
                .returns(ColorType.I, TetfuPage::getColorType)
                .returns(Rotate.Spawn, TetfuPage::getRotate)
                .returns(5, TetfuPage::getX)
                .returns(0, TetfuPage::getY)
                .returns("", TetfuPage::getComment);
        assertField(ColoredFieldFactory.createColoredField(
                "IIII______"
        ), pages.get(0).getField());

        assertThat(pages.get(1))
                .returns(ColorType.Empty, TetfuPage::getColorType)
                .returns("", TetfuPage::getComment);
        assertField(ColoredFieldFactory.createColoredField(
                "" +
                        "__IIIIIIII" +
                        "__XXXXXXXX"
        ), pages.get(1).getField());
    }

    @Test
    void decode6() throws Exception {
        String value = "VhRpHeRpNeAgHvhIAAAAAAAAAAAAAAAAAAAAAAAAAA?A";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(10);
        for (int index = 0; index < 10; index++) {
            assertThat(pages.get(index))
                    .returns(ColorType.Empty, TetfuPage::getColorType)
                    .returns("", TetfuPage::getComment);
        }
        assertField(ColoredFieldFactory.createColoredField(
                "" +
                        "____OO____" +
                        "____OO____"
        ), pages.get(9).getField());
    }

    @Test
    void decode7() throws Exception {
        String value = "+gH8AeI8BeH8AeI8KeAgHvhBpoBAAA";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(3);
        assertThat(pages.get(0)).returns(ColorType.Empty, TetfuPage::getColorType);
        assertThat(pages.get(1)).returns(ColorType.I, TetfuPage::getColorType);
        assertThat(pages.get(2)).returns(ColorType.Empty, TetfuPage::getColorType);
        assertField(ColoredFieldFactory.createColoredField(
                "" +
                        "_XXXXXXXXI" +
                        "_XXXXXXXXI"
        ), pages.get(2).getField());
    }

    @Test
    void decode8() throws Exception {
        String value = "bhD8hlD8JeAgHbhD8h0D8JeAAA";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(2);
        assertThat(pages.get(0)).returns(ColorType.Empty, TetfuPage::getColorType);
        assertThat(pages.get(1)).returns(ColorType.Empty, TetfuPage::getColorType);
        assertField(ColoredFieldFactory.createColoredField(
                "XXXXJJXXXX"
        ), pages.get(1).getField());
    }

    @Test
    void decode9() throws Exception {
        // I-Leftを接着するパターン
        String value = "vhAZEJ";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages.get(0))
                .returns(ColorType.I, TetfuPage::getColorType)
                .returns(Rotate.Left, TetfuPage::getRotate)
                .returns(0, TetfuPage::getX)
                .returns(1, TetfuPage::getY);
    }

    @Test
    void decode10() throws Exception {
        // I-Rightを接着するパターン
        String value = "vhAJEJ";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages.get(0))
                .returns(ColorType.I, TetfuPage::getColorType)
                .returns(Rotate.Right, TetfuPage::getRotate)
                .returns(0, TetfuPage::getX)
                .returns(2, TetfuPage::getY);
    }

    @Test
    @LongTest
    void random() throws Exception {
        // Initialize
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = SRSMinoRotationFactory.createDefault();
        ColorConverter colorConverter = new ColorConverter();

        // Define size
        int height = 4;
        int basicWidth = 3;
        SizedBit sizedBit = new SizedBit(basicWidth, height);
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);

        // Create basic solutions
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        ILockedReachableThreadLocal lockedReachableThreadLocal = new ILockedReachableThreadLocal(minoFactory, minoShifter, minoRotation, height, false);
        Predicate<ColumnField> memorizedPredicate = (columnField) -> true;
        OnDemandBasicSolutions basicSolutions = new OnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate);

        for (int count = 0; count < 15; count++) {
            System.out.println(count);
            // Create field
            int numOfMinos = randoms.nextIntClosed(6, 10);
            Field field = randoms.field(height, numOfMinos);

            // Search
            List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(basicWidth, height, field);
            SolutionFilter solutionFilter = new SRSValidSolutionFilter(field, lockedReachableThreadLocal, sizedBit);
            PerfectPackSearcher searcher = new PerfectPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
            Optional<Result> resultOptional = searcher.findAny();

            BuildUpStream buildUpStream = new BuildUpStream(lockedReachableThreadLocal.get(), height);
            // If found solution
            resultOptional.ifPresent(result -> {
                List<MinoOperationWithKey> list = result.getMemento()
                        .getSeparableMinoStream(basicWidth)
                        .map(SeparableMino::toMinoOperationWithKey)
                        .collect(Collectors.toList());
                Optional<List<MinoOperationWithKey>> validOption = buildUpStream.existsValidBuildPattern(field, list).findAny();
                validOption.ifPresent(operationWithKeys -> {
                    Operations operations = OperationTransform.parseToOperations(field, operationWithKeys, height);
                    List<TetfuElement> elements = operations.getOperations().stream()
                            .map(operation -> {
                                ColorType colorType = colorConverter.parseToColorType(operation.getPiece());
                                Rotate rotate = operation.getRotate();
                                int x = operation.getX();
                                int y = operation.getY();
                                String comment = randoms.string() + randoms.string() + randoms.string();
                                return new TetfuElement(colorType, rotate, x, y, comment);
                            })
                            .collect(Collectors.toList());

                    String encode = new Tetfu(minoFactory, colorConverter).encode(elements);
                    List<TetfuPage> decode = decodeTetfu(minoFactory, colorConverter, encode);

                    assertThat(decode).hasSize(elements.size());
                    for (int index = 0; index < decode.size(); index++) {
                        TetfuElement element = elements.get(index);
                        assertThat(decode.get(index))
                                .returns(element.getColorType(), TetfuPage::getColorType)
                                .returns(element.getRotate(), TetfuPage::getRotate)
                                .returns(element.getX(), TetfuPage::getX)
                                .returns(element.getY(), TetfuPage::getY)
                                .returns(element.getComment(), TetfuPage::getComment);
                    }
                });
            });
        }
    }

    private List<TetfuPage> decodeTetfu(MinoFactory minoFactory, ColorConverter colorConverter, String encode) {
        try {
            return new Tetfu(minoFactory, colorConverter).decode(encode);
        } catch (FinderParseException e) {
            throw new RuntimeException(e);
        }
    }
}