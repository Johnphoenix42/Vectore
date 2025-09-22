package appcustomcontrol;

import appcomponent.DrawPane;
import appcomponent.SubToolsPanel;
import apputil.AppLogger;
import apputil.GlobalDrawPaneConfig;
import apputil.Icon;
import shapes.Circle;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import javax.vecmath.Vector2d;
import java.util.*;

public class CircleButtonTool extends DrawableButtonTool {

    public static final String SHAPE_NAMESPACE = "circle_";
    private final CircleOptions optionButtonsBuilder;
    private DrawPane currentDrawingArea;
    private Circle activeCircle = null;
    private boolean isDrawing = false;
    private double mouseStartPointX = 0, mouseStartPointY = 0;
    private int shapeCounter = 0;
    private final Vector2d radiusVector;

    public CircleButtonTool(GlobalDrawPaneConfig config, SubToolsPanel toolOptionsPanel) {
        super("", config, toolOptionsPanel);
        Icon textToolSvgPath = Icon.CIRCLE_TOOL;
        setGraphic(textToolSvgPath.getSvgPath());
        setTooltip(new Tooltip("Circle"));
        optionButtonsBuilder = new CircleOptions(config);
        nodeTree.put(PRIMARY, new LinkedHashMap<>());
        nodeTree.put(SECONDARY, new LinkedHashMap<>());
        radiusVector = new Vector2d();
    }

    @Override
    public <T extends InputEvent> TreeMap<String, LinkedHashMap<String, Node>> draw(EventType<T> eventType, T ev) {
        TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
        renderTree.put(PRIMARY, new LinkedHashMap<>());
        renderTree.put(SECONDARY, new LinkedHashMap<>());
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
        Circle anchor = null;
        if(!isDrawing) {
            mouseStartPointX = ev.getX();
            mouseStartPointY = ev.getY();
            Circle circle = getCircle(ev);
            activeCircle = circle;

            LinkedHashMap<String, Node> nodeMap = nodeTree.get(PRIMARY);
            nodeMap.put(SHAPE_NAMESPACE + shapeCounter, circle);
            renderTree.get(PRIMARY).put(SHAPE_NAMESPACE + shapeCounter, circle);
            shapeCounter++;
            isDrawing = true;
            config.setSelectedNode(activeCircle);
            anchor = createAnchorPoint(ev.getX(), ev.getY(), 0, renderTree);
            anchor.setId("point_" + shapeCounter + "_" + 0);
        }else {
            double deltaX = Math.abs(mouseStartPointX - ev.getX());
            double deltaY = Math.abs(mouseStartPointY - ev.getY());
            double radius = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
            radiusVector.set(mouseStartPointX - ev.getX(), mouseStartPointY - ev.getY());
            activeCircle.setRadius(radiusVector.length());
            isDrawing = false;
            anchor = createAnchorPoint(ev.getX(), ev.getY(), 1, renderTree);
            anchor.setId("point_" + shapeCounter + "_" + 1);
        }
        nodeTree.get(SECONDARY).putIfAbsent(anchor.getId(), anchor);
        renderTree.get(SECONDARY).putIfAbsent(anchor.getId(), anchor);
        nodeTree.get(SECONDARY).replace(anchor.getId(), anchor);
        renderTree.get(SECONDARY).replace(anchor.getId(), anchor);
    }

    private Circle getCircle(MouseEvent ev) {
        Circle circle = new Circle(0);
        circle.setCenterX(Math.min(mouseStartPointX, ev.getX()));
        circle.setCenterY(Math.min(mouseStartPointY, ev.getY()));
        boolean shouldFill = optionButtonsBuilder.fillColorToggleButton.isSelected();
        circle.setFill(shouldFill ? config.getForegroundColor() : null);
        circle.setStrokeWidth(config.getStrokeWidth());
        boolean shouldStroke = optionButtonsBuilder.strokeColorToggleButton.isSelected();
        circle.setStroke(shouldStroke ? config.getStrokeColor() : null);
        return circle;
    }

    /**
     * Create a path anchor node, i.e. a circle-represented point that received a click event,
     * and set it's onMousePressed event. Each anchor circle is also given an
     * id consisting of the  shapeCounter (how many paths this path tool has drawn),
     * and anchorCounter variable, the latter of which is the zero-based index
     * of anchors drawn since the path started. The anchors id are in this format:
     * "point_0_0".
     * @param x the x pos to draw the anchor node.
     * @param y the y pos to draw the anchor node.
     * @param anchorCounter specific index for the anchor being drawn - first anchor is zero and so on.
     * @param renderTree this node tree this tool gives draw pane to render.
     * @return the circle object which is the anchor node.
     */
    private Circle createAnchorPoint(double x, double y, int anchorCounter, Map<String, LinkedHashMap<String, Node>> renderTree) {
        Circle anchor = new Circle(x, y, config.getStrokeWidth()+4);
        anchor.setFill(Color.TRANSPARENT);
        anchor.setStroke(Color.GRAY);
        anchor.setId("point_" + shapeCounter + "_" + anchorCounter);
        anchor.setOnMousePressed(MouseEvent::consume);
        anchor.setOnMouseDragged(event -> {
            if(isDrawing){
                //if the rectangle is in drawing mode, you cannot drag its anchors.
                AppLogger.log(getClass(), 117, "drag not executing");
                return;
            }
            double eventX = event.getX();
            double eventY = event.getY();
            anchor.setCenterX(eventX);
            anchor.setCenterY(eventY);
            if (anchorCounter == 0) {
                activeCircle.setCenterX(eventX);
                activeCircle.setCenterY(eventY);
                Circle anchor2 = (Circle) nodeTree.get(SECONDARY).get("point_" + shapeCounter + "_" + 1);
                anchor2.setCenterX(eventX - radiusVector.getX());
                anchor2.setCenterY(eventY - radiusVector.getY());
            } else {
                radiusVector.set(activeCircle.getCenterX() - eventX, activeCircle.getCenterY() - eventY);
                activeCircle.setRadius(radiusVector.length());
            }
            event.consume();
        });
        anchor.setOnMouseEntered(event -> anchor.setCursor(Cursor.CROSSHAIR));
        return anchor;
    }

    private void drawOnMouseMoved(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        if (!isDrawing) return;
        double deltaX = Math.abs(mouseStartPointX - event.getX());
        double deltaY = Math.abs(mouseStartPointY - event.getY());
        double radius = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        activeCircle.setRadius(radius);

        optionButtonsBuilder.getXSpinner().getValueFactory().setValue(deltaX);
        optionButtonsBuilder.getYSpinner().getValueFactory().setValue(deltaY);
        optionButtonsBuilder.getRadiusSpinner().getValueFactory().setValue(radius);
    }

    @Override
    public CircleOptions getOptions() {
        return optionButtonsBuilder;
    }

    @Override
    public <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> unDraw(EventType<T> eventType, T event){
        return null;
    }

    @Override
    public void setCurrentToolbarOptions(DrawableButtonTool tool) {
        getOptions().switchToolOptions(toolOptionsPanel.getItems(), getId());
    }

    @Override
    public void addClickListener(DrawableButtonTool prevSelectedButton) {
        super.addClickListener(prevSelectedButton);
        TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
        renderTree.put(PRIMARY, new LinkedHashMap<>());
        renderTree.put(SECONDARY, new LinkedHashMap<>());
        LinkedHashMap<String, Node> anchorsMap = prevSelectedButton.nodeTree.get(SECONDARY);

        renderTree.get(SECONDARY).putAll(anchorsMap);
        anchorsMap.clear();
        Tab tab = toolOptionsPanel.getDrawingTabbedPane().getSelectionModel().getSelectedItem();
        if (tab != null) {
            currentDrawingArea = (DrawPane) toolOptionsPanel.getDrawingTabbedPane().getSelectionModel().getSelectedItem().getContent();
            currentDrawingArea.removeSecondaryNodeFromShapes(renderTree);
        }
        config.setSelectedNode(activeCircle);
    }

    public final class CircleOptions extends OptionButtonsBuilder{

        private Spinner<Double> centerXSpinner;
        private Spinner<Double> centerYSpinner;
        private Spinner<Double> radiusSpinner;

        private CircleOptions(GlobalDrawPaneConfig config){
            super(config);
            double fieldWidth = 20;
            HBox xCoordField = createXCoordToolEntry(fieldWidth);
            HBox yCoordField = createYCoordToolEntry(fieldWidth);
            HBox radiusField = createRadiusToolEntry(fieldWidth);

            LinkedHashMap<String, Node> nodeList = nodeMap.getOrDefault(getId(), new LinkedHashMap<>());
            nodeList.put("x_coordinate_box", xCoordField);
            nodeList.put("y_coordinate_box", yCoordField);
            nodeList.put("radius_box", radiusField);
            nodeMap.put(getId(), nodeList);
        }

        private HBox createFieldToolEntry(Spinner<Double> numberSpinner, String labelString, double fieldWidth){
            numberSpinner.setPrefWidth(80);
            numberSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
            numberSpinner.setEditable(true);
            numberSpinner.setFocusTraversable(false);
            HBox box = new HBox(new Text(labelString), numberSpinner);
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(2));
            box.setBackground(new Background(new BackgroundFill(ToolbarButton.BUTTON_BACKGROUND_COLOR, null, null)));
            return box;
        }

        private HBox createXCoordToolEntry(double fieldWidth){
            Spinner<Double> numberSpinner = new Spinner<>(-500, 500, 10, 1);
            HBox box = createFieldToolEntry(numberSpinner, "x", fieldWidth);
            EventHandler<KeyEvent> keyHandler = event -> {
                if(nodeTree.get(PRIMARY).isEmpty()) return;

                String value = sanitizeTextField(numberSpinner, event);
                try {
                    activeCircle.setCenterX(Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    activeCircle.setCenterX(activeCircle.getCenterX());
                }
            };
            numberSpinner.getEditor().setOnKeyReleased(keyHandler);
            numberSpinner.setValueFactory(new DoubleSpinnerValueFactory(numberSpinner, (SpinnerValueFactory<Double> valueFactory) -> {
                if (activeCircle == null) return;
                activeCircle.setCenterX(valueFactory.getValue());
            }));
            centerXSpinner = numberSpinner;
            return box;
        }

        private HBox createYCoordToolEntry(double fieldWidth){
            Spinner<Double> numberSpinner = new Spinner<>(-500, 500, 10, 1);
            HBox box = createFieldToolEntry(numberSpinner, "y", fieldWidth);
            EventHandler<KeyEvent> keyHandler = event -> {
                if(nodeTree.get(PRIMARY).isEmpty()) return;

                String value = sanitizeTextField(numberSpinner, event);
                try {
                    activeCircle.setCenterY(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                    activeCircle.setCenterY(activeCircle.getCenterY());
                }
            };
            numberSpinner.getEditor().setOnKeyReleased(keyHandler);
            numberSpinner.setValueFactory(new DoubleSpinnerValueFactory(numberSpinner, (SpinnerValueFactory<Double> valueFactory) -> {
                if (activeCircle == null) return;
                activeCircle.setCenterY(valueFactory.getValue());
            }));
            centerYSpinner = numberSpinner;
            return box;
        }

        private HBox createRadiusToolEntry(double fieldWidth){
            Spinner<Double> numberSpinner = new Spinner<>(-500, 500, 10);
            HBox box = createFieldToolEntry(numberSpinner, "radius", fieldWidth);
            EventHandler<KeyEvent> keyHandler = event -> {
                if(nodeTree.get(PRIMARY).isEmpty()) return;

                String value = sanitizeTextField(numberSpinner, event);
                try {
                    activeCircle.setRadius(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                    activeCircle.setRadius(activeCircle.getRadius());
                }
            };
            numberSpinner.getEditor().setOnKeyReleased(keyHandler);
            numberSpinner.setValueFactory(new DoubleSpinnerValueFactory(numberSpinner, (SpinnerValueFactory<Double> valueFactory) -> {
                if (activeCircle == null) return;
                activeCircle.setRadius(valueFactory.getValue());
            }));
            radiusSpinner = numberSpinner;
            return box;
        }

        private String sanitizeTextField(Spinner<Double> spinner, KeyEvent event){
            TextField textField = spinner.getEditor();
            String textInput = textField.getText();
            String inputtedCharacter = event.getText();
            if(inputtedCharacter.matches("\\D")) {
                textField.setText(textInput.split(inputtedCharacter)[0]);
                textField.positionCaret(4);
            }
            if(textInput.length() == 4) textField.setText(textInput.split(inputtedCharacter)[0]);
            return textInput;
        }

        Spinner<Double> getXSpinner () {
            return centerXSpinner;
        }

        Spinner<Double> getYSpinner () {
            return centerYSpinner;
        }

        Spinner<Double> getRadiusSpinner() {
            return radiusSpinner;
        }
    }

    private static class DoubleSpinnerValueFactory extends SpinnerValueFactory<Double> {
        private final Spinner<Double> numberSpinner;
        private final InDecrementListener<Double> inDecrementListener;

        public DoubleSpinnerValueFactory(Spinner<Double> numberSpinner, InDecrementListener<Double> inDecrementListener) {
            this.numberSpinner = numberSpinner;
            this.inDecrementListener = inDecrementListener;
            setValue(10.0);
        }

        @Override
        public void decrement(int steps) {
            SpinnerValueFactory<Double> valueFactory = numberSpinner.getValueFactory();
            if (valueFactory == null) {
                throw new IllegalStateException("Can't decrement Spinner with a null SpinnerValueFactory");
            }
            commitEditorText(valueFactory);
            Double oldValue = valueFactory.getValue();
            valueFactory.setValue((oldValue != null ? oldValue : 0) - steps);

            inDecrementListener.onInDecrement(valueFactory);
        }

        @Override
        public void increment(int steps) {
            SpinnerValueFactory<Double> valueFactory = numberSpinner.getValueFactory();
            if (valueFactory == null) {
                throw new IllegalStateException("Can't decrement Spinner with a null SpinnerValueFactory");
            }
            commitEditorText(valueFactory);
            Double oldValue = valueFactory.getValue();
            valueFactory.setValue(steps + (oldValue != null ? oldValue : 0));

            inDecrementListener.onInDecrement(valueFactory);
        }

        private void commitEditorText(SpinnerValueFactory<Double> valueFactory){
            String text = numberSpinner.getEditor().getText();
            if (valueFactory != null) {
                StringConverter<Double> converter = valueFactory.getConverter();
                if (converter == null) return;
                Double value = converter.fromString(text);
                valueFactory.setValue(value);
            }
        }
    }
}
