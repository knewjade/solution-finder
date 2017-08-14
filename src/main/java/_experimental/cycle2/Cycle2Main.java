package _experimental.cycle2;

import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import core.field.Field;
import core.field.FieldView;
import core.field.SmallField;
import core.mino.MinoFactory;
import core.srs.Rotate;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Cycle2Main {
    private static class Data {
        private final Field field;
        private final double percent;
        private final int count;

        Data(long field, double percent) {
            this(field, percent, -1);
        }

        Data(long field, double percent, int count) {
            this.field = new SmallField(field);
            this.percent = percent;
            this.count = count;
        }
    }

    public static void main(String[] args) throws IOException {
        String blocks = "IJSO";
        List<Data> collect = Files.lines(Paths.get(String.format("./output/cycle2/%s.csv", blocks)))
                .map(line -> line.split(","))
                .map(split -> new Data(Long.valueOf(split[0]), Double.valueOf(split[1])))
//                .map(split -> new Data(Long.valueOf(split[0]), Double.valueOf(split[1]), Integer.valueOf(split[2])))
//                .filter(data -> 1.0 <= data.percent)
                .collect(Collectors.toList());
        System.out.println(collect.size());

        Comparator<Data> comparing = Comparator.comparing(data -> data.count);
        collect.sort(comparing.reversed());

//        for (Data data : collect) {
//            String field = FieldView.toString(data.field, 4);
//            System.out.println(field);
//            System.out.println(data.count);
//        }

        Charset charset = Charset.defaultCharset();
        File outputFile = new File(String.format("output/%s", blocks));
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false), charset))) {
            collect.stream()
                    .forEach(pair -> {
                        String percent = String.format("%.2f", pair.percent * 100);
                        Tetfu tetfu = new Tetfu(minoFactory, converter);
                        ColoredField field = parse(pair.field);
                        String url = tetfu.encode(Collections.singletonList(new TetfuElement(
                                field, ColorType.Empty, Rotate.Spawn, 0, 0
                        )));
                        String format = "<li><a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s %% => http://fumen.zui.jp/?v115@%s </a></li>";
                        String element = String.format(format, url, percent, url);
                        try {
                            writer.write(element);
                            writer.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            writer.flush();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to output file", e);
        }
    }

    private static ColoredField parse(Field field) {
        ColoredField coloredField = ColoredFieldFactory.createField(24);
        for (int y = 0; y < field.getMaxFieldHeight(); y++) {
            for (int x = 0; x < 10; x++) {
                if (!field.isEmpty(x, y))
                    coloredField.setColorType(ColorType.Gray, x, y);
            }
        }
        return coloredField;
    }
}
