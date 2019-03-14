package searcher.spins;

import searcher.spins.candidates.Candidate;

import java.util.stream.Stream;

public interface SpinRunner {
    Stream<? extends Candidate> search(SecondPreSpinRunner secondPreSpinRunner, int minClearedLine);
}
