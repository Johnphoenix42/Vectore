package appcustomcontrol;

import apputil.GlobalDrawPaneConfig;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.*;

public abstract class CircleButtonTool extends DrawableButtonTool {

    public static final String SHAPE_NAMESPACE = "circle_";
    TreeMap<String, LinkedHashMap<String, Node>> nodeTree = new TreeMap<>();
    private Circle activeCircle = null;
    private boolean isDrawing = false;
    private double mouseStartPointX = 0, mouseStartPointY = 0;
    private int shapeCounter = 0;

    public CircleButtonTool(GlobalDrawPaneConfig config) {
        super("Circle", config);
        optionButtonsBuilder = new CircleOptions(config);
        nodeTree.put("primary", new LinkedHashMap<>());
        nodeTree.put("secondary", new LinkedHashMap<>());
    }

    @Override
    public <T extends InputEvent> TreeMap<String, LinkedHashMap<String, Node>> draw(EventType<T> eventType, T ev) {
        TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
        renderTree.put("primary", new LinkedHashMap<>());
        renderTree.put("secondary", new LinkedHashMap<>());
        if (eventType == MouseEvent.MOUSE_PRESSED) {
            drawOnMousePressed((MouseEvent) ev, renderTree);
        } else if (eventType == MouseEvent.MOUSE_MOVED){
            drawOnMouseMoved((MouseEvent) ev, renderTree);
        } else if (eventType == MouseEvent.MOUSE_DRAGGED) {
            drawOnMouseMoved((MouseEvent) ev, renderTree);
        }
        return renderTree;
    }
    private void drawOnMousePressed(MouseEvent ev, TreeMap<String, LinkedHashMap<String, Node>> renderTree){
        if(!isDrawing) {
            mouseStartPointX = ev.getX();
            mouseStartPointY = ev.getY();
            Circle circle = new Circle(0);
            circle.setTranslateX(Math.min(mouseStartPointX, ev.getX()));
            circle.setTranslateY(Math.min(mouseStartPointY, ev.getY()));
            circle.setFill(config.getForegroundColor());
            circle.setStrokeWidth(config.getStrokeWidth());
            circle.setStroke(super.config.getForegroundColor());
            activeCircle = circle;

            LinkedHashMap<String, Node> nodeMap = nodeTree.get("primary");
            nodeMap.put(SHAPE_NAMESPACE + shapeCounter, circle);
            renderTree.get("primary").put(SHAPE_NAMESPACE + shapeCounter, circle);
            shapeCounter++;
            isDrawing = true;
        }else {
            double deltaX = Math.abs(mouseStartPointX - ev.getX());
            double deltaY = Math.abs(mouseStartPointY - ev.getY());
            double radius = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
            activeCircle.setRadius(radius);
            isDrawing = false;
        }
    }

    private void drawOnMouseMoved(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        if (!isDrawing) return;
        double deltaX = Math.abs(mouseStartPointX - event.getX());
        double deltaY = Math.abs(mouseStartPointY - event.getY());
        double radius = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        activeCircle.setRadius(radius);
    }

    @Override
    public OptionButtonsBuilder getOptions() {
        return optionButtonsBuilder;
    }

    @Override
    public <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> unDraw(EventType<T> eventType, T event){
        return null;
    }

    @Override
    public void addClickListener(DrawableButtonTool prevSelectedButton) {
        super.addClickListener(prevSelectedButton);
    }

    public final class CircleOptions extends OptionButtonsBuilder{

        private CircleOptions(GlobalDrawPaneConfig config){
            super(config);
            TextField xLocationField = new TextField();
            xLocationField.setMaxWidth(60);
            xLocationField.setOnKeyTyped(event -> {
                try {
                    activeCircle.setCenterX(Double.parseDouble(xLocationField.getText()));
                } catch (NumberFormatException e) {
                    activeCircle.setCenterX(activeCircle.getCenterX());
                }
            });

            TextField yLocationField = new TextField();
            yLocationField.setMaxWidth(60);
            yLocationField.setOnAction(event -> {
                try {
                    activeCircle.setCenterY(Double.parseDouble(yLocationField.getText()));
                } catch (NumberFormatException e) {
                    activeCircle.setCenterY(activeCircle.getCenterY()+1);
                }
            });

            TextField rotationField = new TextField();
            rotationField.setMaxWidth(60);
            rotationField.setOnAction(event -> {
                try {
                    activeCircle.setRotate(Double.parseDouble(rotationField.getText()));
                } catch (NumberFormatException e) {
                    activeCircle.setRotate(0);
                }
            });
            ArrayList<Node> nodeList = nodeMap.getOrDefault(getId(), new ArrayList<>());
            nodeList.add(xLocationField);
            nodeList.add(yLocationField);
            nodeList.add(rotationField);
            nodeMap.put(getId(), nodeList);
        }
    }
}
