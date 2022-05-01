package entry.spin.output;

import common.datastore.Pair;
import concurrent.LockedReachableThreadLocal;
import concurrent.RotateReachableThreadLocal;
import core.field.Field;
import core.neighbor.SimpleOriginalPiece;
import entry.path.output.FumenParser;
import entry.path.output.MyFile;
import entry.spin.FilterType;
import exceptions.FinderExecuteException;
import output.HTMLBuilder;
import searcher.spins.candidates.Candidate;
import searcher.spins.results.Result;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NoRoofSpinOutput implements SpinOutput {
    private final Formatter formatter;

    public NoRoofSpinOutput(
            FumenParser fumenParser,
            LockedReachableThreadLocal lockedReachableThreadLocal,
            RotateReachableThreadLocal rotateReachableThreadLocal,
            FilterType filterType
    ) {
        this.formatter = new Formatter(fumenParser, lockedReachableThreadLocal, rotateReachableThreadLocal, filterType);
    }

    @Override
    public int output(MyFile myFile, List<Candidate> results, Field initField, int fieldHeight) throws FinderExecuteException {
        HTMLBuilder<NoRoofColumn> htmlBuilder = new HTMLBuilder<>("Spin Result");

        // HTMLを作成する
        for (Candidate candidate : results) {
            add(htmlBuilder, candidate, initField, fieldHeight);
        }

        int size = htmlBuilder.getSize();
        htmlBuilder.addHeader(String.format("%d solutions", size));

        // 書き込み
        try (BufferedWriter writer = myFile.newBufferedWriter()) {
            ArrayList<NoRoofColumn> sorted = new ArrayList<>(htmlBuilder.getRegisteredColumns());
            sorted.sort(Comparator.reverseOrder());

            List<String> lines = htmlBuilder.toList(sorted, true);
            for (String line : lines) {
                writer.write(line);
            }

            writer.flush();
        } catch (Exception e) {
            throw new FinderExecuteException("Failed to output file", e);
        }

        return size;
    }

    private void add(HTMLBuilder<NoRoofColumn> htmlBuilder, Candidate candidate, Field initField, int fieldHeight) {
        // Tを使って消去されるライン数
        Result result = candidate.getResult();
        SimpleOriginalPiece operationT = candidate.getOperationT();
        int clearedLineOnlyT = Long.bitCount(result.getAllMergedFilledLine() & operationT.getUsingKey());

        Optional<Pair<String, Integer>> optional = formatter.get(candidate, null, initField, fieldHeight);
        if (optional.isPresent()) {
            Pair<String, Integer> aLinkSolutionPriority = optional.get();
            NoRoofColumn column = new NoRoofColumn(clearedLineOnlyT, getSendLineString(clearedLineOnlyT));
            htmlBuilder.addColumn(column, aLinkSolutionPriority.getKey(), aLinkSolutionPriority.getValue());
        }
    }

    private String getSendLineString(int clearedLine) {
        assert 1 <= clearedLine && clearedLine <= 3;
        switch (clearedLine) {
            case 1:
                return "Single";
            case 2:
                return "Double";
            case 3:
                return "Triple";
        }
        throw new IllegalStateException();
    }
}
