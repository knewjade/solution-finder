package _experimental.main;

import common.buildup.BuildUpStream;
import common.datastore.BlockCounter;
import common.datastore.OperationWithKey;
import common.datastore.SimpleOperationWithKey;
import common.datastore.pieces.LongPieces;
import common.datastore.pieces.Pieces;
import common.order.OrderLookup;
import common.order.StackOrder;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import entry.path.LockedBuildUpListUpThreadLocal;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        // Load pieces
        Path piecesPath = Paths.get("output/allonhold.txt");
        Set<Pieces> piecesSet = Collections.synchronizedSet(Files.lines(piecesPath, Charset.forName("UTF-8"))
                .limit(1L)
                .map(s -> s.split(""))
                .map(array -> {
                    Pieces pieces = new LongPieces();
                    for (String name : array) {
                        Block block = Block.valueOf(name);
                        pieces = pieces.addAndReturnNew(block);
                    }
                    return pieces;
                })
                .collect(Collectors.toCollection(HashSet::new)));

        System.out.println(piecesSet.size());

        // Load perfect
        int height = 4;
        LockedBuildUpListUpThreadLocal threadLocal = new LockedBuildUpListUpThreadLocal(height);
        MinoFactory minoFactory = new MinoFactory();

        //
        Field field = FieldFactory.createField(height);

        BlockCounter counter;
        for (Pieces pieces : piecesSet) {
            System.out.println(pieces);
            counter = new BlockCounter(pieces.getBlockStream());
        }
        Path perfectPath = Paths.get("output/all10mino.csv");
        Files.lines(perfectPath, Charset.forName("UTF-8"))
                .limit(1L)
                .parallel()
                .map(s -> s.split(";"))
                .filter(strings -> {
                    List<Block> blocks = Arrays.stream(strings)
                            .map(s -> s.split(","))
                            .map(string -> {
                                return Block.valueOf(string[0]);
                            })
                            .collect(Collectors.toList());
                    System.out.println(blocks);
                    System.out.println("---");
                    List<StackOrder<Block>> reverse = OrderLookup.reverseBlocks(blocks, 11);
                    for (StackOrder<Block> order : reverse) {
                        System.out.println(order.toList());
                    }
                    return true;
                })
                .map(strings -> {
                    LinkedList<OperationWithKey> operationWithKeys = Arrays.stream(strings)
                            .map(s -> s.split(","))
                            .map(string -> {
                                Block block = Block.valueOf(string[0]);
                                Rotate rotate = parseToRotate(string[1]);
                                Mino mino = minoFactory.create(block, rotate);
                                int x = Integer.valueOf(string[2]);
                                int y = Integer.valueOf(string[3]);
                                long deleteKey = Long.valueOf(string[4]);
                                long usingKey = Long.valueOf(string[5]);
                                return new SimpleOperationWithKey(mino, x, y, deleteKey, usingKey);
                            })
                            .collect(Collectors.toCollection(LinkedList::new));
                    return operationWithKeys;
                })

                .forEach(operationWithKeys -> {
                    BuildUpStream buildUpStream = threadLocal.get();
                    buildUpStream.existsValidBuildPatternDirectly(field, operationWithKeys)
                            .map(validKeys -> {
                                Pieces pieces = new LongPieces();
                                for (OperationWithKey validKey : validKeys) {
                                    Block block = validKey.getMino().getBlock();
                                    pieces = pieces.addAndReturnNew(block);
                                }
                                return pieces;
                            })
                            .forEach(pieces -> {
                                if (piecesSet.contains(pieces))
                                    piecesSet.remove(pieces);
                            });
                    System.out.println(piecesSet.size());
                });
    }

    private static Rotate parseToRotate(String name) {
        switch (name) {
            case "0":
                return Rotate.Spawn;
            case "R":
                return Rotate.Right;
            case "L":
                return Rotate.Left;
            case "2":
                return Rotate.Reverse;
        }
        throw new IllegalStateException("No reachable");
    }
}
