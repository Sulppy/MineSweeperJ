import io.qt.core.*;
import io.qt.gui.QFont;
import io.qt.gui.QResizeEvent;
import io.qt.statemachine.*;
import io.qt.widgets.*;

import static io.qt.core.QLogging.*;
import static io.qt.core.Qt.AlignmentFlag.*;
import static io.qt.core.Qt.WidgetAttribute.WA_DeleteOnClose;


public class SceneManager extends QMainWindow {

    public QWidget mainmenuWidget;
    QWidget gameboardWidget;

    QPushButton NewGame_btn;
    QPushButton Setting_btn;

    QLayout MainLayout;
    QVBoxLayout mainmenuLayout;
    QVBoxLayout gameboardLayout;

    QSize WindowSize;
    QSize MinWindowSize;

    //Машина состояний
    QStateMachine m_machine;
    QState startState; //Состояние запуска программы
    QState mainmenuState;
    QState inGameState;
    QState victoryState;
    QState defeatState;
    QState endGameState;
    QState setMainMenuS;


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
        if(gm == null)
            gm = new GameManager();
        m_machine = new QStateMachine();
        setMainMenuS = new QState(m_machine);
        mainmenuState = new QState(m_machine);
        inGameState = new QState(m_machine);
        defeatState = new QState(m_machine);
        victoryState = new QState(m_machine);
        endGameState = new QState(m_machine);

        //===========Раздел добавления сигналов для состояний================================
        setMainMenuS.addTransition(mainMenu, mainmenuState);
        mainmenuState.addTransition(startNewGame,inGameState);
        inGameState.addTransition(gm.gameLose, defeatState);
        inGameState.addTransition(gm.gameWon, victoryState);
        defeatState.addTransition(endGame, endGameState);
        victoryState.addTransition(endGame, endGameState);
        endGameState.addTransition(setmainMenu,setMainMenuS);
        //===================================================================================


        QStateMachine.connect(setMainMenuS.entered, () -> {
            setMainMenu();
            mainMenu.emit();
        });
        connect(mainmenuState.entered,()->{
            mainmenuWidget.show();
            MainWindow.resetCurrentSize(this); //Переустанавливаем размер на -1, затем на +1 по ширине, чтобы сработал resizeEvent
            qInfo("Resized buttons");
        });
        connect(inGameState.entered,() -> {
            qInfo("NewGame");
            mainmenuWidget.close();
            setGameBoard();
            qInfo("GameBoard created");
        });
        connect(defeatState.entered, () -> {
            qInfo("GameOver");
            QMessageBox.information(this, "Game Over", "You hit a mine! Game over.");
            inGameState.removeTransition(inGameState.transitions().at(0));
            inGameState.removeTransition(inGameState.transitions().at(0));
            gm.dispose();
            gm = new GameManager();
            inGameState.addTransition(gm.gameWon, victoryState);
            inGameState.addTransition(gm.gameLose, defeatState);
            endGame.emit();
        });
        connect(victoryState.entered,()->{
            qInfo("GameOver");
            QMessageBox.information(this, "Game Over", "You win!");
            endGame.emit();
        });
        connect(endGameState.entered, ()->{
            inGameState.removeTransition(inGameState.transitions().at(0));
            inGameState.removeTransition(inGameState.transitions().at(0));
            gm.dispose();
            gm = new GameManager();
            inGameState.addTransition(gm.gameWon, victoryState);
            inGameState.addTransition(gm.gameLose, defeatState);
            initMMWidget();
            gameboardWidget.close();
            setmainMenu.emit();
        });
        m_machine.setInitialState(setMainMenuS);
        m_machine.start();
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
    public final Signal0 endGame = new Signal0();


}
