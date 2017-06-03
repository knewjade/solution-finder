package entry.util.fig;

import common.tetfu.TetfuPage;
import common.tetfu.field.ColoredField;
import util.gif.FrameType;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

// TODO: unittest: write
public class FigUtilSettings {
    private static final String DEFAULT_OUTPUT_FILE_PATH = "output/fig.gif";

    private FrameType frameType = FrameType.Basic;
    private boolean isUsingHold = true;
    private int height = -1;
    private int delay = 20;
    private List<TetfuPage> tetfuPages = new ArrayList<>();
    private String outputFilePath = DEFAULT_OUTPUT_FILE_PATH;
    private boolean isInfiniteLoop = true;
    private int nextBoxCount = 5;
    private int startPage = 1;
    private int endPage = -1;  // include

    // ********* Getter ************
    FrameType getFrameType() {
        return frameType;
    }

    boolean isUsingHold() {
        return isUsingHold;
    }

    int getHeight() {
        return height;
    }

    List<TetfuPage> getTetfuPages() {
        return tetfuPages;
    }

    String getOutputFilePath() {
        return outputFilePath;
    }

    boolean getInfiniteLoop() {
        return isInfiniteLoop;
    }

    int getNextBoxCount() {
        return nextBoxCount;
    }

    int getDelay() {
        return delay;
    }

    int getStartPage() {
        return startPage;
    }

    int getEndPage() {
        return endPage;
    }

    // ********* Setter ************

    void setUsingHold(Boolean isUsingHold) {
        this.isUsingHold = isUsingHold;
    }

    void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    void setFrameType(FrameType frameType) {
        this.frameType = frameType;
    }

    void setDelay(int delay) {
        this.delay = delay;
    }

    void setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
    }

    void setNextBoxCount(int nextBoxCount) {
        this.nextBoxCount = nextBoxCount;
    }

    void setTetfuPages(List<TetfuPage> tetfuPages, int startPage, int endPage) {
        this.tetfuPages = tetfuPages;
        this.startPage = startPage;
        this.endPage = endPage < 1 ? tetfuPages.size() : endPage;
    }

    void setHeight(int height) {
        this.height = height;
    }

    void adjust() {
        // Quizがない場合はホールドは使えない
        boolean isUsingQuiz = tetfuPages.subList(0, startPage).stream().map(TetfuPage::getComment).anyMatch(s -> s.startsWith("#Q="));
        if (!isUsingQuiz)
            setUsingHold(false);

        // 高さの指定がないときは最も高い場所 + 1とする
        if (this.height == -1) {
            OptionalInt maxHeight = tetfuPages.subList(startPage - 1, endPage).stream()
                    .map(TetfuPage::getField)
                    .mapToInt(ColoredField::getUsingHeight)
                    .max();

            this.height = maxHeight.orElse(-1) + 1;

            if (height <= 0)
                this.height = 1;
        }

        // ホールドを使わない場合はRightに変更
        if (!this.isUsingHold && frameType == FrameType.Basic) {
            frameType = FrameType.Right;
        }

        // ネクストのチェック
        // ネクストがない場合はチェックは必要がない
        if (frameType == FrameType.NoFrame) {
            return;
        }

        // フィールドの高さを計算し、その高さで置けるネクスト数を計算
        int fieldHeight = 34 * this.height + 2;
        int canPutCount = (fieldHeight - 5) / 52;

        // ネクスト (& ホールド)で必要な個数を算出し、ネクストを置けるならチェック終了
        int count = this.isUsingHold && frameType == FrameType.Right ? nextBoxCount + 1 : nextBoxCount;
        if (count <= canPutCount)
            return;

        // ネクストを置けるようにフィールドの高さを調整
        int needHeightPx = 52 * count + 5;
        setHeight((int) Math.ceil((needHeightPx - 2) / 34.0));
    }
}
