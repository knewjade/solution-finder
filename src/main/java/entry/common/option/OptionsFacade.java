package entry.common.option;

import core.srs.MinoRotation;
import entry.common.kicks.factory.DefaultMinoRotationFactory;
import entry.common.kicks.factory.FileMinoRotationFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class OptionsFacade {
    public static Supplier<MinoRotation> createMinoRotationSupplier(String name) {
        name = name.trim().toLowerCase();
        if (name.startsWith("@")) {
            Path path = Paths.get(String.format("kicks/%s.properties", name.substring(1)));
            FileMinoRotationFactory factory = FileMinoRotationFactory.load(path);
            return factory::create;
        } else {
            switch (name) {
                case "default":
                case "srs":
                    return DefaultMinoRotationFactory::createDefault;
                default:
                    throw new IllegalArgumentException("Unsupported kicks name: " + name);
            }
        }
    }
}
