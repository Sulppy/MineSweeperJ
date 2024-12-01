import io.qt.core.QSize;
import io.qt.core.Qt;
import io.qt.gui.QBrush;
import io.qt.gui.QGradient;
import io.qt.gui.QIcon;
import io.qt.gui.QPalette;
import io.qt.widgets.QWidget;

import static io.qt.core.Qt.*;
import static io.qt.gui.QPalette.*;
import static io.qt.gui.QGradient.Preset.*;
import static io.qt.gui.QPalette.ColorGroup.*;

public class MainWindow extends QWidget {
    static void setMainSettings(QWidget mainwindow) {
        QSize MinWindowSize = new QSize(300,200);
        mainwindow.setWindowFlags(WindowType.Window);
        mainwindow.setMinimumSize(MinWindowSize);
        setDefaultSize(mainwindow);
        mainwindow.setWindowTitle("Ну привет, сапёр");
        QIcon icon = new QIcon("icon.png");
        mainwindow.setWindowIcon(new QIcon("Minesweeper_icon.png"));
        QPalette p = new QPalette();
        QGradient gradient = QGradient.create(MeanFruit);
        QBrush brush = new QBrush(gradient);
        p.setBrush(All, ColorRole.Window, brush);
        mainwindow.setPalette(p);
        mainwindow.setAutoFillBackground(true);
    }

    static void setDefaultSize(QWidget mainwindow) {
        setSize(600,400,mainwindow);
    }

    static void setSize(int w, int h, QWidget mainwindow) {
        QSize WindowSize = new QSize(w,h);
        mainwindow.resize(WindowSize);
    }

    static void resetCurrentSize(QWidget mainwindow) {
        QSize WindowSize = mainwindow.size();
        setSize(WindowSize.width()+1, WindowSize.height(), mainwindow);
        setSize(WindowSize.width()-1, WindowSize.height(), mainwindow);
    }
}
