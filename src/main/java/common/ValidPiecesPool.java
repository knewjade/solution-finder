package common;

import common.datastore.blocks.LongPieces;
import common.datastore.blocks.Pieces;
import common.order.OrderLookup;
import common.order.StackOrder;
import common.pattern.PatternGenerator;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Holdも含めた有効ミノ順を全て列挙し、保存しておくためのpool
public class ValidPiecesPool {
    private final boolean isHoldReduced;

    // Holdも考慮した全ての有効ミノ順
    // 11ミノパターン指定でHoldありの場合、10ミノ・11ミノともに含まれる
    private final HashSet<LongPieces> validPieces;

    // パターンから生成される全ての有効ミノ順
    // 11ミノパターン指定でHoldありの場合、11ミノのみ含まれる
    private final HashSet<LongPieces> allPieces;

    // 指定されたパターンの総数
    private final long numOfAllPatternSequences;

    public ValidPiecesPool(PatternGenerator blocksGenerator, int maxDepth, boolean isUsingHold) {
        this.numOfAllPatternSequences = blocksGenerator.blocksStream().count();
        this.isHoldReduced = isHoldReducedPieces(blocksGenerator, maxDepth, isUsingHold);
        this.allPieces = getAllPieces(blocksGenerator, maxDepth, isUsingHold);
        this.validPieces = getValidPieces(blocksGenerator, allPieces, maxDepth, isHoldReduced);
    }

    private boolean isHoldReducedPieces(PatternGenerator blocksGenerator, int maxDepth, boolean isUsingHold) {
        return isUsingHold && maxDepth < blocksGenerator.getDepth();
    }

    private HashSet<LongPieces> getAllPieces(PatternGenerator blocksGenerator, int maxDepth, boolean isUsingHold) {
        if (isUsingHold) {
            // ホールドあり
            if (maxDepth < blocksGenerator.getDepth()) {
                // Reduceあり  // isHoldReduceの対象
                return toReducedHashSetWithHold(blocksGenerator.blocksStream(), maxDepth + 1);
            } else {
                // 場所の交換のみ
                return toReducedHashSetWithHold(blocksGenerator.blocksStream(), maxDepth);
            }
        } else {
            // ホールドなし
            if (maxDepth < blocksGenerator.getDepth()) {
                // Reduceあり
                return toReducedHashSetWithoutHold(blocksGenerator.blocksStream(), maxDepth);
            } else {
                // そのまま
                return toDirectHashSet(blocksGenerator.blocksStream());
            }
        }
    }

    private HashSet<LongPieces> getValidPieces(PatternGenerator blocksGenerator, HashSet<LongPieces> allPieces, int maxDepth, boolean isHoldReduced) {
        if (isHoldReduced) {
            // パフェ時に使用ミノが少なくなるケースのため改めて専用のSetを作る
            return toReducedHashSetWithHold(blocksGenerator.blocksStream(), maxDepth);
        } else {
            return allPieces;
        }
    }

    private HashSet<LongPieces> toReducedHashSetWithHold(Stream<? extends Pieces> blocksStream, int maxDepth) {
        return blocksStream.parallel()
                .map(Pieces::getPieces)
                .flatMap(blocks -> OrderLookup.forwardBlocks(blocks, maxDepth).stream())
                .collect(Collectors.toCollection(HashSet::new))
                .parallelStream()
                .map(StackOrder::toList)
                .map(blocks -> blocks.subList(0, maxDepth))
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private HashSet<LongPieces> toReducedHashSetWithoutHold(Stream<? extends Pieces> blocksStream, int maxDepth) {
        return blocksStream.parallel()
                .map(Pieces::getPieces)
                .map(blocks -> blocks.subList(0, maxDepth))
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private HashSet<LongPieces> toDirectHashSet(Stream<? extends Pieces> blocksStream) {
        return blocksStream.parallel()
                .map(Pieces::getPieces)
                .map(LongPieces::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public boolean isHoldReduced() {
        return isHoldReduced;
    }

    public HashSet<LongPieces> getValidPieces() {
        return validPieces;
    }

    public HashSet<LongPieces> getAllPieces() {
        return allPieces;
    }

    public long getNumOfAllPatternSequences() {
        return numOfAllPatternSequences;
    }
}
