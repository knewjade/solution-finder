package entry;

public class Settings {
    private boolean isUsingHold = true;
    private InvokeType invokeType = InvokeType.PerfectPercent;

    boolean isUsingHold() {
        return isUsingHold;
    }

    public InvokeType getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(InvokeType invokeType) {
        this.invokeType = invokeType;
    }

    public void setUsingHold(boolean flag) {
        this.isUsingHold = flag;
    }
}
