package Game;

import io.qt.QtMetaType;
import io.qt.core.Qt;
import io.qt.gui.QMouseEvent;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QWidget;

import javax.sql.rowset.serial.SerialArray;
import java.io.Serializable;

import static io.qt.core.QLogging.qInfo;

public class qGameButtons extends QPushButton implements Serializable {

    public final transient Signal0 rclicked;

    qGameButtons(QWidget parent) {
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
