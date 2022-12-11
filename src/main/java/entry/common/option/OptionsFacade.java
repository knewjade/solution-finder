package entry.common.option;

import entry.common.kicks.NamedSupplierMinoRotation;
import entry.common.kicks.factory.FileMinoRotationFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class OptionsFacade {
    public static NamedSupplierMinoRotation createNamedMinoRotationSupplier(String name) {
        String trimmedName = name.trim().toLowerCase();
        if (trimmedName.startsWith("@") || trimmedName.startsWith("+")) {
            Path path = Paths.get(String.format("kicks/%s.properties", trimmedName.substring(1)));
            FileMinoRotationFactory factory = FileMinoRotationFactory.load(path);
            return new NamedSupplierMinoRotation(factory::create, name);
        } else {
            switch (trimmedName) {
                case "default":
                case "srs":
                    return NamedSupplierMinoRotation.createDefault();
                default:
                    throw new IllegalArgumentException("kicks name is invalid: " + name);
            }
        }
    }
}
