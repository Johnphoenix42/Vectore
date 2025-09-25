package appcomponent;

import appcustomcontrol.DrawableButtonTool;
import apputil.GlobalDrawPaneConfig;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DrawPane extends StackPane {

    private final GlobalDrawPaneConfig drawAreaConfig;
    private final LinkedHashMap<String, Node> globalPrimaryElements = new LinkedHashMap<>();
    private Pane activeCanvasPane; // the active canvas we are using. This can change when we work with multiple tabs.
    private Node activeCanvasNode = null; // the node on the canvas currently being manipulated
    private Text mouseCoordinateText;
    private double xCanvasPos, yCanvasPos;

    public DrawPane(GlobalDrawPaneConfig config, double width, double height){
        super();
        drawAreaConfig = config;
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        autosize();
        setAlignment(Pos.CENTER);
    }

    public Pane createCanvas(int width, int height) {
        Pane canvasPane = new Pane();
        canvasPane.setStyle("-fx-background-color: white;");
        canvasPane.setPrefSize(width, height);
        canvasPane.setMaxSize(width, height);
        canvasPane.setEffect(new DropShadow(6, Color.BLACK));

        canvasPane.setClip(new Rectangle(width, height));
        canvasPane.setFocusTraversable(true);
        canvasPane.requestFocus();
        this.activeCanvasPane = canvasPane;
        return canvasPane;
    }

    public void addCoordinateText() {
        mouseCoordinateText = new Text("X: null, Y: null");
        mouseCoordinateText.setFill(Color.grayRgb(250));
        setAlignment(mouseCoordinateText, Pos.BOTTOM_LEFT);
        getChildren().add(mouseCoordinateText);
    }

    public void foreignRender(Map<String, LinkedHashMap<String, Node>> nodeTree) {
        LinkedHashMap<String, Node> primaryNodes = nodeTree.get(DrawableButtonTool.PRIMARY);
        LinkedHashMap<String, Node> secondaryNodes = nodeTree.get(DrawableButtonTool.SECONDARY);

        if(primaryNodes != null) {
            for (Map.Entry<String, Node> nodeToAdd : primaryNodes.entrySet()) {
                if (nodeToAdd == null) continue;
                try {
                    globalPrimaryElements.put(nodeToAdd.getKey(), nodeToAdd.getValue());
                    activeCanvasPane.getChildren().add(nodeToAdd.getValue());
                } catch (IllegalArgumentException exception) {
                    System.err.println(">> Render error: You are attempting to render "+ nodeToAdd.getValue() + " twice.");
                }
            }
        }
        if(secondaryNodes == null) return;
        for (Map.Entry<String, Node> nodeToAdd : secondaryNodes.entrySet()) {
            if (nodeToAdd == null || nodeToAdd.getValue() == null) continue;
            try {
                activeCanvasPane.getChildren().add(nodeToAdd.getValue());
            } catch (IllegalArgumentException exception) {
                System.err.println(">> Render error: You are attempting to render "+ nodeToAdd.getValue() + " twice.");
            }
        }
    }

    private <T extends InputEvent> void renderNodes(Pane canvasPane, EventType<T> type, T event){
        Map<String, LinkedHashMap<String, Node>> nodeTree = drawAreaConfig.getCurrentTool().draw(type, event);
        if(nodeTree != null) {
            LinkedHashMap<String, Node> primaryNodes = nodeTree.get(DrawableButtonTool.PRIMARY);
            LinkedHashMap<String, Node> secondaryNodes = nodeTree.get(DrawableButtonTool.SECONDARY);

            if(primaryNodes != null) {
                for (Map.Entry<String, Node> nodeToAdd : primaryNodes.entrySet()) {
                    if (nodeToAdd == null) continue;
                    //nodeToAdd.getValue().setClip(clipRect);
                    globalPrimaryElements.put(nodeToAdd.getKey(), nodeToAdd.getValue());
                    canvasPane.getChildren().add(nodeToAdd.getValue());
                }
            }
            if(secondaryNodes == null) return;
            for (Map.Entry<String, Node> nodeToAdd : secondaryNodes.entrySet()) {
                if (nodeToAdd == null || nodeToAdd.getValue() == null) continue;
                //nodeToAdd.getValue().setClip(clipRect);
                try {
                    canvasPane.getChildren().add(nodeToAdd.getValue());
                } catch (IllegalArgumentException exception) {
                    System.err.println(">> Render error: You are attempting to render "+ nodeToAdd.getValue() + " twice.");
                }
            }
        }
    }

    private <T extends InputEvent> void unRenderNodes(Pane canvasPane, EventType<T> type, T event){
        Map<String, LinkedHashMap<String, Node>> unDrawNodeTree = drawAreaConfig.getCurrentTool().unDraw(type, event);
        if (unDrawNodeTree != null) {
            LinkedHashMap<String, Node> unDrawPrimaryNodes = unDrawNodeTree.get(DrawableButtonTool.PRIMARY);
            LinkedHashMap<String, Node> unDrawSecondaryNodes = unDrawNodeTree.get(DrawableButtonTool.SECONDARY);

            if (unDrawPrimaryNodes != null) {
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
    DrawPane's click event. This in my current opinion is more efficient than using booleans to maintain
    states.
     */
    public void addEventListeners(Pane canvasPane){
        canvasPane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            renderNodes(canvasPane, MouseEvent.MOUSE_PRESSED, event);
            unRenderNodes(canvasPane, MouseEvent.MOUSE_PRESSED, event);
            event.consume();
        });
        canvasPane.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            setMouseCoordinates(event);
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
        canvasPane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            renderNodes(canvasPane, KeyEvent.KEY_PRESSED, event);
            unRenderNodes(canvasPane, KeyEvent.KEY_PRESSED, event);
        });
        canvasPane.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            renderNodes(canvasPane, KeyEvent.KEY_TYPED, event);
            unRenderNodes(canvasPane, KeyEvent.KEY_TYPED, event);
        });

    }

    private void setMouseCoordinates(MouseEvent event) {
        xCanvasPos = event.getX();
        yCanvasPos = event.getY();
        String xPosString = String.valueOf((int) xCanvasPos);
        String yPosString = String.valueOf((int) yCanvasPos);
        mouseCoordinateText.setText("X: " + xPosString + ", Y: " + yPosString);
    }

    public void removeSecondaryNodeFromShapes(Map<String, LinkedHashMap<String, Node>> unDrawNodeTree) {
        LinkedHashMap<String, Node> unDrawPrimaryNodes = unDrawNodeTree.get(DrawableButtonTool.PRIMARY);
        LinkedHashMap<String, Node> unDrawSecondaryNodes = unDrawNodeTree.get(DrawableButtonTool.SECONDARY);

        if (unDrawPrimaryNodes != null) {
            for (Map.Entry<String, Node> nodeToRemove : unDrawPrimaryNodes.entrySet()) {
                if (nodeToRemove == null) continue;
                activeCanvasPane.getChildren().remove(nodeToRemove.getValue());
            }
        }
        if(unDrawSecondaryNodes == null) return;
        for (Map.Entry<String, Node> nodeToRemove : unDrawSecondaryNodes.entrySet()) {
            if (nodeToRemove != null && nodeToRemove.getValue() != null) activeCanvasPane.getChildren().remove(nodeToRemove.getValue());
        }
    }

    /*
    Generates svg code with canvasPane as the root, i.e., the svg tag, the width and height
    of which is fed to viewBox attribute.
     */
    public String generateSVGHTML(){
        Set<Map.Entry<String,Node>> nodeContents = globalPrimaryElements.entrySet();
        int width = (int) activeCanvasPane.getBoundsInLocal().getWidth();
        int height = (int) activeCanvasPane.getBoundsInLocal().getHeight();
        StringBuilder svgString = new StringBuilder("<svg viewBox=\"0 0 " + width + " " + height + "\">");
        for (Map.Entry<String, Node> nodeContent : nodeContents) {
            Node node = nodeContent.getValue();
            SVGTagMaker tagMaker = new SVGTagMaker();
            String svgTag;
            if (node instanceof Rectangle) svgTag = tagMaker.constructSvgElement((Rectangle) node, "rect");
            else if (node instanceof Circle) svgTag = tagMaker.constructSvgElement((Circle) node, "circle");
            else if (node instanceof Path) svgTag = tagMaker.constructSvgElement((Path) node, "path");
            else svgTag = tagMaker.constructSvgElement((Text) node, "text");
            svgString.append("\n\t").append(svgTag);
        }
        svgString.append("\n</svg>");
        return svgString.toString();
    }

    public Pane getCanvas() {
        return activeCanvasPane;
    }

    public LinkedHashMap<String, Node> getGlobalPrimaryElements() {
        return globalPrimaryElements;
    }

    public double getXCanvasPos() {
        return xCanvasPos;
    }

    public double getYCanvasPos() {
        return yCanvasPos;
    }

    public void setActiveCanvasNode (Node activeNode) {
        this.activeCanvasNode = activeNode;
    }

    public Node getActiveCanvasNode(){
        return activeCanvasNode;
    }

    public enum CanvasActionMode {
        DRAW_MODE, SELECT_MODE
    }

}