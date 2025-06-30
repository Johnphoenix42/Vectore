package appcomponent;

import appcustomcontrol.*;
import apputil.AppLogger;
import apputil.GlobalDrawPaneConfig;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class ToolsPanel extends ToolBar {

    private final GlobalDrawPaneConfig config;
    private final SubToolsPanel toolOptionsPanel;
    DrawableButtonTool.OptionButtonsBuilder optionButtonsBuilder;

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
            public void setCurrentToolbarOptions(DrawableButtonTool tool) {
                optionButtonsBuilder = tool.getOptions();
                optionButtonsBuilder.switchToolOptions(items, getId());
            }
        };

        CircleButtonTool circleButton = new CircleButtonTool(config) {
            @Override
            public void setCurrentToolbarOptions(DrawableButtonTool tool) {
                getOptions().switchToolOptions(items, getId());
                optionButtonsBuilder = this.getOptions();
            }
        };

        PathButtonTool pathButton = new PathButtonTool(config) {
            @Override
            public void setCurrentToolbarOptions(DrawableButtonTool tool) {
                //optionButtonsBuilder = tool.getOptions();
                optionButtonsBuilder.switchToolOptions(items, getId());
            }
        };

        TextButtonTool textButton = new TextButtonTool(config) {
            @Override
            public void setCurrentToolbarOptions(DrawableButtonTool tool) {
                optionButtonsBuilder = tool.getOptions();
                optionButtonsBuilder.switchToolOptions(items, getId());
            }
        };
        /*items.addAll(optionButtonsBuilder.getNodes(pathButton.getId()));*/
        setInitialSelectedTool(rectangleButton, items);

        getItems().addAll(rectangleButton, circleButton, pathButton, textButton);

        //AppLogger.log(getClass(), 61, config.getCurrentTool().getClass().getName());
    }

    /**
     * Sets the currently selected toolbar button.
     * @param button
     * @param items
     */
    private void setInitialSelectedTool(DrawableButtonTool button, ObservableList<Node> items) {
        optionButtonsBuilder = button.getOptions();
        items.addAll(optionButtonsBuilder.getNodes("static"));
        button.setAsCurrentlySelectedTool();
        optionButtonsBuilder.switchToolOptions(items, button.getId());
    }

}
