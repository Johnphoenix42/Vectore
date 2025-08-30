package appcustomcontrol;

import appcomponent.SubToolsPanel;
import apputil.GlobalDrawPaneConfig;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public abstract class ToolbarButton extends Button {

    protected final GlobalDrawPaneConfig config;
    protected final SubToolsPanel toolOptionsPanel;
    private boolean persistentlySelectable = false;
    public static final double BUTTON_WIDTH = 40;
    public static final double BUTTON_HEIGHT = 40;
    public static final Color BUTTON_BACKGROUND_COLOR = Color.DARKGRAY;
    public static final double BUTTON_CORNER_RADII = 5;
    public static final double BUTTON_INSETS = 5;

    public ToolbarButton(String name, GlobalDrawPaneConfig config, SubToolsPanel toolOptionsPanel){
        super(name);
        this.config = config;
        this.toolOptionsPanel = toolOptionsPanel;
        setId(name);
        setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        setBackground(new Background(new BackgroundFill(
                BUTTON_BACKGROUND_COLOR,
                new CornerRadii(BUTTON_CORNER_RADII),
                new Insets(BUTTON_INSETS)
        )));
        setTextFill(Color.BLACK);
        setOnMouseClicked(event -> onButtonClick());
    }

    /**
     * Persistently selectable means that the tool can be made into a "currently selected" value which
     * can be gotten by the draw pane, for example. Not all buttons in the toolbar may be persistently
     * selectable, e.g, foregroundButton - when clicked on after say pathButton, it does not become a
     * currently selected tool, therefore, it's not persistently selectable.
     * @return - true if button can be turned into a currently selected tool.
     */
    public boolean isPersistentlySelectable(){
        return persistentlySelectable;
    }

    public void setPersistentlySelectable(boolean persistentlySelectable){
        this.persistentlySelectable = persistentlySelectable;
    }

    void onButtonClick(){
        DrawableButtonTool prevSelectedButton = config.getCurrentTool();
        config.setPreviousTool(prevSelectedButton);
        if (prevSelectedButton != null){
            prevSelectedButton.setBackground(new Background(new BackgroundFill(
                    BUTTON_BACKGROUND_COLOR,
                    new CornerRadii(BUTTON_CORNER_RADII),
                    new Insets(BUTTON_INSETS))));
            prevSelectedButton.setTextFill(Color.BLACK);
        }
        setBackground(new Background((new BackgroundFill(
                Color.BLACK, new CornerRadii(5), new Insets(5)))));
        setTextFill(Color.WHITE);
        addClickListener(prevSelectedButton);
    }

    public abstract void addClickListener(DrawableButtonTool prevSelectedButton);

}
