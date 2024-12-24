package game;

import io.qt.core.Qt;
import io.qt.gui.QPalette;
import java.io.Serializable;

public class gbutton implements Serializable {
    private int num;
    private boolean mine;
    private boolean flagged;
    private boolean opened = false;

    public transient qGameButtons qbtn;

    public gbutton() {
        flagged = false;
        mine = false;
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
        flagged = true;
        QPalette p = new QPalette();
        p.setBrush(QPalette.ColorRole.Button, Qt.GlobalColor.darkRed);
        qbtn.setPalette(p);
    }

    public void unSetFlag() {
        QPalette p = new QPalette();
        p.setBrush(QPalette.ColorRole.Button, Qt.GlobalColor.white);
        qbtn.setPalette(p);
        flagged = false;
    }


    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }
    
    public boolean isFlagged() {
        return flagged;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
