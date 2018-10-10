package entry.setup.output;

import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import core.field.Field;
import core.mino.Piece;
import entry.path.output.FumenParser;
import entry.path.output.MyFile;
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

public class LinkSetupOutput implements SetupOutput {
    private final MyFile outputSetupFile;
    private final SetupFunctions setupFunctions;
    private final FumenParser fumenParser;

    public LinkSetupOutput(SetupSettings setupSettings, SetupFunctions setupFunctions, FumenParser fumenParser) throws FinderInitializeException {
        // 出力ファイルが正しく出力できるか確認
        String outputBaseFilePath = setupSettings.getOutputBaseFilePath();
        MyFile setup = new MyFile(outputBaseFilePath);
        setup.mkdirs();
        setup.verify();

        // 保存
        this.outputSetupFile = setup;
        this.setupFunctions = setupFunctions;
        this.fumenParser = fumenParser;
    }

    @Override
    public void output(SetupResults setupResults, Field initField, SizedBit sizedBit) throws FinderExecuteException {
        Map<BlockField, List<SetupResult>> resultMap = setupResults.getResultMap();
        int maxHeight = sizedBit.getHeight();

        HTMLBuilder<HTMLColumn> htmlBuilder = new HTMLBuilder<>("Setup result");
        BiFunction<List<MinoOperationWithKey>, Field, String> naming = setupFunctions.getNaming();

        resultMap.forEach((keyField, results) -> {
            Field field = initField.freeze(maxHeight);
            for (Piece piece : Piece.values())
                field.merge(keyField.get(piece));

            HTMLColumn column = new FieldHTMLColumn(field, maxHeight);
            StringBuilder builder = new StringBuilder();

            results.forEach(setupResult -> {
                // 操作に変換
                List<MinoOperationWithKey> operationWithKeys = setupResult.getSolution();

                // 譜面の作成
                String encode = fumenParser.parse(operationWithKeys, initField, maxHeight);

                // 名前の作成
                String name = naming.apply(operationWithKeys, setupResult.getRawField());

                String link = String.format("<a href='http://fumen.zui.jp/?v115@%s' target='_blank'>%s</a>", encode, name);
                String line = String.format("<div>%s</div>", link);

                builder.append(line);
            });

            htmlBuilder.addColumn(column, builder.toString());
        });

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
