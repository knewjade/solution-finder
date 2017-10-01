import entry.EntryPointMain;

public class DevMain {
    public static void main(String[] args) {
        String command = "path -f csv -k p -t v115@9gF8DeF8DeF8DeF8NeAgH -p T,*p4";
        int returnCode = EntryPointMain.main(command.split(" "));
        System.exit(returnCode);
    }
}
