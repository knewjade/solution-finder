package searcher.spins.spin;

public class SpinDefaultPriority {
    public static int getSpinPriority(Spin spin) {
        int clearedLine = spin.getClearedLine();

        switch (spin.getSpin()) {
            case Regular: {
                switch (spin.getName()) {
                    case NoName: {
                        return clearedLine * 10 + 5;
                    }
                    case Fin: {
                        return clearedLine * 10 + 4;
                    }
                    case Iso: {
                        return clearedLine * 10 + 3;
                    }
                }
            }
            case Mini: {
                switch (spin.getName()) {
                    case NoName: {
                        return clearedLine * 10 + 2;
                    }
                    case Neo: {
                        return clearedLine * 10 + 1;
                    }
                }
            }
        }

        throw new IllegalStateException(spin.toString());
    }
}
