import entry.EntryPointMain;

public class Main2 {
    public static void main(String[] args) {
        String[] split = "path -t v115@9gwhDeE8whCeF8whBeG8whCeF8JeAgH -p I,*p3 -r true".split(" ");
        int returnCode = EntryPointMain.main(split);
        System.exit(returnCode);
    }
}
