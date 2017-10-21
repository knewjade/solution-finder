import entry.EntryPointMain;

public class DevMain {
    public static void main(String[] args) throws Exception {
//        String command = "path -f csv -k p -t v115@9gF8DeF8DeF8DeF8NeAgH -p T,*p4";
        String command = "setup -t v115@9gSpGeB8whGeC8GeC8QeAgH -p *! -r true -m i";
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
//        BlocksGenerator generator = new BlocksGenerator("TISZ*![tisz]");
//        int depth = generator.getDepth();
//        System.out.println(depth);
//        generator.blocksStream().map(Blocks::getBlocks).forEach(System.out::println);
    }
}
