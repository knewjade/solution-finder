package entry.util.seq;

import common.SyntaxException;
import common.datastore.blocks.Pieces;
import common.order.ForwardOrderLookUp;
import common.pattern.LoadedPatternGenerator;
import core.mino.Piece;
import entry.EntryPoint;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderTerminateException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        PieceOutput output = settings.isDistinct() ? new DistinctPieceOutput() : new SimplePieceOutput();
        int cuttingSize = settings.getCuttingSize();
        PieceCutting limit = 0 < cuttingSize ? new UsePieceCutting(cuttingSize) : new NoPieceCutting();

        Predicate<Pieces> predicate = createPiecesPredicate(settings.getExpression());
        SeqUtilModes mode = settings.getSeqUtilMode();
        List<String> patterns = settings.getPatterns();
        switch (mode) {
            case Pass: {
                List<LoadedPatternGenerator> generators = createGenerators(patterns);
                for (LoadedPatternGenerator generator : generators) {
                    generator.blocksStream()
                            .map(limit::get)
                            .filter(predicate)
                            .forEach(output::output);
                }
                break;
            }
            case Forward: {
                List<LoadedPatternGenerator> generators = createGenerators(patterns);
                for (LoadedPatternGenerator generator : generators) {
                    int fromDepth = generator.getDepth();
                    ForwardOrderLookUp orderLookUp = new ForwardOrderLookUp(limit.toDepth(fromDepth), fromDepth);
                    generator.blocksStream()
                            .flatMap(it -> orderLookUp.parse(it.getPieces()))
                            .map(limit::get)
                            .filter(predicate)
                            .forEach(output::output);
                }

                break;
            }
            default: {
                throw new UnsupportedOperationException("Unsupported mode: mode = " + mode);
            }
        }
    }

    private Predicate<Pieces> createPiecesPredicate(String expression) {
        String trim = expression.trim();
        if (trim.isEmpty()) {
            return pieces -> true;
        }

        Pattern pattern = Pattern.compile(trim);
        return pieces -> {
            String piecesStr = pieces.blockStream().map(Piece::getName).collect(Collectors.joining());
            Matcher matcher = pattern.matcher(piecesStr);
            return matcher.find();
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

    @Override
    public void close() throws FinderTerminateException {
    }
}
