package appcustomcontrol;

import apputil.GlobalDrawPaneConfig;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.security.Key;
import java.util.*;

public abstract class RectangleButtonTool extends DrawableButtonTool {

    public static final String SHAPE_NAMESPACE = "rectangle_";
    TreeMap<String, LinkedHashMap<String, Node>> nodeTree = new TreeMap<>();
    private Rectangle activeRectangle = null;
    private boolean isDrawing = false;
    private double mouseStartPointX = 0, mouseStartPointY = 0;
    private int shapeCounter = 0;

    public RectangleButtonTool(GlobalDrawPaneConfig config) {
        super("Rect", config);
        optionButtonsBuilder = new RectangleOptions(config);
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
        }
        return renderTree;
    }

    private void drawOnMousePressed(MouseEvent ev, TreeMap<String, LinkedHashMap<String, Node>> renderTree){
        if(!isDrawing) {
            mouseStartPointX = ev.getX();
            mouseStartPointY = ev.getY();
            Rectangle rectangle = new Rectangle(0, 0);
            rectangle.setTranslateX(mouseStartPointX);
            rectangle.setTranslateY(mouseStartPointY);
            rectangle.setFill(config.getForegroundColor());
            rectangle.setStrokeWidth(config.getStrokeWidth());
            rectangle.setStroke(super.config.getForegroundColor());
            activeRectangle = rectangle;

            LinkedHashMap<String, Node> nodeMap = nodeTree.get("primary");
            nodeMap.put(SHAPE_NAMESPACE + shapeCounter, rectangle);
            renderTree.get("primary").put(SHAPE_NAMESPACE + shapeCounter, rectangle);
            shapeCounter++;
            isDrawing = true;
        }else {
            activeRectangle.setTranslateX(Math.min(mouseStartPointX, ev.getX()));
            activeRectangle.setTranslateY(Math.min(mouseStartPointY, ev.getY()));
            activeRectangle.setWidth(Math.abs(mouseStartPointX - ev.getX()));
            activeRectangle.setHeight(Math.abs(mouseStartPointY - ev.getY()));
            isDrawing = false;
        }
    }

    private void drawOnMouseMoved(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        if (!isDrawing) return;
        activeRectangle.setTranslateX(Math.min(mouseStartPointX, event.getX()));
        activeRectangle.setTranslateY(Math.min(mouseStartPointY, event.getY()));
        activeRectangle.setWidth(Math.abs(mouseStartPointX - event.getX()));
        activeRectangle.setHeight(Math.abs(mouseStartPointY - event.getY()));
    }

    @Override
    public <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> unDraw(EventType<T> eventType, T event){
        return null;
    }

    @Override
    public RectangleOptions getOptions() {
        return (RectangleOptions) optionButtonsBuilder;
    }

    @Override
    public void addClickListener(DrawableButtonTool prevSelectedButton) {
        super.addClickListener(prevSelectedButton);
    }

    public final class RectangleOptions extends OptionButtonsBuilder{

        private RectangleOptions(GlobalDrawPaneConfig config){
            super(config);
            double fieldWidth = 40;
            HBox widthBox = createWidthToolEntry(fieldWidth);
            HBox heightBox = createHeightToolEntry(fieldWidth);
            HBox rotationBox = createRotationToolEntry(fieldWidth);

            Separator separator = new Separator(Orientation.VERTICAL);

            Button widthButton = new Button("Width");
            widthButton.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
            widthButton.setTextFill(Color.WHITE);
            widthButton.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
            Button heightButton = new Button("Height");
            heightButton.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);

            ArrayList<Node> nodeList = nodeMap.getOrDefault(getId(), new ArrayList<>());
            nodeList.add(widthBox);
            nodeList.add(heightBox);
            nodeList.add(rotationBox);
            nodeList.add(separator);
            nodeList.add(widthButton);
            nodeList.add(heightButton);
            nodeMap.put(getId(), nodeList);
        }
    }

    private HBox createFieldToolEntry(String labelString, double fieldWidth, EventHandler<KeyEvent> event){
        TextField textField = new TextField();
        textField.setMaxWidth(fieldWidth);
        textField.setOnKeyReleased(event);
        textField.setFocusTraversable(false);
        HBox box = new HBox(new Text(labelString), textField);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(2));
        box.setBackground(new Background(new BackgroundFill(ToolbarButton.BUTTON_BACKGROUND_COLOR, null, null)));
        return box;
    }

    private HBox createWidthToolEntry(double fieldWidth){
        EventHandler<KeyEvent> keyHandler = event -> {
            if(nodeTree.get("primary").isEmpty()) return;

            String textInput = sanitizeTextField(event);
            try {
                activeRectangle.setWidth(Double.parseDouble(textInput));
                activeRectangle.setFill(null);
            } catch (NumberFormatException ex) {
                activeRectangle.setWidth(activeRectangle.getWidth());
            }
        };
        return createFieldToolEntry("width", fieldWidth, keyHandler);
    }

    private HBox createHeightToolEntry(double fieldWidth){
        String labelString = "height";
        EventHandler<KeyEvent> keyHandler = event -> {
            if(nodeTree.get("primary").isEmpty()) return;

            String textInput = sanitizeTextField(event);
            try {
                activeRectangle.setHeight(Double.parseDouble(textInput));
            } catch (NumberFormatException ex) {
                activeRectangle.setHeight(activeRectangle.getHeight());
            }
        };
        return createFieldToolEntry(labelString, fieldWidth, keyHandler);
    }

    private HBox createRotationToolEntry(double fieldWidth){
        String labelString = "rotate";
        EventHandler<KeyEvent> keyHandler = event -> {
            if(nodeTree.get("primary").isEmpty()) return;

            String textInput = sanitizeTextField(event);
            try {
                activeRectangle.setRotate(Double.parseDouble(textInput));
            } catch (NumberFormatException ex) {
                activeRectangle.setHeight(activeRectangle.getHeight());
            }
        };
        return createFieldToolEntry(labelString, fieldWidth, keyHandler);
    }

    private String sanitizeTextField(KeyEvent event){
        TextField textField = ((TextField) event.getSource());
        String textInput = textField.getText();
        String inputtedCharacter = event.getText();
        if(inputtedCharacter.matches("\\D")) {
            textField.setText(textInput.split(inputtedCharacter)[0]);
            textField.positionCaret(4);
        }
        if(textInput.length() == 4)
            textField.setText(textInput.split(inputtedCharacter)[0]);
        return textInput;
    }
}
