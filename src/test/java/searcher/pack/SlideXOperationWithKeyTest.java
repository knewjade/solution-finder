package searcher.pack;

import common.datastore.FullOperationWithKey;
import core.mino.Mino;
import core.mino.Piece;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlideXOperationWithKeyTest {
    @Test
    void get() {
        Randoms randoms = new Randoms();

        for (int count = 0; count < 10000; count++) {
            Piece piece = randoms.block();
            Rotate rotate = randoms.rotate();
            Mino mino = new Mino(piece, rotate);
            int x = randoms.nextIntOpen(0, 10);
            int y = randoms.nextIntOpen(0, 10);
            long usingKey = randoms.key();
            long deleteKey = randoms.key();
            FullOperationWithKey operationWithKey = new FullOperationWithKey(mino, x, y, deleteKey, usingKey);

            int slide = randoms.nextIntOpen(4);
            SlideXOperationWithKey key = new SlideXOperationWithKey(operationWithKey, slide);
            assertThat(key)
                    .returns(x + slide, SlideXOperationWithKey::getX)
                    .returns(y, SlideXOperationWithKey::getY)
                    .returns(deleteKey, SlideXOperationWithKey::getNeedDeletedKey)
                    .returns(usingKey, SlideXOperationWithKey::getUsingKey);
        }
    }
}