package _experimental.putter;

import _experimental.cycle1.EasyTetfu;
import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.MappedBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.Result;
import searcher.pack.task.TaskResultHelper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PutterMain3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        // ある特定の形に組める組み合わせを列挙
        int width = 3;
        int height = 4;
        SizedBit sizedBit = new SizedBit(width, height);

        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, ? extends MinoFields> calculate = calculator.calculate();


        Field field = FieldFactory.createInverseField("" +
                "__________" +
                "___XXX____" +
                "XXXXXXX___" +
                "XXXXXX____"
        );
        System.out.println(FieldView.toString(field, height));

        System.out.println("===");

        LockedReachableThreadLocal reachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        Field emptyField = FieldFactory.createField(height);
        SolutionFilter solutionFilter = new SRSValidSolutionFilter(emptyField, reachableThreadLocal, sizedBit);

        BasicSolutions basicSolutions = new MappedBasicSolutions(calculate, solutionFilter);
        PackSearcher searcher = createSearcher(sizedBit, basicSolutions, field, solutionFilter);

        List<Result> results = searcher.toList();

        EasyTetfu easyTetfu = new EasyTetfu();
        BlockCounter allBlocks = new BlockCounter(Block.valueList());
        for (Result result : results) {
            List<OperationWithKey> operationsList = result.getMemento().getOperationsStream(width).collect(Collectors.toList());
            BlockCounter blockCounter = new BlockCounter(operationsList.stream().map(OperationWithKey::getMino).map(Mino::getBlock));
            if (allBlocks.containsAll(blockCounter)) {
                String encode = easyTetfu.encode(emptyField, operationsList, height);
                System.out.println(blockCounter.getBlocks());
                System.out.println(encode);
            }
        }
    }

    private static PackSearcher createSearcher(SizedBit sizedBit, BasicSolutions basicSolutions, Field initField, SolutionFilter solutionFilter) throws InterruptedException, ExecutionException {
        // フィールドの変換
        int width = sizedBit.getWidth();
        int height = sizedBit.getHeight();
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(width, height, initField);

        // 探索準備
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        return new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
    }
}
