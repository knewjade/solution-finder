package common.parser;

import core.mino.Block;
import core.srs.Rotate;

class StringEnumTransform {
    static Block toBlock(String name) {
        switch (name) {
            case "T":
                return Block.T;
            case "S":
                return Block.S;
            case "Z":
                return Block.Z;
            case "I":
                return Block.I;
            case "O":
                return Block.O;
            case "J":
                return Block.J;
            case "L":
                return Block.L;
        }
        throw new IllegalArgumentException("No reachable");
    }

    static Rotate toRotate(String name) {
        switch (name) {
            case "0":
                return Rotate.Spawn;
            case "L":
                return Rotate.Left;
            case "2":
                return Rotate.Reverse;
            case "R":
                return Rotate.Right;
        }
        throw new IllegalArgumentException("No reachable");
    }

    static String toString(Block block) {
        return block.getName().toLowerCase();
    }

    static String toString(Rotate rotate) {
        switch (rotate) {
            case Spawn:
                return "0";
            case Left:
                return "L";
            case Reverse:
                return "2";
            case Right:
                return "R";
        }
        throw new IllegalStateException("No reachable");
    }
}
