package appcustomcontrol;

import appcomponent.SubToolsPanel;
import apputil.AppLogger;
import apputil.GlobalDrawPaneConfig;
import com.sun.istack.internal.NotNull;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public abstract class DrawableButtonTool extends ToolbarButton implements DrawTriggerable, OptionToolbarSettable{

    public static final String SECONDARY = "secondary";
    public static final String PRIMARY = "primary";

    // PathButtonTool's own very nodeTree. It's "primary" childNode contains every path appcomponent.DrawPane still has, i.e. not deleted.
    TreeMap<String, LinkedHashMap<String, Node>> nodeTree = new TreeMap<>();
    protected OptionButtonsBuilder optionButtonsBuilder;

    public DrawableButtonTool(String label, GlobalDrawPaneConfig config, SubToolsPanel toolOptionsPanel) {
        super(label, config, toolOptionsPanel);
        optionButtonsBuilder = new OptionButtonsBuilder(config);
        setPersistentlySelectable(true);
        Color hoverColor = Color.BLUE;
        Color defaultColor = Color.color(0.2, 0.2, 0.2);
        DropShadow shadow = new DropShadow(5, defaultColor);
        setEffect(shadow);
        setOnMouseEntered(event -> shadow.setColor(hoverColor));
        setOnMouseExited(event -> shadow.setColor(defaultColor));
    }

    public void setAsCurrentlySelectedTool(){
        if(!isPersistentlySelectable()) return;
        config.setCurrentTool(this);
        AppLogger.log(getClass(), 31, "Current tool is " + config.getCurrentTool());
        setBackground(new Background((new BackgroundFill(Color.BLACK,
                new CornerRadii(5), new Insets(5)))));
        setTextFill(Color.WHITE);
    }

    @Override
    public boolean isPersistentlySelectable(){
        return true;
    }

    @Override
    public void addClickListener(DrawableButtonTool prevSelectedButton) {
        setAsCurrentlySelectedTool();
        setCurrentToolbarOptions(this);
    }

    public abstract OptionButtonsBuilder getOptions();

    public abstract void setCurrentToolbarOptions(DrawableButtonTool tool);

    public static class OptionButtonsBuilder{
        public static final String GLOBAL_NODE_OPTIONS = "static";

        protected LinkedHashMap<String, LinkedHashMap<String, Node>> nodeMap;
        private final GlobalDrawPaneConfig config;

        ColorPicker colorPicker;
        ColorPicker strokeColorPicker;
        ToggleButton fillColorToggleButton;
        ToggleButton fillGradientToggleButton;
        ToggleButton fillPatternToggleButton;
        ToggleButton strokeColorToggleButton;
        ToggleButton strokeGradientToggleButton;
        ToggleButton strokePatternToggleButton;

        OptionButtonsBuilder(GlobalDrawPaneConfig config){
            this.config = config;
            ToggleGroup toggleGroup = new ToggleGroup();
            createFillButtons(toggleGroup);
            createStrokeButtons(toggleGroup);
            Separator separator = new Separator(Orientation.VERTICAL);

            nodeMap = new LinkedHashMap<>();
            LinkedHashMap<String, Node> toolsMap = new LinkedHashMap<>();

            toolsMap.put("fill_stroke_grid", createGridAndFill());
            toolsMap.put("separator", separator);
            nodeMap.put(GLOBAL_NODE_OPTIONS, toolsMap);

            colorPicker.setOnAction(event -> {
                config.setForegroundColor(colorPicker.getValue());
                this.setColorPickerOnAction(fillColorToggleButton);
            });
            strokeColorPicker.setOnAction(event -> {
                config.setStrokeColor(strokeColorPicker.getValue());
                this.setColorPickerOnAction(strokeColorToggleButton);
            });
        }

        private void createFillButtons (ToggleGroup toggleGroup) {
            colorPicker = new ColorPicker(Color.BLACK);
            colorPicker.setPrefSize(BUTTON_WIDTH, 30);
            fillColorToggleButton = new ToggleButton("Co");
            fillGradientToggleButton = new ToggleButton("Gr");
            fillPatternToggleButton = new ToggleButton("Pa");

            fillColorToggleButton.setToggleGroup(toggleGroup);
            fillColorToggleButton.setUserData(colorPicker);
            fillColorToggleButton.setOnAction(event -> {
                if (fillColorToggleButton.isSelected()) {
                    colorPicker.setDisable(false);
                    colorPicker.show();
                } else colorPicker.setDisable(true);
            });
            fillGradientToggleButton.setToggleGroup(toggleGroup);
            fillPatternToggleButton.setToggleGroup(toggleGroup);
        }

        private void createStrokeButtons (ToggleGroup toggleGroup) {
            strokeColorPicker = new ColorPicker(Color.BLACK);
            strokeColorPicker.setPrefSize(BUTTON_WIDTH, 30);
            strokeColorToggleButton = new ToggleButton("Co");
            strokeColorToggleButton.setSelected(true);
            strokeGradientToggleButton = new ToggleButton("Gr");
            strokePatternToggleButton = new ToggleButton("Pa");

            strokeColorToggleButton.setToggleGroup(toggleGroup);
            strokeColorToggleButton.setUserData(strokeColorPicker);
            strokeColorToggleButton.setOnAction(event -> {
                if (strokeColorToggleButton.isSelected()) {
                    colorPicker.setDisable(false);
                    strokeColorPicker.show();
                } else colorPicker.setDisable(true);
            });
            strokeGradientToggleButton.setToggleGroup(toggleGroup);
            strokePatternToggleButton.setToggleGroup(toggleGroup);
        }

        private GridPane createGridAndFill () {
            GridPane fillStrokeGrid = new GridPane();
            fillStrokeGrid.add(new Label("Fill"), 0, 0);
            fillStrokeGrid.add(colorPicker, 1, 0);
            fillStrokeGrid.add(fillColorToggleButton, 2, 0);
            fillStrokeGrid.add(fillGradientToggleButton, 3, 0);
            fillStrokeGrid.add(fillPatternToggleButton, 4, 0);

            fillStrokeGrid.add(new Label("Stroke"), 0, 1);
            fillStrokeGrid.add(strokeColorPicker, 1, 1);
            fillStrokeGrid.add(strokeColorToggleButton, 2, 1);
            fillStrokeGrid.add(strokeGradientToggleButton, 3, 1);
            fillStrokeGrid.add(strokePatternToggleButton, 4, 1);
            return fillStrokeGrid;
        }

        /**
         * Applies selected color from a ColorPicker to a shape, using ToggleButton as a switch.
         * If no canvas shape is selected, nothing happens.
         * @param toggleButton control that will be used as the switch. When the toggle button is on, color is
         *                     added. When off, color is removed, i.e. shape is hollow, not filled with TRANSPARENT
         */
        protected void setColorPickerOnAction(@NotNull ToggleButton toggleButton){
            Shape canvasActiveNode = (Shape) config.getSelectedNode();
            if (canvasActiveNode == null) return;
            ColorPicker picker = (ColorPicker) toggleButton.getUserData();
            if (picker == this.colorPicker) canvasActiveNode.setFill(toggleButton.isSelected() ? picker.getValue() : null);
            else canvasActiveNode.setStroke(toggleButton.isSelected() ? picker.getValue() : null);
        }

        public LinkedHashMap<String, Node> getNodes(String key) {
            nodeMap.computeIfAbsent(key, k -> new LinkedHashMap<>(0));
            return nodeMap.get(key);
        }

        /**
         * Switching from one button to another involves clearing the currently shown controls
         * of the options toolbar and filling it up with the controls specific to a newly selected
         * tool.
         *
         * @param items lists the options controls that are specific to the newly selected toolbar button.
         * @param newID id of the new button or tool selected.
         */
        public void switchToolOptions(ObservableList<Node> items, String newID){
            DrawableButtonTool prevTool = config.getPrevSelectedTool();
            if (prevTool != null) {
                LinkedHashMap<String, Node> prevToolOptions = prevTool.getOptions().getNodes(prevTool.getId());
                for (Map.Entry<String, Node> prevToolOptionSet : prevToolOptions.entrySet()) {
                    items.remove(prevToolOptions.get(prevToolOptionSet.getKey()));
                }
            }
            LinkedHashMap<String, Node> newToolOptions = getNodes(newID);
            for (Map.Entry<String, Node> newToolOptionSet : newToolOptions.entrySet()) {
                items.add(newToolOptions.get(newToolOptionSet.getKey()));
            }
//            config.setSelectedNode(null);
        }
    }
}
