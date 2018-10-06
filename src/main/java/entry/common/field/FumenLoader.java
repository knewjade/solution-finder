package entry.common.field;

import common.tetfu.Tetfu;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import entry.CommandLineWrapper;
import entry.NormalCommandLineWrapper;
import entry.common.CommandLineFactory;
import exceptions.FinderParseException;
import org.apache.commons.cli.CommandLine;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FumenLoader {
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;
    private final CommandLineFactory commandLineFactory;

    public FumenLoader(CommandLineFactory commandLineFactory, MinoFactory minoFactory, ColorConverter colorConverter) {
        this.commandLineFactory = commandLineFactory;
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
    }

    public FieldData load(String data, int page) throws FinderParseException {
        String removeDomainData = Tetfu.removeDomainData(data);
        String removePrefixData = Tetfu.removePrefixData(removeDomainData);
        if (removePrefixData == null) {
            throw new FinderParseException("Unsupported tetfu: data=" + removeDomainData);
        }

        // テト譜面のエンコード
        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        List<TetfuPage> decoded = tetfu.decode(removePrefixData);

        // 指定されたページを抽出
        TetfuPage tetfuPage = extractTetfuPage(decoded, page);

        return load(tetfuPage);
    }

    private TetfuPage extractTetfuPage(List<TetfuPage> tetfuPages, int page) throws FinderParseException {
        if (page < 1) {
            throw new FinderParseException(String.format("Page of fumen should be 1 <= page: page=%d", page));
        } else if (page <= tetfuPages.size()) {
            return tetfuPages.get(page - 1);
        } else {
            throw new FinderParseException(String.format("Page of fumen is over max page: page=%d", page));
        }
    }

    private FieldData load(TetfuPage page) {
        // フィールドを設定
        ColoredField coloredField = page.getField();
        if (page.isPutMino()) {
            ColorType colorType = page.getColorType();
            Rotate rotate = page.getRotate();
            Mino mino = minoFactory.create(colorConverter.parseToBlock(colorType), rotate);

            int x = page.getX();
            int y = page.getY();
            coloredField.putMino(mino, x, y);
        }

        // コメントの確認
        String comment = page.getComment().trim();
        if (comment.isEmpty()) {
            return new FieldData(coloredField);
        }

        // オプションとして読み込む
        if (Character.isDigit(comment.charAt(0))) {
            // コメントの先頭が数字
            // 削除する行のオプションを追加
            comment = "--clear-line " + comment;
        }

        List<String> commentArgs = Arrays.stream(comment.split(" "))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        try {
            CommandLine commandLineTetfu = commandLineFactory.parse(commentArgs);
            CommandLineWrapper newWrapper = new NormalCommandLineWrapper(commandLineTetfu);
            return new FieldData(coloredField, newWrapper);
        } catch (FinderParseException ignore) {
            return new FieldData(coloredField);
        }
    }
}
