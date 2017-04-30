package newfield;

import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import core.mino.MinoFactory;
import misc.Stopwatch;
import newfield.step1.ColumnParityLimitation;
import newfield.step1.DeltaLimitedMino;
import newfield.step1.EstimateBuilder;
import newfield.step2.FullLimitedMino;
import newfield.step2.PositionLimitParser;
import newfield.step3.Search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main2 {
    public static void main(String[] args) {
        List<Block> usedBlocks = Arrays.asList(
                Block.L, Block.S, Block.J
        );
        BlockCounter blockCounter = new BlockCounter(usedBlocks);
        System.out.println(blockCounter);

        Field field = FieldFactory.createField("" +
                "XXXXXXX___" +
                "XXXXXXX___" +
                "XXXXXXX___" +
                "XXXXXXX___" +
                ""
        );
        int maxClearLine = 4;
        System.out.println(FieldView.toString(field, maxClearLine));

        ParityField parityField = new ParityField(field);
        System.out.println(parityField);

        Stopwatch stopwatch = Stopwatch.createStoppedStopwatch();
        for (int count = 0; count < 1; count++) {
            stopwatch.start();

            ArrayList<List<List<FullLimitedMino>>> allSets = new ArrayList<>();
            MinoFactory minoFactory = new MinoFactory();
            PositionLimitParser positionLimitParser = new PositionLimitParser(minoFactory, maxClearLine);

            ColumnParityLimitation limitation = new ColumnParityLimitation(blockCounter, parityField, maxClearLine);
            List<EstimateBuilder> estimateBuilders = limitation.enumerate();
            for (EstimateBuilder builder : estimateBuilders) {
                List<List<DeltaLimitedMino>> lists = builder.create();
                for (List<DeltaLimitedMino> list : lists) {
                    System.out.println(list);
                    List<List<FullLimitedMino>> sets = list.stream()
                            .map(positionLimitParser::parse)
                            .collect(Collectors.toList());
                    allSets.add(sets);
                }
            }

            for (List<List<FullLimitedMino>> sets : allSets) {
                Search searcher = new Search(field, sets);
                searcher.search();
            }

            stopwatch.stop();
        }
        System.out.println(stopwatch.toMessage(TimeUnit.MILLISECONDS));

    }
}
