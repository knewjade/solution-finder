package entry.common.kicks.factory;

import core.srs.MinoRotation;
import entry.common.kicks.KickPattern;
import entry.common.kicks.KickPatternInterpreter;
import entry.common.kicks.KickPatterns;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class FileMinoRotationFactory {
    public static FileMinoRotationFactory load(Path path) {
        Properties properties = new Properties();

        try (InputStream stream = Files.newInputStream(path)) {
            properties.load(stream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        List<KickPattern> kickPatternList = properties.entrySet().stream()
                .map(property -> {
                    Object key = property.getKey();
                    if (!(key instanceof String)) {
                        throw new IllegalStateException("key is not string");
                    }

                    Object value = property.getValue();
                    if (!(value instanceof String)) {
                        throw new IllegalStateException("value is not string");
                    }

                    return KickPatternInterpreter.create((String) key, (String) value);
                })
                .collect(Collectors.toList());

        KickPatterns kickPatterns = KickPatterns.create(kickPatternList);
        MinoRotationSupplier supplier = new MinoRotationSupplier(kickPatterns);
        return new FileMinoRotationFactory(supplier);
    }

    private final MinoRotationSupplier supplier;

    public FileMinoRotationFactory(MinoRotationSupplier supplier) {
        this.supplier = supplier;
    }

    public MinoRotation create() {
        return supplier.get();
    }
}
