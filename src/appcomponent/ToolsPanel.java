package appcomponent;

import appcustomcontrol.*;
import apputil.AppLogger;
import apputil.GlobalDrawPaneConfig;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class ToolsPanel extends ToolBar {

    private final GlobalDrawPaneConfig config;
    private final SubToolsPanel toolOptionsPanel;

    public ToolsPanel(GlobalDrawPaneConfig config, SubToolsPanel toolOptionsPanel){
        super();
        this.toolOptionsPanel = toolOptionsPanel;
        this.config = config;
        autosize();
        addButtons();
        setOrientation(Orientation.VERTICAL);
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
    }

    private void addButtons(){
        ObservableList<Node> items = toolOptionsPanel.getItems();
        RectangleButtonTool rectangleButton = new RectangleButtonTool(config) {
            @Override
            protected void setCurrentToolbarOptions() {
                getOptions().switchToolOptions(items, getId());
            }
        };
        rectangleButton.setAsCurrentlySelectedTool();
        DrawableButtonTool.OptionButtonsBuilder optionButtonsBuilder = rectangleButton.getOptions();
        items.addAll(optionButtonsBuilder.getNodes("static"));
        items.addAll(optionButtonsBuilder.getNodes(rectangleButton.getId()));

        DrawableButtonTool circleButton = new CircleButtonTool(config) {
            @Override
            protected void setCurrentToolbarOptions() {
                getOptions().switchToolOptions(items, getId());
            }
        };

        PathButtonTool pathButton = new PathButtonTool(config) {
            @Override
            protected void setCurrentToolbarOptions() {
                getOptions().switchToolOptions(items, getId());
            }
        };

        DrawableButtonTool textButton = new TextButtonTool(config) {
            @Override
            protected void setCurrentToolbarOptions() {
                getOptions().switchToolOptions(items, getId());
            }
        };

        getItems().addAll(rectangleButton, circleButton, pathButton, textButton);

        AppLogger.log(getClass(), 61, config.getCurrentTool().getClass().getName());
    }

}
