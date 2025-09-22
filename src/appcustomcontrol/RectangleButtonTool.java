package appcustomcontrol;

import appcomponent.DrawPane;
import appcomponent.SubToolsPanel;
import apputil.AppLogger;
import apputil.GlobalDrawPaneConfig;
import apputil.Icon;
import shapes.Rectangle;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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
import javafx.scene.shape.*;
import javafx.scene.text.Text;

import java.util.*;

public class RectangleButtonTool extends DrawableButtonTool {

    public static final String SHAPE_NAMESPACE = "rectangle_";
    private final RectangleOptions optionButtonsBuilder;
    private DrawPane currentDrawingArea;
    private Rectangle activeRectangle = null;
    private final Circle[] anchors = new Circle[2];
    private boolean isDrawing = false;
    private double mouseStartPointX = 0, mouseStartPointY = 0;
    private double rectBottomPointX = 0, rectBottomPointY = 0;
    private int shapeCounter = 0;

    public RectangleButtonTool(GlobalDrawPaneConfig config, SubToolsPanel toolOptionsPanel) {
        super("", config, toolOptionsPanel);
        Icon textToolSvgPath = Icon.RECTANGLE_TOOL;
        setGraphic(textToolSvgPath.getSvgPath());
        setTooltip(new Tooltip("Rectangle"));
        optionButtonsBuilder = new RectangleOptions(config);
        nodeTree.put(PRIMARY, new LinkedHashMap<>());
        nodeTree.put(SECONDARY, new LinkedHashMap<>());
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
        } else if (eventType == MouseEvent.MOUSE_DRAGGED){
            drawOnMouseDragged((MouseEvent) ev, renderTree);
        } else if (eventType == MouseEvent.MOUSE_RELEASED){
            drawOnMouseReleased((MouseEvent) ev, renderTree);
        }
        return renderTree;
    }


    @Override
    public <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> unDraw(EventType<T> eventType, T event){
        //Keep in mind that the draw method will run before this one. That means booleans can be weird here.
        if (eventType == MouseEvent.MOUSE_PRESSED) {
            TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
            renderTree.put(PRIMARY, new LinkedHashMap<>());
            renderTree.put(SECONDARY, new LinkedHashMap<>());
            return renderTree;
        }
        return null;
    }

    private void drawOnMousePressed(MouseEvent ev, TreeMap<String, LinkedHashMap<String, Node>> renderTree){
        if(!isDrawing) {
            mouseStartPointX = ev.getX();
            mouseStartPointY = ev.getY();
            Rectangle rectangle = getRectangle();
            activeRectangle = rectangle;

            LinkedHashMap<String, Node> nodeMap = nodeTree.get(PRIMARY);
            nodeMap.put(SHAPE_NAMESPACE + shapeCounter, rectangle);
            renderTree.get(PRIMARY).put(SHAPE_NAMESPACE + shapeCounter, rectangle);
            shapeCounter++;
            isDrawing = true;
            config.setSelectedNode(activeRectangle);

            anchors[0] = createAnchorPoint(ev.getX(), ev.getY(), 0, renderTree);
        }else {
            activeRectangle.setRectX(Math.min(mouseStartPointX, ev.getX()));
            activeRectangle.setRectY(Math.min(mouseStartPointY, ev.getY()));
            activeRectangle.setWidth(Math.abs(mouseStartPointX - ev.getX()));
            activeRectangle.setHeight(Math.abs(mouseStartPointY - ev.getY()));

            anchors[1] = createAnchorPoint(ev.getX(), ev.getY(), 1, renderTree);
            isDrawing = false;

            if (nodeTree.get(SECONDARY).containsKey("point_0_1")) return;
            nodeTree.get(SECONDARY).putIfAbsent(anchors[1].getId(), anchors[1]);
            renderTree.get(SECONDARY).put(anchors[1].getId(), anchors[1]);
            nodeTree.get(SECONDARY).replace(anchors[1].getId(), anchors[1]);
        }
        if (nodeTree.get(SECONDARY).containsKey("point_0_0")) return;
        nodeTree.get(SECONDARY).putIfAbsent(anchors[0].getId(), anchors[0]);
        renderTree.get(SECONDARY).put(anchors[0].getId(), anchors[0]);
        nodeTree.get(SECONDARY).replace(anchors[0].getId(), anchors[0]);
    }

    private Rectangle getRectangle() {
        Rectangle rectangle = new Rectangle(0, 0);
        rectangle.setId(SHAPE_NAMESPACE + shapeCounter);
        boolean shouldFill = optionButtonsBuilder.fillColorToggleButton.isSelected();
        rectangle.setFill(shouldFill ? config.getForegroundColor() : null);
        rectangle.setRectX(mouseStartPointX);
        rectangle.setRectY(mouseStartPointY);
        rectangle.setStrokeWidth(config.getStrokeWidth());
        boolean shouldStroke = optionButtonsBuilder.strokeColorToggleButton.isSelected();
        rectangle.setStroke(shouldStroke ? super.config.getStrokeColor() : null);
        return rectangle;
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
        Circle anchor = (Circle) nodeTree.get(SECONDARY).getOrDefault("point_" + 0 + "_" + anchorCounter, new Circle(x, y, config.getStrokeWidth()+4));
        anchor.setCenterX(x);
        anchor.setCenterY(y);
        anchor.setFill(Color.TRANSPARENT);
        anchor.setStroke(Color.GRAY);
        anchor.setId("point_0_" + anchorCounter);
        anchor.setOnMousePressed(event -> {
            rectBottomPointX = Math.max(event.getX(), event.getX() + activeRectangle.getWidth());
            rectBottomPointY = Math.max(event.getY(), event.getY() + activeRectangle.getHeight());
            event.consume();
        });
        anchor.setOnMouseDragged(event -> {
            if(isDrawing){
                //if the rectangle is in drawing mode, you cannot drag its anchors.
                AppLogger.log(getClass(), 282, "drag not executing");
                return;
            }
            double eventX = event.getX();
            double eventY = event.getY();
            if (anchorCounter == 0) {
                anchor.setCenterX(eventX);
                anchor.setCenterY(eventY);
                activeRectangle.setRectX(eventX);
                activeRectangle.setRectY(eventY);
                //Circle anchor2 = (Circle) nodeTree.get(SECONDARY).get("point_" + shapeCounter + "_" + 1);
                anchors[1].setCenterX(activeRectangle.getX() + activeRectangle.getWidth());
                anchors[1].setCenterY(activeRectangle.getY() + activeRectangle.getHeight());
            } else {
                anchor.setCenterX(Math.max(eventX, activeRectangle.getX()));
                anchor.setCenterY(Math.max(eventY, activeRectangle.getY()));
                activeRectangle.setRectWidth(anchor.getCenterX() - activeRectangle.getRectX());
                activeRectangle.setRectHeight(anchor.getCenterY() - activeRectangle.getY());
            }
            event.consume();
        });
        anchor.setOnMouseReleased(event -> {
            mouseStartPointX = Math.min(activeRectangle.getX(), event.getX());
            mouseStartPointY = Math.min(activeRectangle.getY(), event.getY());
        });
        anchor.setOnMouseEntered(event -> anchor.setCursor(Cursor.CROSSHAIR));
        return anchor;
    }

    private void drawOnMouseMoved(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        if (!isDrawing) return;
        activeRectangle.setRectX(Math.min(mouseStartPointX, event.getX()));
        activeRectangle.setRectY(Math.min(mouseStartPointY, event.getY()));
        activeRectangle.setRectWidth(Math.abs(mouseStartPointX - event.getX()));
        activeRectangle.setRectHeight(Math.abs(mouseStartPointY - event.getY()));

        Node widthSpinner = getOptions().getWidthSpinner();
        ((Spinner<Double>) widthSpinner).getValueFactory().setValue(activeRectangle.getWidth());
        Node heightSpinner = getOptions().getHeightSpinner();
        ((Spinner<Double>) heightSpinner).getValueFactory().setValue(activeRectangle.getHeight());
        Node rotateSpinner = getOptions().getRotateSpinner();
        ((Spinner<Double>) rotateSpinner).getValueFactory().setValue(activeRectangle.getRotate());
    }

    private void drawOnMouseDragged(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        if (!isDrawing) return;
        activeRectangle.setRectX(Math.min(mouseStartPointX, event.getX()));
        activeRectangle.setRectY(Math.min(mouseStartPointY, event.getY()));
        activeRectangle.setRectWidth(Math.abs(mouseStartPointX - event.getX()));
        activeRectangle.setRectHeight(Math.abs(mouseStartPointY - event.getY()));
    }

    private void drawOnMouseReleased(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        if (isDrawing) return;
        anchors[0].setCenterX(activeRectangle.getX());
        anchors[0].setCenterY(activeRectangle.getY());
        anchors[1].setCenterX(activeRectangle.getX() + activeRectangle.getWidth());
        anchors[1].setCenterY(activeRectangle.getY() + activeRectangle.getHeight());
    }

    @Override
    public RectangleOptions getOptions() {
        return optionButtonsBuilder;
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
            currentDrawingArea = (DrawPane) tab.getContent();
            currentDrawingArea.removeSecondaryNodeFromShapes(renderTree);
        }
        config.setSelectedNode(activeRectangle);
    }

    public final class RectangleOptions extends OptionButtonsBuilder{

        private RectangleOptions(GlobalDrawPaneConfig config){
            super(config);
            double fieldWidth = 18;
            HBox widthBox = createWidthToolEntry(fieldWidth);
            HBox heightBox = createHeightToolEntry(fieldWidth);
            HBox rotationBox = createRotationToolEntry(fieldWidth);

            Separator separator = new Separator(Orientation.VERTICAL);

            LinkedHashMap<String, Node> nodeList = nodeMap.getOrDefault(getId(), new LinkedHashMap<>());
            nodeList.put("width_box", widthBox);
            nodeList.put("height_box", heightBox);
            nodeList.put("rotation_box", rotationBox);
            nodeList.put("separator_1", separator);
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

        private HBox createWidthToolEntry(double fieldWidth){
            Spinner<Double> numberSpinner = new Spinner<>(-500, 500, 10, 1);
            HBox box = createFieldToolEntry(numberSpinner, "width", fieldWidth);
            EventHandler<KeyEvent> keyHandler = event -> {
                if(nodeTree.get(PRIMARY).isEmpty()) return;

                String value = sanitizeTextField(numberSpinner, event);
                try {
                    activeRectangle.setWidth(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                    activeRectangle.setWidth(activeRectangle.getWidth());
                }
            };
            numberSpinner.getEditor().setOnKeyReleased(keyHandler);
            numberSpinner.setValueFactory(new DoubleSpinnerValueFactory(numberSpinner, (SpinnerValueFactory<Double> valueFactory) -> {
                if (activeRectangle == null) return;
                activeRectangle.setWidth(valueFactory.getValue());
            }));
            return box;
        }

        private HBox createHeightToolEntry(double fieldWidth){
            Spinner<Double> numberSpinner = new Spinner<>(-500, 500, 10, 1);
            HBox box = createFieldToolEntry(numberSpinner, "height", fieldWidth);
            EventHandler<KeyEvent> keyHandler = event -> {
                if(nodeTree.get(PRIMARY).isEmpty()) return;

                String value = sanitizeTextField(numberSpinner, event);
                try {
                    activeRectangle.setHeight(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                    activeRectangle.setHeight(activeRectangle.getHeight());
                }
            };
            numberSpinner.getEditor().setOnKeyReleased(keyHandler);
            numberSpinner.setValueFactory(new DoubleSpinnerValueFactory(numberSpinner, (SpinnerValueFactory<Double> valueFactory) -> {
                if (activeRectangle == null) return;
                activeRectangle.setHeight(valueFactory.getValue());
            }));
            return box;
        }

        private HBox createRotationToolEntry(double fieldWidth){
            Spinner<Double> numberSpinner = new Spinner<>(-500, 500, 10);
            HBox box = createFieldToolEntry(numberSpinner, "rotate", fieldWidth);
            EventHandler<KeyEvent> keyHandler = event -> {
                if(nodeTree.get(PRIMARY).isEmpty()) return;

                String value = sanitizeTextField(numberSpinner, event);
                try {
                    activeRectangle.setRotate(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                    activeRectangle.setRotate(activeRectangle.getRotate());
                }
            };
            numberSpinner.getEditor().setOnKeyReleased(keyHandler);
            numberSpinner.setValueFactory(new DoubleSpinnerValueFactory(numberSpinner, (SpinnerValueFactory<Double> valueFactory) -> {
                if (activeRectangle == null) return;
                activeRectangle.setRotate(valueFactory.getValue());
                anchors[0].setCenterX(activeRectangle.getX());
                anchors[0].setCenterY(activeRectangle.getY());
            }));
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

        @Override
        public void switchToolOptions(ObservableList<Node> items, String newID) {
            super.switchToolOptions(items, newID);
        }

        Node getWidthSpinner () {
            return (((HBox) getOptions().getNodes(getId()).get("width_box")).getChildren().get(1));
        }

        Node getHeightSpinner () {
            return (((HBox) getOptions().getNodes(getId()).get("height_box")).getChildren().get(1));
        }

        Node getRotateSpinner() {
            return (((HBox) getOptions().getNodes(getId()).get("rotation_box")).getChildren().get(1));
        }
    }
}
