package newfield.step2;

class DeleteKey {
    public static DeleteKey create(long deleteKey, int lowerY) {
        return new DeleteKey(deleteKey, lowerY);
    }

    private final Long deleteKey;
    private final int lowerY;

    private DeleteKey(Long deleteKey, int lowerY) {
        this.deleteKey = deleteKey;
        this.lowerY = lowerY;
    }
}
