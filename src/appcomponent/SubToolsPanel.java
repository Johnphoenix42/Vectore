package appcomponent;

import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class SubToolsPanel extends ToolBar {

    public SubToolsPanel(){
        super();
        autosize();
        setOrientation(Orientation.HORIZONTAL);
        setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));
    }

}
