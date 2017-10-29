package common.datastore;

public interface OperationWithKey extends Operation {
    long getNeedDeletedKey();

    long getUsingKey();
}
