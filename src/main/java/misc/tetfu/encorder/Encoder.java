package misc.tetfu.encorder;

import java.util.ArrayList;
import java.util.List;

import static misc.tetfu.TetfuTable.ENCODE_TABLE_SIZE;

public abstract class Encoder {
    private final List<Integer> encodedValues = new ArrayList<>();

    void pushValues(int value, int splitCount) {
        int current = value;
        for (int count = 0; count < splitCount; count++) {
            this.encodedValues.add(current % ENCODE_TABLE_SIZE);
            current /= ENCODE_TABLE_SIZE;
        }
    }

    public List<Integer> getEncodedValues() {
        return encodedValues;
    }
}
