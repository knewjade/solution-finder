package searcher.spins.wall;

import common.datastore.PieceCounter;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Piece;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.Solutions;
import searcher.spins.SpinCommons;
import searcher.spins.candidates.CandidateWithMask;
import searcher.spins.candidates.Candidate;
import searcher.spins.pieces.bits.BitBlocks;
import searcher.spins.results.AddLastResult;
import searcher.spins.results.Result;
import searcher.spins.scaffold.ScaffoldRunner;
import searcher.spins.wall.results.AddLastWallResult;
import searcher.spins.wall.results.EmptyWallResult;
import searcher.spins.wall.results.WallResult;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WallRunner {
    public static WallRunner create(BitBlocks bitBlocks, ScaffoldRunner scaffoldRunner, int allowFillMaxHeight, int fieldHeight) {
        SpinMaskFields spinMaskFields = new SpinMaskFields(allowFillMaxHeight, fieldHeight);
        return new WallRunner(bitBlocks, spinMaskFields, scaffoldRunner);
    }

    private final BitBlocks bitBlocks;
    private final SpinMaskFields spinMaskFields;
    private final ScaffoldRunner scaffoldRunner;

    private WallRunner(BitBlocks bitBlocks, SpinMaskFields spinMaskFields, ScaffoldRunner scaffoldRunner) {
        this.bitBlocks = bitBlocks;
        this.spinMaskFields = spinMaskFields;
        this.scaffoldRunner = scaffoldRunner;
    }

    public Stream<CandidateWithMask> search(Candidate candidate) {
        assert candidate.getResult().operationStream().anyMatch(it -> it.equals(candidate.getOperationT()));

        // Tミノを指定された場所に置いたら、Tスピンになる地形である
        if (canSpin(candidate)) {
            Field notAllowed = FieldFactory.createField(candidate.getAllMergedFieldWithoutT().getMaxFieldHeight());
            return Stream.of(new CandidateWithMask(candidate.getResult(), candidate.getOperationT(), notAllowed));
        }

        // 使っていないミノを置いてみてTスピンができないか探索
        Solutions<Long> solutions = new Solutions<>();

        SimpleOriginalPiece operationT = candidate.getOperationT();
        Result result = candidate.getResult();

        // すでに使用されているブロック
        Field allMergedField = result.getAllMergedField();

        // マスクを取得する
        // Tミノ以外のフィールドで揃っているラインを考慮する
        long filledLineWithoutT = candidate.getAllMergedFilledLineWithoutT();
        assert (filledLineWithoutT & operationT.getNeedDeletedKey()) == operationT.getNeedDeletedKey();
        Stream<MaskField> maskFields = spinMaskFields.get(operationT.getX(), operationT.getY(), filledLineWithoutT);

        // すでに揃っているライン
        long initFilledLine = result.getAllMergedFilledLine();

        // まだブロックがない部分同じ形のマスクを取り除く
        return maskFields
                .filter(maskField -> {
                    // 既に置くことができない場所にブロックがある
                    Field notAllowed = maskField.getNotAllowed();
                    return allMergedField.canMerge(notAllowed);
                })
                .flatMap(maskField -> {
                    // 置くことができないブロック
                    EmptyWallResult emptyWallResult = new EmptyWallResult(candidate, maskField);

                    // すでにすべてが埋まっている
                    if (emptyWallResult.isVisitedAll()) {
                        solutions.add(emptyWallResult.toKeyStream().collect(Collectors.toSet()));
                        return Stream.of(emptyWallResult);
                    }

                    // 探索
                    return this.next(candidate.getOperationT(), emptyWallResult, solutions, initFilledLine, filledLineWithoutT);
                })
                .map(wallResult -> new CandidateWithMask(wallResult.getLastResult(), operationT, wallResult.getNotAllowed()));
    }

    private boolean canSpin(Candidate candidate) {
        SimpleOriginalPiece operationT = candidate.getOperationT();
        Field fieldWithoutT = candidate.getAllMergedFieldWithoutT();
        return SpinCommons.canTSpin(fieldWithoutT, operationT.getX(), operationT.getY(), operationT.getNeedDeletedKey());
    }

    private Stream<WallResult> next(
            SimpleOriginalPiece operationT, WallResult initResult, Solutions<Long> solutions,
            long initFilledLine, long filledLineWithoutT
    ) {
        Result lastResult = initResult.getLastResult();
        long filledLine = lastResult.getAllMergedFilledLine();

        // 消去されるラインが探索開始時から変わっていない
        if (filledLine != initFilledLine) {
            return Stream.empty();
        }

        // すべてのミノを使い切った
        PieceCounter remainderPieceCounter = lastResult.getRemainderPieceCounter();
        if (remainderPieceCounter.isEmpty()) {
            return Stream.empty();
        }

        // 次に置くミノ一覧
        assert !initResult.getRemain().isPerfect();
        EnumMap<Piece, List<SimpleOriginalPiece>> nextOriginPiecesMap = bitBlocks.getNextOriginPiecesMap(initResult.getRemain());
        Result initLastResult = initResult.getLastResult();

        // 置けない場所
        Field notAllowed = lastResult.getAllMergedField().freeze();
        notAllowed.merge(initResult.getNotAllowed());

        // 置くことができない領域
        return remainderPieceCounter.getBlockStream()
                .flatMap(piece -> {
                    // 実際にミノをおく
                    return nextOriginPiecesMap.get(piece).stream()
                            .filter(originalPiece -> {
                                long needDeletedKey = originalPiece.getNeedDeletedKey();
                                return (filledLineWithoutT & needDeletedKey) == needDeletedKey;
                            })
                            .flatMap(it -> {
                                if (!notAllowed.canMerge(it.getMinoField())) {
                                    return null;
                                }

                                Result result = AddLastResult.create(initLastResult, it);

                                // 消去されるラインが探索開始時から変わっていない
                                if (result.getAllMergedFilledLine() != initFilledLine) {
                                    return Stream.empty();
                                }

                                CandidateWithMask candidateWithMask = new CandidateWithMask(result, operationT, notAllowed);
                                return scaffoldRunner.build(candidateWithMask, it)
                                        .map(scaffoldResult -> new AddLastWallResult(initResult, scaffoldResult));
                            })
                            .filter(Objects::nonNull)
                            .filter(nextResult -> {
                                // 消去されるラインが探索開始時から変わっていない
                                return nextResult.getLastResult().getAllMergedFilledLine() == initFilledLine;
                            })
                            .flatMap(nextResult -> {
                                // 既に解として登録済み
                                Set<Long> keys = nextResult.toKeyStream().collect(Collectors.toSet());
                                if (solutions.contains(keys)) {
                                    return Stream.empty();
                                }

                                // すべてが埋まっている
                                if (nextResult.isVisitedAll()) {
                                    solutions.add(keys);
                                    return Stream.of(nextResult);
                                }

                                return this.next(operationT, nextResult, solutions, initFilledLine, filledLineWithoutT);
                            });
                });
    }
}
