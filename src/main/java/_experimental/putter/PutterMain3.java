package _experimental.putter;

import _experimental.cycle1.EasyPath;
import _experimental.cycle1.EasyPool;
import _experimental.cycle1.EasyTetfu;
import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import searcher.pack.task.Result;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PutterMain3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        // ある特定の形に組める組み合わせを列挙
        int width = 3;
        int height = 4;

        EasyPool easyPool = new EasyPool();
        EasyPath easyPath = new EasyPath(easyPool);
        EasyTetfu easyTetfu = new EasyTetfu();

        String goalFieldMarks = "" +
                "XXXXXX____" +
                "XXXXXXX___" +
                "XXXXXXXX__" +
                "XXXXXXX___";
        Field emptyField = FieldFactory.createField(height);
        List<Result> results = easyPath.setUp(goalFieldMarks, emptyField, width, height);

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
}
