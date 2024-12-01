import io.qt.widgets.*;

public class Main {

    public static void main(String[] args) {
        QApplication.initialize(args);
        QApplication.setApplicationName("MineSweeper");
        //QMessageBox.information(null, "QtJambi", "Hello World!");
        QApplication.exec();
        QApplication.shutdown();
    }
}