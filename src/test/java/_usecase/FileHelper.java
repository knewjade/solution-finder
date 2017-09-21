package _usecase;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

class FileHelper {
    static void deleteFileAndClose(File file) {
        if (file.exists()) {
            // noinspection ResultOfMethodCallIgnored
            file.delete();

            try {
                while (file.exists())
                    Thread.sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    static String concatPath(String... names) {
        return Arrays.stream(names).collect(Collectors.joining(File.separator));
    }
}
