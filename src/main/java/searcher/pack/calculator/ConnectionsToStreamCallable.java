package searcher.pack.calculator;

import common.datastore.OperationWithKey;
import core.column_field.ColumnField;
import core.field.Field;
import searcher.pack.SeparableMinos;
import searcher.pack.connections.ColumnFieldConnection;
import searcher.pack.connections.ColumnFieldConnections;
import searcher.pack.connections.StreamColumnFieldConnections;
import searcher.pack.mino_field.RecursiveMinoField;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.separable_mino.SeparableMino;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class ConnectionsToStreamCallable implements Callable<Stream<RecursiveMinoField>> {
    private final SolutionsCalculator calculator;
    private final ColumnField initColumnField;
    private final ColumnField outerColumnField;
    private final Field wallField;
    private final ColumnField limitOuterField;
    private final Field needFilledField;

    public ConnectionsToStreamCallable(SolutionsCalculator calculator, ColumnField initColumnField, ColumnField outerColumnField, Field wallField, ColumnField limitOuterField, Field needFilledField) {
        this.calculator = calculator;
        this.initColumnField = initColumnField;
        this.outerColumnField = outerColumnField;
        this.wallField = wallField;
        this.limitOuterField = limitOuterField;
        this.needFilledField = needFilledField;
    }

    @Override
    public Stream<RecursiveMinoField> call() {
        ColumnFieldConnections connections = calculator.getConnections(this.initColumnField);
        return connections.getConnectionStream().parallel()
                .flatMap(this::parseConnectionToMinoField);
    }

    private Stream<? extends RecursiveMinoField> parseConnectionToMinoField(ColumnFieldConnection connection) {
        // outerで重なりがないか確認する
        ColumnField nextOuterField = connection.getOuterField();
        if (nextOuterField.canMerge(limitOuterField) && nextOuterField.canMerge(outerColumnField)) {
            ColumnField freeze = nextOuterField.freeze(calculator.getHeight());

            // フィールドとミノ順を進める
            SeparableMino currentMino = connection.getMino();
            freeze.merge(outerColumnField);

            // 新しいフィールドを基に探索
            ColumnField innerField = connection.getInnerField();
            return calculate(innerField, freeze, wallField, currentMino);
        }
        return Stream.empty();
    }

    // columnField = inner + outer
    // outerColumnField = outer only
    private Stream<RecursiveMinoField> calculate(ColumnField columnField, ColumnField outerColumnField, Field wallField, SeparableMino currentMino) {
        // 最初の関数呼び出しで通ることはない
        // 全てが埋まったとき、それまでの手順を解とする
        if (calculator.isFilled(columnField))
            return parseWhenFilled(columnField, outerColumnField, wallField, currentMino);

        // 最初の関数呼び出しで通ることはない
        // すでに探索済みのフィールドなら、その情報を利用する
        RecursiveMinoFields minoFields = calculator.getRecursiveMinoFields(columnField);
        if (minoFields == null)
            return Stream.empty();

        return parseWhenNext(outerColumnField, currentMino, minoFields);
    }

    private Stream<RecursiveMinoField> parseWhenFilled(ColumnField columnField, ColumnField outerColumnField, Field wallField, SeparableMino currentMino) {
        // これからブロックをおく場所以外を、すでにブロックで埋めたフィールドを作成
        Field freeze = wallField.freeze(calculator.getHeight());
        Field invertedOuterField = calculator.parseInvertedOuterField(outerColumnField);
        freeze.merge(invertedOuterField);

        // 置くブロック以外がすでに埋まっていると仮定したとき、正しく接着できる順があるか確認
        SeparableMinos separableMinos = calculator.getSeparableMinos();
        RecursiveMinoField result = new RecursiveMinoField(currentMino, outerColumnField.freeze(calculator.getHeight()), separableMinos);

        // 次に埋めるべき場所がないときは結果をそのまま返す
        if (needFilledField.isPerfect()) {
            return Stream.of(result);
        }

        // 現在のフィールドに埋めるべきところがないが、次以降のフィールドに残っている場合をケアする
        List<SeparableMino> allMinos = separableMinos.getMinos();
        return over(columnField, outerColumnField, allMinos, separableMinos, result);
    }

    private Stream<RecursiveMinoField> over(ColumnField columnField, ColumnField outerColumnField, List<SeparableMino> minos, SeparableMinos separableMinos, RecursiveMinoField result) {
        Stream.Builder<RecursiveMinoField> builder = Stream.builder();
        builder.accept(result);

        StreamColumnFieldConnections connections = new StreamColumnFieldConnections(minos, columnField, calculator.getSizedBit());

        Stream<RecursiveMinoField> stream = connections.getConnectionStream()
                .filter(connection -> {
                    ColumnField nextOuterField = connection.getOuterField();
                    SeparableMino mino = connection.getMino();
                    return !needFilledField.canMerge(mino.getField())  // 次に埋めるべき場所を埋めてある
                            && nextOuterField.canMerge(limitOuterField)  // 次のフィールドの制限範囲と重なっていない
                            && nextOuterField.canMerge(outerColumnField);  // 次のフィールドにあるブロックと重なっていない
                })
                .flatMap(connection -> {
                    // 使用されるブロックを算出
                    ColumnField usingBlock = connection.getOuterField().freeze(calculator.getHeight());
                    usingBlock.merge(outerColumnField);
                    RecursiveMinoField t = new RecursiveMinoField(connection.getMino(), result, usingBlock, separableMinos);

                    List<SeparableMino> allMinos = separableMinos.getMinos();
                    List<SeparableMino> minos2 = allMinos.subList(separableMinos.toIndex(connection.getMino()) + 1, allMinos.size());

                    return over(connection.getInnerField(), usingBlock, minos2, separableMinos, t);
                });

        return Stream.concat(Stream.of(result), stream);
    }

    private Stream<RecursiveMinoField> parseWhenNext(ColumnField outerColumnField, SeparableMino currentMino, RecursiveMinoFields minoFields) {
        SeparableMinos separableMinos = calculator.getSeparableMinos();
        int index = separableMinos.toIndex(currentMino);

        return minoFields.recursiveStream()
                .filter(minoField -> minoField.getMaxIndex() <= index)
                .map(minoField -> {
                    // outerで、最終的に使用されるブロック と すでに使っているブロックが重ならないことを確認
                    ColumnField lastOuterField = minoField.getOuterField();
                    if (!lastOuterField.canMerge(outerColumnField))
                        return null;

                    OperationWithKey currentOperations = currentMino.toMinoOperationWithKey();
                    long currentDeleteKey = currentOperations.getNeedDeletedKey();
                    long currentUsingKey = currentOperations.getUsingKey();

                    // いま置こうとしているミノと、それまでの結果に矛盾があるか確認
                    boolean isContradiction = currentDeleteKey != 0L && minoField.getOperationsStream()
                            .anyMatch(operationWithKey -> {
                                long deletedKey = operationWithKey.getNeedDeletedKey();
                                long usingKey = operationWithKey.getUsingKey();
                                return (currentUsingKey & deletedKey) != 0L && (usingKey & currentDeleteKey) != 0L;
                            });

                    // 矛盾があるときはスキップ
                    if (isContradiction)
                        return null;

                    // 使用されるブロックを算出
                    ColumnField usingBlock = lastOuterField.freeze(calculator.getHeight());
                    usingBlock.merge(outerColumnField);

                    return new RecursiveMinoField(currentMino, minoField, usingBlock, separableMinos);
                })
                .filter(Objects::nonNull);
    }
}
