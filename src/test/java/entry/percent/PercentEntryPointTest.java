package entry.percent;

import org.junit.Test;

public class PercentEntryPointTest {
    @Test
    public void test1() throws Exception {
        String commands = "--hold avoid --tetfu v115@9gD8DeF8CeG8BeH8CeC8JeAgWBAUAAAA -h";
        PercentEntryPoint entryPoint = new PercentEntryPoint(commands.split(" "));
        entryPoint.run();
    }
}
