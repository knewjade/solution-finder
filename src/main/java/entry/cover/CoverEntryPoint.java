package entry.cover;

import common.comparator.PiecesNumberComparator;
import common.cover.*;
import common.cover.reachable.LastSoftdropReachableForCover;
import common.cover.reachable.ReachableForCover;
import common.cover.reachable.ReachableForCoverWrapper;
import common.datastore.MinoOperationWithKey;
import common.datastore.Pair;
import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.pattern.PatternGenerator;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import common.tetfu.field.ColoredFieldView;
import core.FinderConstant;
import core.action.reachable.*;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import entry.DropType;
import entry.EntryPoint;
import entry.Verify;
import entry.path.output.MyFile;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import exceptions.FinderTerminateException;
import lib.Stopwatch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CoverEntryPoint implements EntryPoint {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final CoverSettings settings;
    private final BufferedWriter logWriter;

    public CoverEntryPoint(CoverSettings settings) throws FinderInitializeException {
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
        int height = 24;

        ColorConverter colorConverter = new ColorConverter();

        List<CoverParameter> parameters = settings.getParameters();

        output("# Setup Field");

        for (CoverParameter parameter : parameters) {
            // Setup field
            Field field = parameter.getField();
            Verify.field(field);

            ColoredField coloredField = ColoredFieldFactory.createGrayField(field);
            parameter.getOperationList().forEach(minoOperationWithKey -> {
                ColorType colorType = colorConverter.parseToColorType(minoOperationWithKey.getPiece());
                Field minoField = minoOperationWithKey.createMinoField(height);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < 10; x++) {
                        if (minoField.exists(x, y)) {
                            coloredField.setColorType(colorType, x, y);
                        }
                    }
                }
            });

            output(String.format("[%s]", parameter.getLabel()));
            output(ColoredFieldView.toStringWithType(coloredField, Math.min(coloredField.getUsingHeight() + 1, height)));
        }

        output("Using hold: " + (settings.isUsingHold() ? "use" : "avoid"));
        output("Kicks: " + settings.getKicksName().toLowerCase());
        output("Drop: " + settings.getDropType().name().toLowerCase());
        output("Mode: " + settings.getCoverModes().name().toLowerCase());

        if (CoverModes.isNLinesMode(settings.getCoverModes())) {
            output("Max Softdrop Times: " + this.settings.getMaxSoftdropTimes().orElse(-1));
            output("Max Clear Line Times: " + this.settings.getMaxClearLineTimes().orElse(-1));
        }

        output("StartingB2B: " + settings.getStartingB2B());
        output("Priority: " + (settings.isUsingPriority() ? "yes" : "no"));
        output("Last drop: " + settings.getLastSoftdrop());
        output("Version: " + FinderConstant.VERSION);

        // ========================================
        output("Searching patterns:");

        // Setup patterns
        List<String> patterns = settings.getPatterns();
        PatternGenerator generator = Verify.patterns(patterns);

        // Output patterns
        for (String pattern : patterns.subList(0, Math.min(5, patterns.size())))
            output("  " + pattern);

        if (5 < patterns.size())
            output(String.format("  ... and more, total %s lines", patterns.size()));

        // 探索パターンの列挙
        List<LongPieces> piecesList = generator.blocksStream()
                .collect(Collectors.toSet())
                .stream()
                .sorted(PiecesNumberComparator::comparePieces)
                .map(Pieces::getPieces)
                .map(LongPieces::new)
                .collect(Collectors.toList());

        output();

        output("Searching pattern size ( no dup. ) = " + piecesList.size());

        output();

        // ========================================

        output("# Calculate");
        output("  -> Stopwatch start");
        output("     ... calculating");

        Stopwatch stopwatch = Stopwatch.createStartedStopwatch();

        MinoRotation minoRotation = settings.createMinoRotationSupplier().get();
        ReachableForCover reachableForCover = getReachableForCover(minoRotation, settings.getLastSoftdrop(), height);
        List<BitSet> results = new ArrayList<>();

        // Check
        Cover cover = createCover(minoRotation);

        boolean isUsingPrioritized = settings.isUsingPriority();

        int parameterSize = parameters.size();
        piecesList.forEach(pieces -> {
            BitSet result = new BitSet(parameterSize);

            List<Piece> pieceList = pieces.blockStream().collect(Collectors.toList());

            for (int index = 0; index < parameterSize; index++) {
                CoverParameter parameter = parameters.get(index);
                List<MinoOperationWithKey> operations = parameter.getOperationList();
                Field field = parameter.getField();

                int maxDepth = operations.size();
                boolean success = settings.isUsingHold() ? cover.canBuildWithHold(
                        field, operations.stream(), pieceList, height, reachableForCover, maxDepth
                ) : cover.canBuild(
                        field, operations.stream(), pieceList, height, reachableForCover, maxDepth
                );

                if (success) {
                    result.set(index);

                    if (isUsingPrioritized) {
                        break;
                    }
                }
            }

            results.add(result);
        });

        // Collect
        List<AtomicInteger> successCounters = IntStream.range(0, parameterSize)
                .mapToObj(i -> new AtomicInteger(0))
                .collect(Collectors.toList());

        AtomicInteger andCounter = new AtomicInteger(0);
        AtomicInteger orCounter = new AtomicInteger(0);

        for (BitSet result : results) {
            boolean and = true;
            boolean or = false;

            for (int parameterIndex = 0; parameterIndex < parameterSize; parameterIndex++) {
                boolean success = result.get(parameterIndex);
                if (success) {
                    successCounters.get(parameterIndex).incrementAndGet();
                }

                and &= success;
                or |= success;
            }

            if (and) {
                andCounter.incrementAndGet();
            }

            if (or) {
                orCounter.incrementAndGet();
            }
        }

        stopwatch.stop();

        output("     ... done");
        output("  -> Stopwatch stop : " + stopwatch.toMessage(TimeUnit.MILLISECONDS));

        output();

        // ========================================

        output("# Output");

        output("success:");

        assert successCounters.size() == parameterSize;

        List<OutputData> outputDataList = new ArrayList<>();
        for (int index = 0; index < parameterSize; index++) {
            AtomicInteger counter = successCounters.get(index);
            CoverParameter parameter = parameters.get(index);
            outputDataList.add(new OutputData(index, counter, parameter));
        }

        assert outputDataList.size() == parameterSize;

        Comparator<OutputData> comparator = getOutputDataComparator();
        outputDataList.sort(comparator);

        int all = piecesList.size();

        if (settings.getShowsAccumulation()) {
            // Pairs of (andCounter, orCounter) for each parameter
            List<Pair<AtomicInteger, AtomicInteger>> counters = new ArrayList<>();
            for (int parameterIndex = 0; parameterIndex < parameterSize; parameterIndex++) {
                counters.add(new Pair<>(new AtomicInteger(0), new AtomicInteger(0)));
            }
            assert counters.size() == parameterSize;

            // Recalculate And/Or counters to match parameter output order
            for (BitSet result : results) {
                boolean and = true;
                boolean or = false;

                for (int outputDataIndex = 0; outputDataIndex < outputDataList.size(); outputDataIndex++) {
                    int parameterIndex = outputDataList.get(outputDataIndex).getIndex();
                    boolean success = result.get(parameterIndex);

                    and &= success;
                    or |= success;

                    if (and) {
                        counters.get(outputDataIndex).getKey().incrementAndGet();
                    }

                    if (or) {
                        counters.get(outputDataIndex).getValue().incrementAndGet();
                    }
                }
            }

            // The last result of the accumulation matches the overall result
            assert counters.get(parameterSize - 1).getKey().get() == andCounter.get();
            assert counters.get(parameterSize - 1).getValue().get() == orCounter.get();

            for (int outputDataIndex = 0; outputDataIndex < outputDataList.size(); outputDataIndex++) {
                OutputData outputData = outputDataList.get(outputDataIndex);
                int success = outputData.getSuccessCount();
                Pair<AtomicInteger, AtomicInteger> countersPair = counters.get(outputDataIndex);
                output(String.format("%.2f %% [%d/%d] (accum. OR=%d, AND=%d): %s",
                        success * 100.0 / all, success, all, countersPair.getValue().get(), countersPair.getKey().get(),
                        outputData.getUrl()
                ));
            }
        } else {
            for (OutputData outputData : outputDataList) {
                int success = outputData.getSuccessCount();
                output(String.format("%.2f %% [%d/%d]: %s",
                        success * 100.0 / all, success, all, outputData.getUrl()
                ));
            }
        }

        output(">>>");
        {
            int i = orCounter.get();
            output(String.format("OR  = %.2f %% [%d/%d]", i * 100.0 / all, i, all));
        }
        {
            int i = andCounter.get();
            output(String.format("AND = %.2f %% [%d/%d]", i * 100.0 / all, i, all));
        }

        // ========================================

        // baseファイル
        MyFile base = new MyFile(settings.getOutputBaseFilePath());
        base.mkdirs();

        try (BufferedWriter bw = base.newBufferedWriter()) {
            // Header
            bw.write("sequence,");
            bw.write(
                    IntStream.range(0, parameterSize)
                            .mapToObj(outputDataList::get)
                            .mapToInt(OutputData::getIndex)
                            .mapToObj(parameters::get)
                            .map(CoverParameter::getLabel)
                            .collect(Collectors.joining(","))
            );
            bw.newLine();

            // Body
            for (int index = 0; index < piecesList.size(); index++) {
                LongPieces pieces = piecesList.get(index);
                String key = pieces.blockStream().map(Piece::getName).collect(Collectors.joining());
                bw.write(key + ",");

                BitSet result = results.get(index);
                String body = IntStream.range(0, parameterSize)
                        .mapToObj(outputDataList::get)
                        .mapToInt(OutputData::getIndex)
                        .mapToObj(result::get)
                        .map(success -> success ? "O" : "X")
                        .collect(Collectors.joining(","));
                bw.write(body);

                bw.newLine();
            }

            bw.flush();
        } catch (IOException e) {
            throw new FinderExecuteException("Failed to output file", e);
        }
    }

    private Cover createCover(MinoRotation minoRotation) {
        boolean use180Rotation = this.settings.getDropType().uses180Rotation();

        CoverModes modes = this.settings.getCoverModes();
        switch (modes) {
            case Normal: {
                if (!this.settings.getMaxSoftdropTimes().isPresent() && !this.settings.getMaxClearLineTimes().isPresent())
                    return ClearLinesCover.createNormal();
                return createClearLinesCover(1, false);
            }
            case B2BContinuous: {
                return new B2BContinuousCover(minoRotation, use180Rotation);
            }
            case TSpinMini: {
                return TSpinCover.createTSpinMiniCover(minoRotation, use180Rotation, this.settings.getStartingB2B());
            }
            case TSpinSingle: {
                return TSpinCover.createRegularTSpinCover(minoRotation, 1, this.settings.getStartingB2B(), use180Rotation);
            }
            case TSpinDouble: {
                return TSpinCover.createRegularTSpinCover(minoRotation, 2, this.settings.getStartingB2B(), use180Rotation);
            }
            case TSpinTriple: {
                return TSpinCover.createRegularTSpinCover(minoRotation, 3, this.settings.getStartingB2B(), use180Rotation);
            }
            case Tetris: {
                return new TetrisCover();
            }
            case TetrisEnd: {
                return new TetrisEndCover();
            }
            case OneLine: {
                return createClearLinesCover(1, false);
            }
            case OneLineOrPC: {
                return createClearLinesCover(1, true);
            }
            case TwoLines: {
                return createClearLinesCover(2, false);
            }
            case TwoLinesOrPC: {
                return createClearLinesCover(2, true);
            }
            case ThreeLines: {
                return createClearLinesCover(3, false);
            }
            case ThreeLinesOrPC: {
                return createClearLinesCover(3, true);
            }
            case FourLines: {
                return createClearLinesCover(4, false);
            }
            case FourLinesOrPC: {
                return createClearLinesCover(4, true);
            }
            default: {
                throw new IllegalStateException("Unknown cover mode: " + modes);
            }
        }
    }

    private Cover createClearLinesCover(int requiredClearLines, boolean allowsPc) {
        Optional<Integer> maxSoftdropTimes = this.settings.getMaxSoftdropTimes();
        Optional<Integer> maxClearLineTimes = this.settings.getMaxClearLineTimes();
        return ClearLinesCover.createEqualToOrGreaterThan(
                requiredClearLines, allowsPc, maxSoftdropTimes.orElse(Integer.MAX_VALUE), maxClearLineTimes.orElse(Integer.MAX_VALUE)
        );
    }

    private ReachableForCover getReachableForCover(
            MinoRotation minoRotation, int lastSoftdrop, int maxY
    ) {
        DropType dropType = settings.getDropType();
        Reachable reachable = createReachable(minoRotation, dropType, maxY);
        if (lastSoftdrop <= 0) {
            return new ReachableForCoverWrapper(reachable);
        }
        return new LastSoftdropReachableForCover(reachable, minoRotation, maxY, lastSoftdrop, dropType.uses180Rotation());
    }

    private Reachable createReachable(MinoRotation minoRotation, DropType dropType, int maxY) {
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        boolean use180Rotation = dropType.uses180Rotation();

        switch (dropType) {
            case Harddrop:
                return new HarddropReachable(minoFactory, minoShifter, maxY);
            case Softdrop:
            case Softdrop180:
                return ReachableFacade.createLocked(minoFactory, minoShifter, minoRotation, maxY, use180Rotation);
            case SoftdropTOnly:
            case SoftdropTOnly180:
                return new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, maxY, use180Rotation);
            case TSpinZero:
            case TSpinZero180:
                return new TSpinOrHarddropReachable(minoFactory, minoShifter, minoRotation, maxY, 0, false, use180Rotation);
            case TSpinMini:
            case TSpinMini180:
                return new TSpinOrHarddropReachable(minoFactory, minoShifter, minoRotation, maxY, 1, false, use180Rotation);
            case TSpinSingle:
            case TSpinSingle180:
                return new TSpinOrHarddropReachable(minoFactory, minoShifter, minoRotation, maxY, 1, true, use180Rotation);
            case TSpinDouble:
            case TSpinDouble180:
                return new TSpinOrHarddropReachable(minoFactory, minoShifter, minoRotation, maxY, 2, true, use180Rotation);
            case TSpinTriple:
            case TSpinTriple180:
                return new TSpinOrHarddropReachable(minoFactory, minoShifter, minoRotation, maxY, 3, true, use180Rotation);
        }

        throw new IllegalStateException("Unknown drop type: " + dropType);
    }

    private Comparator<OutputData> getOutputDataComparator() {
        Comparator<OutputData> comparator = getAscendingDataComparator();
        if (settings.isAscendingSort()) {
            return comparator;
        }
        return comparator.reversed();
    }

    private Comparator<OutputData> getAscendingDataComparator() {
        CoverSortType sortType = settings.getCoverSortType();
        switch (sortType) {
            case Input:
                return Comparator.comparingInt(OutputData::getIndex);
            case SuccessRate:
                return Comparator.comparingInt(OutputData::getSuccessCount);
        }
        throw new IllegalStateException("Unknown sort type: " + sortType);
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

    private static class OutputData {
        private final int index;
        private final AtomicInteger counter;
        private final CoverParameter parameter;

        public OutputData(int index, AtomicInteger counter, CoverParameter parameter) {
            this.index = index;
            this.counter = counter;
            this.parameter = parameter;
        }

        public int getIndex() {
            return index;
        }

        public int getSuccessCount() {
            return counter.get();
        }

        public String getUrl() {
            return parameter.getUrl();
        }
    }
}
