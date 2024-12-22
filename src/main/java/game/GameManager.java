package game;

import io.qt.core.*;
import io.qt.gui.*;
import io.qt.widgets.*;

import java.util.Objects;
import java.util.Random;

import java.util.ArrayList;

import static io.qt.core.QLogging.*;

public class GameManager extends GameBoard {

    private ArrayList<gbutton> btn;
    QScrollArea scroll;
    private QLabel flagCounter;
    private boolean isEmitted;
    private QWidget widget;
    protected QWidget topPanel;
    private QGridLayout gbox;

    public final Signal1<Boolean> endGame = new Signal1<>();

    public void setWidget(QWidget widget) {
        this.widget =widget;
    }

    public void setBtn(ArrayList<gbutton> btn) {
        this.btn = btn;
    }

    //Установка базовых настроек доски
    public void createBoard(Difficult.difficulty dif) {
        switch (dif) {
            case easy:
                difficult = new Difficult(10, 9, 9);
                break;
            case medium:
                difficult = new Difficult(40, 16, 16);
                break;
            case hard:
                difficult = new Difficult(99, 25, 21);
                break;
            default:
                qCritical("Difficult is null!!");
                QApplication.closeAllWindows();
        }
        countFlags = difficult.n_mines;
        initWidgets();

        btn = new ArrayList<>();
        for(int i = 0; i < difficult.rows * difficult.cols; i++){
            btn.add(new gbutton());
        }
        connectButton();
        countFlags = difficult.n_mines;
    }

    public void continueBoard(GameManager game) {
        difficult = game.difficult;
        btn = game.btn;
        countFlags = game.countFlags;
        if(game.getFlagMouseButton() == Qt.MouseButton.RightButton){
            setFlagButtonR();
        } else {
            setFlagButtonL();
        }
        initWidgets();
        connectButton();
        setFilled();
    }

    private void connectButton() {
        for (int r = 0, c = 0, i = 0; i < difficult.rows * difficult.cols; i++) {
            gbox.addWidget(btn.get(i).qbtn, r, c++, 1, 1);
            if (c / difficult.cols == 1) {
                r++;
                c = 0;
            }
            btn.get(i).qbtn.clicked.connect(this::openCellEvent);
            btn.get(i).qbtn.rclicked.connect(this::FlagCell);
        }
    }

    private void initWidgets(){
        QWidget gboxWidget = new QWidget();
        int indent = 50;
        gboxWidget.setFixedSize(difficult.cols * 30 + indent, difficult.rows * 30 + indent);
        QRect rect =  Objects.requireNonNull(QApplication.activeWindow()).getRect();
        topPanel = new QWidget(widget);
        topPanel.setGeometry(0,0, rect.width(), 30);
        topPanel.setPalette(new QPalette(Qt.GlobalColor.lightGray));
        topPanel.setAutoFillBackground(true);
        flagCounter = new QLabel(topPanel);
        flagCounter.setNum(countFlags);
        flagCounter.setAlignment(Qt.AlignmentFlag.AlignAbsolute);
        flagCounter.setGeometry(topPanel.getGeometry().width()/2, topPanel.getGeometry().height()/4, topPanel.getGeometry().width(), topPanel.getGeometry().height());

        scroll = new QScrollArea(widget);
        scroll.sizePolicy().setHeightForWidth(true);
        scroll.setAlignment(Qt.AlignmentFlag.AlignCenter);
        scroll.setAutoFillBackground(true);
        scroll.setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);
        scroll.setVerticalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);
        scroll.setWidgetResizable(true);
        scroll.setWidget(gboxWidget);
        scroll.setFrameShape(QFrame.Shape.NoFrame);
        scroll.setAutoFillBackground(false);
        scroll.setGeometry(0,30,0,0);
        scroll.setFixedSize(rect.width(), rect.height()-30);
        scroll.setWidgetResizable(false);


        gbox = new QGridLayout(gboxWidget);
        gboxWidget.setAutoFillBackground(false);
        gbox.setAlignment(Qt.AlignmentFlag.AlignCenter);
        gbox.setSpacing(1);
    }

    private int[] findButton(QPushButton button) {
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
        return new int[]{row, col};
    }

    private void fillBoard(QPushButton button) {
        if (button == null) return;
        int[] position = findButton(button);
        for (int i = position[0] - 1; i <= position[0] + 1; i++)
            for (int j = position[1] - 1; j <= position[1] + 1; ++j) {
                try {
                    btn.get((i) * difficult.cols + j).num = 0;
                } catch (Exception ignored) {
                }
            }
        placeMines();
        calculateAdjacentMines();
        setFilled();
    }

    private void checkCell(int row, int col) {
        if (btn.get((row) * difficult.cols + col).isMine) {
            QPalette palette = new QPalette();
            palette.setColor(QPalette.ColorRole.Button, Qt.GlobalColor.white);
            palette.setColor(QPalette.ColorRole.ButtonText, Qt.GlobalColor.black);
            btn.get((row) * difficult.cols + col).qbtn.setPalette(palette);
            btn.get((row) * difficult.cols + col).qbtn.setText("*");
            setWin(false);
            setEnd();
        } else {
            if (countAdjacentMines(row, col) == 0) {
                QPalette p = new QPalette();
                p.setBrush(QPalette.ColorRole.Button, Qt.GlobalColor.darkGray);
                btn.get((row) * difficult.cols + col).qbtn.setPalette(p);
                btn.get((row) * difficult.cols + col).qbtn.setEnabled(false);
                revealAdjacentCells(row, col);
            } else {
                handleNumberButtonClick(row, col);
            }
            btn.get((row) * difficult.cols + col).setOpened(true);
            if (!isEnd())
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

    private void clickOnBoard(QPushButton button) {
        if (button == null) return;
        int[] position = findButton(button);
        if (btn.get((position[0]) * difficult.cols + position[1]).isFlagged)
            return;
        checkCell(position[0], position[1]);
        if (!isEnd())
            checkWinCondition();
    }


    //Считаем и выставляем число в клетку - количество мин вокруг клетки
    void handleNumberButtonClick(int row, int col) {
        if (countAdjacentFlags(row, col) == btn.get((row) * difficult.cols + col).num && !btn.get((row) * difficult.cols + col).qbtn.text().isEmpty()) {
            revealAdjacentCells(row, col);
        }
        if (!isEnd()) {
            btn.get((row) * difficult.cols + col).qbtn.setText(String.valueOf(QString.number(btn.get((row) * difficult.cols + col).num)));
            QPalette p = new QPalette();
            p.setColor(QPalette.ColorRole.Button, Qt.GlobalColor.gray);

            switch (btn.get(row * difficult.cols + col).num) {
                case 1:
                    p.setBrush(QPalette.ColorRole.ButtonText, Qt.GlobalColor.darkCyan);
                    break;
                case 2:
                    p.setBrush(QPalette.ColorRole.ButtonText, Qt.GlobalColor.green);
                    break;
                case 3:
                    p.setBrush(QPalette.ColorRole.ButtonText, new QColor(255, 153, 51)); //Orange
                    break;
                case 4:
                    p.setBrush(QPalette.ColorRole.ButtonText, Qt.GlobalColor.blue);
                    break;
                case 5:
                    p.setBrush(QPalette.ColorRole.ButtonText, Qt.GlobalColor.red);
                    break;
                case 6:
                    p.setBrush(QPalette.ColorRole.ButtonText, Qt.GlobalColor.cyan);
                    break;
                case 7:
                    p.setBrush(QPalette.ColorRole.ButtonText, new QColor(153, 76, 0));
                    break;
                case 8:
                    p.setBrush(QPalette.ColorRole.ButtonText, Qt.GlobalColor.black);
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

    public void setEmitted() {
        isEmitted = true;
    }

    public void setAllFlags() {
        for (gbutton gbutton : btn) {
            if (gbutton.isMine && !gbutton.isFlagged) {
                gbutton.setFlag();
                countFlags--;
                flagCounter.setNum(countFlags);
            }
        }
    }

    private void openCellEvent() {
        if (sender() == null) return;
        QPushButton button = (QPushButton) sender();
        if (!isBoardFilled())
            fillBoard(button);
        clickOnBoard(button);
        if (isWinCondition() && !isEmitted && isEnd()) {
            setAllFlags();
            setEmitted();
            endGame.emit(isWinCondition());
        } else if (!isEmitted && isEnd()) {
            setEmitted();
            endGame.emit(isWinCondition());
        }
    }

    private void FlagCell() {
        if (sender() == null) return;
        QPushButton button = (QPushButton) sender();
        assert button != null;
        if (!button.text().isEmpty() && !button.text().equals("F") || !button.isEnabled())
            return;
        int[] position = findButton(button);
        if (btn.get((position[0]) * difficult.cols + position[1]).isFlagged) {
            btn.get((position[0]) * difficult.cols + position[1]).unSetFlag();
            button.setText("");
            countFlags++;
            flagCounter.setNum(countFlags);
        } else if (countFlags > 0) {
            btn.get((position[0]) * difficult.cols + position[1]).setFlag();
            countFlags--;
            flagCounter.setNum(countFlags);
        }
    }

    public ArrayList<gbutton> getButtons() {
        return btn;
    }

    public void resize() {
        topPanel.setGeometry(0,0, widget.width(), 40);
        flagCounter.setGeometry(widget.width()/2,flagCounter.geometry().y(), widget.width(), flagCounter.geometry().height());
        scroll.setFixedSize(widget.width(),widget.height()-50);
    }
}
