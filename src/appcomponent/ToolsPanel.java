package appcomponent;

import appcustomcontrol.*;
import apputil.GlobalDrawPaneConfig;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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
                optionButtonsBuilder = this.getOptions();
                getOptions().switchToolOptions(items, getId());
            }
        };

        PathButtonTool pathButton = new PathButtonTool(config) {
            @Override
            public void setCurrentToolbarOptions(DrawableButtonTool tool) {
                optionButtonsBuilder = tool.getOptions();
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
    }

    /**
     * Sets the initially selected toolbar button. This is the button that is automatically selected
     * as the app loads.
     * @param button the button to set as the first selected tool
     * @param items contains the controls specific to this tool button that will fill up the sub-tool option bar when selected.
     */
    private void setInitialSelectedTool(DrawableButtonTool button, ObservableList<Node> items) {
        optionButtonsBuilder = button.getOptions();
        VBox vBox = new VBox(3);
        vBox.setAlignment(Pos.CENTER);
        HBox hBox = new HBox(3);
        ArrayList<Node> temp = new ArrayList<>();
        LinkedHashMap<String, Node> globalOptionNodes = optionButtonsBuilder.getNodes(DrawableButtonTool.OptionButtonsBuilder.GLOBAL_NODE_OPTIONS);
        for (Map.Entry<String, Node> globalToolOption : globalOptionNodes.entrySet()) {
            if (globalToolOption.getKey().equals("color_picker")) vBox.getChildren().add(globalOptionNodes.get(globalToolOption.getKey()));
            else if ("fill_toggle_button stroke_toggle_button".contains(globalToolOption.getKey())) {
                hBox.getChildren().add(globalOptionNodes.get(globalToolOption.getKey()));
            }
            else temp.add(globalOptionNodes.get(globalToolOption.getKey()));
        }
        vBox.getChildren().add(hBox);
        items.add((vBox));
        items.addAll(temp);
        button.setAsCurrentlySelectedTool();
        optionButtonsBuilder.switchToolOptions(items, button.getId());
    }

    class ButtonTool<T extends DrawableButtonTool> {

        ButtonTool(T t){
            //t.setCurrentToolbarOptions(DrawableButtonTool);
        }

    }

}
