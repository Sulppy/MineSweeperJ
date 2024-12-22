import game.Difficult;
import game.GameManager;
import game.gbutton;
import game.qGameButtons;
import io.qt.core.*;
import io.qt.gui.QColor;
import io.qt.gui.QPalette;


import java.io.*;
import java.util.ArrayList;

import static io.qt.core.QLogging.qInfo;

public class GameSave {
    private static final String filePath = "./saves/game.bin";

    static private Difficult difficult;
    static private int countFlags;
    static private ArrayList<gbutton> btn;
    static private Qt.MouseButton flagMB;

    // Метод для сохранения прогресса игры в файл
    public static void saveGame(GameManager gameManager) {
        if(!gameManager.isBoardFilled()) return;
        if(checkFile()) {
            deleteFile();
        }
        difficult = gameManager.difficult;
        countFlags = gameManager.countFlags;
        btn = gameManager.getButtons();
        flagMB = gameManager.getFlagMouseButton();
        QFile file = new QFile(filePath);
        QDataStream dataStream = new QDataStream(file);
        file.open(QIODeviceBase.OpenModeFlag.WriteOnly);
        dataStream.writeObject(difficult);
        dataStream.writeInt(countFlags);
        dataStream.writeInt(btn.size());
        for(gbutton b : btn) {
            dataStream.writeObject(b);
        }
        dataStream.writeObject(flagMB);
        file.close();
        qInfo("Game Saved");
    }

    // Метод для загрузки прогресса игры из файла
    public static GameManager loadGame() {
        QFile file = new QFile(filePath);
        QDataStream dataStream = new QDataStream(file);
        file.open(QIODeviceBase.OpenModeFlag.ReadOnly);
        difficult = dataStream.readObject(Difficult.class);
        countFlags = dataStream.readInt();
        int size = dataStream.readInt();
        btn = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            btn.add(dataStream.readObject(gbutton.class));
        }
        flagMB = dataStream.readObject(Qt.MouseButton.class);
        file.close();
        GameManager gameManager = new GameManager();
        gameManager.difficult = difficult;
        gameManager.countFlags = countFlags;
        resetButtons();
        gameManager.setBtn(btn);
        if (flagMB == Qt.MouseButton.LeftButton) {
            gameManager.setFlagButtonL();
        }
        else if (flagMB == Qt.MouseButton.RightButton) {
            gameManager.setFlagButtonR();
        }
        return gameManager;
    }

    private static void resetButtons() {
            for (gbutton b : btn) {
                b.qbtn = new qGameButtons();
                b.qbtn.setFixedSize(30, 30);
                if(b.isOpened()){

                    if (b.num == 0){
                        QPalette p = new QPalette();
                        p.setBrush(QPalette.ColorRole.Button, Qt.GlobalColor.darkGray);
                        b.qbtn.setPalette(p);
                        b.qbtn.setEnabled(false);
                    } else{
                        b.qbtn.setText(String.valueOf(QString.number(b.num)));
                        QPalette p = new QPalette();
                        p.setColor(QPalette.ColorRole.Button, Qt.GlobalColor.gray);
                        switch (b.num) {
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
                        b.qbtn.setPalette(p);
                    }
                }
                else if(b.isFlagged){
                    b.setFlag();
                }
                else {
                    b.unSetFlag();
                }
            }
    }

    public static boolean checkFile(){
        File file = new File(filePath);
        return file.exists();
    }

    public static boolean deleteFile() {
        File file = new File(filePath);
        return file.delete();
    }
}
