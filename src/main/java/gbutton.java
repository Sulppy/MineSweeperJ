import io.qt.core.Qt;
import io.qt.gui.QPalette;

public class gbutton {
    int num;
    boolean isMine;
    boolean isFlagged = false;
    qGameButtons qbtn;

    gbutton() {
        isFlagged = false;
        isMine = false;
        num = -1;
        qbtn = new qGameButtons();
        qbtn.setFixedSize(30, 30);
        qbtn.setText("");
        QPalette p = new QPalette();
        p.setBrush(QPalette.ColorRole.Button, Qt.GlobalColor.white);
        qbtn.setPalette(p);
    }
}
