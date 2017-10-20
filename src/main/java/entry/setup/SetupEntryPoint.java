package entry.setup;

import common.datastore.BlockField;
import common.datastore.Operation;
import common.datastore.OperationWithKey;
import common.datastore.Operations;
import common.parser.OperationInterpreter;
import common.parser.OperationTransform;
import common.parser.OperationWithKeyInterpreter;
import common.pattern.IBlocksGenerator;
import common.tetfu.common.ColorConverter;
import core.FinderConstant;
import core.column_field.ColumnField;
import core.field.BlockFieldView;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import entry.EntryPoint;
import entry.Verify;
import entry.path.ForPathSolutionFilter;
import entry.path.output.MyFile;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import helper.EasyPath;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.mino_fields.RecursiveMinoFields;
import searcher.pack.solutions.BasicSolutionsCalculator;
import searcher.pack.solutions.MappedBasicSolutions;
import searcher.pack.task.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
        Field field2 = settings.getField();

        // Setup max clear line
        int maxClearLine = settings.getMaxClearLine();
        Verify.maxClearLineUnder10(maxClearLine);

        // Output field
        output(FieldView.toString(field2, maxClearLine));

        // Setup reserved blocks
        BlockField reservedBlocks = settings.getReservedBlock();
        if (settings.isRevered()) {
            Verify.reservedBlocks(reservedBlocks);
            output("");
            output("# Setup Reserved blocks");
            output(BlockFieldView.toString(reservedBlocks));
        }

        // Setup inverse field
        String goalFieldMarks = FieldView.toString(field2, maxClearLine, "");
        Field goalField = FieldFactory.createInverseField(goalFieldMarks);
        Verify.field(goalField);

        // Setup max depth
        int maxDepth = Verify.maxDepth(goalField, maxClearLine);  // セットアップに必要なミノ数

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
        IBlocksGenerator generator = Verify.patterns(patterns, maxDepth);

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
        output("Need Pieces = " + maxDepth);

        output();

        // ========================================

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        ColorConverter colorConverter = new ColorConverter();
        SizedBit sizedBit = decideSizedBitSolutionWidth(maxClearLine);
        SolutionFilter solutionFilter = new ForPathSolutionFilter(generator, maxClearLine);

        // Holdができるときは必要なミノ分（maxDepth + 1）だけを取り出す。maxDepth + 1だけないときはブロックの個数をそのまま指定
        output("# Enumerate pieces");
        boolean isUsingHold = settings.isUsingHold();
        int piecesDepth = generator.getDepth();
        output("Piece pop count = " + (isUsingHold && maxDepth < piecesDepth ? maxDepth + 1 : maxDepth));

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

        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, goalField);

        // Create
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        BasicSolutionsCalculator calculator = new BasicSolutionsCalculator(separableMinos, sizedBit);
        Map<ColumnField, RecursiveMinoFields> calculate = calculator.calculate();
        BasicSolutions basicSolutions =  new MappedBasicSolutions(calculate);
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();

        // パフェ手順の列挙
        PerfectPackSearcher searcher = new PerfectPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
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
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new FinderExecuteException(e);
        }
    }

    private SizedBit decideSizedBitSolutionWidth(int maxClearLine) {
        return maxClearLine <= 4 ? new SizedBit(3, maxClearLine) : new SizedBit(2, maxClearLine);
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
