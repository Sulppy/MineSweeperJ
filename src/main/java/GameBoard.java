import io.qt.core.QObject;

public abstract class GameBoard extends QObject {

    private static boolean End;
    private static boolean WinCondition;
    private boolean BoardFilled;
    public int countFlags;
    public Difficult difficult;

    public GameBoard() {
        End = false;
        WinCondition = false;
        BoardFilled = false;
    }

    public void setEnd() {
        End = true;
    }

    public static boolean isEnd() {
        return End;
    }

    public static boolean isWinCondition() {
        return WinCondition;
    }

    public void setWin(boolean winCondition) {
        WinCondition = winCondition;
    }

    public void setWin() {
        WinCondition = true;
    }

    public void setFilled() {
        BoardFilled = true;
    }

    public void setFilled(boolean filled) {
        BoardFilled = filled;
    }

    public boolean isBoardFilled() {
        return BoardFilled;
    }

    abstract void placeMines();
    abstract void calculateAdjacentMines();
    abstract int countAdjacentMines(int row, int col);
    abstract void revealAdjacentCells(int row, int col);
    abstract int countAdjacentFlags(int row, int col);
    abstract void handleNumberButtonClick(int row, int col);
    abstract void checkWinCondition();
}
