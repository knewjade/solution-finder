package tetfu;

public class ActionFlags {
    public final String escapedComment;
    public final String prevEscapedComment;
    public final boolean isLock = true;
    public final boolean isCommented = true;
    public final boolean isColor;  // 基本的にindex == 0のときのみtrue
    public final boolean isMirror = false;
    public final boolean isBlockUp = false;

    ActionFlags(String escapedComment, String prevEscapedComment, int index) {
        this.escapedComment = escapedComment;
        this.prevEscapedComment = prevEscapedComment;
        this.isColor = index == 0;
    }
}
