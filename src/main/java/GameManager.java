import io.qt.core.*;
import io.qt.gui.QColor;
import io.qt.gui.QMouseEvent;
import io.qt.gui.QPalette;
import io.qt.widgets.*;

import java.util.Random;

import java.util.ArrayList;

import static io.qt.core.Qt.AlignmentFlag.AlignCenter;
import static io.qt.core.Qt.GlobalColor.*;
import static io.qt.core.Qt.MouseButton.*;
import static io.qt.gui.QPalette.ColorRole.*;

class GameManager extends GameBoard {

    private ArrayList<gbutton> btn;
    private QLabel flagCounter;
    private boolean isEmited;



    public final Signal0 gameLose = new Signal0();
    public final Signal0 gameWon = new Signal0();

    private void fillBoard(QPushButton button) {
        if (button == null) return;
        int row, col = 0;
        boolean found = false;
        for (row = 0; row < difficult.rows; row++) {
            for (col = 0; col < difficult.cols; col++) {
                if (btn.get((row) * difficult.cols + col).qbtn == button) {
                    found = true;
                    break;
                }
            }
            if (found) break;
        }
        for(int i = row-1; i <=row+1;i++)
            for (int j = col-1; j <= col+1; ++j) {
                btn.get((i) * difficult.cols + j).num = 0;
            }
        placeMines();
        calculateAdjacentMines();
        setFilled();

    }

    private void checkCell(int row, int col) {
        if (btn.get((row) * difficult.cols + col).isMine) {
            btn.get((row) * difficult.cols + col).qbtn.setText("*");
            setWin(false);
            setEnd();
        } else {
            if (!btn.get((row) * difficult.cols + col).isMine) {
                if (countAdjacentMines(row, col) == 0) {
                    QPalette p = new QPalette();
                    p.setBrush(Button, gray);
                    btn.get((row) * difficult.cols + col).qbtn.setPalette(p);
                    btn.get((row) * difficult.cols + col).qbtn.setEnabled(false);
                    revealAdjacentCells(row, col);
                }
                else {
                    handleNumberButtonClick(row, col);
                }

            }
            if(!isEnd())
                checkWinCondition();
        }
    }

    void placeMines() {
        int placedMines = 0;
        while (placedMines < difficult.n_mines) {
            int row = new Random().nextInt(difficult.rows);
            int col = new Random().nextInt(difficult.cols);
            if (!btn.get((row) * difficult.cols + col).isMine && btn.get((row) * difficult.cols + col).num < 0) {
                btn.get((row) * difficult.cols + col).isMine = true;
                ++placedMines;
            }
        }
    }

    void calculateAdjacentMines() {
        for (int row = 0; row < difficult.rows; ++row) {
            for (int col = 0; col < difficult.cols; ++col) {
                if (btn.get((row) * difficult.cols + col).isMine) continue;
                btn.get((row) * difficult.cols + col).num = countAdjacentMines(row, col);
            }
        }
    }

    int countAdjacentMines(int row, int col) {
        int mineCount = 0;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < difficult.rows && newCol >= 0 && newCol < difficult.cols &&
                        btn.get((newRow) * difficult.cols + newCol).isMine) {
                    ++mineCount;
                }
            }
        }
        return mineCount;
    }

    int countAdjacentFlags(int row, int col) {
        int flagCount = 0;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < difficult.rows && newCol >= 0 && newCol < difficult.cols && btn.get((newRow) * difficult.cols + newCol).isFlagged) {
                    ++flagCount;
                }
            }
        }
        return flagCount;
    }

    //проверяем соседние клетки
    void revealAdjacentCells(int row, int col) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (isEnd())
                    break;
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < difficult.rows && newCol >= 0 && newCol < difficult.cols && btn.get((newRow) * difficult.cols + newCol).qbtn.isEnabled() && !btn.get((newRow) * difficult.cols + newCol).isFlagged && btn.get((newRow) * difficult.cols + newCol).qbtn.text().isEmpty()) {
                    clickOnBoard(btn.get((newRow) * difficult.cols + newCol).qbtn);
                }
            }
            if (isEnd())
                break;
        }
    }


    //Считаем и выставляем число в клетку - количество мин вокруг клетки
    void handleNumberButtonClick(int row, int col) {
        if (countAdjacentFlags(row, col) == btn.get((row) * difficult.cols + col).num && !btn.get((row) * difficult.cols + col).qbtn.text().isEmpty()) {
            revealAdjacentCells(row, col);
        }
        if(!isEnd()) {
            btn.get((row) * difficult.cols + col).qbtn.setText(String.valueOf(QString.number(btn.get((row) * difficult.cols + col).num)));
            QPalette p = new QPalette();


            switch (btn.get(row * difficult.cols + col).num) {
                case 1:
                    p.setBrush(Button, darkCyan);
                    break;
                case 2:
                    p.setBrush(Button, green);
                    break;
                case 3:
                    p.setBrush(Button, new QColor(255,153,51,127)); //Orange
                    break;
                case 4:
                    p.setBrush(Button, blue);
                    break;
                case 5:
                    p.setBrush(Button, red);
                    break;
                case 6:
                    p.setBrush(Button, cyan);
                    break;
                case 7:
                    p.setBrush(Button, new QColor(153,76,0));
                    break;
                case 8:
                    p.setBrush(Button, black);
                    break;
            }
            btn.get((row) * difficult.cols + col).qbtn.setPalette(p);
        }
    }

    void checkWinCondition() {
        for (int row = 0; row < difficult.rows; ++row) {
            for (int col = 0; col < difficult.cols; ++col) {
                if (!btn.get((row) * difficult.cols + col).isMine && btn.get((row) * difficult.cols + col).qbtn.isEnabled() && btn.get((row) * difficult.cols + col).qbtn.text().isEmpty()) {
                    return;
                }
            }
        }
        setWin();
        setEnd();
    }

    public void setEmited() {
        isEmited = true;
    }

    //explicit GameManager(QWidget *parent = nullptr);
    public void createBoard(QVBoxLayout vbox, Difficult.difficulty dif) {
        switch (dif) {
            case easy:
                difficult = new Difficult(10, 9, 9);
                break;
            case medium:
                difficult = new Difficult(40, 16, 16);
                break;
            case hard:
                difficult =new Difficult(99, 30, 16);
                break;
        }
        countFlags = difficult.n_mines;
        flagCounter = new QLabel();
        countFlags = difficult.n_mines;
        flagCounter.setNum(countFlags);
        QWidget gbwidget = new QWidget();
        QGridLayout gbox = new QGridLayout(gbwidget);
        vbox.addWidget(flagCounter, 0,AlignCenter);
        vbox.addWidget(gbwidget);
        btn = new ArrayList<>();
        for (int r = 0, c = 0, i = 0; i < difficult.rows * difficult.cols; i++) {
            btn.add(new gbutton());
            btn.get(i).qbtn = new QPushButton();
            gbox.addWidget(btn.get(i).qbtn, r, c++, 1, 1);
            if (c / difficult.cols == 1) {
                r++;
                c = 0;
            }
            btn.get(i).isFlagged = false;
            btn.get(i).isMine = false;
            btn.get(i).num = -1;
            btn.get(i).qbtn.setFixedSize(30, 30);
            btn.get(i).qbtn.setText("");
            QPalette p = new QPalette();
            p.setBrush(Button, white);
            btn.get(i).qbtn.setPalette(p);
            connect(btn.get(i).qbtn, "clicked()", this,"clickOnBoard()");
            btn.get(i).qbtn.installEventFilter(this);
//        connect(&btn[i], SIGNAL(pressed()), this, SLOT(eventFilter()));
        }
        countFlags = difficult.n_mines;
        gbox.setAlignment(AlignCenter);
        gbox.setSpacing(1);
    }


    @Override
    public boolean eventFilter(QObject obj, QEvent event) {
        if(event.type() == QEvent.Type.MouseButtonPress) {
            QMouseEvent pMouseEvent = (QMouseEvent) event;
            if (pMouseEvent.button() == RightButton) {
                QPushButton button = (QPushButton) obj;
                if(!button.text().isEmpty() && !button.text().equals("F") || !button.isEnabled())
                    return super.eventFilter(obj,event);
                int row, col = 0;
                boolean found = false;
                for (row = 0; row < difficult.rows; ++row) {
                    for (col = 0; col < difficult.cols; ++col) {
                        if (btn.get((row) * difficult.cols + col).qbtn == button) {
                            found = true;
                            break;
                        }
                    }
                    if (found) break;
                }
                if (btn.get((row) * difficult.cols + col).isFlagged) {
                    button.setText("");
                    countFlags++;
                    flagCounter.setNum(countFlags);
                    QPalette p = new QPalette();
                    p.setBrush(Button, white);
                    btn.get((row) * difficult.cols + col).qbtn.setPalette(p);
                    btn.get((row) * difficult.cols + col).isFlagged = false;
                    connect(button, "clicked()", this, "clickOnBoard()");
                } else {
                    button.setText("F");
                    countFlags--;
                    flagCounter.setNum(countFlags);
                    btn.get((row) * difficult.cols + col).isFlagged = true;
                    QPalette p = new QPalette();
                    p.setBrush(Button, darkRed);
                    btn.get((row) * difficult.cols + col).qbtn.setPalette(p);
                    disconnect(button, "clicked()", this, "clickOnBoard()");
                }
            }
        }
        return super.eventFilter(obj,event);
    }

    void clickOnBoard(){
        QPushButton button = (QPushButton) sender();
        if (!isBoardFilled())
            fillBoard(button);
        clickOnBoard(button);
    }

    private void clickOnBoard(QPushButton button){
        if (button == null) return;
        int row, col = 0;
        boolean found = false;
        for (row = 0; row < difficult.rows; row++) {
            for (col = 0; col < difficult.cols; col++) {
                if (btn.get((row) * difficult.cols + col).qbtn == button) {
                    found = true;
                    break;
                }
            }
            if (found) break;
        }
        if(btn.get((row) * difficult.cols + col).isFlagged)
            return;
        checkCell(row, col);
        if(!isEnd())
            checkWinCondition();
        else if (isWinCondition() && !isEmited) {
            setEmited();
            gameWon.emit();
        }
        else if (!isEmited) {
            setEmited();
            gameLose.emit();
        }
    }
}
