package appcustomcontrol;

import appcomponent.DrawPane;
import apputil.GlobalDrawPaneConfig;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;

public abstract class TextButtonTool extends DrawableButtonTool {

    public static final String SHAPE_NAMESPACE = "text_";
    private final TextAreaInterface textAreaInterface;
    private boolean isTextDrawing = false;
    private final TextOptions optionButtonsBuilder;
    private Rectangle activeText = null;
    private double mouseStartPointX = 0, mouseStartPointY = 0;
    private int shapeCounter = 0;

    public TextButtonTool(GlobalDrawPaneConfig config) {
        super("Text", config);
        textAreaInterface = new TextAreaInterface(Font.font(
                "Arial", 20), 0, 0);
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
        }
        return renderTree;
    }

    private void drawOnMousePressed(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        mouseStartPointX = event.getX();
        mouseStartPointY = event.getY();
    }

    private void drawOnMouseDragged(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        double boxLength = Math.abs(event.getX() - mouseStartPointX);
        double boxDepth = Math.abs(event.getY() - mouseStartPointY);
        mouseStartPointX = Math.min(event.getX(), mouseStartPointX);
        mouseStartPointY = Math.min(event.getY(), mouseStartPointY);
    }

    private void drawOnMouseReleased(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        isTextDrawing = !isTextDrawing;
        if (isTextDrawing) {
            textAreaInterface.x = event.getX();
            textAreaInterface.y = event.getY();
            TextArea textArea = getTextArea();
            textArea.setTranslateX(textAreaInterface.x);
            textArea.setTranslateY(textAreaInterface.y);
            nodeTree.get(SECONDARY).put(textArea.getId(), textArea);
        } else {
            String textString = textAreaInterface.getTextArea().getText();
            if(textString.isEmpty()) return;
            Text text = new Text(textString);
            text.setTranslateX(textAreaInterface.x);
            text.setTranslateY(textAreaInterface.y);
            text.setFont(textAreaInterface.font);
            textAreaInterface.getTextArea().setText("");
            isTextDrawing = !isTextDrawing;
            nodeTree.get(SECONDARY).put(SHAPE_NAMESPACE + shapeCounter, text);
        }
    }

    @Override
    public TextOptions getOptions() {
        return optionButtonsBuilder;
    }

    @Override
    public <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> unDraw(EventType<T> eventType, T event){
        if (eventType != MouseEvent.MOUSE_PRESSED) return null;
        if (isTextDrawing) return null;
        TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
        LinkedHashMap<String, Node> nodeMap = new LinkedHashMap<>();
        TextArea textArea = textAreaInterface.getTextArea();
        nodeMap.put(textArea.getId(), textArea);
        renderTree.put(PRIMARY, nodeMap);
        return renderTree;
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
        DrawPane.removeSecondaryNodeFromShapes(renderTree);
        config.setSelectedNode(null);
    }

    private TextArea getTextArea() {
        TextArea textArea = textAreaInterface.getTextArea();
        textArea.setWrapText(true);
        textArea.requestFocus();
        textArea.setPromptText("Write Here");
        textArea.setFont(textAreaInterface.font);
        textArea.setTranslateX(textAreaInterface.x);
        textArea.setTranslateY(textAreaInterface.y);
        textArea.setMinSize(50, 75);
        textArea.setPrefSize(100, 150);
        textArea.setMaxSize(200, 100);
        return textArea;
    }

    private static class TextAreaInterface{

        private final static TextArea textArea = new TextArea();
        private double x;
        private double y;
        private final Font font;

        TextAreaInterface(Font font, double x, double y){
            textArea.setId("textarea");
            this.x = x;
            this.y = y;
            this.font = font;
        }

        TextArea getTextArea(){
            return textArea;
        }
    }

    public final class TextOptions extends OptionButtonsBuilder{

        private TextOptions(GlobalDrawPaneConfig config){
            super(config);
            double fieldWidth = 20;
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
                    activeText.setWidth(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                    activeText.setWidth(activeText.getWidth());
                }
            };
            numberSpinner.getEditor().setOnKeyReleased(keyHandler);
            numberSpinner.setValueFactory(new DoubleSpinnerValueFactory(numberSpinner, (SpinnerValueFactory<Double> valueFactory) -> {
                if (activeText == null) return;
                activeText.setWidth(valueFactory.getValue());
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
                    activeText.setHeight(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                    activeText.setHeight(activeText.getHeight());
                }
            };
            numberSpinner.getEditor().setOnKeyReleased(keyHandler);
            numberSpinner.setValueFactory(new DoubleSpinnerValueFactory(numberSpinner, (SpinnerValueFactory<Double> valueFactory) -> {
                if (activeText == null) return;
                activeText.setHeight(valueFactory.getValue());
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
                    activeText.setRotate(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                    activeText.setRotate(activeText.getRotate());
                }
            };
            numberSpinner.getEditor().setOnKeyReleased(keyHandler);
            numberSpinner.setValueFactory(new DoubleSpinnerValueFactory(numberSpinner, (SpinnerValueFactory<Double> valueFactory) -> {
                if (activeText == null) return;
                activeText.setRotate(valueFactory.getValue());
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
