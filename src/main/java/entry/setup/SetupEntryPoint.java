package entry.setup;

import common.datastore.BlockField;
import common.datastore.Operation;
import common.datastore.OperationWithKey;
import common.datastore.Operations;
import common.parser.OperationTransform;
import common.pattern.IBlocksGenerator;
import core.FinderConstant;
import core.column_field.ColumnField;
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
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SolutionFilter;
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

        // Setup need filled field
        Field needFilledField = settings.getNeedFilledField();
        Verify.field(needFilledField);

        // Setup not filled field
        Field notFilledField = settings.getNotFilledField();

        // Setup max clear line
        int maxClearLine = settings.getMaxClearLine();
        Verify.maxClearLineUnder10(maxClearLine);

        // Setup reserved blocks
        BlockField reservedBlocks = settings.getReservedBlock();
        if (settings.isReserved()) {
            Verify.reservedBlocks(reservedBlocks);

            for (int y = maxClearLine - 1; 0 <= y; y--) {
                StringBuilder builder = new StringBuilder();
                for (int x = 0; x < 10; x++) {
                    if (reservedBlocks.getBlock(x, y) != null)
                        builder.append(reservedBlocks.getBlock(x, y).getName());
                    else if (!needFilledField.isEmpty(x, y))
                        builder.append('X');
                    else if (!notFilledField.isEmpty(x, y))
                        builder.append('_');
                    else
                        builder.append('.');
                }
                output(builder.toString());
            }
        } else {
            for (int y = maxClearLine - 1; 0 <= y; y--) {
                StringBuilder builder = new StringBuilder();
                for (int x = 0; x < 10; x++) {
                    if (!needFilledField.isEmpty(x, y))
                        builder.append('X');
                    else if (!notFilledField.isEmpty(x, y))
                        builder.append('_');
                    else
                        builder.append('.');
                }
                output(builder.toString());
            }
        }

        // Setup min depth
        int minDepth = Verify.minDepth(needFilledField);  // 最低でも必要なミノ数

        output();

        // ========================================

        // Output user-defined
        output("# Initialize / User-defined");
        output("Max clear lines: " + maxClearLine);
//        output("Using hold: " + (settings.isUsingHold() ? "use" : "avoid"));
//        output("Drop: " + settings.getDropType().name().toLowerCase());
        output("Searching patterns:");

        // Setup patterns
        List<String> patterns = settings.getPatterns();
        IBlocksGenerator generator = Verify.patterns(patterns, minDepth);

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
        output("Need Pieces = " + minDepth);

        output();

        // ========================================

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        SizedBit sizedBit = decideSizedBitSolutionWidth(maxClearLine);
        SolutionFilter solutionFilter = new ForPathSolutionFilter(generator, maxClearLine);

        output();

        // ========================================

        // 必ず置かないブロック
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, notFilledField);

        // Create
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();

        // 必ず置く必要があるブロック
        ArrayList<BasicSolutions> basicSolutions = new ArrayList<>();
        List<InOutPairField> pairs = InOutPairField.createInOutPairFields(sizedBit, needFilledField);
        List<ColumnField> needFillFields = new ArrayList<>();
        for (InOutPairField pairField : pairs) {
            ColumnField innerField = pairField.getInnerField();
            OnDemandBasicSolutions solutions = new OnDemandBasicSolutions(separableMinos, sizedBit, innerField.getBoard(0));
            basicSolutions.add(solutions);
            needFillFields.add(innerField);
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
