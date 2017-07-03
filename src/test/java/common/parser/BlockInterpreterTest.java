package common.parser;

import core.mino.Block;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BlockInterpreterTest {
    @Test
    public void parse10WithJust() throws Exception {
        Stream<Block> stream = BlockInterpreter.parse10("TIJLOSZTIJ");
        List<Block> blocks = stream.collect(Collectors.toList());
        assertThat(blocks, is(Arrays.asList(Block.T, Block.I, Block.J, Block.L, Block.O, Block.S, Block.Z, Block.T, Block.I, Block.J)));
    }

    @Test
    public void parse10Over() throws Exception {
        Stream<Block> stream = BlockInterpreter.parse10("SJOTLZOJSZIJLTIO");
        List<Block> blocks = stream.collect(Collectors.toList());
        assertThat(blocks, is(Arrays.asList(Block.S, Block.J, Block.O, Block.T, Block.L, Block.Z, Block.O, Block.J, Block.S, Block.Z)));
    }

    @Test
    public void parse11Just() throws Exception {
        Stream<Block> stream = BlockInterpreter.parse11("ZLSJZLSJTIO");
        List<Block> blocks = stream.collect(Collectors.toList());
        assertThat(blocks, is(Arrays.asList(Block.Z, Block.L, Block.S, Block.J, Block.Z, Block.L, Block.S, Block.J, Block.T, Block.I, Block.O)));
    }

    @Test
    public void parse11Over() throws Exception {
        Stream<Block> stream = BlockInterpreter.parse11("LZISTOJLZISTOJ");
        List<Block> blocks = stream.collect(Collectors.toList());
        assertThat(blocks, is(Arrays.asList(Block.L, Block.Z, Block.I, Block.S, Block.T, Block.O, Block.J, Block.L, Block.Z, Block.I, Block.S)));
    }
}