package _usecase.spin.files;

import searcher.spins.spin.Spin;
import searcher.spins.spin.TSpinNames;
import searcher.spins.spin.TSpins;

public enum TSpinType {
    RegularSingle(new Spin(TSpins.Regular, TSpinNames.NoName, 1)),
    RegularDouble(new Spin(TSpins.Regular, TSpinNames.NoName, 2)),
    RegularTriple(new Spin(TSpins.Regular, TSpinNames.NoName, 3)),
    FinSingle(new Spin(TSpins.Regular, TSpinNames.Fin, 1)),
    FinDouble(new Spin(TSpins.Regular, TSpinNames.Fin, 2)),
    NeoSingle(new Spin(TSpins.Mini, TSpinNames.Neo, 1)),
    NeoDouble(new Spin(TSpins.Mini, TSpinNames.Neo, 2)),
    IsoSingle(new Spin(TSpins.Regular, TSpinNames.Iso, 1)),
    IsoDouble(new Spin(TSpins.Regular, TSpinNames.Iso, 2)),
    ;

    private final Spin spin;

    TSpinType(Spin spin) {
        this.spin = spin;
    }
}
