package _experimental.allcomb;

public class InOutPairField {
    private final ColumnField innerField;
    private final ColumnField outerField;

    public InOutPairField(ColumnField innerField, ColumnField outerField) {
        this.innerField = innerField;
        this.outerField = outerField;
    }

    public ColumnField getOuterField() {
        return outerField;
    }

    public ColumnField getInnerField() {
        return innerField;
    }
}
