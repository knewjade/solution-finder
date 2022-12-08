package entry.verify.kicks;

import core.srs.MinoRotation;
import entry.common.kicks.factory.DefaultMinoRotationFactory;

public class VerifyKicksSettings {
    MinoRotation createMinoRotation() {
        return DefaultMinoRotationFactory.createDefault();
    }
}
