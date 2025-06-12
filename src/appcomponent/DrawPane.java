package appcomponent;

import apputil.GlobalDrawPaneConfig;
import javafx.collections.ObservableList;
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
import java.util.Map;

public class DrawPane extends StackPane {

    private final GlobalDrawPaneConfig drawAreaConfig;
    private static Pane canvasPane;

    public DrawPane(GlobalDrawPaneConfig config, double width, double height){
        super();
        drawAreaConfig = config;
        setBackground(new Background(new BackgroundFill(Color.WHEAT, null, null)));
        setMaxSize(width, height);
        setMinSize(600, 600);
        autosize();
        setAlignment(Pos.CENTER);
    }

    public static Pane createCanvas(int width, int height) {
        Pane canvasPane = new Pane();
        canvasPane.setStyle("-fx-background-color: white;");
        canvasPane.setPrefSize(width, height);
        canvasPane.setMaxSize(width, height);
        canvasPane.setEffect(new DropShadow(6, Color.BLACK));

        canvasPane.setClip(new Rectangle(width, height));
        DrawPane.canvasPane = canvasPane;
        return canvasPane;
    }

    private <T extends InputEvent> void renderNodes(Pane canvasPane, EventType<T> type, T event){
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
            if(secondaryNodes == null) return;
            for (Map.Entry<String, Node> nodeToAdd : secondaryNodes.entrySet()) {
                if (nodeToAdd == null || nodeToAdd.getValue() == null) continue;
                //nodeToAdd.getValue().setClip(clipRect);
                System.out.println(nodeToAdd.getKey());
                canvasPane.getChildren().add(nodeToAdd.getValue());
            }
        }
    }

    private <T extends InputEvent> void unRenderNodes(Pane canvasPane, EventType<T> type, T event){
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

    /*
    Note: I have used EventHandler to render and unRender because eventHandler traverses from the child
    node up to the tree root while EventFilter executes from the root down to the child. By using
    EventHandler, I am able to consume certain events before they bubble up to the root, e.g. when the
    path controls (the smaller, black-filled ones) are clicked on, the event doesn't need to get to the
    appcomponent.DrawPane's click event. This in my current opinion is more efficient than using booleans to maintain
    states.
     */
    public void addEventListeners(Pane canvasPane){
        canvasPane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            renderNodes(canvasPane, MouseEvent.MOUSE_PRESSED, event);
            unRenderNodes(canvasPane, MouseEvent.MOUSE_PRESSED, event);
        });
        canvasPane.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            renderNodes(canvasPane, MouseEvent.MOUSE_MOVED, event);
            unRenderNodes(canvasPane, MouseEvent.MOUSE_MOVED, event);
        });
        canvasPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            renderNodes(canvasPane, MouseEvent.MOUSE_DRAGGED, event);
            unRenderNodes(canvasPane, MouseEvent.MOUSE_DRAGGED, event);
        });
        canvasPane.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            renderNodes(canvasPane, MouseEvent.MOUSE_RELEASED, event);
            unRenderNodes(canvasPane, MouseEvent.MOUSE_RELEASED, event);
        });
    }

    /*
    Generates svg code with canvasPane as the root, i.e., the svg tag, the width and height
    of which is fed to viewBox attribute.
     */
    public String generateSVGHTML(){
        ObservableList<Node> nodeContents = canvasPane.getChildren();
        double width = canvasPane.getBoundsInLocal().getWidth();
        double height = canvasPane.getBoundsInLocal().getHeight();
        StringBuilder svgString = new StringBuilder("<svg viewBox=\"0 0 " + width + " " + height + "\">");
        for (Node nodeContent : nodeContents) {
            String tagName = nodeContent.getClass().getSimpleName().toLowerCase();
            String textContent = "";
            svgString.append("\n\t<")
                    .append(tagName)
                    .append(">")
                    .append(textContent)
                    .append("</")
                    .append(tagName)
                    .append(">");
        }
        svgString.append("\n</svg>");
        return svgString.toString();
    }

    public static Pane getPane() {
        return canvasPane;
    }
}