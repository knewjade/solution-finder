import entry.EntryPointMain;
import output.HTMLBuilder;
import output.HTMLColumn;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DevMain {
    public static void main(String[] args) throws Exception {
//        String command = "path -f csv -k p -t v115@9gF8DeF8DeF8DeF8NeAgH -p T,*p4";

//        String command = "setup -t v115@Pg1hDe1hBeB8FtCeA8FtB8AeB8EtA8BeD8CtA8CeE8?AtB8AeI8AeG8JeAgH -p *p7 -m i -f z";
        String command = "percent -t v115@9gD8FeD8FeD8FeD8PeAgH -p *p7 -td 1";

        int returnCode = EntryPointMain.main(command.split(" "));
        System.exit(returnCode);

//        ExecutorService executorService = Executors.newFixedThreadPool(6);
//        try (AsyncBufferedFileWriter writer = new AsyncBufferedFileWriter(new File("output/test"), Charset.defaultCharset(), false, 10L)) {
//            for (int i = 0; i < 10; i++) {
//                int finalI = i;
//                executorService.submit(() -> {
//                    for (int j = 0; j < 10; j++) {
//                        writer.writeAndNewLine(String.format("hello %d-%03d", finalI, j));
//                    }
//                });
//            }
//            executorService.shutdown();
//            executorService.awaitTermination(100L, TimeUnit.SECONDS);
//        }
//        LoadedPatternGenerator generator = new LoadedPatternGenerator("TISZ*![tisz]");
//        int depth = generator.getDepth();
//        System.out.println(depth);
//        generator.blocksStream().map(Pieces::getPieces).forEach(System.out::println);

        HTMLBuilder<TestColumn> builder = new HTMLBuilder<>("hello");
        builder.addColumn(TestColumn.SECTION1, "hello");
        builder.addColumn(TestColumn.SECTION2, "test1");
        builder.addColumn(TestColumn.SECTION1, "world");
        builder.addColumn(TestColumn.SECTION2, "test2");
        List<String> list = builder.toList(Arrays.asList(TestColumn.SECTION1, TestColumn.SECTION2), true);

        StringBuilder builder1 = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        for (String s : list) {
            builder1.append(s).append(lineSeparator);
        }
        System.out.println(builder1.toString());

    }

    public enum TestColumn implements HTMLColumn {
        SECTION1,
        SECTION2;

        @Override
        public String getTitle() {
            return name();
        }

        @Override
        public String getId() {
            return name();
        }

        @Override
        public Optional<String> getDescription() {
            return Optional.empty();
        }
    }
}
