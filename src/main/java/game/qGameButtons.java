package game;

import io.qt.core.Qt;
import io.qt.gui.QMouseEvent;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QWidget;

public class qGameButtons extends QPushButton {

    public final  Signal0 rclicked;

    public qGameButtons(QWidget parent) {
        rclicked = new Signal0();
        super.setParent(parent);
    }

    public qGameButtons() {
        rclicked = new Signal0();
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
