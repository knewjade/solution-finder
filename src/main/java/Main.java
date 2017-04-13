import core.field.Field;
import core.field.FieldFactory;
import entry.CheckerEntry;
import misc.PiecesGenerator;
import misc.SyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

// TODO: List of ALL
// Write unittest for searcher.common
// Computerize from main
// Write unittest for main
public class Main {
    private static final String FIELD_TXT = "field.txt";
    private static final String CHARSET_NAME = "utf-8";
    private static final String PATTERNS_TXT = "patterns.txt";

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        int maxClearLine;
        String marks = "";
        try (Scanner scanner = new Scanner(new File(FIELD_TXT), CHARSET_NAME)) {
            if (!scanner.hasNextInt())
                throw new IllegalArgumentException("Cannot read Field Height from " + FIELD_TXT);
            maxClearLine = scanner.nextInt();

            if (maxClearLine < 2 || 12 < maxClearLine)
                throw new IllegalArgumentException("Field Height should be 2 <= height <= 12 in " + FIELD_TXT);

            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNext())
                stringBuilder.append(scanner.nextLine());

            marks = stringBuilder.toString();
        }

        ArrayList<String> patterns = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(PATTERNS_TXT), CHARSET_NAME)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.equals(""))
                    continue;

                try {
                    PiecesGenerator.verify(line);
                    patterns.add(line);
                } catch (SyntaxException e) {
                    System.err.println("Cannot parse pattern from " + PATTERNS_TXT);
                    System.err.println("Syntax Error: " + e.getMessage());
                    System.exit(1);
                }
            }
        }

        if (patterns.isEmpty())
            throw new IllegalArgumentException("Cannot read patterns from " + PATTERNS_TXT);

        Field field = FieldFactory.createField(marks);

        try (Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("last_output.txt"), "utf-8"))) {
            CheckerEntry entry = new CheckerEntry(fileWriter);
            entry.invoke(field, patterns, maxClearLine);
        }
    }
}
