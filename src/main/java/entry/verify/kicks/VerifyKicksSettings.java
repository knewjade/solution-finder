package entry.verify.kicks;

import core.srs.MinoRotation;
import entry.common.kicks.NamedSupplierMinoRotation;
import entry.common.option.OptionsFacade;

public class VerifyKicksSettings {
    private NamedSupplierMinoRotation namedSupplierMinoRotation = NamedSupplierMinoRotation.createDefault();

    MinoRotation createMinoRotation() {
        return namedSupplierMinoRotation.getSupplier().get();
    }

    void setKicks(String name) {
        namedSupplierMinoRotation = OptionsFacade.createNamedMinoRotationSupplier(name);
    }
}
