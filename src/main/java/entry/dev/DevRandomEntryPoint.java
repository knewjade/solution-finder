package entry.dev;

import common.SyntaxException;
import common.datastore.blocks.Pieces;
import common.pattern.LoadedPatternGenerator;
import common.pattern.PatternGenerator;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.field.ArrayColoredField;
import common.tetfu.field.ColoredField;
import core.mino.MinoFactory;
import entry.EntryPoint;
import exceptions.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DevRandomEntryPoint implements EntryPoint {
    private final String pattern;
    private final String code;

    public DevRandomEntryPoint(List<String> args) {
        switch (args.size()) {
            case 1:
                this.code = "";
                this.pattern = args.get(0);
                break;
            case 2:
                this.code = args.get(0);
                this.pattern = args.get(1);
                break;
            default:
                throw new UnsupportedOperationException(args.toString());
        }
    }

    @Override
    public void run() throws FinderException {
        PatternGenerator generator = createBlockGenerator(pattern);
        List<Pieces> blocks = generator.blocksStream().collect(Collectors.toList());
        int index = new Random().nextInt(blocks.size());
        Pieces selected = blocks.get(index);
        String quiz = Tetfu.encodeForQuiz(selected.getPieces());

        MinoFactory minoFactory = new MinoFactory();
        ColorConverter converter = new ColorConverter();

        ColoredField coloredField = getTetfu(minoFactory, converter);


        Tetfu tetfu = new Tetfu(minoFactory, converter);
        TetfuElement element = new TetfuElement(coloredField, quiz);
        String encode = tetfu.encode(Collections.singletonList(element));

        System.out.println("v115@" + encode);
    }

    private PatternGenerator createBlockGenerator(String pattern) throws FinderInitializeException, FinderExecuteException {
        try {
            return new LoadedPatternGenerator(pattern);
        } catch (SyntaxException e) {
            throw new FinderInitializeException("Pattern syntax error", e);
        }
    }

    private ColoredField getTetfu(MinoFactory minoFactory, ColorConverter converter) throws FinderParseException {
        if (!code.isEmpty()) {
            Tetfu tetfu = new Tetfu(minoFactory, converter);
            String removeDomainData = Tetfu.removeDomainData(code);
            String data = Tetfu.removePrefixData(removeDomainData);
            List<TetfuPage> decode = tetfu.decode(data);
            TetfuPage lastPage = decode.get(decode.size() - 1);
            return lastPage.getField();
        }
        return new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);
    }

    @Override
    public void close() throws FinderTerminateException {

    }
}
