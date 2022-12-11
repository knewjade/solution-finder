package entry.setup;

import common.ValidPiecesPool;
import common.buildup.BuildUpStream;
import common.datastore.*;
import common.iterable.CombinationIterable;
import common.pattern.PatternGenerator;
import common.tetfu.common.ColorConverter;
import concurrent.HarddropReachableThreadLocal;
import concurrent.ILockedReachableThreadLocal;
import concurrent.SoftdropTOnlyReachableThreadLocal;
import core.FinderConstant;
import core.action.reachable.Reachable;
import core.column_field.ColumnField;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import entry.DropType;
import entry.EntryPoint;
import entry.Verify;
import entry.path.BuildUpListUpThreadLocal;
import entry.path.ForPathSolutionFilter;
import entry.path.ReducePatternGenerator;
import entry.path.output.FumenParser;
import entry.path.output.MyFile;
import entry.path.output.OneFumenParser;
import entry.path.output.SequenceFumenParser;
import entry.setup.filters.*;
import entry.setup.functions.CombinationFunctions;
import entry.setup.functions.OrderFunctions;
import entry.setup.functions.SetupFunctions;
import entry.setup.operation.FieldOperation;
import entry.setup.output.CSVSetupOutput;
import entry.setup.output.LinkSetupOutput;
import entry.setup.output.SetupOutput;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import lib.Stopwatch;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.separable_mino.AllMinoFactory;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

        // Setup free field
        Field freeField = settings.getFreeField();

        // Setup not filled field
        Field notFilledField = settings.getNotFilledField();

        // Setup max height
        int maxHeight = settings.getMaxHeight();
        Verify.maxClearLineUnder10(maxHeight);

        // Show input field & NoHoles is On?
        {
            for (int y = maxHeight - 1; 0 <= y; y--) {
                StringBuilder builder = new StringBuilder();
                for (int x = 0; x < 10; x++) {
                    if (!initField.isEmpty(x, y))
                        builder.append('X');
                    else if (!needFilledField.isEmpty(x, y))
                        builder.append('*');
                    else if (!freeField.isEmpty(x, y))
                        builder.append('+');
                    else if (!notFilledField.isEmpty(x, y))
                        builder.append('_');
                    else {
                        builder.append('.');
                    }
                }
                output(builder.toString());
            }
        }

        // Setup min depth
        int minDepth = Verify.depth(needFilledField);  // 最低でも必要なミノ数

        // Setup patterns
        List<String> patterns = settings.getPatterns();
        PatternGenerator generator = Verify.patterns(patterns, minDepth);

        // ミノを置く最大個数を計算
        boolean isLocalSearch = !settings.isCombination() || settings.getNumOfPieces() != Integer.MAX_VALUE;
        int maxDepth = generator.getDepth();

        {
            // 実際における個数が少なかったら更新
            Field fieldForMaxDepth = FieldFactory.createField(maxHeight);
            for (int y = 0; y < maxHeight; y++)
                fieldForMaxDepth.fillLine(y);
            fieldForMaxDepth.reduce(notFilledField);
            int fieldMaxDepth = Verify.depth(fieldForMaxDepth);
            maxDepth = Math.min(fieldMaxDepth, maxDepth);
        }

        {
            // --n-piecesで指定があったらそれも考慮する
            int numOfPieces = settings.getNumOfPieces();
            if (numOfPieces < maxDepth) {
                maxDepth = numOfPieces;
            }
        }

        output();

        // ========================================

        // Output user-defined
        DropType dropType = settings.getDropType();
        output("# Initialize / User-defined");
        output("Max height: " + maxHeight);
        output("Kicks: " + settings.getKicksName().toLowerCase());
        output("Drop: " + dropType.name().toLowerCase());
        output("Operations: " + settings.getAddOperations().stream().map(FieldOperation::toName).collect(Collectors.joining(" -> ")));
        output("Exclude: " + settings.getExcludeType().name().toLowerCase());
        output("Using N pieces:" + (isLocalSearch ? " == " : " <= ") + maxDepth);

        if (!settings.isCombination())
            output("Using hold: " + (settings.isUsingHold() ? "use" : "avoid"));

        // Output patterns
        output("Searching patterns [" + (settings.isCombination() ? "combination" : "order") + "]:");
        for (String pattern : patterns.subList(0, Math.min(5, patterns.size())))
            output("  " + pattern);

        if (5 < patterns.size())
            output(String.format("  ... and more, total %s lines", patterns.size()));

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
        Supplier<MinoRotation> minoRotationSupplier = settings.createMinoRotationSupplier();
        ColorConverter colorConverter = new ColorConverter();
        SizedBit sizedBit = decideSizedBitSolutionWidth(maxHeight);
        TaskResultHelper taskResultHelper = new BasicMinoPackingHelper();
        SolutionFilter solutionFilter = new ForPathSolutionFilter(generator, maxHeight);
        ThreadLocal<BuildUpStream> buildUpStreamThreadLocal = createBuildUpStreamThreadLocal(minoRotationSupplier, dropType, maxHeight);
        boolean use180Rotation = this.settings.getDropType().uses180Rotation();
        FumenParser oneFumenParser = createFumenParser(settings.isTetfuSplit(), minoFactory, minoRotationSupplier.get(), colorConverter, use180Rotation);

        // ミノリストの作成
        long deleteKeyMask = getDeleteKeyMask(notFilledField, initField, needFilledField, freeField, maxHeight);
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

        // 組み合わせか順番かで処理を変更する
        SetupFunctions setupFunctions = createSetupFunctions(settings.isCombination(), generator, buildUpStreamThreadLocal, initField, maxDepth, settings.isUsingHold());

        // ホールを取り除く
        SetupSolutionFilter filter = setupFunctions.getSetupSolutionFilter();
        // ホールを持ってはいけないエリアがある場合は、新たにフィルターを追加する
        switch (settings.getExcludeType()) {
            case Holes: {
                if (freeField.isEmpty())
                    filter = new SimpleHolesFilter(maxHeight).and(filter);
                else
                    filter = new SimpleHolesWithFreeFilter(maxHeight, freeField).and(filter);
                break;
            }
            case StrictHoles: {
                if (freeField.isEmpty())
                    filter = new StrictHolesFilter(maxHeight).and(filter);
                else
                    filter = new StrictHolesWithFreeFilter(maxHeight, freeField).and(filter);
                break;
            }
            default: {
                // noop
            }
        }

        // 探索
        SetupPackSearcher searcher = new SetupPackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper, needFillFields, separableMinos, needFilledField);
        List<Result> results = getResults(initField, sizedBit, buildUpStreamThreadLocal, searcher);

        // 探索結果を操作に変換
        List<List<MinoOperationWithKey>> resultOperations = results.stream()
                .map(Result::getMemento)
                .map(memento -> memento.getSeparableMinoStream(sizedBit.getWidth()))
                .map(stream -> stream.map(SeparableMino::toMinoOperationWithKey).collect(Collectors.toList()))
                .collect(Collectors.toList());

        // 必要があればローカルサーチをする
        List<SetupTemp> tempOperations = localSearchIfNeed(notFilledField, maxHeight, generator, isLocalSearch, resultOperations, maxDepth, minoFactory, minoShifter, buildUpStreamThreadLocal, initField);

        // 結果のフィルタリング
        SetupSolutionFilter finalFilter = filter;
        Map<BlockField, List<SetupResult>> resultMap = new HashMap<>();
        tempOperations.stream()
                .map(setupTemp -> {
                    List<MinoOperationWithKey> solution = setupTemp.getSolution();

                    // フィールドに変換
                    Field field = initField.freeze(maxHeight);
                    for (OperationWithKey operation : solution) {
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
                    for (FieldOperation operation : settings.getAddOperations())
                        operation.operate(testField);

                    testField.clearLine();

                    SetupResult setupResult = new SetupResult(solution, field, testField);
                    return new Pair<>(setupTemp.getKeyField(), setupResult);
                })
                .filter(pair -> finalFilter.test(pair.getValue()))
                .forEach((pair) -> {
                    List<SetupResult> setupResults = resultMap.computeIfAbsent(pair.getKey(), (v) -> new ArrayList<>());
                    setupResults.add(pair.getValue());
                });

        output("     Found solutions = " + resultMap.size());
        if (isLocalSearch) {
            output("     Found sub solutions = " + resultMap.values().stream().mapToInt(List::size).sum());
        }

        stopwatch.stop();
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();

        // ========================================

        output("# Output file");

        SetupOutput setupOutput = createOutput(settings.getOutputType(), minoFactory, colorConverter, buildUpStreamThreadLocal, oneFumenParser, setupFunctions);
        SetupResults setupResults = new SetupResults(resultMap);
        setupOutput.output(setupResults, initField, sizedBit);

        output();

        // ========================================

        output("# Finalize");
        output("done");
    }

    private SetupOutput createOutput(OutputType outputType, MinoFactory minoFactory, ColorConverter colorConverter, ThreadLocal<BuildUpStream> buildUpStreamThreadLocal, FumenParser fumenParser, SetupFunctions setupFunctions) throws FinderInitializeException, FinderExecuteException {
        switch (outputType) {
            case CSV:
                return new CSVSetupOutput(settings, setupFunctions, fumenParser, buildUpStreamThreadLocal);
            case HTML:
                return new LinkSetupOutput(settings, setupFunctions, fumenParser, buildUpStreamThreadLocal, minoFactory, colorConverter);
        }
        throw new FinderExecuteException("Unsupported format: format=" + outputType);
    }

    private FumenParser createFumenParser(
            boolean isTetfuSplit, MinoFactory minoFactory, MinoRotation minoRotation, ColorConverter colorConverter, boolean use180Rotation
    ) {
        if (isTetfuSplit)
            return new SequenceFumenParser(minoFactory, minoRotation, colorConverter, use180Rotation);
        return new OneFumenParser(minoFactory, colorConverter);
    }

    private List<SetupTemp> localSearchIfNeed(Field notFilledField, int maxHeight, PatternGenerator generator, boolean isLocalSearch, List<List<MinoOperationWithKey>> resultOperations, int numOfPieces, MinoFactory minoFactory, MinoShifter minoShifter, ThreadLocal<BuildUpStream> buildUpStreamThreadLocal, Field initField) throws FinderExecuteException {
        if (!isLocalSearch) {
            return resultOperations.stream()
                    .map(o -> new SetupTemp(o, maxHeight))
                    .collect(Collectors.toList());
        }

        // 使っていないミノに対してローカルサーチする
        output("     Search sub solutions for '--n-pieces'");

        // 可能性のあるすべてのミノを列挙しておく
        AllMinoFactory factory = new AllMinoFactory(minoFactory, minoShifter, 10, maxHeight, Long.MAX_VALUE);
        List<FieldOperationWithKey> allMinos = factory.create().stream().map(FieldOperationWithKey::new).collect(Collectors.toList());

        // PieceごとのList<FieldOperationWithKey>を事前に計算しておく
        EnumMap<Piece, List<FieldOperationWithKey>> minoEachPieceMap = new EnumMap<>(Piece.class);
        for (Piece piece : Piece.values()) {
            List<FieldOperationWithKey> minos = allMinos.stream()
                    .filter(mino -> mino.getPiece() == piece)
                    .collect(Collectors.toList());
            minoEachPieceMap.put(piece, minos);
        }

        // 探索をさらに進める
        return resultOperations.stream()
                .flatMap((operationWithKeys) -> {
                    // 必要なミノを数える
                    PieceCounter pieceCounter = new PieceCounter(operationWithKeys.stream().map(MinoOperationWithKey::getPiece));
                    int numOfUsedPieces = pieceCounter.getBlocks().size();

                    // 必要な数以上使っている
                    if (numOfPieces < numOfUsedPieces) {
                        return Stream.empty();
                    }

                    // 必要な数だけ使っている
                    if (numOfPieces == numOfUsedPieces) {
                        return Stream.of(new SetupTemp(operationWithKeys, maxHeight));
                    }

                    // 必要な数を使っていない
                    // フィールド作成  // すでにおかれたピース+おくことができない場所
                    Field field = initField.freeze(maxHeight);
                    for (MinoOperationWithKey operation : operationWithKeys) {
                        Field pieceField = FieldFactory.createField(maxHeight);
                        pieceField.put(operation.getMino(), operation.getX(), operation.getY());
                        pieceField.insertWhiteLineWithKey(operation.getNeedDeletedKey());
                        field.merge(pieceField);
                    }
                    field.merge(notFilledField);

                    // 探索する
                    int needNumOfPieces = numOfPieces - numOfUsedPieces;
                    return generator.blockCountersStream()
                            .flatMap((allUsablePieceCounter) -> {
                                PieceCounter noUsedPieceCounter = allUsablePieceCounter.removeAndReturnNew(pieceCounter);

                                // 使えるミノ組み合わせを列挙する
                                CombinationIterable<Piece> iterable = new CombinationIterable<>(noUsedPieceCounter.getBlocks(), needNumOfPieces);
                                return StreamSupport.stream(iterable.spliterator(), false)
                                        .map(LinkedList::new)
                                        .peek(blocks -> blocks.sort(Comparator.comparing(Piece::getNumber)))
                                        .flatMap(blocks -> localSearch(operationWithKeys, field, blocks, minoEachPieceMap, maxHeight, buildUpStreamThreadLocal, initField, null, 0));
                            })
                            .map((solution) -> new SetupTemp(operationWithKeys, solution, maxHeight));
                })
                .collect(Collectors.toList());
    }

    private Stream<? extends List<MinoOperationWithKey>> localSearch(List<MinoOperationWithKey> operationWithKeys, Field field, LinkedList<Piece> pieces, EnumMap<Piece, List<FieldOperationWithKey>> minoEachPieceMap, int maxHeight, ThreadLocal<BuildUpStream> buildUpStreamThreadLocal, Field initField, Piece prev, int prevUsingIndex) {
        Stream<? extends List<MinoOperationWithKey>> stream = Stream.empty();

        Piece piece = pieces.pollFirst();
        List<FieldOperationWithKey> minos = minoEachPieceMap.get(piece);
        int startIndex = prev == piece ? prevUsingIndex + 1 : 0;
        for (int index = startIndex; index < minos.size(); index++) {
            FieldOperationWithKey fieldOperationWithKey = minos.get(index);
            Field minoField = fieldOperationWithKey.getField();
            if (field.canMerge(minoField)) {
                // 次の手順
                LinkedList<MinoOperationWithKey> newOperations = new LinkedList<>(operationWithKeys);
                newOperations.add(fieldOperationWithKey.getOperation());

                if (pieces.isEmpty()) {
                    // 全てのPieceを使った

                    // 地形の中で組むことができるoperationsを一つ作成
                    BuildUpStream buildUpStream = buildUpStreamThreadLocal.get();
                    Optional<List<MinoOperationWithKey>> result = buildUpStream.existsValidBuildPattern(initField, newOperations)
                            .findFirst();

                    // 地形の中で組むことができるものがあるときは結果として記録する
                    if (result.isPresent()) {
                        stream = Stream.concat(stream, Stream.of(result.get()));
                    }
                } else {
                    // 次のフィールド
                    Field freeze = field.freeze(maxHeight);
                    freeze.merge(minoField);

                    stream = Stream.concat(stream, localSearch(newOperations, freeze, pieces, minoEachPieceMap, maxHeight, buildUpStreamThreadLocal, initField, piece, index));
                }
            }
        }
        pieces.addFirst(piece);

        return stream;
    }

    private SetupFunctions createSetupFunctions(boolean isCombination, PatternGenerator generator, ThreadLocal<BuildUpStream> buildUpStreamThreadLocal, Field initField, int maxDepth, boolean isUsingHold) {
        if (isCombination)
            return new CombinationFunctions();

        int depth = maxDepth + (isUsingHold ? 1 : 0);
        ReducePatternGenerator reduce = new ReducePatternGenerator(generator, Math.min(generator.getDepth(), depth));
        ValidPiecesPool piecesPool = new ValidPiecesPool(reduce, maxDepth, isUsingHold);
        return new OrderFunctions(buildUpStreamThreadLocal, piecesPool, initField);
    }

    private long getDeleteKeyMask(Field notFilledField, Field initField, Field needFilledField, Field freeField, int maxHeight) {
        Field freeze = notFilledField.freeze(maxHeight);
        freeze.inverse();
        freeze.merge(initField);
        freeze.merge(needFilledField);
        freeze.merge(freeField);
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

    private ThreadLocal<BuildUpStream> createBuildUpStreamThreadLocal(
            Supplier<MinoRotation> minoRotationSupplier, DropType dropType, int maxClearLine
    ) throws FinderInitializeException {
        ThreadLocal<? extends Reachable> reachableThreadLocal = createReachableThreadLocal(minoRotationSupplier, dropType, maxClearLine);
        return new BuildUpListUpThreadLocal(reachableThreadLocal, maxClearLine);
    }

    private ThreadLocal<? extends Reachable> createReachableThreadLocal(
            Supplier<MinoRotation> minoRotationSupplier, DropType dropType, int maxClearLine
    ) throws FinderInitializeException {
        boolean use180Rotation = dropType.uses180Rotation();

        switch (dropType) {
            case Harddrop:
                return new HarddropReachableThreadLocal(maxClearLine);
            case Softdrop:
            case Softdrop180:
                return new ILockedReachableThreadLocal(minoRotationSupplier, maxClearLine, use180Rotation);
            case SoftdropTOnly:
                return new SoftdropTOnlyReachableThreadLocal(minoRotationSupplier, maxClearLine, use180Rotation);
        }
        throw new FinderInitializeException("Unsupported droptype: droptype=" + dropType);
    }

    private void output() throws FinderExecuteException {
        output("");
    }

    public void output(String str) throws FinderExecuteException {
        try {
            logWriter.append(str).append(LINE_SEPARATOR);
        } catch (IOException e) {
            throw new FinderExecuteException(e);
        }

        if (settings.isLogOutputToConsole())
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
