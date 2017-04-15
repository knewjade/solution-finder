package main;

import core.mino.Block;
import concurrent.checker.invoker.OrderLookup;
import concurrent.checker.invoker.Pieces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static core.mino.Block.*;

public class Experiment2 {
    public static void main(String[] args) {
        List<Block> blocks = Arrays.asList(T, I, S);
        ArrayList<Pieces> reverses = OrderLookup.reverse(blocks, blocks.size() + 1);
        for (Pieces reverse : reverses) {
            System.out.println(reverse);
        }
    }
}



