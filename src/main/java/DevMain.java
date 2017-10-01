import entry.EntryPointMain;

public class DevMain {
    public static void main(String[] args) {
        String command = "path -f csv -k p -t v115@9gE8EeE8DeF8CeG8DeA8JeAgH -p I,*p4";
        int returnCode = EntryPointMain.main(command.split(" "));
        System.exit(returnCode);
    }
}
