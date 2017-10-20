package entry.setup;

import common.datastore.BlockField;
import common.datastore.Operation;
import common.datastore.OperationWithKey;
import common.datastore.Operations;
import common.parser.OperationTransform;
import common.tetfu.common.ColorConverter;
import concurrent.LockedReachableThreadLocal;
import core.FinderConstant;
import core.column_field.ColumnField;
import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnFieldView;
import core.column_field.ColumnSmallField;
import core.field.BlockFieldView;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import entry.EntryPoint;
import entry.Verify;
import entry.path.output.MyFile;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.AllPassedSolutionFilter;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.solutions.OnDemandBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.Result;
import searcher.pack.task.SetupPackSearcher;
import searcher.pack.task.TaskResultHelper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SetupEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final SetupSettings settings;
    private final BufferedWriter logWriter;

    public SetupEntryPoint(SetupSettings settings) throws FinderInitializeException {
        this.settings = settings;

        // ログファイルの出力先を整備
        String logFilePath = settings.getLogFilePath();
        MyFile logFile = new MyFile(logFilePath);

        logFile.mkdirs();
        logFile.verify();

        try {
            this.logWriter = logFile.newBufferedWriter();
        } catch (IOException e) {
            throw new FinderInitializeException(e);
        }
    }

    @Override
    public void run() throws FinderException {
        output("# Setup Field");

        // Setup field
        Field field = settings.getNeedFillField();

        // Setup max clear line
        int maxClearLine = settings.getMaxClearLine();
        Verify.maxClearLineUnder10(maxClearLine);

        // Output field
        output(FieldView.toString(field, maxClearLine));

        // Setup reserved blocks
        BlockField reservedBlocks = settings.getReservedBlock();
        if (settings.isRevered()) {
            Verify.reservedBlocks(reservedBlocks);
            output("");
            output("# Setup Reserved blocks");
            output(BlockFieldView.toString(reservedBlocks));
        }

        // Setup inverse field
        output("# Setup Inverse Field");

        Field goalField = settings.getDoNotFillField();

        output(FieldView.toString(goalField, maxClearLine));

        output();

        // ========================================

        // Output user-defined
        output("# Initialize / User-defined");
        output("Max clear lines: " + maxClearLine);
        output("Using hold: " + (settings.isUsingHold() ? "use" : "avoid"));
        output("Drop: " + settings.getDropType().name().toLowerCase());
        output("Searching patterns:");

        // Setup patterns
        List<String> patterns = settings.getPatterns();
//        IBlocksGenerator generator = Verify.patterns(patterns, maxDepth);

        // Output patterns
        for (String pattern : patterns)
            output("  " + pattern);

        output();

        // ========================================

        // Setup core
        output("# Initialize / System");
        int core = Runtime.getRuntime().availableProcessors();

        // Output system-defined
        output("Version = " + FinderConstant.VERSION);
        output("Available processors = " + core);

        output();

        // ========================================

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        ColorConverter colorConverter = new ColorConverter();
        SizedBit sizedBit = decideSizedBitSolutionWidth(maxClearLine);
//        SolutionFilter solutionFilter = new ForPathSolutionFilter(generator, maxClearLine);
        SolutionFilter solutionFilter = createSRSSolutionFilter(sizedBit, FieldFactory.createField(maxClearLine));
//        solutionFilter = new AllPassedSolutionFilter();

        output();

        // ========================================

        output("# Cache");
//
//        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
//        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
//        Map<ColumnField, RecursiveMinoFields> solutions = calculator.calculate();
//        MappedBasicSolutions basicSolutions = new MappedBasicSolutions(solutions);
//        ColumnSmallField columnField = ColumnFieldFactory.createField("" +
//                        "______" +
//                        "XXX___" +
//                        "XXX___" +
//                        "XXX___"
//                , 4);
//        MinoFields parse = basicSolutions.parse(columnField);
//        Set<ColumnField> columnFields = basicSolutions.getSolutions().keySet();
//        for (ColumnField column : columnFields) {
//            System.out.println(ColumnFieldView.toString(column, 6, 4));
//            System.out.println("---");
//        }
//        System.out.println("====");
//        System.out.println(parse.stream().count());
//        parse.stream()
//                .forEach(s -> {
//                    BlockCounter blockCounter = s.getBlockCounter();
//                    System.out.println(blockCounter);
//                    System.out.println(ColumnFieldView.toString(s.getOuterField(), 6, 4));
//                    System.out.println(s.getOperationsStream().count());
//                });


        // 置く可能性のある場所

        // 必ず置かないブロック
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, goalField);

        // Create
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
//        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit, needFillBoard);
//        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
//        BasicSolutions basicSolutions = new MappedBasicSolutions(calculate);
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();

        // 必ず置く必要があるブロック
        ArrayList<BasicSolutions> basicSolutions = new ArrayList<>();
        List<InOutPairField> pairs = InOutPairField.createInOutPairFields(sizedBit, field);
        List<ColumnField> needFillFields = new ArrayList<>();
        for (int index = 0; index < pairs.size(); index++) {
            InOutPairField pairField = pairs.get(index);
            ColumnField innerField = pairField.getInnerField();
            System.out.println(ColumnFieldView.toString(innerField, sizedBit));
            System.out.println("===");
            OnDemandBasicSolutions solutions = new OnDemandBasicSolutions(separableMinos, sizedBit, innerField.getBoard(0));

            basicSolutions.add(solutions);
            needFillFields.add(innerField);

            if (index == 0) {
                ColumnSmallField field1 = ColumnFieldFactory.createField(0L);
                MinoFields parse = solutions.parse(field1);
                System.out.println(parse.stream().count());
            } else if (index == 1) {
                ColumnSmallField field1 = ColumnFieldFactory.createField("" +
                        "_XX" +
                        "_XX" +
                        "_XX" +
                        "_XX", 4);
                MinoFields parse = solutions.parse(field1);
                System.out.println(parse.stream().count());
            }
        }

        SetupPackSearcher searcher = new SetupPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper, needFillFields);
        try {
            List<Result> results = searcher.toList();
            System.out.println(results.size());
            for (Result result : results) {
                List<OperationWithKey> list = result.getMemento().getOperationsStream(sizedBit.getWidth()).collect(Collectors.toList());
                Field allField = FieldFactory.createField(sizedBit.getHeight());
                Operations operations = OperationTransform.parseToOperations(allField, list, sizedBit.getHeight());
                List<? extends Operation> operationList = operations.getOperations();
                for (Operation operation : operationList) {
                    Mino mino = minoFactory.create(operation.getBlock(), operation.getRotate());
                    int x = operation.getX();
                    int y = operation.getY();
                    allField.put(mino, x, y);
                    System.out.println(mino);
                }
                System.out.println(FieldView.toString(allField));
                System.out.println("*****");
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new FinderExecuteException(e);
        }
    }

    private SizedBit decideSizedBitSolutionWidth(int maxClearLine) {
        return maxClearLine <= 4 ? new SizedBit(3, maxClearLine) : new SizedBit(2, maxClearLine);
    }

    private SolutionFilter createSRSSolutionFilter(SizedBit sizedBit, Field initField) {
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(sizedBit.getHeight());
        return new SRSValidSolutionFilter(initField, lockedReachableThreadLocal, sizedBit);
    }

    private void output() throws FinderExecuteException {
        output("");
    }

    private void output(String str) throws FinderExecuteException {
        try {
            logWriter.append(str).append(LINE_SEPARATOR);
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }

        if (settings.isOutputToConsole())
            System.out.println(str);
    }

    private void flush() throws FinderExecuteException {
        try {
            logWriter.flush();
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }
    }

    @Override
    public void close() throws FinderTerminateException {
        try {
            flush();
            logWriter.close();
        } catch (IOException | FinderExecuteException e) {
            throw new FinderTerminateException(e);
        }
    }
}
