package entry.setup;

import common.buildup.BuildUpStream;
import common.datastore.*;
import common.iterable.CombinationIterable;
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
import entry.path.*;
import entry.path.output.MyFile;
import entry.path.output.OneFumenParser;
import entry.setup.filters.*;
import entry.setup.functions.CombinationFunctions;
import entry.setup.functions.OrderFunctions;
import entry.setup.functions.SetupFunctions;
import entry.setup.operation.FieldOperation;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import lib.Stopwatch;
import output.HTMLBuilder;
import output.HTMLColumn;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.calculator.BasicSolutions;
import searcher.pack.memento.MinoFieldMemento;
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
import java.util.function.BiFunction;
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

        // Setup margin field
        Field marginField = settings.getMarginField();

        // Setup not filled field
        Field notFilledField = settings.getNotFilledField();

        // Setup max height
        int maxHeight = settings.getMaxHeight();
        Verify.maxClearLineUnder10(maxHeight);

        // Show input field & NoHoles is On?
        boolean existsNoHolesMargin = false;
        {
            for (int y = maxHeight - 1; 0 <= y; y--) {
                StringBuilder builder = new StringBuilder();
                for (int x = 0; x < 10; x++) {
                    if (!initField.isEmpty(x, y))
                        builder.append('X');
                    else if (!needFilledField.isEmpty(x, y))
                        builder.append('*');
                    else if (!marginField.isEmpty(x, y))
                        builder.append('.');
                    else if (!notFilledField.isEmpty(x, y))
                        builder.append('_');
                    else {
                        existsNoHolesMargin = true;
                        builder.append('+');
                    }
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
            maxDepth = fieldMaxDepth < maxDepth ? fieldMaxDepth : maxDepth;
        }

        {
            // --n-piecesで指定があったらそれも考慮する
            int numOfPieces = settings.getNumOfPieces();
            if (numOfPieces < maxDepth) {
                maxDepth = numOfPieces;
            }
        }

        // 組み合わせか順番かで処理を変更する
        SetupFunctions data = createSetupFunctions(settings.isCombination(), generator, buildUpStreamThreadLocal, initField, maxDepth, settings.isUsingHold());

        // ホールを取り除く
        SetupSolutionFilter filter = data.getSetupSolutionFilter();
        if (existsNoHolesMargin) {
            // ホールを持ってはいけないエリアがある場合は、新たにフィルターを追加する
            switch (settings.getExcludeType()) {
                case Holes: {
                    if (marginField.isPerfect())
                        filter = new SimpleHolesFilter(maxHeight).and(filter);
                    else
                        filter = new SimpleHolesWithMarginFilter(maxHeight, marginField).and(filter);
                    break;
                }
                case StrictHoles: {
                    if (marginField.isPerfect())
                        filter = new StrictHolesFilter(maxHeight).and(filter);
                    else
                        filter = new StrictHolesWithMarginFilter(maxHeight, marginField).and(filter);
                    break;
                }
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
        List<SetupTemp> tempOperations = localSearchIfNeed(notFilledField, maxHeight, generator, separableMinos, isLocalSearch, resultOperations, maxDepth);

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

        HTMLBuilder<HTMLColumn> htmlBuilder = new HTMLBuilder<>("Setup result");
        BiFunction<List<MinoOperationWithKey>, Field, String> naming = data.getNaming();

        resultMap.forEach((keyField, setupResults) -> {
            Field field = initField.freeze(maxHeight);
            for (Piece piece : Piece.values())
                field.merge(keyField.get(piece));

            HTMLColumn column = new FieldHTMLColumn(field, maxHeight);
            StringBuilder builder = new StringBuilder();

            setupResults.forEach(setupResult -> {
                // 操作に変換
                List<MinoOperationWithKey> operationWithKeys = setupResult.getSolution();

                // 譜面の作成
                String encode = fumenParser.parse(operationWithKeys, initField, maxHeight);

                // 名前の作成
                String name = naming.apply(operationWithKeys, setupResult.getRawField());

                String link = String.format("<a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a>", encode, name);
                String line = String.format("<div>%s</div>", link);

                builder.append(line);
            });

            htmlBuilder.addColumn(column, builder.toString());
        });

        ArrayList<HTMLColumn> columns = new ArrayList<>(htmlBuilder.getRegisteredColumns());
        columns.sort(Comparator.comparing(HTMLColumn::getTitle).reversed());
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

    private List<SetupTemp> localSearchIfNeed(Field notFilledField, int maxHeight, PatternGenerator generator, SeparableMinos separableMinos, boolean isLocalSearch, List<List<MinoOperationWithKey>> resultOperations, int numOfPieces) throws FinderExecuteException {
        if (!isLocalSearch)
            return resultOperations.stream().map(o -> new SetupTemp(o, maxHeight)).collect(Collectors.toList());

        // 使っていないミノに対してローカルサーチする
        output("     Search sub solutions for '--n-pieces'");

        // PieceごとのList<SeparableMino>を事前に計算しておく
        EnumMap<Piece, List<SeparableMino>> separableMinoMap = new EnumMap<>(Piece.class);
        for (Piece piece : Piece.values()) {
            List<SeparableMino> minos = separableMinos.getMinos().stream()
                    .filter(mino -> mino.toMinoOperationWithKey().getPiece() == piece)
                    .collect(Collectors.toList());
            separableMinoMap.put(piece, minos);
        }

        // 探索をさらに進める
        return resultOperations.stream()
                .flatMap((operationWithKeys) -> {
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
                    // フィールド作成  // おくことができない場所
                    Field field = FieldFactory.createField(maxHeight);
                    for (MinoOperationWithKey operation : operationWithKeys) {
                        Field pieceField = FieldFactory.createField(maxHeight);
                        pieceField.put(operation.getMino(), operation.getX(), operation.getY());
                        pieceField.insertWhiteLineWithKey(operation.getNeedDeletedKey());
                        field.merge(pieceField);
                    }
                    field.merge(notFilledField);

                    // 探索する
                    int needNumOfPieces = numOfPieces - numOfUsedPieces;
                    return generator.blockCountersStream().flatMap((allUsablePieceCounter) -> {
                        PieceCounter noUsedPieceCounter = allUsablePieceCounter.removeAndReturnNew(pieceCounter);

                        // 使えるミノ組み合わせを列挙する
                        HashSet<PieceCounter> usable = new HashSet<>();
                        CombinationIterable<Piece> iterable = new CombinationIterable<>(noUsedPieceCounter.getBlocks(), needNumOfPieces);
                        for (List<Piece> pieces : iterable)
                            usable.add(new PieceCounter(pieces));

                        return usable.stream()
                                .map(PieceCounter::getBlocks)
                                .map(LinkedList::new)
                                .flatMap(blocks -> localSearch(operationWithKeys, field, blocks, separableMinoMap, maxHeight));
                    }).map((solution) -> new SetupTemp(operationWithKeys, solution, maxHeight));
                })
                .collect(Collectors.toList());
    }

    private Stream<? extends List<MinoOperationWithKey>> localSearch(List<MinoOperationWithKey> operationWithKeys, Field field, LinkedList<Piece> pieces, EnumMap<Piece, List<SeparableMino>> separableMinoMap, int maxHeight) {
        Stream<? extends List<MinoOperationWithKey>> stream = Stream.empty();

        Piece piece = pieces.pollFirst();
        List<SeparableMino> separableMinos = separableMinoMap.get(piece);
        for (SeparableMino mino : separableMinos) {
            Field minoField = mino.getField();
            if (field.canMerge(minoField)) {
                // 次の手順
                ArrayList<MinoOperationWithKey> newOperations = new ArrayList<>(operationWithKeys);
                newOperations.add(mino.toMinoOperationWithKey());

                if (pieces.isEmpty()) {
                    // 全てのPieceを使った
                    stream = Stream.concat(stream, Stream.of(newOperations));
                } else {
                    // 次のフィールド
                    Field freeze = field.freeze(maxHeight);
                    freeze.merge(minoField);

                    stream = Stream.concat(stream, localSearch(newOperations, freeze, pieces, separableMinoMap, maxHeight));
                }
            }
        }
        pieces.addFirst(piece);

        return stream;
    }

    private SetupFunctions createSetupFunctions(boolean isCombination, PatternGenerator generator, ThreadLocal<BuildUpStream> buildUpStreamThreadLocal, Field initField, int maxDepth, boolean isUsingHold) {
        if (isCombination)
            return new CombinationFunctions();

        ReducePatternGenerator reduce = new ReducePatternGenerator(generator, maxDepth);
        ValidPiecesPool piecesPool = new ValidPiecesPool(reduce, maxDepth, isUsingHold);
        return new OrderFunctions(buildUpStreamThreadLocal, piecesPool, initField);
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

    private Stream<MinoFieldMemento> concat(Stream<MinoFieldMemento> stream, Piece piece, SeparableMinos separableMinos, Map<Piece, List<SeparableMino>> map) {
        return Stream.empty();
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