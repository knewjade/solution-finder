package util.fig.output;

import common.tetfu.TetfuPage;

import java.io.IOException;
import java.util.List;

public interface FigWriter {
     void write(List<TetfuPage> tetfuPages) throws IOException;
}
