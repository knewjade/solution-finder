package searcher.pack.task;

import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnFieldView;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.field.FieldView;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.connections.StreamColumnFieldConnections;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_field.MinoField;
import searcher.pack.mino_field.RecursiveMinoField;
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.separable_mino.SeparableMino;

import java.util.List;
import java.util.stream.Stream;

// Width=2のBasicSolutionsのためのタスク
class MinoPackingTaskWidthForWidth2 implements PackingTask {
    private static final PackingTask EMPTY_TASK = null;

    private final PackSearcher searcher;
    private final ColumnField innerField;
    private final ColumnField outerField;
    private final MinoFieldMemento memento;
    private final int index;
    private final SeparableMinos separableMinos;
    private final Field needFilledField;

    MinoPackingTaskWidthForWidth2(PackSearcher searcher, ColumnField innerField, MinoFieldMemento memento, int index) {
        this(searcher, innerField, searcher.getInOutPairFields().get(index).getOuterField(), memento, index, null, null);
    }

    MinoPackingTaskWidthForWidth2(PackSearcher searcher, ColumnField innerField, MinoFieldMemento memento, int index, SeparableMinos separableMinos, Field needFilledField) {
        this(searcher, innerField, searcher.getInOutPairFields().get(index).getOuterField(), memento, index, separableMinos, needFilledField);
    }

    private MinoPackingTaskWidthForWidth2(PackSearcher searcher, ColumnField innerField, ColumnField outerField, MinoFieldMemento memento, int index, SeparableMinos separableMinos, Field needFilledField) {
        this.searcher = searcher;
        this.innerField = innerField;
        this.outerField = outerField;
        this.memento = memento;
        this.index = index;
        this.separableMinos = separableMinos;
        this.needFilledField = needFilledField;
        System.out.println(FieldView.toString(needFilledField));
    }

    @Override
    public Stream<Result> compute() {
        if (searcher.isFilled(innerField, index)) {
            // innerFieldが埋まっている
            if (index == searcher.getLastIndex()) {
                // 最後の計算
                SizedBit sizedBit = searcher.getSizedBit();
                long innerFieldBoard = outerField.getBoard(0) >> sizedBit.getMaxBitDigit();
                MinoFieldMemento nextMemento = memento.skip();
                return searcher.getTaskResultHelper().fixResult(searcher, innerFieldBoard, nextMemento);
            } else {
                // 途中の計算  // 自分で計算する
                if (needFilledField == null) {
                    MinoFieldMemento nextMemento = memento.skip();
                    long innerFieldBoard = outerField.getBoard(0) >> searcher.getSizedBit().getMaxBitDigit();
                    return createTask(searcher, innerFieldBoard, nextMemento, index + 1).compute();
                } else {
                    return go(innerField, outerField, separableMinos.getMinos(), null);
                }
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
                        .map(this::split)
                        .filter(this::isValidTask)
                        .flatMap(PackingTask::compute);
            }
        }
    }

    private Stream<Result> go(ColumnField innerField, ColumnField outerField, List<SeparableMino> separableMinoList, RecursiveMinoField minoField) {
        PackingTask c1 = c(outerField, minoField);
        Stream<Result> c = isValidTask(c1) ? c1.compute() : Stream.empty();

        StreamColumnFieldConnections connections = new StreamColumnFieldConnections(separableMinoList, innerField, searcher.getSizedBit());
        Stream<Result> stream = connections.getConnectionStream()
                .filter(connection -> {
                    ColumnField minoInnerField = connection.getInnerField();
                    ColumnField minoOuterField = connection.getOuterField();

                    System.out.println(FieldView.toString(needFilledField));
                    // 注目範囲外outerで重なりがないか確認
                    return innerField.canMerge(minoInnerField)
                            && outerField.canMerge(minoOuterField)
                            && !needFilledField.canMerge(connection.getMino().getField());
                })
                .flatMap(connection -> {
                    System.out.println(ColumnFieldView.toString(innerField, 5, 5));
                    System.out.println("======");

                    // 有効なおきかた
                    int height = searcher.getSizedBit().getHeight();

                    ColumnField mergedInnerField = innerField.freeze(height);
                    ColumnField minoInnerField = connection.getInnerField();
                    mergedInnerField.merge(minoInnerField);

                    ColumnField mergedOuterField = outerField.freeze(height);
                    ColumnField minoOuterField = connection.getOuterField();
                    mergedOuterField.merge(minoOuterField);

                    RecursiveMinoField nextMinoField;
                    SeparableMino currentMino = connection.getMino();
                    if (minoField == null) {
                        nextMinoField = new RecursiveMinoField(currentMino, mergedOuterField, separableMinos);
                    } else {
                        nextMinoField = new RecursiveMinoField(currentMino, minoField, mergedOuterField, separableMinos);
                    }

                    List<SeparableMino> allMinos = separableMinos.getMinos();
                    List<SeparableMino> minos = allMinos.subList(this.separableMinos.toIndex(currentMino) + 1, allMinos.size());

                    return go(mergedInnerField, mergedOuterField, minos, nextMinoField);
                });

        return Stream.concat(c, stream);
    }

    private PackingTask c(ColumnField outerField, MinoField minoField) {
        long innerFieldBoard = outerField.getBoard(0) >> searcher.getSizedBit().getMaxBitDigit();

        if (minoField == null) {
            MinoFieldMemento nextMemento = memento.skip();
            return createTask(searcher, innerFieldBoard, nextMemento, index + 1);
        } else {
            MinoFieldMemento nextMemento = memento.concat(minoField);
            return checkAndCreateTask(innerFieldBoard, nextMemento, index + 1);
        }
    }

    private PackingTask createTask(PackSearcher searcher, long innerFieldBoard, MinoFieldMemento memento, int index) {
        long fillBoard = searcher.getSizedBit().getFillBoard();
        ColumnSmallField over = ColumnFieldFactory.createField(innerFieldBoard & ~fillBoard);
        ColumnField outerField = searcher.getInOutPairFields().get(index).getOuterField();

        if (over.canMerge(outerField)) {
            over.merge(outerField);
            ColumnSmallField innerField = ColumnFieldFactory.createField(innerFieldBoard & fillBoard);

            SizedBit sizedBit = searcher.getSizedBit();
            Field freeze = needFilledField.freeze(sizedBit.getHeight());
            freeze.slideLeft(sizedBit.getWidth());
            return new MinoPackingTaskWidthForWidth2(searcher, innerField, over, memento, index, separableMinos, freeze);
        }

        return EMPTY_TASK;
    }

    private PackingTask split(MinoField minoField) {
        ColumnField minoOuterField = minoField.getOuterField();

        // 注目範囲外outerで重なりがないか確認
        if (outerField.canMerge(minoOuterField)) {

            // 有効なおきかた
            SizedBit sizedBit = searcher.getSizedBit();
            ColumnField mergedOuterField = outerField.freeze(sizedBit.getHeight());
            mergedOuterField.merge(minoOuterField);

            ColumnField mergedInnerField = innerField.freeze(sizedBit.getHeight());
            ColumnField minoInnerField = minoField.get();
            mergedInnerField.merge(minoInnerField);

            ColumnField mergedOuterField = outerField.freeze(height);
            ColumnField minoOuterField = connection.getOuterField();
            mergedOuterField.merge(minoOuterField);
            return go(innerField, )

//
//            long innerFieldBoard = mergedOuterField.getBoard(0) >> sizedBit.getMaxBitDigit();
//            MinoFieldMemento nextMemento = memento.concat(minoField);
//            return go(innerFieldBoard)
//            return checkAndCreateTask(innerFieldBoard, nextMemento, index + 1);
        }

        return EMPTY_TASK;
    }

    private PackingTask checkAndCreateTask(long innerFieldBoard, MinoFieldMemento memento, int index) {
        SolutionFilter solutionFilter = searcher.getSolutionFilter();
        if (solutionFilter.test(memento))
            return createTask(searcher, innerFieldBoard, memento, index);
        return EMPTY_TASK;
    }

    private Stream<Result> splitAndFixResult(MinoField minoField) {
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

    private boolean isValidTask(PackingTask task) {
        return task != EMPTY_TASK;
    }
}
