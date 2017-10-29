package searcher.pack;

import common.datastore.OperationWithKey;
import common.datastore.MinoOperationWithKey;
import core.mino.Block;
import core.mino.Mino;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlideXOperationWithKeyTest {
    @Test
    void get() {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            Block block = randoms.block();
            Rotate rotate = randoms.rotate();
            Mino mino = new Mino(block, rotate);
            int x = randoms.nextInt(0, 10);
            int y = randoms.nextInt(0, 10);
            long usingKey = randoms.key();
            long deleteKey = randoms.key();
            OperationWithKey operationWithKey = new MinoOperationWithKey(mino, x, y, deleteKey, usingKey);

            int slide = randoms.nextInt(4);
            SlideXOperationWithKey key = new SlideXOperationWithKey(operationWithKey, slide);
            assertThat(key)
                    .returns(x + slide, SlideXOperationWithKey::getX)
                    .returns(y, SlideXOperationWithKey::getY)
                    .returns(mino, SlideXOperationWithKey::getMino)
                    .returns(deleteKey, SlideXOperationWithKey::getNeedDeletedKey)
                    .returns(usingKey, SlideXOperationWithKey::getUsingKey);
        }
    }
}