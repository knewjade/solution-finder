package entry.util.fumen.converter;

import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.TetfuPage;
import common.tetfu.common.ColorConverter;
import core.mino.MinoFactory;
import exceptions.FinderParseException;

import java.util.List;
import java.util.stream.Collectors;

public class RemoveCommentConverter implements FumenConverter {
    private final MinoFactory minoFactory;
    private final ColorConverter colorConverter;

    public RemoveCommentConverter(MinoFactory minoFactory, ColorConverter colorConverter) {
        this.minoFactory = minoFactory;
        this.colorConverter = colorConverter;
    }

    @Override
    public String parse(String data) throws FinderParseException {
        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        List<TetfuPage> pages = tetfu.decode(data);

        List<TetfuElement> elements = pages.stream().map(page -> new TetfuElement(
                page.getField(), page.getColorType(), page.getRotate(), page.getX(), page.getY(), "",
                page.isLock(), page.isMirror(), page.isBlockUp(), page.getBlockUpList()
        )).collect(Collectors.toList());
        String encode = tetfu.encode(elements);

        return String.format("v115@%s", encode);
    }
}
