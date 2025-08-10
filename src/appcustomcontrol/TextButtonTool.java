package appcustomcontrol;

import appcomponent.DrawPane;
import apputil.AppLogger;
import apputil.GlobalDrawPaneConfig;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;

public abstract class TextButtonTool extends DrawableButtonTool {

    public static final String SHAPE_NAMESPACE = "text_";
    private boolean isDrawing = false;
    private final TextOptions optionButtonsBuilder;
    private Rectangle activeTextBounds = null;
    private Text activeText = null;
    private double mouseStartPointX = 0, mouseStartPointY = 0;
    private int shapeCounter = 0;
    private boolean isTextWriting = false;

    public TextButtonTool(GlobalDrawPaneConfig config) {
        super("Text", config);
        optionButtonsBuilder = new TextOptions(config);
        nodeTree.put(PRIMARY, new LinkedHashMap<>());
        nodeTree.put(SECONDARY, new LinkedHashMap<>());
    }

    @Override
    public <T extends InputEvent> TreeMap<String, LinkedHashMap<String, Node>> draw(EventType<T> eventType, T event) {
        TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
        renderTree.put(PRIMARY, new LinkedHashMap<>());
        renderTree.put(SECONDARY, new LinkedHashMap<>());

        if (eventType == MouseEvent.MOUSE_PRESSED){
            drawOnMousePressed((MouseEvent) event, renderTree);
        } else if (eventType == MouseEvent.MOUSE_DRAGGED){
            drawOnMouseDragged((MouseEvent) event, renderTree);
        } else if (eventType == MouseEvent.MOUSE_RELEASED) {
            drawOnMouseReleased((MouseEvent) event, renderTree);
        } else if (eventType == KeyEvent.KEY_TYPED) {
            drawOnKeyTyped((KeyEvent) event, renderTree);
        }
        return renderTree;
    }

    private void drawOnKeyTyped(KeyEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        activeText.setText(activeText.getText() + event.getCharacter());
    }

    private void drawOnMousePressed(MouseEvent ev, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        Circle anchor = null;
        isTextWriting = false;
        if(!isDrawing) {
            mouseStartPointX = ev.getX();
            mouseStartPointY = ev.getY();
            activeTextBounds = (Rectangle) nodeTree.get(SECONDARY).getOrDefault(SHAPE_NAMESPACE + 0, new Rectangle(0, 0));
            activeTextBounds.setId(SHAPE_NAMESPACE + 0);
            activeTextBounds.setFill(Color.TRANSPARENT);
            activeTextBounds.setX(mouseStartPointX);
            activeTextBounds.setY(mouseStartPointY);
            activeTextBounds.setStrokeWidth(config.getStrokeWidth());
            activeTextBounds.setStroke(Color.BLACK);
            activeTextBounds.setStrokeDashOffset(20);

            if (activeTextBounds != null) {
                activeTextBounds.setOnMouseEntered(event -> {
                    activeTextBounds.setCursor(Cursor.TEXT);
                });
                activeTextBounds.setOnMousePressed(event -> {
                    isTextWriting = true;
                    event.consume();
                });
            }
            isDrawing = true;
            renderTree.get(SECONDARY).put(SHAPE_NAMESPACE + 0, activeTextBounds);
            nodeTree.get(SECONDARY).putIfAbsent(SHAPE_NAMESPACE + 0, activeTextBounds);

            anchor = createAnchorPoint(ev.getX(), ev.getY(), 0, renderTree);
        }

        if (anchor != null) {
            nodeTree.get(SECONDARY).putIfAbsent(anchor.getId(), anchor);
            renderTree.get(SECONDARY).putIfAbsent(anchor.getId(), anchor);
            nodeTree.get(SECONDARY).replace(anchor.getId(), anchor);
            renderTree.get(SECONDARY).replace(anchor.getId(), anchor);
        }
    }
    private Circle createAnchorPoint(double x, double y, int anchorCounter, Map<String, LinkedHashMap<String, Node>> renderTree) {
        Circle anchor = (Circle) nodeTree.get(SECONDARY).getOrDefault("point_" + 0 + "_" + anchorCounter, new Circle(x, y, config.getStrokeWidth()+4));
        anchor.setCenterX(x);
        anchor.setCenterY(y);
        anchor.setFill(Color.TRANSPARENT);
        anchor.setStroke(Color.GRAY);
        anchor.setId("point_" + shapeCounter + "_" + anchorCounter);
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
                activeTextBounds.setX(eventX);
                activeTextBounds.setY(eventY);
                activeText.setX(eventX);
                activeText.setY(eventY + activeText.getFont().getSize());
                Circle anchor2 = (Circle) nodeTree.get(SECONDARY).get("point_" + shapeCounter + "_" + 1);
                anchor2.setCenterX(activeTextBounds.getX() + activeTextBounds.getWidth());
                anchor2.setCenterY(activeTextBounds.getY() + activeTextBounds.getHeight());
            } else {
                anchor.setCenterX(Math.max(eventX, activeTextBounds.getX()));
                anchor.setCenterY(Math.max(eventY, activeTextBounds.getY()));
                activeTextBounds.setWidth(Math.max(0, eventX - mouseStartPointX));
                activeTextBounds.setHeight(Math.max(0, eventY - mouseStartPointY));
            }
            event.consume();
        });
        anchor.setOnMouseReleased(event -> {
            mouseStartPointX = Math.min(activeTextBounds.getX(), event.getX());
            mouseStartPointY = Math.min(activeTextBounds.getY(), event.getY());
        });
        anchor.setOnMouseEntered(event -> anchor.setCursor(Cursor.CROSSHAIR));
        return anchor;
    }

    private void drawOnMouseDragged(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        if (!isDrawing) return;
        activeTextBounds.setX(Math.min(mouseStartPointX, event.getX()));
        activeTextBounds.setY(Math.min(mouseStartPointY, event.getY()));
        activeTextBounds.setWidth(Math.abs(mouseStartPointX - event.getX()));
        activeTextBounds.setHeight(Math.abs(mouseStartPointY - event.getY()));
    }

    private void drawOnMouseReleased(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        isDrawing = false;
        if (!isTextWriting) {
            if (activeTextBounds.getWidth() < 5 && activeTextBounds.getHeight() < 5) {
                activeTextBounds.setWidth(30);
                activeTextBounds.setHeight(10);
            }
            Text text = new Text();
            text.setX(activeTextBounds.getX());
            text.setY(activeTextBounds.getY() + text.getFont().getSize());
            text.setFont(Font.font(10));
            activeText = text;
            activeText.setWrappingWidth(activeTextBounds.getWidth());
            config.setSelectedNode(activeText);
            shapeCounter++;
            renderTree.get(PRIMARY).put(SHAPE_NAMESPACE + shapeCounter, text);
            nodeTree.get(PRIMARY).put(SHAPE_NAMESPACE + shapeCounter, text);
            DrawPane.getPane().requestFocus();
            text.setOnMouseEntered(textEvent -> {
                text.setCursor(Cursor.TEXT);
            });
            text.setOnMousePressed(textEvent -> {
                setAsCurrentlySelectedTool();
                config.setCurrentTool(TextButtonTool.this);
                setCurrentToolbarOptions(TextButtonTool.this);
                textEvent.consume();
            });
        }
        isTextWriting = true;
    }

    @Override
    public TextOptions getOptions() {
        return optionButtonsBuilder;
    }

    @Override
    public <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> unDraw(EventType<T> eventType, T event){
        //Keep in mind that the draw method will run before this one. That means booleans can be weird here.
        /* if (eventType == MouseEvent.MOUSE_PRESSED) {
            TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
            renderTree.put(PRIMARY, new LinkedHashMap<>());
            renderTree.put(SECONDARY, new LinkedHashMap<>());
            LinkedHashMap<String, Node> anchorsMap = nodeTree.get(SECONDARY);

            //!isDrawing at this point means rectangle is not drawing.
            if (!isDrawing) {

                /*
                This next block checks if a new Path has been created and the last path's control points
                remain active. If they are, it clears them all except for the first control point
                 */
                /*if (anchorsMap.containsKey("point_" + (shapeCounter - 1) + "_0")) {
                    Node newestNodeAnchor = anchorsMap.remove("point_" + shapeCounter + "_0");
                    renderTree.get(SECONDARY).putAll(anchorsMap);
                    anchorsMap.clear();
                    anchorsMap.put("point_" + shapeCounter + "_0", newestNodeAnchor);
                }
            }
            return renderTree;
        }*/
        return null;
    }

    @Override
    public void addClickListener(DrawableButtonTool prevSelectedButton) {
        super.addClickListener(prevSelectedButton);
        TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
        renderTree.put(PRIMARY, new LinkedHashMap<>());
        renderTree.put(SECONDARY, new LinkedHashMap<>());
        LinkedHashMap<String, Node> anchorsMap = prevSelectedButton.nodeTree.get(SECONDARY);

        // Copy every secondary node from the last selected button into this buttons render tree so that
        // it can be removed from to canvas
        renderTree.get(SECONDARY).putAll(anchorsMap);
        anchorsMap.clear();
        DrawPane.removeSecondaryNodeFromShapes(renderTree);
        config.setSelectedNode(null);
    }

    public final class TextOptions extends OptionButtonsBuilder{

        private TextOptions(GlobalDrawPaneConfig config){
            super(config);
            double fieldWidth = 20;
            HBox fontSizeBox = createWidthToolEntry(fieldWidth);

            Separator separator = new Separator(Orientation.VERTICAL);

            LinkedHashMap<String, Node> nodeList = nodeMap.getOrDefault(getId(), new LinkedHashMap<>());
            nodeList.put("width_box", fontSizeBox);
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
            HBox box = createFieldToolEntry(numberSpinner, "size", fieldWidth);
            EventHandler<KeyEvent> keyHandler = event -> {
                if(nodeTree.get(PRIMARY).isEmpty()) return;

                String value = sanitizeTextField(numberSpinner, event);
                try {
                    activeText.setFont(Font.font(Double.parseDouble(value)));
                } catch (NumberFormatException ex) {
                    System.out.println(">>Number Format error");
                }
            };
            numberSpinner.getEditor().setOnKeyReleased(keyHandler);
            numberSpinner.setValueFactory(new DoubleSpinnerValueFactory(numberSpinner, (SpinnerValueFactory<Double> valueFactory) -> {
                if (activeTextBounds == null) return;
                activeText.setFont(Font.font(valueFactory.getValue()));
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

    }

}
