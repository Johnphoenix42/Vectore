package appcomponent;

import appcustomcontrol.*;
import apputil.GlobalDrawPaneConfig;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.util.LinkedHashMap;
import java.util.Map;

public class ToolsPanel extends ToolBar {

    private final GlobalDrawPaneConfig config;
    protected final SubToolsPanel toolOptionsPanel;
    DrawableButtonTool.OptionButtonsBuilder optionButtonsBuilder;
    private TabPane drawingTabbedPane;

    public ToolsPanel(GlobalDrawPaneConfig config, SubToolsPanel toolOptionsPanel){
        super();
        this.toolOptionsPanel = toolOptionsPanel;
        this.config = config;
        autosize();
        addButtons(toolOptionsPanel);
        setOrientation(Orientation.VERTICAL);
        setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
    }

    private void addButtons(SubToolsPanel toolOptionsPanel){
        ObservableList<Node> items = toolOptionsPanel.getItems();
        RectangleButtonTool rectangleButton = new RectangleButtonTool(config, toolOptionsPanel);
        //rectangleButton.setShape(new Polygon(5, 8, 12, 20, 13, 6));
        //rectangleButton.setStyle("-fx-");

        CircleButtonTool circleButton = new CircleButtonTool(config, toolOptionsPanel);

        PathButtonTool pathButton = new PathButtonTool(config, toolOptionsPanel);

        TextButtonTool textButton = new TextButtonTool(config, toolOptionsPanel);
        /*items.addAll(optionButtonsBuilder.getNodes(pathButton.getId()));*/
        setInitialSelectedTool(rectangleButton, items);

        getItems().addAll(rectangleButton, circleButton, pathButton, textButton);
    }

    /**
     * Sets the initially selected toolbar button. This is the button that is automatically selected
     * as the app loads.
     * @param button the button to set as the first selected tool
     * @param items contains the controls specific to this tool button that will fill up the sub-tool option bar when selected.
     */
    private void setInitialSelectedTool(DrawableButtonTool button, ObservableList<Node> items) {
        optionButtonsBuilder = button.getOptions();
        LinkedHashMap<String, Node> globalOptionNodes = optionButtonsBuilder.getNodes(DrawableButtonTool.OptionButtonsBuilder.GLOBAL_NODE_OPTIONS);
        for (Map.Entry<String, Node> globalToolOption : globalOptionNodes.entrySet()) {
            items.add(globalToolOption.getValue());
        }
        button.setAsCurrentlySelectedTool();
        optionButtonsBuilder.switchToolOptions(items, button.getId());
    }

    public void setTabbedPane(TabPane drawingTabbedPane) {
        this.drawingTabbedPane = drawingTabbedPane;
    }

    class ButtonTool<T extends DrawableButtonTool> {

        ButtonTool(T t){
            //t.setCurrentToolbarOptions(DrawableButtonTool);
        }

    }

}
