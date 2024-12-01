import io.qt.Nullable;
import io.qt.core.*;
import io.qt.gui.QFont;
import io.qt.gui.QResizeEvent;
import io.qt.statemachine.*;
import io.qt.widgets.*;

import static io.qt.core.QLogging.*;
import static io.qt.core.Qt.AlignmentFlag.*;
import static io.qt.QtObject.*;


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
        super((QPrivateConstructor) null);
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
        connect(NewGame_btn, "QPushButton::clicked", this, "startNewGame");
        connect(Setting_btn, "QPushButton::clicked", this, "btn_Settings()");
        this.setCentralWidget(mainmenuWidget);
    }

    public void setupStateMachine() {
        if(gm == null)
            gm = new GameManager();
        m_machine = new QStateMachine();
        startState = new QState(m_machine);
        setMainMenuS = new QState(m_machine);
        mainmenuState = new QState(m_machine);
        inGameState = new QState(m_machine);
        defeatState = new QState(m_machine);
        victoryState = new QState(m_machine);
        endGameState = new QState(m_machine);

        //===========Раздел добавления сигналов для состояний================================
        startState.addTransition(this,"setmainMenu", setMainMenuS);
        setMainMenuS.addTransition(this,"mainMenu", mainmenuState);
        mainmenuState.addTransition(this,"startNewGame",inGameState);
        inGameState.addTransition(gm, "GameManager::gameLose", defeatState);
        inGameState.addTransition(gm, "GameManager::gameWon", victoryState);
        defeatState.addTransition(this,"endGame", endGameState);
        victoryState.addTransition(this,"endGame", endGameState);
        endGameState.addTransition(this,"setmainMenu",setMainMenuS);
        //===================================================================================


        connect(startState, "QState::entered", () -> {
            setmainMenu.emit();
        });
        connect(setMainMenuS, "QState::entered",()->{
            setMainMenu();
            mainMenu.emit();
        });
        connect(mainmenuState, "QState::entered",()->{
            mainmenuWidget.show();
            MainWindow.resetCurrentSize(this); //Переустанавливаем размер на -1, затем на +1 по ширине, чтобы сработал resizeEvent
            qInfo("Resized buttons");
        });
        connect(inGameState,"QState::entered",() -> {
            qInfo("NewGame");
            mainmenuWidget.close();
            setGameBoard();
            qInfo("GameBoard created");
        });
        connect(defeatState, "QState::entered", () -> {
            qInfo("GameOver");
            QMessageBox.information(this, "Game Over", "You hit a mine! Game over.");
            inGameState.removeTransition(inGameState.transitions().at(0));
            inGameState.removeTransition(inGameState.transitions().at(0));
            gm.dispose();
            gm = new GameManager();
            inGameState.addTransition(gm, "GameManager::gameWon", victoryState);
            inGameState.addTransition(gm, "GameManager::gameLose", defeatState);
            endGame.emit();
        });
        connect(victoryState, "QState::entered", ()->{
            qInfo("GameOver");
            QMessageBox.information(this, "Game Over", "You win!");
            endGame.emit();
        });
        connect(endGameState, "QState::entered", ()->{
            inGameState.removeTransition(inGameState.transitions().at(0));
            inGameState.removeTransition(inGameState.transitions().at(0));
            gm.dispose();
            gm = new GameManager();
            inGameState.addTransition(gm, "GameManager::gameWon", victoryState);
            inGameState.addTransition(gm, "GameManager::gameLose", defeatState);
            initMMWidget();
            gameboardWidget.close();
            setmainMenu.emit();
        });
        m_machine.setInitialState(startState);
        m_machine.start();
        qInfo("State Machine was started");
    }

    void initMMWidget(); //Инициализация нового mainmenuWidget

    void initGBWidget(); //Инициализация нового gameboardWidget

    void setGameBoard();

    @Override
    public void resizeEvent(QResizeEvent e) {

    }

//Основные сигналы для смены состояний приложения
    public final Signal0 setmainMenu = new Signal0();
    public final Signal0 mainMenu = new Signal0();
    public final Signal0 startNewGame = new Signal0();
    public final Signal0 endGame = new Signal0();


}
