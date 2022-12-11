package entry.util.fig;

import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.Rotate;
import util.fig.FrameType;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

// TODO: unittest: write
public class FigUtilSettings {
    private static final String DEFAULT_OUTPUT_FILE_PATH = "output/fig.gif";
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";

    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private FrameType frameType = FrameType.Basic;
    private boolean isUsingHold = true;
    private int height = -1;
    private int delay = 30;
    private List<TetfuPage> tetfuPages = new ArrayList<>();
    private String outputFilePath = DEFAULT_OUTPUT_FILE_PATH;
    private boolean isInfiniteLoop = true;
    private int nextBoxCount = 5;
    private int startPage = 1;
    private int endPage = -1;  // include
    private FigFormat figFormat = FigFormat.Gif;
    private String colorTheme = "default";

    // ********* Getter ************
    String getLogFilePath() {
        return logFilePath;
    }

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

    int getStartPageIndex() {
        return startPage - 1;
    }

    int getEndPage() {
        return endPage;
    }

    FigFormat getFigFormat() {
        return figFormat;
    }

    String getColorTheme() {
        return colorTheme;
    }

    // ********* Setter ************

    void setLogFilePath(String path) {
        this.logFilePath = path;
    }

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
        this.endPage = endPage;
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
            MinoFactory minoFactory = new MinoFactory();
            ColorConverter colorConverter = new ColorConverter();
            OptionalInt maxHeight = tetfuPages.subList(startPage - 1, endPage).stream()
                    .mapToInt(page -> {
                        ColoredField field = page.getField();
                        int fieldHeight = field.getUsingHeight();
                        ColorType colorType = page.getColorType();
                        if (ColorType.isMinoBlock(colorType)) {
                            Piece piece = colorConverter.parseToBlock(colorType);
                            Rotate rotate = page.getRotate();
                            Mino mino = minoFactory.create(piece, rotate);
                            int minoHeight = page.getY() + mino.getMaxY() + 1;
                            return Math.max(fieldHeight, minoHeight);
                        } else {
                            return fieldHeight;
                        }
                    })
                    .max();

            this.height = maxHeight.orElse(0) + 1;

            if (height <= 0)
                this.height = 1;
            else if (23 <= height)
                this.height = 23;
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

    void setFigFormat(FigFormat figFormat) {
        this.figFormat = figFormat;
    }

    boolean isOutputToConsole() {
        return true;
    }

    void setColorTheme(String colorTheme) {
        this.colorTheme = colorTheme.startsWith("@") || colorTheme.startsWith("+")
                ? colorTheme.substring(1)
                : colorTheme;
    }
}
