import entry.EntryPointMain;

public class Main2 {
    public static void main(String[] args) {
        String s = "path -f nousecsv -c 4 -p *p5 -t v115@BhF8DeF8DeF8DeF8JeAgH";
        int returnCode = EntryPointMain.main(s.split(" "));
        System.exit(returnCode);
    }
}
