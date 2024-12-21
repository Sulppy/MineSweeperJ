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

    private void setChooseDifficultWidget(){
        initCDWidget();
        difficultyButtons = new ArrayList<>();
        for(Difficult.difficulty dif : Difficult.difficulty.values()){
            difficultyButtons.add(new QPushButton(dif.getName(),difficultyWidget));
            connect(difficultyButtons.getLast().clicked, ()->{
                startNewGame.emit(dif);
            });
            difficultyLayout.addWidget(difficultyButtons.getLast(), 0, AlignCenter);
        }
        difficultyLayout.setSpacing(20);
        difficultyLayout.setAlignment(AlignCenter);
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
            setChooseDifficultWidget();
            difficultyWidget.show();
            MainWindow.resetCurrentSize(this); //Переустанавливаем размер на -1, затем на +1 по ширине, чтобы сработал resizeEvent
            qInfo("Resized buttons");
        });
        connect(startNewGame, (Difficult.difficulty dif) ->{
            qInfo("NewGame\nInitialisation GameBoard...");
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
        gm = new GameManager(gameboardWidget);
        gm.endGame.connect(this::onEndGame);
        gm.createBoard(dif);
        setCentralWidget(gameboardWidget);
    }

    @Override
    public void resizeEvent(QResizeEvent e) {
        if(this.centralWidget() == mainmenuWidget) {
            QSize Size= new QSize(Math.min(Math.max(mainmenuWidget.width() / 2, 200),600),
                    Math.min(Math.max(mainmenuWidget.height() / 13, 40), 80));
            NewGame_btn.setFixedSize(Size);
            QFont Font = NewGame_btn.font();
            Font.setPointSizeF(NewGame_btn.height() / 2.5 > 12 ? NewGame_btn.height() / 2.5 : 12);
            NewGame_btn.setFont(Font);
        }
        else if(this.centralWidget() == difficultyWidget) {
            QSize Size= new QSize(Math.min(Math.max(difficultyWidget.width() / 2, 150),500),
                    Math.min(Math.max(difficultyWidget.height() / 13, 40),60));

            for(QPushButton button : difficultyButtons) {
                button.setFixedSize(Size);
                QFont Font = button.font();
                Font.setPointSizeF(button.height() / 2.5 > 12 ? button.height() / 2.5 : 12);
                button.setFont(Font);
            }
        }
        else if(this.centralWidget() == gameboardWidget) {
            gm.resize();
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
