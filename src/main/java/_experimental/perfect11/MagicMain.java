package _experimental.perfect11;

import common.datastore.Pair;
import core.column_field.ColumnFieldFactory;
import core.column_field.ColumnSmallField;
import core.field.SmallField;
import searcher.pack.SizedBit;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class MagicMain {
    public static void main(String[] args) {
        SizedBit sizedBit = new SizedBit(1, 6);
        Obj obj = new Obj(sizedBit);
        Stream<ColumnSmallField> fields = obj.createBasicFields();
        List<Pair<ColumnSmallField, SmallField>> pairs = fields.map(columnSmallField -> toPair(columnSmallField, sizedBit))
//                .peek(System.out::println)
                .collect(Collectors.toList());

//        int x = 0;
//        long board = ColumnOperators.parseToBoardWidth12(x);
        System.out.println("switch (column) {");
        for (Pair<ColumnSmallField, SmallField> pair : pairs) {
            System.out.printf("case %d:%n", pair.getKey().getBoard(0));
            System.out.printf("    return %dL;%n", pair.getValue().getBoard(0));
        }
        System.out.println("}");

//        Random random = new Random();
//        long magic = createMagicNumber(random, 3);
//
//        for (int count = 0; count < 10; count++) {
//            HashSet<Long> candidate = new HashSet<>();
//            for (Pair<ColumnSmallField, SmallField> pair : pairs) {
//            }
//
//        }
    }

    private static long createMagicNumber(Random random, int max) {
        long number = random.nextLong();
        for (int count = 0; count < max - 1; count++) {
            number &= random.nextLong();
        }
        return number;
    }

    private static Pair<ColumnSmallField, SmallField> toPair(ColumnSmallField columnField, SizedBit sizedBit) {
        SmallField invertedField = new SmallField();
        for (int y = 0; y < sizedBit.getHeight(); y++) {
            for (int x = 0; x < sizedBit.getWidth(); x++) {
                if (columnField.isEmpty(x, y, sizedBit.getHeight()))
                    invertedField.setBlock(x, y);
            }
        }

        return new Pair<>(columnField, invertedField);
    }

    private static class Obj {
        private final SizedBit sizedBit;

        public Obj(SizedBit sizedBit) {
            this.sizedBit = sizedBit;
        }

        // 存在する基本フィールドをすべて列挙
        public Stream<ColumnSmallField> createBasicFields() {
            return LongStream.rangeClosed(0, sizedBit.getFillBoard())
                    .boxed()
                    .sorted(Comparator.comparingLong(Long::bitCount).reversed())
                    .map(ColumnFieldFactory::createField);
        }
    }
}
