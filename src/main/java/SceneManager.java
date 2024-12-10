import io.qt.core.*;
import io.qt.gui.QFont;
import io.qt.gui.QResizeEvent;
import io.qt.widgets.*;

import java.util.ArrayList;

import static io.qt.core.QLogging.*;
import static io.qt.core.Qt.AlignmentFlag.*;
import static io.qt.core.Qt.WidgetAttribute.WA_DeleteOnClose;


public class SceneManager extends QMainWindow {

    private QWidget mainmenuWidget;
    private QWidget gameboardWidget;
    private QWidget difficultyWidget;

    QPushButton NewGame_btn;


    ArrayList<QPushButton> difficultyButtons;

    QVBoxLayout mainmenuLayout;
    QVBoxLayout gameboardLayout;
    QVBoxLayout difficultyLayout;

    GameManager gm;

    public SceneManager() {
        super((QWidget) null);
        initMMWidget();
        setupStateMachine();
        MainWindow.setMainSettings(this);
    }

    private void setChooseDifficult(){
        initCDWidget();
        for(Difficult.difficulty dif : Difficult.difficulty.values()){
            difficultyButtons.add(new QPushButton(dif.getName(),difficultyWidget));
            connect(difficultyButtons.getLast().clicked, ()->{
                startNewGame.emit(dif);
            });
        }
        this.setCentralWidget(difficultyWidget);
    }

    private void setMainMenu() {
        NewGame_btn = new QPushButton("New Game", mainmenuWidget);
        mainmenuLayout.addWidget(NewGame_btn, 0, AlignCenter);
        mainmenuLayout.setSpacing(20);
        mainmenuLayout.setAlignment(AlignCenter);
        QFont Font = NewGame_btn.font();
        Font.setPointSizeF(12);
        NewGame_btn.setFont(Font);
        //connect(NewGame_btn.clicked, startNewGame);
        connect(NewGame_btn.clicked, chooseDifficult);
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
        connect(chooseDifficult, () -> {
            qInfo("Choose difficulty");
            mainmenuWidget.close();
            setChooseDifficult();
            difficultyWidget.show();

        });
        connect(startNewGame, (Difficult.difficulty dif) ->{
            qInfo("NewGame\nInitialisation GameBoard...");
            gm = new GameManager();
            gm.endGame.connect(this::onEndGame);
            difficultyWidget.close();
            setGameBoard(dif);
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


    private void initCDWidget() {
        difficultyWidget = new QWidget(this);
        difficultyLayout = new QVBoxLayout(difficultyWidget);
        difficultyWidget.setAttribute(WA_DeleteOnClose);
    }

    void setGameBoard(Difficult.difficulty dif) {
        initGBWidget();
        gm.createBoard(gameboardLayout, dif);
        setCentralWidget(gameboardWidget);
    }

    @Override
    public void resizeEvent(QResizeEvent e) {
        if(this.centralWidget() == mainmenuWidget) {
            QSize Size= new QSize(Math.max(mainmenuWidget.width() / 2, 200),
                    Math.max(mainmenuWidget.height() / 13, 40));
            NewGame_btn.setFixedSize(Size);
            QFont Font = NewGame_btn.font();
            Font.setPointSizeF(NewGame_btn.height() / 2.5 > 12 ? NewGame_btn.height() / 2.5 : 12);
            NewGame_btn.setFont(Font);
        }
    }

//Основные сигналы для смены состояний приложения
    public final Signal0 setmainMenu = new Signal0();
    public final Signal0 mainMenu = new Signal0();
    public final Signal0 chooseDifficult = new Signal0();
    public final Signal1<Difficult.difficulty> startNewGame = new Signal1<>();

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
