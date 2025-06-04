import apputil.GlobalDrawPaneConfig;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.util.TreeMap;

public class SubToolsPanel extends ToolBar {

    SubToolsPanel(){
        super();
        autosize();
        setOrientation(Orientation.HORIZONTAL);
        setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));
    }

}
