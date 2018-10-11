package entry.setup.output;

import common.buildup.BuildUpStream;
import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.datastore.Pair;
import common.tetfu.common.ColorConverter;
import core.field.Field;
import core.mino.MinoFactory;
import core.mino.Piece;
import entry.path.output.BufferedFumenParser;
import entry.path.output.FumenParser;
import entry.path.output.MyFile;
import entry.path.output.OneFumenParser;
import entry.setup.FieldHTMLColumn;
import entry.setup.SetupResults;
import entry.setup.SetupSettings;
import entry.setup.filters.SetupResult;
import entry.setup.functions.SetupFunctions;
import exceptions.FinderExecuteException;
import exceptions.FinderInitializeException;
import output.HTMLBuilder;
import output.HTMLColumn;
import searcher.pack.SizedBit;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class LinkSetupOutput implements SetupOutput {
    private final MyFile outputSetupFile;
    private final SetupFunctions setupFunctions;
    private final FumenParser fumenParser;
    private final ThreadLocal<BuildUpStream> buildUpStreamThreadLocal;
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;

    public LinkSetupOutput(SetupSettings setupSettings, SetupFunctions setupFunctions, FumenParser fumenParser, ThreadLocal<BuildUpStream> buildUpStreamThreadLocal, MinoFactory minoFactory, ColorConverter colorConverter) throws FinderInitializeException {
        // 出力ファイルが正しく出力できるか確認
        String outputBaseFilePath = setupSettings.getOutputBaseFilePath();
        MyFile setup = new MyFile(outputBaseFilePath);
        setup.mkdirs();
        setup.verify();

        // 保存
        this.outputSetupFile = setup;
        this.setupFunctions = setupFunctions;
        this.fumenParser = fumenParser;
        this.buildUpStreamThreadLocal = buildUpStreamThreadLocal;
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
    }

    @Override
    public void output(SetupResults setupResults, Field initField, SizedBit sizedBit) throws FinderExecuteException {
        Map<BlockField, List<SetupResult>> resultMap = setupResults.getResultMap();
        int maxHeight = sizedBit.getHeight();

        HTMLBuilder<HTMLColumn> htmlBuilder = new HTMLBuilder<>("Setup result");
        htmlBuilder.addHeader(String.format("<div>%d solutions / %d sub solutions</div>", resultMap.size(), resultMap.values().stream().mapToInt(List::size).sum()));

        BiFunction<List<MinoOperationWithKey>, Field, String> naming = setupFunctions.getNaming();

        OneFumenParser oneFumenParser = new OneFumenParser(minoFactory, colorConverter);
        BufferedFumenParser bufferedFumenParser = new BufferedFumenParser(minoFactory, colorConverter, oneFumenParser);

        Comparator<Pair<Long, ?>> comparator = Comparator.comparingLong(Pair::getKey);

        for (Map.Entry<BlockField, List<SetupResult>> entry : resultMap.entrySet()) {
            BlockField keyField = entry.getKey();
            List<SetupResult> results = entry.getValue();

            Field field = initField.freeze(maxHeight);
            for (Piece piece : Piece.values())
                field.merge(keyField.get(piece));

            FieldHTMLColumn column = new FieldHTMLColumn(field, maxHeight);

            results.stream()
                    .map(setupResult -> {
                        List<MinoOperationWithKey> operationWithKeys = setupResult.getSolution();

                        BuildUpStream buildUpStream = buildUpStreamThreadLocal.get();
                        long counter = buildUpStream.existsValidBuildPattern(initField, operationWithKeys).count();
                        return new Pair<>(counter, setupResult);
                    })
                    .sorted(comparator.reversed())
                    .forEach(pair -> {
                        Long counter = pair.getKey();
                        SetupResult setupResult = pair.getValue();

                        // 操作に変換
                        List<MinoOperationWithKey> operationWithKeys = setupResult.getSolution();

                        // パターンを表す名前 を生成
                        String blocksName = operationWithKeys.stream()
                                .map(OperationWithKey::getPiece)
                                .map(Piece::getName)
                                .collect(Collectors.joining());

                        bufferedFumenParser.add(operationWithKeys, initField, maxHeight, String.format("%d : %s", counter, blocksName), counter);

                        // 譜面の作成
                        String encode = fumenParser.parse(operationWithKeys, initField, maxHeight);

                        // 名前の作成
                        String name = naming.apply(operationWithKeys, setupResult.getRawField());

                        String link = String.format("<a href='http://fumen.zui.jp/?v115@%s'>%s</a> <span style='color: #999'>[%d]</span>", encode, name, counter);
                        String line = String.format("<div>%s</div>", link);

                        htmlBuilder.addColumn(column, line, -counter);
                    });
        }

        String mergedFumen = bufferedFumenParser.parse();
        htmlBuilder.addHeader(String.format("<div><a href='http://fumen.zui.jp/?v115@%s'>All solutions<a></div>", mergedFumen));

        ArrayList<HTMLColumn> columns = new ArrayList<>(htmlBuilder.getRegisteredColumns());
        columns.sort(Comparator.comparing(HTMLColumn::getTitle).reversed());
        try (BufferedWriter bufferedWriter = outputSetupFile.newBufferedWriter()) {
            for (String line : htmlBuilder.toList(columns, true)) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new FinderExecuteException("Failed to output file", e);
        }
    }
}
