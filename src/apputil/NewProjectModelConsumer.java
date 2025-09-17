package apputil;

import appcomponent.AppMenuBar;
import appcomponent.DrawPane;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Screen;

import java.util.function.Consumer;

public class NewProjectModelConsumer implements Consumer<AppMenuBar.NewProjectModel> {

    private static final Double WIDTH;
    private static final Double HEIGHT;
    static {
        Rectangle2D screenRect = Screen.getPrimary().getVisualBounds();
        WIDTH = screenRect.getWidth();
        HEIGHT = screenRect.getHeight();
    }

    private TabPane drawingTabbedPane;
    private final GlobalDrawPaneConfig config;

    public NewProjectModelConsumer(GlobalDrawPaneConfig config){
        super();
        this.config = config;
    }

    @Override
    public void accept(AppMenuBar.NewProjectModel response) {
        Tab tab = new Tab(response.getProjectName());
        drawingTabbedPane.getTabs().add(tab);
        final DrawPane drawingArea = new DrawPane(this.config, 500, 200);
        drawingArea.setFocusTraversable(true);
        Pane canvasPane = drawingArea.createCanvas(response.getWidth(), response.getHeight());
        drawingArea.getChildren().add(canvasPane);
        drawingArea.addCoordinateText();
        drawingArea.addEventListeners(canvasPane);
        tab.setContent(drawingArea);
    }

    public void setTabbedPane(final TabPane drawingTabbedPane) {
        this.drawingTabbedPane = drawingTabbedPane;
    }
}
