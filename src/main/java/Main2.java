import entry.EntryPoint;
import entry.all_path.AllPathEntryPoint;
import entry.percent.PercentEntryPoint;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main2 {
    public static void main(String[] args) throws ParseException {
        if (args.length < 1)
            throw new IllegalArgumentException("No command: Use p-percent, all-path");

        // 引数リストの作成
        List<String> argsList = new ArrayList<>(Arrays.asList(args).subList(1, args.length));

        // 実行を振り分け
        EntryPoint entryPoint = createEntryPoint(args[0], argsList);
        entryPoint.run();
    }

    private static EntryPoint createEntryPoint(String type, List<String> commands) {
        // 実行を振り分け
        switch (type) {
            case "p-percent":
                return new PercentEntryPoint(commands);
            case "all-path":
                return new AllPathEntryPoint(commands);
            default:
                throw new IllegalArgumentException("Invalid type: Use percent, allpath");
        }
    }
}
