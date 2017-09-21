package _usecase;

import _usecase.util.fig.UtilFigResources;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FigureFileHelper {
    private static final String GIF_PATH = concatPath("output", "fig.gif");

    private static String concatPath(String... names) {
        return FileHelper.concatPath(names);
    }

    public static void deleteGifFile() throws IOException {
        File file = new File(GIF_PATH);
        FileHelper.deleteFileAndClose(file);
    }

    public static ByteSource loadGifByteSource() {
        File file = new File(GIF_PATH);
        return Files.asByteSource(file);
    }

    public static ByteSource loadResourceByteSource(UtilFigResources resources) {
        URL url = Resources.getResource(resources.getResourceName());
        File file = new File(url.getFile());
        assert file.isFile();
        return Files.asByteSource(file);
    }

    public static List<ByteSource> loadResourcesByteSource(UtilFigResources resources) throws IOException {
        URL url = Resources.getResource(resources.getResourceName());
        File directory = new File(url.getFile());
        assert directory.isDirectory();
        return java.nio.file.Files.walk(directory.toPath())
                .map(Path::toFile)
                .filter(File::isFile)
                .sorted(Comparator.comparing(File::getName))
                .map(Files::asByteSource)
                .collect(Collectors.toList());
    }
}
