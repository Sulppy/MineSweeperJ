import io.qt.core.*;
import io.qt.gui.QFont;
import io.qt.gui.QResizeEvent;
import io.qt.widgets.*;

import static io.qt.core.QLogging.*;
import static io.qt.core.Qt.AlignmentFlag.*;
import static io.qt.core.Qt.WidgetAttribute.WA_DeleteOnClose;


public class SceneManager extends QMainWindow {

    public QWidget mainmenuWidget;
    QWidget gameboardWidget;

    QPushButton NewGame_btn;
    QPushButton Setting_btn;

    QVBoxLayout mainmenuLayout;
    QVBoxLayout gameboardLayout;

    //Машина состояний
//    QStateMachine m_machine;
//    QState mainmenuState;
//    QState inGameState;
//    QState victoryState;
//    QState defeatState;
//    QState endGameState;
//    QState setMainMenuS;


    GameManager gm;

    public SceneManager() {
        super((QWidget) null);
        initMMWidget();
        setupStateMachine();
        MainWindow.setMainSettings(this);
    }

    void setMainMenu() {
        NewGame_btn = new QPushButton("New Game", mainmenuWidget);
        Setting_btn = new QPushButton("Settings", mainmenuWidget);
        mainmenuLayout.addWidget(NewGame_btn, 0, AlignCenter);
        mainmenuLayout.addWidget(Setting_btn, 0, AlignCenter);
        mainmenuLayout.setSpacing(20);
        mainmenuLayout.setAlignment(AlignCenter);
        QFont Font = NewGame_btn.font();
        Font.setPointSizeF(12);
        NewGame_btn.setFont(Font);
        connect(NewGame_btn.clicked, startNewGame);
        //connect(Setting_btn.clicked, btn_Settings());
        this.setCentralWidget(mainmenuWidget);
    }

    public void setupStateMachine() {
         connect(setmainMenu, () ->{
            setMainMenu();
            mainMenu.emit();
        });
        connect(mainMenu, () ->{
            mainmenuWidget.show();
            MainWindow.resetCurrentSize(this); //Переустанавливаем размер на -1, затем на +1 по ширине, чтобы сработал resizeEvent
            qInfo("Resized buttons");
        });
        connect(startNewGame, () ->{
            qInfo("NewGame\nInitialisation GameBoard...");
            gm = new GameManager();
            gm.endGame.connect(this::onEndGame);
            mainmenuWidget.close();
            setGameBoard();
            qInfo("GameBoard created");
        });
        setmainMenu.emit();
        qInfo("State Machine was started");
    }

    void initMMWidget() //Инициализация нового mainmenuWidget
    {
        mainmenuWidget = new QWidget(this);
        mainmenuLayout = new QVBoxLayout(mainmenuWidget);
        mainmenuWidget.setAttribute(WA_DeleteOnClose); //Аттрибут, чтобы виджет удалился при закрытии ( mainmenuWidget.
    }

    void initGBWidget() //Инициализация нового gameboardWidget
    {
        gameboardWidget = new QWidget(this);
        gameboardLayout = new QVBoxLayout(gameboardWidget);
        gameboardWidget.setAttribute(WA_DeleteOnClose);
    }

    void setGameBoard() {
        initGBWidget();
        gm.createBoard(gameboardLayout, Difficult.difficulty.easy);
        setCentralWidget(gameboardWidget);
    }

    @Override
    public void resizeEvent(QResizeEvent e) {
        //qInfo() << "Resize Event";
        if(this.centralWidget() == mainmenuWidget) {
            QSize Size= new QSize(Math.max(mainmenuWidget.width() / 2, 200),
                    Math.max(mainmenuWidget.height() / 13, 40));
            NewGame_btn.setFixedSize(Size);
            Setting_btn.setFixedSize(Size);
            QFont Font = NewGame_btn.font();
            Font.setPointSizeF(NewGame_btn.height() / 2.5 > 12 ? NewGame_btn.height() / 2.5 : 12);
            NewGame_btn.setFont(Font);
            Setting_btn.setFont(Font);
        }
    }

//Основные сигналы для смены состояний приложения
    public final Signal0 setmainMenu = new Signal0();
    public final Signal0 mainMenu = new Signal0();
    public final Signal0 startNewGame = new Signal0();

//Слоты
   private void onEndGame(boolean win){
       qInfo("GameOver");
       if(win){
           QMessageBox.information(this, "Game Over", "You win!");
       }
       else{
           QMessageBox.information(this, "Game Over", "You hit a mine! Game over.");
       }

       gm.dispose();
       initMMWidget();
       gameboardWidget.close();
       setmainMenu.emit();
   }

}
