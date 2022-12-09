package entry.verify.kicks;

import core.srs.MinoRotation;
import entry.common.kicks.factory.DefaultMinoRotationFactory;
import entry.common.option.OptionsFacade;

import java.util.function.Supplier;

public class VerifyKicksSettings {
    private Supplier<MinoRotation> minoRotationSupplier = DefaultMinoRotationFactory::createDefault;

    MinoRotation createMinoRotation() {
        return minoRotationSupplier.get();
    }

    public void setKicks(String name) {
        minoRotationSupplier = OptionsFacade.createMinoRotationSupplier(name);
    }
}
