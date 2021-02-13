package entry.util.seq;

import common.SyntaxException;
import common.order.ForwardOrderLookUp;
import common.pattern.LoadedPatternGenerator;
import entry.EntryPoint;
import exceptions.FinderException;
import exceptions.FinderExecuteException;
import exceptions.FinderTerminateException;

import java.util.ArrayList;
import java.util.List;

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

        SeqUtilModes mode = settings.getSeqUtilMode();
        List<String> patterns = settings.getPatterns();
        switch (mode) {
            case Pass: {
                List<LoadedPatternGenerator> generators = createGenerators(patterns);
                for (LoadedPatternGenerator generator : generators) {
                    generator.blocksStream().map(limit::get).forEach(output::output);
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
                            .forEach(output::output);
                }

                break;
            }
            default: {
                throw new UnsupportedOperationException("Unsupported mode: mode = " + mode);
            }
        }
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
