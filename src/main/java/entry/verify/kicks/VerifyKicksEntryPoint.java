package entry.verify.kicks;

import common.tetfu.Tetfu;
import common.tetfu.TetfuElement;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ArrayColoredField;
import common.tetfu.field.ColoredField;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import core.srs.RotateDirection;
import entry.EntryPoint;
import exceptions.FinderException;
import exceptions.FinderTerminateException;

import java.util.ArrayList;
import java.util.List;

public class VerifyKicksEntryPoint implements EntryPoint {
    private final VerifyKicksSettings settings;

    public VerifyKicksEntryPoint(VerifyKicksSettings settings) {
        this.settings = settings;
    }

    @Override
    public void run() throws FinderException {
        MinoFactory minoFactory = new MinoFactory();
        MinoRotation minoRotation = settings.createMinoRotation();
        ColorConverter colorConverter = new ColorConverter();

        for (Piece piece : Piece.values()) {
            for (Rotate fromRotate : Rotate.values()) {
                List<TetfuElement> elements = new ArrayList<>();
                ColorType colorType = colorConverter.parseToColorType(piece);
                Mino mino = minoFactory.create(piece, fromRotate);

                int cx = Math.abs(mino.getMinX()) < Math.abs(mino.getMaxX()) ? 4 : 5;
                int cy = 10;

                ColoredField coloredField = new ArrayColoredField(24);

                // first
                {
                    String comment = String.format("from %s-%s", piece, fromRotate);
                    elements.add(new TetfuElement(coloredField, colorType, fromRotate, cx, cy, comment));
                }

                for (int[] position : mino.getPositions()) {
                    int x = cx + position[0];
                    int y = cy + position[1];
                    coloredField.setColorType(ColorType.Gray, x, y);
                }

                for (RotateDirection direction : RotateDirection.valuesWith180()) {
                    if (direction == RotateDirection.Rotate180 && minoRotation.noSupports180()) {
                        elements.add(new TetfuElement("180 is not supported"));
                        continue;
                    }

                    Rotate toRotate = fromRotate.get(direction);

                    int[][] patterns = minoRotation.getPatternsFrom(mino, direction);
                    for (int index = 0; index < patterns.length; index++) {
                        int[] pattern = patterns[index];
                        int x = cx + pattern[0];
                        int y = cy + pattern[1];
                        String mark = minoRotation.isPrivilegeSpins(mino, direction, index) ? " @" : "";
                        String comment = String.format("to %s: test %d%s", toDirectionName(direction), index + 1, mark);
                        elements.add(new TetfuElement(coloredField, colorType, toRotate, x, y, comment, false));
                    }
                }

                String encode = new Tetfu(minoFactory, colorConverter).encode(elements);
                System.out.printf("%s (from %s):%n", piece, fromRotate);
                System.out.printf("https://fumen.zui.jp/?D115@%s%n", encode);
            }
        }
    }

    private static String toDirectionName(RotateDirection direction) {
        switch (direction) {
            case Left:
                return "left";
            case Right:
                return "right";
            case Rotate180:
                return "180";
        }
        throw new IllegalStateException();
    }

    @Override
    public void close() throws FinderTerminateException {
    }
}


