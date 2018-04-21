package entry.setup;

import common.buildup.BuildUpStream;
import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import common.datastore.Operation;
import common.datastore.OperationWithKey;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.pattern.PatternGenerator;
import common.tetfu.common.ColorConverter;
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
import entry.path.ValidPiecesPool;
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
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetupEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final int FIELD_WIDTH = 10;

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
        int minDepth = Verify.depth(needFilledField);  // 最低でも必要なミノ数

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

        // 有効ミノ順poolを作成
        Field fieldForMaxDepth = FieldFactory.createField(maxHeight);
        for (int y = 0; y < maxHeight; y++)
            fieldForMaxDepth.fillLine(y);
        fieldForMaxDepth.reduce(notFilledField);
        int maxDepth = Verify.depth(fieldForMaxDepth);

        ValidPiecesPool validPiecesPool = !settings.isCombination() ? new ValidPiecesPool(generator, maxDepth, settings.isUsingHold()) : null;

        // 解をフィルタリングする準備を行う
        // TODO: Excule with holes (merge)
        SetupSolutionFilter filter;
        if (settings.isCombination()) {
            filter = new CombinationFilter();
        } else {
            filter = new OrderFilter(validPiecesPool, sizedBit);
        }

        // ホールを取り除く
        if (!settings.isAllowedHoles()) {
            // TODO: softdropの時は複雑なものにしたい
            filter = new SimpleHolesFilter(maxHeight).and(filter);
        }

        // 探索
        SetupPackSearcher searcher = new SetupPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper, needFillFields, separableMinos, needFilledField);
        List<Result> results = getResults(initField, sizedBit, buildUpStreamThreadLocal, searcher);

        // TODO: 使っていないミノに対してローカルサーチする -> List<Result>

        List<SetupResult> setupResults = results.stream()
                .map(result -> {
                    // 操作に変換
                    List<MinoOperationWithKey> operationWithKeys = result.getMemento()
                            .getSeparableMinoStream(sizedBit.getWidth())
                            .map(SeparableMino::toMinoOperationWithKey)
                            .collect(Collectors.toList());

                    // フィールドに変換
                    Field field = initField.freeze(maxHeight);
                    for (OperationWithKey operation : operationWithKeys) {
                        Field pieceField = FieldFactory.createField(maxHeight);
                        Mino mino = minoFactory.create(operation.getPiece(), operation.getRotate());
                        int x = operation.getX();
                        int y = operation.getY();
                        pieceField.put(mino, x, y);
                        pieceField.insertWhiteLineWithKey(operation.getNeedDeletedKey());
                        field.merge(pieceField);
                    }

                    // テストフィールドに操作を加える
                    Field testField = field.freeze(maxHeight);
                    for (Operation operation : settings.getAddOperations())
                        testField.put(minoFactory.create(operation.getPiece(), operation.getRotate()), operation.getX(), operation.getY());

                    testField.clearLine();

                    // テストフィールドがラインが揃っているとみなす
                    for (Integer y : settings.getAssumeFilledLines())
                        testField.fillLine(y);

                    testField.clearLine();

                    return new SetupResult(result, field, testField);
                })
                .filter(filter)
                .collect(Collectors.toList());

        output("     Found solution = " + setupResults.size());

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();

        // ========================================

        output("# Output file");

        HTMLBuilder<FieldHTMLColumn> htmlBuilder = new HTMLBuilder<>("Setup result");

        setupResults.forEach(setupResult -> {
            Result result = setupResult.getResult();

            // 操作に変換
            List<MinoOperationWithKey> operationWithKeys = result.getMemento()
                    .getSeparableMinoStream(sizedBit.getWidth())
                    .map(SeparableMino::toMinoOperationWithKey)
                    .collect(Collectors.toList());

            // 譜面の作成
            String encode = fumenParser.parse(operationWithKeys, initField, maxHeight);

            // 名前の作成
            LongPieces pieces = new LongPieces(operationWithKeys.stream().map(MinoOperationWithKey::getPiece));
            if (validPiecesPool != null) {
                HashSet<LongPieces> validPieces = validPiecesPool.getValidPieces();
                BuildUpStream buildUpStream = buildUpStreamThreadLocal.get();
                Optional<LongPieces> first = buildUpStream.existsValidBuildPattern(setupResult.getRawField(), operationWithKeys)
                        .map(operations -> new LongPieces(operations.stream().map(MinoOperationWithKey::getPiece)))
                        .filter(validPieces::contains)
                        .findFirst();
                if (first.isPresent()) {
                    pieces = first.get();
                }
            }

            String name = pieces.blockStream()
                    .map(Piece::getName)
                    .collect(Collectors.joining());

            String link = String.format("<a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a>", encode, name);
            String line = String.format("<div>%s</div>", link);

            FieldHTMLColumn column = new FieldHTMLColumn(setupResult.getRawField(), maxHeight);
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

interface SetupSolutionFilter extends Predicate<SetupResult> {
    default SetupSolutionFilter and(Predicate<? super SetupResult> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }
}

class SimpleHolesFilter implements SetupSolutionFilter {
    private final int maxHeight;

    public SimpleHolesFilter(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public boolean test(SetupResult result) {
        Field field = result.getTestField();
        Field freeze = field.freeze(maxHeight);
        freeze.slideDown();
        return field.contains(freeze);
    }
}

class CombinationFilter implements SetupSolutionFilter {
    @Override
    public boolean test(SetupResult result) {
        return true;
    }
}

class OrderFilter implements SetupSolutionFilter {
    private final ValidPiecesPool validPiecesPool;
    private final SizedBit sizedBit;

    OrderFilter(ValidPiecesPool validPiecesPool, SizedBit sizedBit) {
        this.validPiecesPool = validPiecesPool;
        this.sizedBit = sizedBit;
    }

    @Override
    public boolean test(SetupResult result) {
        Stream<Piece> stream = result.getResult().getMemento()
                .getSeparableMinoStream(sizedBit.getWidth())
                .map(SeparableMino::toMinoOperationWithKey)
                .map(OperationWithKey::getPiece);
        Pieces pieces = new LongPieces(stream);
        return validPiecesPool.getValidPieces().contains(pieces);
    }
}

class SetupResult {
    private final Result result;
    private final Field rawField;
    private final Field testField;

    SetupResult(Result result, Field rawField, Field testField) {
        this.result = result;
        this.rawField = rawField;
        this.testField = testField;
    }

    public Result getResult() {
        return result;
    }

    public Field getRawField() {
        return rawField;
    }

    public Field getTestField() {
        return testField;
    }
}