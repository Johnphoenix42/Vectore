import apputil.AppLogger;
import apputil.GlobalDrawPaneConfig;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DrawPane extends StackPane {

    private final GlobalDrawPaneConfig drawAreaConfig;
    private final Pane canvasPane;
    Rectangle clipRect;

    public DrawPane(GlobalDrawPaneConfig config, double width, double height){
        super();
        drawAreaConfig = config;
        setBackground(new Background(new BackgroundFill(Color.WHEAT, null, null)));
        setMaxSize(width, height);
        setMinSize(600, 600);
        autosize();
        setAlignment(Pos.CENTER);
        //context = createAndAddCanvas(width/2, height - 100);
        canvasPane = new Pane();
        canvasPane.setStyle("-fx-background-color: white;");
        canvasPane.setPrefSize(400, 400);
        canvasPane.setMaxSize(400, 400);
        canvasPane.setEffect(new DropShadow(6, Color.BLACK));

        getChildren().add(canvasPane);
        clipRect = new Rectangle(400, 400);
        canvasPane.setClip(clipRect);
        //canvasPane.setTranslateX(-canvasPane.getBoundsInParent().getWidth() / 2);
        //canvasPane.setTranslateY(-canvasPane.getBoundsInParent().getHeight() / 2);
        addEventListeners();
    }

    private <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> renderNodes(EventType<T> type, T event){
        Map<String, LinkedHashMap<String, Node>> nodeTree = drawAreaConfig.getCurrentTool().draw(type, event);
        if(nodeTree != null) {
            LinkedHashMap<String, Node> primaryNodes = nodeTree.get("primary");
            LinkedHashMap<String, Node> secondaryNodes = nodeTree.get("secondary");

            if(primaryNodes != null) {
                for (Map.Entry<String, Node> nodeToAdd : primaryNodes.entrySet()) {
                    if (nodeToAdd == null) continue;
                    //nodeToAdd.getValue().setClip(clipRect);
                    canvasPane.getChildren().add(nodeToAdd.getValue());
                }
            }
            if(secondaryNodes == null) return nodeTree;
            for (Map.Entry<String, Node> nodeToAdd : secondaryNodes.entrySet()) {
                if (nodeToAdd == null || nodeToAdd.getValue() == null) continue;
                //nodeToAdd.getValue().setClip(clipRect);
                System.out.println(nodeToAdd.getKey());
                canvasPane.getChildren().add(nodeToAdd.getValue());
            }
        }
        return nodeTree;
    }

    private <T extends InputEvent> void unRenderNodes(EventType<T> type, T event){
        Map<String, LinkedHashMap<String, Node>> unDrawNodeTree = drawAreaConfig.getCurrentTool().unDraw(type, event);
        if(unDrawNodeTree != null) {
            LinkedHashMap<String, Node> unDrawPrimaryNodes = unDrawNodeTree.get("primary");
            LinkedHashMap<String, Node> unDrawSecondaryNodes = unDrawNodeTree.get("secondary");

            if(unDrawPrimaryNodes != null) {
                for (Map.Entry<String, Node> nodeToRemove : unDrawPrimaryNodes.entrySet()) {
                    if (nodeToRemove == null) continue;
                    canvasPane.getChildren().remove(nodeToRemove.getValue());
                }
            }
            if(unDrawSecondaryNodes == null) return;
            for (Map.Entry<String, Node> nodeToRemove : unDrawSecondaryNodes.entrySet()) {
                if (nodeToRemove != null && nodeToRemove.getValue() != null) canvasPane.getChildren().remove(nodeToRemove.getValue());
            }
        }
    }

    private void addEventListeners(){
        canvasPane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            renderNodes(MouseEvent.MOUSE_PRESSED, event);
            unRenderNodes(MouseEvent.MOUSE_PRESSED, event);
        });
        canvasPane.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            renderNodes(MouseEvent.MOUSE_MOVED, event);
            unRenderNodes(MouseEvent.MOUSE_MOVED, event);
        });
        canvasPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            renderNodes(MouseEvent.MOUSE_DRAGGED, event);
            unRenderNodes(MouseEvent.MOUSE_DRAGGED, event);
        });
        canvasPane.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            renderNodes(MouseEvent.MOUSE_RELEASED, event);
            unRenderNodes(MouseEvent.MOUSE_RELEASED, event);
        });
    }

}
