package appcustomcontrol;

import apputil.AppLogger;
import apputil.GlobalDrawPaneConfig;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class DrawableButtonTool extends ToolbarButton implements DrawTriggerable{

    protected OptionButtonsBuilder optionButtonsBuilder;

    public DrawableButtonTool(String label, GlobalDrawPaneConfig config) {
        super(label, config);
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
        setCurrentToolbarOptions();
    }

    public abstract OptionButtonsBuilder getOptions();

    protected abstract void setCurrentToolbarOptions();

    public static class OptionButtonsBuilder{

        protected LinkedHashMap<String, ArrayList<Node>> nodeMap;
        private final GlobalDrawPaneConfig config;
        protected ColorPicker colorPicker;

        OptionButtonsBuilder(GlobalDrawPaneConfig config){
            this.config = config;
            colorPicker = createColorPicker(config);

            Separator separator = new Separator(Orientation.VERTICAL);

            nodeMap = new LinkedHashMap<>();
            ArrayList<Node> toolsList = new ArrayList<>();
            toolsList.add(colorPicker);
            toolsList.add(separator);
            nodeMap.put("static", toolsList);
        }

        public ColorPicker createColorPicker(GlobalDrawPaneConfig config) {
            colorPicker = new ColorPicker(Color.BLACK);
            colorPicker.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
            colorPicker.setOnAction(event -> {
                System.out.println("          setOnActionClick is testing");
                setColorPickerOnAction(colorPicker);
            });
            return colorPicker;
        }

        protected void setColorPickerOnAction(ColorPicker colorPicker){
            config.setForegroundColor(colorPicker.getValue());
        }

        public ArrayList<Node> getNodes(String key) {
            nodeMap.computeIfAbsent(key, k -> new ArrayList<>(0));
            return nodeMap.get(key);
        }

        public final void switchToolOptions(ObservableList<Node> items, String newID){
            DrawableButtonTool prevTool = config.getPrevSelectedTool();
            items.removeAll(prevTool.getOptions().getNodes(prevTool.getId()));
            items.addAll(getNodes(newID));
        }
    }
}
