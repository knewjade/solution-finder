package entry.common.kicks;

import core.srs.MinoRotation;
import entry.common.kicks.factory.DefaultMinoRotationFactory;

import java.util.function.Supplier;

public class NamedSupplierMinoRotation {
    public static NamedSupplierMinoRotation createDefault() {
        return new NamedSupplierMinoRotation(DefaultMinoRotationFactory::createDefault, "default");
    }

    private final Supplier<MinoRotation> minoRotationSupplier;
    private final String name;

    public NamedSupplierMinoRotation(Supplier<MinoRotation> minoRotationSupplier, String name) {
        this.minoRotationSupplier = minoRotationSupplier;
        this.name = name;
    }

    public Supplier<MinoRotation> getSupplier() {
        return minoRotationSupplier;
    }

    public String getName() {
        return name;
    }
}
