package appcustomcontrol;

import apputil.GlobalDrawPaneConfig;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

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
    public DrawableButtonTool.OptionButtonsBuilder getOptions() {
        return optionButtonsBuilder;
    }

    @Override
    public void addClickListener(DrawableButtonTool prevSelectedButton) {
        super.addClickListener(prevSelectedButton);
    }

    public final class RectangleOptions extends OptionButtonsBuilder{

        ToggleButton fillToggleButton;

        private RectangleOptions(GlobalDrawPaneConfig config){
            super(config);
            double fieldWidth = 20;
            HBox widthBox = createWidthToolEntry(fieldWidth);
            HBox heightBox = createHeightToolEntry(fieldWidth);
            HBox rotationBox = createRotationToolEntry(fieldWidth);

            Separator separator = new Separator(Orientation.VERTICAL);

            fillToggleButton = new ToggleButton("fill");
            fillToggleButton.setOnAction(event -> {
                if (activeRectangle == null) return;
                if (fillToggleButton.isSelected()){
                    activeRectangle.setFill(config.getForegroundColor());
                }else{
                    activeRectangle.setFill(null);
                }
            });
            ToggleButton strokeToggleButton = new ToggleButton("Stroke");
            strokeToggleButton.setOnAction(event -> {
                if (activeRectangle == null) return;
                if (strokeToggleButton.isSelected()){
                    activeRectangle.setStroke(config.getForegroundColor());
                }else{
                    activeRectangle.setStroke(null);
                }
            });

            ArrayList<Node> nodeList = nodeMap.getOrDefault(getId(), new ArrayList<>());
            nodeList.add(widthBox);
            nodeList.add(heightBox);
            nodeList.add(rotationBox);
            nodeList.add(separator);
            nodeList.add(fillToggleButton);
            nodeList.add(strokeToggleButton);
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
                if(nodeTree.get("primary").isEmpty()) return;

                String value = sanitizeTextField(numberSpinner, event);
                try {
                    activeRectangle.setWidth(Double.parseDouble(value));
                    activeRectangle.setFill(null);
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
                if(nodeTree.get("primary").isEmpty()) return;

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
                if(nodeTree.get("primary").isEmpty()) return;

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
        protected void setColorPickerOnAction(ColorPicker colorPicker, ToggleButton toggleButton) {
            super.setColorPickerOnAction(colorPicker, toggleButton);
            System.out.println("Rectangle");
            //if (activeRectangle == null) return;
            if (toggleButton.isSelected()) {
                activeRectangle.setFill(colorPicker.getValue());
            }else{
                activeRectangle.setFill(null);
            }
        }

        @Override
        public void switchToolOptions(ObservableList<Node> items, String newID) {
            super.switchToolOptions(items, newID);
            colorPicker.setOnAction(event -> {
                System.out.println("rectangle set on action");
                config.setSelectedNode(activeRectangle);
                setColorPickerOnAction(colorPicker, fillToggleButton);
            });
        }
    }

    private class DoubleSpinnerValueFactory extends SpinnerValueFactory<Double> {
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

            if (activeRectangle == null) return;
            activeRectangle.setRotate(valueFactory.getValue());
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
