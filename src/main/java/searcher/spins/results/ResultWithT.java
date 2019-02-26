package searcher.spins.results;


import common.datastore.PieceCounter;
import core.field.Field;
import core.neighbor.SimpleOriginalPiece;

public class ResultWithT {
    private final Result result;
    private final SimpleOriginalPiece operationT;
    private final Field notAllowed;

    public ResultWithT(Result result, SimpleOriginalPiece operationT, Field notAllowed) {
        this.result = result;
        this.operationT = operationT;
        this.notAllowed = notAllowed;
    }

    public Result getResult() {
        return result;
    }

    public SimpleOriginalPiece getOperationT() {
        return operationT;
    }

    public Field getNotAllowed() {
        return notAllowed;
    }

    public Field getAllMergedField() {
        return result.getAllMergedField();
    }

    public Field freezeAllMergedField() {
        return result.freezeAllMergedField();
    }

    public int getNumOfUsingPiece() {
        return result.getNumOfUsingPiece();
    }

    public PieceCounter getReminderPieceCounter() {
        return result.getRemainderPieceCounter();
    }

//    public Field getInitField() {
//        return result.getInitField();
//    }
//
//    public Stream<SimpleOriginalPiece> operationStream() {
//        return result.operationStream();
//    }
//
}
