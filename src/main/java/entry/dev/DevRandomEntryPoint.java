package entry.dev;

import common.datastore.pieces.Blocks;
import common.pattern.BlocksGenerator;
import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import core.mino.MinoFactory;
import entry.EntryPoint;
import exceptions.FinderException;
import exceptions.FinderTerminateException;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DevRandomEntryPoint implements EntryPoint {
    private final String pattern;

    public DevRandomEntryPoint(List<String> args) {
        assert args.size() == 1;
        this.pattern = args.get(0);
    }

    @Override
    public void run() throws FinderException {
        BlocksGenerator generator = new BlocksGenerator(pattern);
        List<Blocks> blocks = generator.blocksStream().collect(Collectors.toList());
        int index = new Random().nextInt(blocks.size());
        Blocks selected = blocks.get(index);
        String quiz = Tetfu.encodeForQuiz(selected.getBlocks());

        MinoFactory minoFactory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(minoFactory, converter);
        TetfuElement element = new TetfuElement(quiz);
        String encode = tetfu.encode(Collections.singletonList(element));

        System.out.println("v115@" + encode);
    }

    @Override
    public void close() throws FinderTerminateException {

    }
}
