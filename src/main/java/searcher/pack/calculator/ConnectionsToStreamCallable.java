package searcher.pack.calculator;

import common.datastore.OperationWithKey;
import core.column_field.ColumnField;
import searcher.pack.SeparableMinos;
import searcher.pack.connections.ColumnFieldConnection;
import searcher.pack.connections.ColumnFieldConnections;
import searcher.pack.mino_field.RecursiveMinoField;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.separable_mino.SeparableMino;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class ConnectionsToStreamCallable implements Callable<Stream<RecursiveMinoField>> {
    private final SolutionsCalculator calculator;
    private final ColumnField initColumnField;
    private final ColumnField outerColumnField;
    private final ColumnField limitOuterField;

    public ConnectionsToStreamCallable(SolutionsCalculator calculator, ColumnField initColumnField, ColumnField outerColumnField, ColumnField limitOuterField) {
        this.calculator = calculator;
        this.initColumnField = initColumnField;
        this.outerColumnField = outerColumnField;
        this.limitOuterField = limitOuterField;
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
            return calculate(innerField, freeze, currentMino);
        }
        return Stream.empty();
    }

    // columnField = inner
    // outerColumnField = outer only
    private Stream<RecursiveMinoField> calculate(ColumnField columnField, ColumnField outerColumnField, SeparableMino currentMino) {
        // 最初の関数呼び出しで通ることはない
        // 全てが埋まったとき、それまでの手順を解とする
        if (calculator.isFilled(columnField))
            return parseWhenFilled(outerColumnField, currentMino);

        // 最初の関数呼び出しで通ることはない
        // すでに探索済みのフィールドなら、その情報を利用する
        RecursiveMinoFields minoFields = calculator.getRecursiveMinoFields(columnField);
        if (minoFields == null)
            return Stream.empty();

        return parseWhenNext(outerColumnField, currentMino, minoFields);
    }

    private Stream<RecursiveMinoField> parseWhenFilled(ColumnField outerColumnField, SeparableMino currentMino) {
        // 置くブロック以外がすでに埋まっていると仮定したとき、正しく接着できる順があるか確認
        SeparableMinos separableMinos = calculator.getSeparableMinos();
        RecursiveMinoField result = new RecursiveMinoField(currentMino, outerColumnField.freeze(calculator.getHeight()), separableMinos);
        return Stream.of(result);
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
