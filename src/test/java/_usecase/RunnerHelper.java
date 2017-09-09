package _usecase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class RunnerHelper {
    static Log runnerCatchingLog(RunnableWithException runnable) throws Exception {
        PrintStream outBackup = System.out;
        PrintStream errBackup = System.err;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));

        int returnCode = runnable.run();

        System.setOut(outBackup);
        System.setErr(errBackup);

        return new Log(returnCode, out.toString(), err.toString());
    }
}
