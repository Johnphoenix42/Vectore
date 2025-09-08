package appcustomcontrol;

import appcomponent.DrawPane;
import appcomponent.SubToolsPanel;
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
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.*;

public class TextButtonTool extends DrawableButtonTool {

    public static final String SHAPE_NAMESPACE = "text_";
    private boolean isDrawing = false;
    private final TextOptions optionButtonsBuilder;
    private Rectangle activeTextBounds = null;
    private Text activeText = null;
    private final Circle[] anchors = new Circle[2];
    private double mouseStartPointX = 0, mouseStartPointY = 0;
    private int shapeCounter = 0;
    private boolean isTextWriting = false;
    private TypeEventValue typeValueEnum;

    public TextButtonTool(GlobalDrawPaneConfig config, SubToolsPanel toolOptionsPanel) {
        super("Text", config, toolOptionsPanel);
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
        } else if (eventType == KeyEvent.KEY_PRESSED) {
            drawOnKeyPressed(( KeyEvent) event, renderTree);
        } else if (eventType == KeyEvent.KEY_TYPED) {
            drawOnKeyTyped(( KeyEvent) event, renderTree);
        }
        return renderTree;
    }

    private void drawOnKeyPressed(KeyEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        int typeOrdinal = event.getCode().ordinal(); // 1 == backspace and 81 == delete
        // arrow keys <^>V - 16, 17, 18, 19
        /*TypeEnum typeEnum = TypeEnum.FORWARD_NEGATIVE_TYPING;
        typeEnum.setOrdinal(ordinal);
        Optional<Integer> ordinalOptional = Optional.ofNullable(typeEnum.getOrdinal());*/
        switch (typeOrdinal) {
            case 1: typeValueEnum = TypeEventValue.BACKSPACE;
                break;
            case 81: typeValueEnum = TypeEventValue.DELETE;
                break;
            default: typeValueEnum = TypeEventValue.POSITIVE;
        }
        event.consume();
    }

    private void drawOnKeyTyped(KeyEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        String newTextString;
        switch (typeValueEnum) {
            case BACKSPACE: {
                String textString = activeText.getText();
                newTextString = textString.substring(0, textString.length() - 1);
                break;
            }
            case POSITIVE:
            default: {
                newTextString = activeText.getText() + event.getCharacter();
            }
        }
        activeText.setText(newTextString);
        //event.consume();
    }

    private void drawOnMousePressed(MouseEvent ev, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        if (isTextWriting) {
            isTextWriting = false;
            return;
        }
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
                    config.getDrawingAreaContext().getCanvas().requestFocus();
                    event.consume();
                });
            }
            isDrawing = true;
            if (nodeTree.get(SECONDARY).get(SHAPE_NAMESPACE + 0) == null) {
                renderTree.get(SECONDARY).put(SHAPE_NAMESPACE + 0, activeTextBounds);
                nodeTree.get(SECONDARY).putIfAbsent(SHAPE_NAMESPACE + 0, activeTextBounds);
            }
            anchors[0] = createAnchorPoint(ev.getX(), ev.getY(), 0, renderTree);
            anchors[1] = createAnchorPoint(ev.getX() + activeTextBounds.getWidth(),
                    ev.getY() + activeTextBounds.getHeight(), 1, renderTree);
        }

        if (!nodeTree.get(SECONDARY).containsValue(anchors[0])){
            nodeTree.get(SECONDARY).putIfAbsent(anchors[0].getId(), anchors[0]);
            nodeTree.get(SECONDARY).putIfAbsent(anchors[1].getId(), anchors[1]);
            renderTree.get(SECONDARY).put(anchors[0].getId(), anchors[0]);
            renderTree.get(SECONDARY).put(anchors[1].getId(), anchors[1]);
            nodeTree.get(SECONDARY).replace(anchors[0].getId(), anchors[0]);
            nodeTree.get(SECONDARY).replace(anchors[1].getId(), anchors[1]);
        }
    }
    private Circle createAnchorPoint(double x, double y, int anchorCounter, Map<String, LinkedHashMap<String, Node>> renderTree) {
        Circle anchor = (Circle) nodeTree.get(SECONDARY).getOrDefault("point_" + 0 + "_" + anchorCounter, new Circle(x, y, config.getStrokeWidth()+4));
        anchor.setCenterX(x);
        anchor.setCenterY(y);
        anchor.setFill(Color.TRANSPARENT);
        anchor.setStroke(Color.GRAY);
        anchor.setId("point_0" + "_" + anchorCounter);
        anchor.setOnMousePressed(MouseEvent::consume);
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
                Circle anchor2 = (Circle) nodeTree.get(SECONDARY).get("point_0" + "_" + 1);
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
            activeText.setWrappingWidth(activeTextBounds.getWidth());
            event.consume();
        });
        anchor.setOnMouseEntered(event -> anchor.setCursor(Cursor.CROSSHAIR));
        return anchor;
    }

    private void drawOnMouseDragged(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        if (!isDrawing) return;
        if (anchors[1].getCursor() == Cursor.TEXT) return;
        activeTextBounds.setX(Math.min(mouseStartPointX, event.getX()));
        activeTextBounds.setY(Math.min(mouseStartPointY, event.getY()));
        activeTextBounds.setWidth(Math.abs(mouseStartPointX - event.getX()));
        activeTextBounds.setHeight(Math.abs(mouseStartPointY - event.getY()));
        anchors[1].setCenterX(event.getX());
        anchors[1].setCenterY(event.getY());
    }

    private void drawOnMouseReleased(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        if (!isTextWriting) {
            Spinner<Double> fontSizeSpinner = getOptions().getFontSizeSpinner();
            if (activeTextBounds.getWidth() < 5 && activeTextBounds.getHeight() < 5) {
                activeTextBounds.setWidth(30);
                activeTextBounds.setHeight(fontSizeSpinner.getValue() + 10);
            }
            Text text = new Text();
            text.setX(activeTextBounds.getX());
            text.setY(activeTextBounds.getY() + text.getFont().getSize());
            text.setFont(Font.font(fontSizeSpinner.getValue()));
            activeText = text;
            activeText.setWrappingWidth(activeTextBounds.getWidth());
            config.setSelectedNode(activeText);
            shapeCounter++;
            text.setId(SHAPE_NAMESPACE + shapeCounter);
            renderTree.get(PRIMARY).put(SHAPE_NAMESPACE + shapeCounter, text);
            nodeTree.get(PRIMARY).put(SHAPE_NAMESPACE + shapeCounter, text);
            config.getDrawingAreaContext().getCanvas().requestFocus(); // so that key events can work.
            text.setOnMouseEntered(textEvent -> {
                text.setCursor(Cursor.TEXT);
            });
            text.setOnMousePressed(textEvent -> {
                DrawPane drawingArea = config.getDrawingAreaContext();
                if (!drawingArea.getCanvas().isFocused()) drawingArea.getCanvas().requestFocus();

                TreeMap<String, LinkedHashMap<String, Node>> anotherRenderTree = new TreeMap<>();
                anotherRenderTree.put(PRIMARY, new LinkedHashMap<>());
                anotherRenderTree.put(SECONDARY, new LinkedHashMap<>());

                onButtonClick();
                resetSelectBoundParameter(text);
                anotherRenderTree.get(SECONDARY).put(SHAPE_NAMESPACE + 0, activeTextBounds);
                anotherRenderTree.get(SECONDARY).put(anchors[0].getId(), anchors[0]);
                anotherRenderTree.get(SECONDARY).put(anchors[1].getId(), anchors[1]);
                drawingArea.foreignRender(anotherRenderTree);
                activeText = text;
                config.setSelectedNode(text);
                textEvent.consume();
            });
            text.setOnMouseReleased(MouseEvent::consume);
            if (isDrawing) isTextWriting = true; // if activeTextBounds is being redrawn, we enter this mode.
        }
        TextSelectData textSelectData = (activeText.getUserData() == null) ? new TextSelectData() : (TextSelectData) activeText.getUserData();
        double selectX = activeTextBounds.getX(), selectY = activeTextBounds.getY();
        textSelectData.setBounds(selectX, selectY, activeTextBounds.getWidth(),
                activeTextBounds.getHeight());
        activeText.setUserData(textSelectData);
        isDrawing = false;
    }

    private void resetSelectBoundParameter(Text text) {
        TextSelectData selectData = (TextSelectData) text.getUserData();
        activeTextBounds.setX(selectData.getX());
        activeTextBounds.setY(selectData.getY());
        activeTextBounds.setWidth(selectData.getWidth());
        activeTextBounds.setHeight(selectData.getHeight());
        anchors[0].setCenterX(selectData.getX());
        anchors[0].setCenterY(selectData.getY());
        anchors[1].setCenterX(selectData.getX() + selectData.getWidth());
        anchors[1].setCenterY(selectData.getY() + selectData.getHeight());
    }

    @Override
    public void setCurrentToolbarOptions(DrawableButtonTool tool) {
        //toolOptionsPanel.getItems().clear();
        getOptions().switchToolOptions(toolOptionsPanel.getItems(), tool.getId());
    }

    @Override
    public TextOptions getOptions() {
        return optionButtonsBuilder;
    }

    @Override
    public <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> unDraw(EventType<T> eventType, T event){
        //Keep in mind that the draw method will run before this one. That means booleans can be weird here.
         if (eventType == MouseEvent.MOUSE_PRESSED) {
            TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
            renderTree.put(PRIMARY, new LinkedHashMap<>());
            renderTree.put(SECONDARY, new LinkedHashMap<>());

            if (activeText != null && activeText.getText().isEmpty()){
                LinkedHashMap<String, Node> primaryNodeTree = nodeTree.get(PRIMARY);
                Text textNode = (Text) primaryNodeTree.remove(SHAPE_NAMESPACE + shapeCounter); //removes empty text from nodeTree
                renderTree.get(PRIMARY).put(SHAPE_NAMESPACE + shapeCounter, textNode);
                shapeCounter--;
            }
            return renderTree;
        }
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
        config.getDrawingAreaContext().removeSecondaryNodeFromShapes(renderTree);
        config.setSelectedNode(null);
    }

    public final class TextOptions extends OptionButtonsBuilder{

        ComboBox<String> fontComboBox;
        Spinner<String> fontSizeSpinner;

        private TextOptions(GlobalDrawPaneConfig config){
            super(config);
            double fieldWidth = 20;
            HBox fontSizeBox = createFontSizeToolEntry(fieldWidth);
            HBox fontBox = createFontToolEntry(fieldWidth);

            Separator separator = new Separator(Orientation.VERTICAL);

            LinkedHashMap<String, Node> nodeList = nodeMap.getOrDefault(getId(), new LinkedHashMap<>());
            nodeList.put("font_box", fontBox);
            nodeList.put("font_size_box", fontSizeBox);
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

        private HBox createFontToolEntry(double fieldWidth) {
            ComboBox<String> fontComboBox = new ComboBox<>();
            fontComboBox.setFocusTraversable(false);
            fontComboBox.setPrefWidth(80);
            fontComboBox.getItems().addAll(Font.getFontNames());
            fontComboBox.getSelectionModel().select(Font.getDefault().getName());
            fontComboBox.setOnAction(event -> {
                if (activeText == null) return;
                String fontName = fontComboBox.getSelectionModel().getSelectedItem();
                Font currentFont = activeText.getFont();
                Font newFont = Font.font(fontName, FontWeight.LIGHT, FontPosture.REGULAR, currentFont.getSize());
                activeText.setFont(newFont);
            });
            this.fontComboBox = fontComboBox;

            HBox box = new HBox(new Text("font"), fontComboBox);
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(2));
            box.setBackground(new Background(new BackgroundFill(ToolbarButton.BUTTON_BACKGROUND_COLOR, null, null)));
            EventHandler<KeyEvent> keyHandler = event -> {
                if(nodeTree.get(PRIMARY).isEmpty()) return;

                try {
                    Font defaultFont = Font.getDefault();
                    activeText.setFont(Font.font(defaultFont.getName(), activeText.getFont().getSize()));
                } catch (NumberFormatException ex) {
                    System.out.println(">>Number Format error");
                }
            };
            fontComboBox.getEditor().setOnKeyReleased(keyHandler);
            return box;
        }

        private HBox createFontSizeToolEntry(double fieldWidth){
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

        Spinner<Double> getFontSizeSpinner () {
            return (Spinner<Double>) ((HBox) getNodes(getId()).get("font_size_box")).getChildren().get(1);
        }

    }

    private static class TextSelectData {
        private double x;
        private double y;
        private double width;
        private double height;

        public void setBounds(double selectX, double selectY, double width, double height) {
            this.x = selectX;
            this.y = selectY;
            this.width = width;
            this.height = height;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }
    }

    private enum TypeEventValue {
        BACKSPACE(1), DELETE(81), POSITIVE(0);

        TypeEventValue(int ordinal) {}
    }

}
