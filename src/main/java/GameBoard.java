import io.qt.core.QObject;

public abstract class GameBoard extends QObject {

    private static boolean End;
    private boolean isWinCondition;
    private boolean isBoardFilled;
    public int countFlags;
    public Difficult difficult;

    public GameBoard() {
        End = false;
        isWinCondition = false;
        isBoardFilled = false;
    }

    public void setEnd() {
        End = true;
    }

    public void setEnd(boolean end) {
        End = end;
    }

    public static boolean isEnd() {
        return End;
    }

    public void setWin(boolean winCondition) {
        isWinCondition = winCondition;
    }

    public void setWin() {
        isWinCondition = true;
    }

    public void setFilled() {
        isBoardFilled = true;
    }

    public void setFilled(boolean filled) {
        isBoardFilled = filled;
    }

    abstract void placeMines();
    abstract void calculateAdjacentMines();
    abstract int countAdjacentMines(int row, int col);
    abstract void revealAdjacentCells(int row, int col);
    abstract int countAdjacentFlags(int row, int col);
    abstract void handleNumberButtonClick(int row, int col);
    abstract void checkWinCondition();
}
