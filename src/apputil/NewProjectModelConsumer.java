package apputil;

import appcomponent.AppMenuBar;
import appcomponent.DrawPane;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;

public class NewProjectModelConsumer implements Consumer<AppMenuBar.NewProjectModel> {

    private DrawPane drawingArea;

    public NewProjectModelConsumer(){
        super();
    }

    @Override
    public void accept(AppMenuBar.NewProjectModel response) {
        Pane canvasPane = DrawPane.createCanvas(response.getWidth(), response.getHeight());
        drawingArea.getChildren().add(canvasPane);
        drawingArea.addEventListeners(canvasPane);
    }

    public void setDrawPane(DrawPane drawingArea) {
        this.drawingArea = drawingArea;
    }

    public DrawPane getDrawPane() {
        return drawingArea;
    }
}
