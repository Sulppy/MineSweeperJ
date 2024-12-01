import io.qt.widgets.*;

public class Main {

    public static void main(String[] args) {
        QApplication.initialize(args);
        QApplication.setApplicationName("MineSweeper");
        SceneManager sceneManager = new SceneManager();
        sceneManager.show();
        QApplication.exec();
        QApplication.shutdown();
    }
}