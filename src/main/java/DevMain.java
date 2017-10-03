import entry.EntryPointMain;

public class DevMain {
    public static void main(String[] args) {
//        String command = "path -f csv -k p -t v115@KhA8FeG8CeH8LeAgH -p *!";
//        String command = "path -f link -t v115@KhA8FeF8DeI8KeAgH -p *!";
//        String command = "path -f link -t v115@BhR4ywA8DeA8R4wwB8DeF8DeF8JeAgH -p *!";
        String command = "path -f link -t v115@BhR4ywA8DeA8R4wwB8DeF8DeF8JeAgH -p [IZSLJ]!";


        int returnCode = EntryPointMain.main(command.split(" "));
        System.exit(returnCode);
    }
}
