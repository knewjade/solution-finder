package searcher.pack.task.setup;

import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnSmallField;
import core.field.Field;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.connections.StreamColumnFieldConnections;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_field.MinoField;
import searcher.pack.mino_field.RecursiveMinoField;
import searcher.pack.mino_field.WrappedMinoField;
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.PackingTask;
import searcher.pack.task.Result;

import java.util.List;
import java.util.stream.Stream;

// Width=3のBasicSolutionsのためのタスク
public class MinoSetupTaskWidthForWidth3 implements PackingTask {
    private static final PackingTask EMPTY_TASK = null;

    private final PackSearcher searcher;
    private final ColumnField innerField;
    private final ColumnField outerField;
    private final MinoFieldMemento memento;
    private final int index;
    private final SeparableMinos separableMinos;
    private final Field needFilledField;

    public MinoSetupTaskWidthForWidth3(PackSearcher searcher, ColumnField innerField, MinoFieldMemento memento, int index, SeparableMinos separableMinos, Field needFilledField) {
        this(searcher, innerField, searcher.getInOutPairFields().get(index).getOuterField(), memento, index, separableMinos, needFilledField);
    }

    private MinoSetupTaskWidthForWidth3(PackSearcher searcher, ColumnField innerField, ColumnField outerField, MinoFieldMemento memento, int index, SeparableMinos separableMinos, Field needFilledField) {
        this.searcher = searcher;
        this.innerField = innerField;
        this.outerField = outerField;
        this.memento = memento;
        this.index = index;
        this.separableMinos = separableMinos;
        this.needFilledField = needFilledField;
    }

    @Override
    public Stream<Result> compute() {
        if (searcher.isFilled(innerField, index)) {
            // innerFieldが埋まっている
            List<InOutPairField> inOutPairFields = searcher.getInOutPairFields();
            if (index == searcher.getLastIndex()) {
                // 最後の計算
                SizedBit sizedBit = searcher.getSizedBit();
                ColumnField lastOuterField = inOutPairFields.get(index).getOuterField();
                long innerFieldBoard = lastOuterField.getBoard(0) >> sizedBit.getMaxBitDigit();
                MinoFieldMemento nextMemento = memento.skip();
                return searcher.getTaskResultHelper().fixResult(searcher, innerFieldBoard, nextMemento);
            } else {
                // 途中の計算  // 自分で計算する
                return createNextTasks(null);
            }
        } else {
            MinoFields minoFields = searcher.getSolutions(index).parse(innerField);

            // innerFieldが埋まっていない
            if (index == searcher.getLastIndex()) {
                // 最後の計算
                return minoFields.stream().parallel()
                        .flatMap(this::splitAndFixResult);
            } else {
                // 途中の計算
                return minoFields.stream().parallel()
                        .flatMap(this::split);
            }
        }
    }

    private PackingTask createTask(PackSearcher searcher, ColumnField innerField, MinoFieldMemento memento, int index) {
        SizedBit sizedBit = searcher.getSizedBit();

        // 必要なミノを左にずらす
        Field freeze = needFilledField.freeze(sizedBit.getHeight());
        freeze.slideLeft(sizedBit.getWidth());
        return new MinoSetupTaskWidthForWidth3(searcher, innerField, memento, index, separableMinos, needFilledField);
    }

    private Stream<Result> split(MinoField minoField) {
        ColumnField outerField = searcher.getInOutPairFields().get(index).getOuterField();
        ColumnField minoOuterField = minoField.getOuterField();

        // 注目範囲外outerで重なりがないか確認
        if (outerField.canMerge(minoOuterField)) {
            // 有効なおきかた
            return createNextTasks(minoField);
        }

        return Stream.empty();
    }

    private PackingTask checkAndCreateTask(ColumnField innerField, MinoFieldMemento memento, int index) {
        SolutionFilter solutionFilter = searcher.getSolutionFilter();
        if (solutionFilter.test(memento))
            return createTask(searcher, innerField, memento, index);
        return EMPTY_TASK;
    }

    private Stream<Result> splitAndFixResult(MinoField minoField) {
        ColumnField outerField = searcher.getInOutPairFields().get(index).getOuterField();
        ColumnField minoOuterField = minoField.getOuterField();

        // 注目範囲外outerで重なりがないか確認
        if (outerField.canMerge(minoOuterField)) {
            // 有効なおきかた
            SizedBit sizedBit = searcher.getSizedBit();
            ColumnField mergedOuterField = outerField.freeze(searcher.getSizedBit().getHeight());
            mergedOuterField.merge(minoOuterField);

            long innerFieldBoard = mergedOuterField.getBoard(0) >> sizedBit.getMaxBitDigit();
            MinoFieldMemento nextMemento = memento.concat(minoField);
            return searcher.getTaskResultHelper().fixResult(searcher, innerFieldBoard, nextMemento);
        }

        return Stream.empty();
    }

    private Stream<Result> createNextTasks(MinoField minoField) {
        SizedBit sizedBit = searcher.getSizedBit();
        List<SeparableMino> allMinos = separableMinos.getMinos();

        if (minoField != null) {
            // innerFieldを再計算
            ColumnField mergedInnerField = ColumnFieldFactory.createField();
            minoField.getSeparableMinoStream().forEach(mino -> mergedInnerField.merge(mino.getColumnField()));

            // outerFieldの計算
            ColumnField minoOuterField = minoField.getOuterField();
            ColumnField mergedOuterField = outerField.freeze(sizedBit.getHeight());
            mergedOuterField.merge(minoOuterField);

            return createNextTasksAndPutMino(minoField, mergedInnerField, mergedOuterField, sizedBit, allMinos);
        } else {
            return createNextTasksAndPutMino(minoField, innerField, outerField, sizedBit, allMinos);
        }
    }

    // 現在の結果を追加して、次のフィールドの必要な部分を埋めるパターンを探索
    private Stream<Result> createNextTasksAndPutMino(MinoField minoField, ColumnField innerField, ColumnField outerField, SizedBit sizedBit, List<SeparableMino> minos) {
        // ミノを追加フィールドをそのまま追加
        MinoFieldMemento nextMemento;
        if (minoField != null) {
            nextMemento = memento.concat(minoField);
        } else {
            nextMemento = memento.skip();
        }
        Stream<Result> result = checkAndCreateTaskToStream(outerField, nextMemento, sizedBit);

        StreamColumnFieldConnections connections = new StreamColumnFieldConnections(minos, innerField, sizedBit);
        int height = sizedBit.getHeight();
        Stream<Result> stream = connections.getConnectionStream()
                .filter(connection -> {
                    SeparableMino mino = connection.getMino();
                    return outerField.canMerge(mino.getColumnField()) && !needFilledField.canMerge(mino.getField());
                })
                .flatMap(connection -> {
                    // innerFieldの計算
                    ColumnField mergedInnerField = connection.getInnerField();

                    // outerFieldの計算
                    ColumnField mergedOuterField = outerField.freeze(height);
                    mergedOuterField.merge(connection.getOuterField());

                    SeparableMino currentMino = connection.getMino();
                    MinoField nextMinoField;
                    if (minoField != null) {
                        nextMinoField = new WrappedMinoField(currentMino, minoField, mergedOuterField, separableMinos);
                    } else {
                        nextMinoField = new RecursiveMinoField(currentMino, mergedOuterField, separableMinos);
                    }

                    List<SeparableMino> allMinos = separableMinos.getMinos();
                    List<SeparableMino> minos2 = allMinos.subList(this.separableMinos.toIndex(currentMino) + 1, allMinos.size());

                    return createNextTasksAndPutMino(nextMinoField, mergedInnerField, mergedOuterField, sizedBit, minos2);
                });

        return Stream.concat(result, stream);
    }

    private Stream<Result> checkAndCreateTaskToStream(ColumnField outerField, MinoFieldMemento nextMemento, SizedBit sizedBit) {
        long innerFieldBoard = outerField.getBoard(0) >> sizedBit.getMaxBitDigit();
        ColumnSmallField innerField = ColumnFieldFactory.createField(innerFieldBoard);
        PackingTask task = checkAndCreateTask(innerField, nextMemento, index + 1);
        if (!isValidTask(task)) {
            return Stream.empty();
        }
        return task.compute();
    }

    private boolean isValidTask(PackingTask task) {
        return task != EMPTY_TASK;
    }
}
