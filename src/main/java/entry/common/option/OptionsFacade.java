package entry.common.option;

import entry.common.kicks.NamedSupplierMinoRotation;
import entry.common.kicks.factory.FileMinoRotationFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class OptionsFacade {
    public static NamedSupplierMinoRotation createNamedMinoRotationSupplier(String name) {
        String trimmedName = name.trim().toLowerCase();

        switch (trimmedName) {
            case "default":
            case "srs":
                return NamedSupplierMinoRotation.createDefault();
        }

        if (trimmedName.startsWith("@") || trimmedName.startsWith("+")) {
            Path path = Paths.get(String.format("kicks/%s.properties", trimmedName.substring(1)));
            FileMinoRotationFactory factory = FileMinoRotationFactory.load(path);
            return new NamedSupplierMinoRotation(factory::create, name);
        }

        Path path = getPath(name).orElseThrow(() -> new IllegalArgumentException("Not found a kick file: " + name));
        FileMinoRotationFactory factory = FileMinoRotationFactory.load(path);
        return new NamedSupplierMinoRotation(factory::create, name);
    }

    private static Optional<Path> getPath(String name) {
        // 現在のカレントディレクトリからの相対パス
        Path relativePath = Paths.get(String.format("%s", name));
        if (relativePath.toFile().exists()) {
            return Optional.of(relativePath);
        }

        // jarファイルからの相対パス
        {
            File root = new File(System.getProperty("java.class.path"));
            Path resolved = root.toPath().getParent().resolve(relativePath);
            if (resolved.toFile().exists()) {
                return Optional.of(resolved);
            }
        }

        return Optional.empty();
    }
}
