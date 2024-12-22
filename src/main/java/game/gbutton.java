package game;

import io.qt.core.Qt;
import io.qt.gui.QPalette;
import java.io.Serializable;

public class gbutton implements Serializable {
    public int num;
    public boolean isMine;
    public boolean isFlagged;
    private boolean isOpened = false;

    public transient qGameButtons qbtn;

    public gbutton() {
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

    public void setFlag() {
        qbtn.setText("F");
        isFlagged = true;
        QPalette p = new QPalette();
        p.setBrush(QPalette.ColorRole.Button, Qt.GlobalColor.darkRed);
        qbtn.setPalette(p);
    }

    public void unSetFlag() {
        QPalette p = new QPalette();
        p.setBrush(QPalette.ColorRole.Button, Qt.GlobalColor.white);
        qbtn.setPalette(p);
        isFlagged = false;
    }


    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }
}
