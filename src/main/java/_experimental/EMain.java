package _experimental;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EMain {
    public static void main(String[] args) {
        Path path = Paths.get("output/test.csv");
        System.out.println(path);

        File file = path.toFile();
        System.out.println(file);

        Path fileName = path.getFileName();
        System.out.println(fileName);

        String name = file.getName();
        System.out.println(name);

        File file1 = new File("");
        System.out.println(";" + file1);
    }
}
