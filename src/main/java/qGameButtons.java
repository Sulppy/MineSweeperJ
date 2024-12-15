import io.qt.core.Qt;
import io.qt.gui.QMouseEvent;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QWidget;

import static io.qt.core.QLogging.qInfo;

public class qGameButtons extends QPushButton {

    public final Signal0 rclicked = new Signal0();

    qGameButtons(QWidget parent) {
        super.setParent(parent);
    }

    qGameButtons() {
        super.setParent(null);
    }

    @Override
    protected void mouseReleaseEvent(QMouseEvent event) {
        assert event != null;
        if (event.button() == Qt.MouseButton.RightButton && rect().contains(event.pos())) {
            rclicked.emit();
        }
        super.mouseReleaseEvent(event);
    }
}
