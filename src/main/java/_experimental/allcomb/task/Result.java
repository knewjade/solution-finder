package _experimental.allcomb.task;

import _experimental.allcomb.memento.MinoFieldMemento;

public class Result {
    private final MinoFieldMemento memento;

    public Result(MinoFieldMemento memento) {
        // TODO: result作成前に回転入れのチェック
//            System.out.println(memento.getRawOperations().size());
        this.memento = memento;
    }
}
