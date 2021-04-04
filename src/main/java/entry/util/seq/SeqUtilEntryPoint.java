package entry.util.seq;

import common.SyntaxException;
import common.datastore.PieceCounter;
import common.datastore.blocks.LongPieces;
import common.order.*;
import common.pattern.LoadedPatternGenerator;
import core.mino.Piece;
import entry.EntryPoint;
import entry.util.seq.equations.HoldEquation;
import entry.util.seq.equations.PieceEquation;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderTerminateException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * beta command
 */
public class SeqUtilEntryPoint implements EntryPoint {
    private final SeqUtilSettings settings;

    public SeqUtilEntryPoint(SeqUtilSettings settings) {
        this.settings = settings;
    }

    @Override
    public void run() throws FinderException {
        int length = settings.getLength();
        PieceTransformer limit = 0 < length ? new UsePieceLength(length) : new NoPieceTransformer();

        Predicate<PieceCounter> pieceCounterPredicate = createPieceCounterPredicate(settings.getPieceEquations());
        Predicate<String> expressionPredicate = createExpressionPredicate(
                settings.getExpression(), settings.getNotExpression()
        );
        SeqUtilModes mode = settings.getSeqUtilMode();
        List<String> patterns = settings.getPatterns();
        switch (mode) {
            case Pass: {
                PieceOutput output = settings.isDistinct() ? new DistinctPieceOutput() : new SimplePieceOutput();

                List<LoadedPatternGenerator> generators = createGenerators(patterns);
                for (LoadedPatternGenerator generator : generators) {
                    generator.blocksStream()
                            .map(limit::get)
                            .filter(pieces -> pieceCounterPredicate.test(new PieceCounter(pieces.blockStream())))
                            .filter(pieces -> expressionPredicate.test(pieces.blockStream().map(Piece::getName).collect(Collectors.joining())))
                            .forEach(output::output);
                }
                break;
            }
            case Forward: {
                PieceOutput output = settings.isDistinct() ? new DistinctPieceOutput() : new SimplePieceOutput();

                List<LoadedPatternGenerator> generators = createGenerators(patterns);
                for (LoadedPatternGenerator generator : generators) {
                    int fromDepth = generator.getDepth();
                    Predicate<WithHoldCount<Piece>> holdPredicate = settings.getHoldEquation()
                            .map(HoldEquation::toPredict)
                            .orElse(e -> true);

                    CountForwardOrderLookUp countForwardOrderLookUp;
                    if (settings.isStartsWithoutHold()) {
                        countForwardOrderLookUp = new CountForwardOrderLookUpStartsWithEmpty(limit.toDepth(fromDepth, true), fromDepth);
                    } else {
                        countForwardOrderLookUp = new CountForwardOrderLookUpStartsWithAny(limit.toDepth(fromDepth, true), fromDepth);
                    }

                    generator.blocksStream()
                            .flatMap(it -> countForwardOrderLookUp.parse(it.getPieces()))
                            .filter(holdPredicate)
                            .map(it -> new LongPieces(it.getList()))
                            .map(limit::get)
                            .filter(pieces -> pieceCounterPredicate.test(new PieceCounter(pieces.blockStream())))
                            .filter(pieces -> expressionPredicate.test(pieces.blockStream().map(Piece::getName).collect(Collectors.joining())))
                            .forEach(output::output);
                }

                break;
            }
            case Backward: {
                PieceOutputForBackward output = settings.isDistinct() ? new DistinctPieceOutputForBackward() : new SimplePieceOutputForBackward();

                List<LoadedPatternGenerator> generators = createGenerators(patterns);
                for (LoadedPatternGenerator generator : generators) {
                    int toDepth = generator.getDepth();
                    Predicate<WithHoldCount<Piece>> holdPredicate = settings.getHoldEquation()
                            .map(HoldEquation::toPredict)
                            .orElse(e -> true);

                    CountReverseOrderLookUp countReverseOrderLookUp;
                    if (settings.isStartsWithoutHold()) {
                        countReverseOrderLookUp = new CountReverseOrderLookUpStartsWithEmpty(toDepth, limit.fromDepth(toDepth));
                    } else {
                        countReverseOrderLookUp = new CountReverseOrderLookUpStartsWithAny(toDepth, limit.fromDepth(toDepth));
                    }

                    generator.blocksStream()
                            .flatMap(it -> countReverseOrderLookUp.parse(it.getPieces()))
                            .filter(holdPredicate)
                            .map(pieceWithHoldCount -> limit.get(pieceWithHoldCount.getList()))
                            .filter(pieceList -> pieceCounterPredicate.test(new PieceCounter(pieceList.stream().filter(Objects::nonNull))))
                            .map(pieceList -> pieceList.stream().map(piece -> piece != null ? piece.getName() : "*").collect(Collectors.joining()))
                            .filter(expressionPredicate)
                            .forEach(output::output);
                }

                break;
            }
            case BackwardAndPass: {
                PieceOutputForBackward output = settings.isDistinct() ? new DistinctPieceOutputForBackward() : new SimplePieceOutputForBackward();

                List<LoadedPatternGenerator> generators = createGenerators(patterns);
                for (LoadedPatternGenerator generator : generators) {
                    int toDepth = generator.getDepth();
                    Predicate<WithHoldCount<Piece>> holdPredicate = settings.getHoldEquation()
                            .map(HoldEquation::toPredict)
                            .orElse(e -> true);

                    CountReverseOrderLookUp countReverseOrderLookUp;
                    if (settings.isStartsWithoutHold()) {
                        countReverseOrderLookUp = new CountReverseOrderLookUpStartsWithEmpty(toDepth, limit.fromDepth(toDepth));
                    } else {
                        countReverseOrderLookUp = new CountReverseOrderLookUpStartsWithAny(toDepth, limit.fromDepth(toDepth));
                    }

                    generator.blocksStream()
                            .flatMap(it -> countReverseOrderLookUp.parse(it.getPieces()))
                            .filter(holdPredicate)
                            .map(pieceWithHoldCount -> limit.get(pieceWithHoldCount.getList()))
                            .filter(pieceList -> pieceCounterPredicate.test(new PieceCounter(pieceList.stream().filter(Objects::nonNull))))
                            .filter(pieceList -> {
                                String str = pieceList.stream().map(piece -> piece != null ? piece.getName() : "*").collect(Collectors.joining());
                                return expressionPredicate.test(str);
                            })
                            .flatMap(this::expand)
                            .map(pieceList -> pieceList.stream().map(Piece::getName).collect(Collectors.joining()))
                            .forEach(output::output);
                }

                break;
            }
            default: {
                throw new UnsupportedOperationException("Unsupported mode: mode = " + mode);
            }
        }
    }

    private Predicate<PieceCounter> createPieceCounterPredicate(List<PieceEquation> pieceEquations) {
        List<Predicate<EnumMap<Piece, Integer>>> predicates = pieceEquations.stream()
                .map(PieceEquation::toPredict)
                .collect(Collectors.toList());
        return (pieceCounter) -> {
            EnumMap<Piece, Integer> map = pieceCounter.getEnumMap();
            for (Predicate<EnumMap<Piece, Integer>> predicate : predicates) {
                if (!predicate.test(map)) {
                    return false;
                }
            }
            return true;
        };
    }

    private Predicate<String> createExpressionPredicate(String expression, String notExpression) {
        List<Predicate<String>> predicates = new ArrayList<>();

        {
            String trim = expression.trim();
            if (!trim.isEmpty()) {
                Pattern pattern = Pattern.compile(trim);
                predicates.add(piecesStr -> {
                    Matcher matcher = pattern.matcher(piecesStr);
                    return matcher.find();
                });
            }
        }

        {
            String trim = notExpression.trim();
            if (!trim.isEmpty()) {
                Pattern pattern = Pattern.compile(trim);
                predicates.add(piecesStr -> {
                    Matcher matcher = pattern.matcher(piecesStr);
                    return !matcher.find();
                });
            }
        }

        if (predicates.isEmpty()) {
            return piecesStr -> true;
        }

        return piecesStr -> {
            for (Predicate<String> predicate : predicates) {
                if (!predicate.test(piecesStr)) {
                    return false;
                }
            }
            return true;
        };
    }

    private List<LoadedPatternGenerator> createGenerators(List<String> patterns) throws FinderExecuteException {
        List<LoadedPatternGenerator> generators = new ArrayList<>();
        for (String pattern : patterns) {
            try {
                LoadedPatternGenerator generator = new LoadedPatternGenerator(pattern);
                generators.add(generator);
            } catch (SyntaxException e) {
                throw new FinderExecuteException(e);
            }
        }
        return generators;
    }

    private Stream<List<Piece>> expand(List<Piece> pieceList) {
        // nullのインデックスを取得
        ArrayList<Integer> nullIndexes = new ArrayList<>();
        for (int index = 0; index < pieceList.size(); index++) {
            if (pieceList.get(index) == null) {
                nullIndexes.add(index);
            }
        }

        // nullが含まれない場合はそのまま返す
        if (nullIndexes.isEmpty()) {
            return Stream.of(pieceList);
        }

        ArrayList<List<Piece>> lists = new ArrayList<>();
        lists.add(pieceList);

        // nullの位置に各ミノを展開
        for (int nullIndex : nullIndexes) {
            ArrayList<List<Piece>> next = new ArrayList<>();
            for (List<Piece> pieces : lists) {
                for (Piece piece : Piece.valueList()) {
                    ArrayList<Piece> p = new ArrayList<>(pieces);
                    p.set(nullIndex, piece);
                    next.add(p);
                }
            }
            lists = next;
        }

        return lists.stream();
    }

    @Override
    public void close() throws FinderTerminateException {
    }
}
