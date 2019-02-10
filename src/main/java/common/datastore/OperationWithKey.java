package common.datastore;

public interface OperationWithKey extends Operation {
    long getUsingKey();

    long getNeedDeletedKey();
}
