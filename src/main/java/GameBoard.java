import io.qt.core.QObject;
import io.qt.core.Qt;

public abstract class GameBoard extends QObject {

    private static boolean End;
    private static boolean WinCondition;
    private boolean BoardFilled;
    public int countFlags;
    public Difficult difficult;

    private Qt.MouseButton flagMouseButton;
    private Qt.MouseButton openMouseButton;

    public GameBoard() {
        End = false;
        WinCondition = false;
        BoardFilled = false;
        setFlagButtonR();
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

    public boolean isBoardFilled() {
        return BoardFilled;
    }

    public void setFlagButtonR() {
        flagMouseButton = Qt.MouseButton.RightButton;
        openMouseButton = Qt.MouseButton.LeftButton;
    }

    public void setFlagButtonL() {
        flagMouseButton = Qt.MouseButton.LeftButton;
        openMouseButton = Qt.MouseButton.RightButton;
    }

    public Qt.MouseButton getFlagMouseButton() {
        return flagMouseButton;
    }
    public Qt.MouseButton getOpenMouseButton() {
        return openMouseButton;
    }
}
