package entry.setup;

import common.buildup.BuildUpStream;
import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.order.ForwardOrderLookUp;
import common.pattern.PatternGenerator;
import common.tetfu.common.ColorConverter;
import common.tree.SuccessTreeHead;
import core.FinderConstant;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import entry.DropType;
import entry.EntryPoint;
import entry.Verify;
import entry.path.ForPathSolutionFilter;
import entry.path.HarddropBuildUpListUpThreadLocal;
import entry.path.LockedBuildUpListUpThreadLocal;
import entry.path.output.MyFile;
import entry.path.output.OneFumenParser;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import lib.Stopwatch;
import output.HTMLBuilder;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.solutions.OnDemandBasicSolutions;
import searcher.pack.task.BasicMinoPackingHelper;
import searcher.pack.task.Result;
import searcher.pack.task.SetupPackSearcher;
import searcher.pack.task.TaskResultHelper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        // Setup init field
        Field initField = settings.getInitField();
        Verify.field(initField);

        // Setup need filled field
        Field needFilledField = settings.getNeedFilledField();
        Verify.needFilledField(needFilledField);

        // Setup not filled field
        Field notFilledField = settings.getNotFilledField();

        // Setup max height
        int maxHeight = settings.getMaxHeight();
        Verify.maxClearLineUnder10(maxHeight);

        // Show input field
        BlockField reservedBlocks = settings.getReservedBlock();
        if (settings.isReserved()) {
            Verify.reservedBlocks(reservedBlocks);

            for (int y = maxHeight - 1; 0 <= y; y--) {
                StringBuilder builder = new StringBuilder();
                for (int x = 0; x < 10; x++) {
                    if (reservedBlocks.getBlock(x, y) != null)
                        builder.append(reservedBlocks.getBlock(x, y).getName());
                    else if (!initField.isEmpty(x, y))
                        builder.append('X');
                    else if (!needFilledField.isEmpty(x, y))
                        builder.append('*');
                    else if (!notFilledField.isEmpty(x, y))
                        builder.append('_');
                    else
                        builder.append('.');
                }
                output(builder.toString());
            }
        } else {
            for (int y = maxHeight - 1; 0 <= y; y--) {
                StringBuilder builder = new StringBuilder();
                for (int x = 0; x < 10; x++) {
                    if (!initField.isEmpty(x, y))
                        builder.append('X');
                    else if (!needFilledField.isEmpty(x, y))
                        builder.append('*');
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
        DropType dropType = settings.getDropType();
        output("# Initialize / User-defined");
        output("Max height: " + maxHeight);
        output("Drop: " + dropType.name().toLowerCase());
        output("Searching patterns:");

        // Setup patterns
        List<String> patterns = settings.getPatterns();
        PatternGenerator generator = Verify.patterns(patterns, minDepth);

        // Output patterns
        for (String pattern : patterns)
            output("  " + pattern);

        // Setup output file
        MyFile base = new MyFile(settings.getOutputBaseFilePath());
        base.mkdirs();
        base.verify();

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

        output("# Search");
        output("  -> Stopwatch start");
        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        ColorConverter colorConverter = new ColorConverter();
        SizedBit sizedBit = decideSizedBitSolutionWidth(maxHeight);
        TaskResultHelper taskResultHelper = new BasicMinoPackingHelper();
        SolutionFilter solutionFilter = new ForPathSolutionFilter(generator, maxHeight);
        ThreadLocal<BuildUpStream> buildUpStreamThreadLocal = createBuildUpStreamThreadLocal(dropType, maxHeight);
        OneFumenParser fumenParser = new OneFumenParser(minoFactory, colorConverter);

        // ミノリストの作成
        long deleteKeyMask = getDeleteKeyMask(notFilledField, maxHeight);
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit, deleteKeyMask);

        // 絶対に置かないブロック
        List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(sizedBit, notFilledField);

        // 絶対に置く必要があるブロック
        ArrayList<BasicSolutions> basicSolutions = new ArrayList<>();
        List<ColumnField> needFillFields = InOutPairField.createInnerFields(sizedBit, needFilledField);

        for (ColumnField innerField : needFillFields) {
            OnDemandBasicSolutions solutions = new OnDemandBasicSolutions(separableMinos, sizedBit, innerField.getBoard(0));
            basicSolutions.add(solutions);
        }

        // 解をフィルタリングする準備を行う
        SetupSolutionFilter filter;
        if (settings.isCombination()) {
            filter = new CombinationFilter();
        } else if (settings.isUsingHold()) {
            filter = new OrderWithHoldFilter(generator, sizedBit);
        } else {
            filter = new OrderWithoutHoldFilter(generator, sizedBit);
        }

        // 探索
        SetupPackSearcher searcher = new SetupPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper, needFillFields, separableMinos, needFilledField);
        List<Result> results = getResults(initField, sizedBit, buildUpStreamThreadLocal, searcher)
                .stream()
                .filter(filter::test)
                .collect(Collectors.toList());
        output("     Found solution = " + results.size());

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();

        // ========================================

        output("# Output file");

        HTMLBuilder<FieldHTMLColumn> htmlBuilder = new HTMLBuilder<>("Setup result");

        results.parallelStream()
                .forEach(result -> {
                    List<MinoOperationWithKey> operationWithKeys = result.getMemento()
                            .getSeparableMinoStream(sizedBit.getWidth())
                            .map(SeparableMino::toMinoOperationWithKey)
                            .collect(Collectors.toList());

                    Field allField = initField.freeze(maxHeight);
                    for (OperationWithKey operation : operationWithKeys) {
                        Field pieceField = FieldFactory.createField(maxHeight);
                        Mino mino = minoFactory.create(operation.getPiece(), operation.getRotate());
                        int x = operation.getX();
                        int y = operation.getY();
                        pieceField.put(mino, x, y);
                        pieceField.insertWhiteLineWithKey(operation.getNeedDeletedKey());
                        allField.merge(pieceField);
                    }

                    // 譜面の作成
                    String encode = fumenParser.parse(operationWithKeys, initField, maxHeight);

                    String name = operationWithKeys.stream().map(OperationWithKey::getPiece).map(Piece::getName).collect(Collectors.joining());
                    String link = String.format("<a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a>", encode, name);
                    String line = String.format("<div>%s</div>", link);

                    FieldHTMLColumn column = new FieldHTMLColumn(allField, maxHeight);
                    htmlBuilder.addColumn(column, line);
                });

        ArrayList<FieldHTMLColumn> columns = new ArrayList<>(htmlBuilder.getRegisteredColumns());
        columns.sort(Comparator.comparing(FieldHTMLColumn::getTitle).reversed());
        try (BufferedWriter bufferedWriter = base.newBufferedWriter()) {
            for (String line : htmlBuilder.toList(columns, true)) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }

        output();

        // ========================================

        output("# Finalize");
        output("done");
    }

    private long getDeleteKeyMask(Field notFilledField, int maxHeight) {
        Field freeze = notFilledField.freeze(maxHeight);
        freeze.inverse();
        return freeze.clearLineReturnKey();
    }

    private List<Result> getResults(Field initField, SizedBit sizedBit, ThreadLocal<BuildUpStream> buildUpStreamThreadLocal, SetupPackSearcher searcher) throws FinderExecuteException {
        try {
            List<Result> candidates = searcher.toList();
            return candidates.parallelStream()
                    .filter(result -> {
                        LinkedList<MinoOperationWithKey> operationWithKeys = result.getMemento()
                                .getSeparableMinoStream(sizedBit.getWidth())
                                .map(SeparableMino::toMinoOperationWithKey)
                                .collect(Collectors.toCollection(LinkedList::new));

                        // 地形の中で組むことができるoperationsを一つ作成
                        BuildUpStream buildUpStream = buildUpStreamThreadLocal.get();
                        List<MinoOperationWithKey> sampleOperations = buildUpStream.existsValidBuildPatternDirectly(initField, operationWithKeys)
                                .findFirst()
                                .orElse(Collections.emptyList());

                        // 地形の中で組むことができるものがないときはスキップ
                        return !sampleOperations.isEmpty();
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new FinderExecuteException(e);
        }
    }

    private SizedBit decideSizedBitSolutionWidth(int maxClearLine) {
        return maxClearLine <= 4 ? new SizedBit(3, maxClearLine) : new SizedBit(2, maxClearLine);
    }

    private ThreadLocal<BuildUpStream> createBuildUpStreamThreadLocal(DropType dropType, int maxClearLine) throws FinderInitializeException {
        switch (dropType) {
            case Softdrop:
                return new LockedBuildUpListUpThreadLocal(maxClearLine);
            case Harddrop:
                return new HarddropBuildUpListUpThreadLocal(maxClearLine);
        }
        throw new FinderInitializeException("Unsupport droptype: droptype=" + dropType);
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

interface SetupSolutionFilter {
    boolean test(Result result);
}

class CombinationFilter implements SetupSolutionFilter {
    @Override
    public boolean test(Result result) {
        return true;
    }
}

class OrderWithHoldFilter implements SetupSolutionFilter {
    private final SuccessTreeHead head;
    private final SizedBit sizedBit;

    OrderWithHoldFilter(PatternGenerator generator, SizedBit sizedBit) {
        // パターンツリーを作成
        int depth = generator.getDepth();
        ForwardOrderLookUp lookup = new ForwardOrderLookUp(depth, depth);
        SuccessTreeHead head = new SuccessTreeHead();
        generator.blocksStream()
                .map(Pieces::getPieces)
                .flatMap(lookup::parse)
                .sequential()
                .forEach(head::register);

        this.head = head;
        this.sizedBit = sizedBit;
    }

    @Override
    public boolean test(Result result) {
        Stream<Piece> stream = result.getMemento()
                .getSeparableMinoStream(sizedBit.getWidth())
                .map(SeparableMino::toMinoOperationWithKey)
                .map(OperationWithKey::getPiece);
        Pieces pieces = new LongPieces(stream);
        return head.checksWithHold(pieces);
    }
}

class OrderWithoutHoldFilter implements SetupSolutionFilter {
    private final SuccessTreeHead head;
    private final SizedBit sizedBit;

    OrderWithoutHoldFilter(PatternGenerator generator, SizedBit sizedBit) {
        // パターンツリーを作成
        int depth = generator.getDepth();
        ForwardOrderLookUp lookup = new ForwardOrderLookUp(depth, depth);
        SuccessTreeHead head = new SuccessTreeHead();
        generator.blocksStream()
                .map(Pieces::getPieces)
                .flatMap(lookup::parse)
                .sequential()
                .forEach(head::register);

        this.head = head;
        this.sizedBit = sizedBit;
    }

    @Override
    public boolean test(Result result) {
        Stream<Piece> stream = result.getMemento()
                .getSeparableMinoStream(sizedBit.getWidth())
                .map(SeparableMino::toMinoOperationWithKey)
                .map(OperationWithKey::getPiece);
        Pieces pieces = new LongPieces(stream);
        return head.checksWithoutHold(pieces);
    }
}