package appcustomcontrol;

import apputil.GlobalDrawPaneConfig;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.util.*;

public abstract class CircleButtonTool extends DrawableButtonTool {

    public static final String SHAPE_NAMESPACE = "circle_";
    TreeMap<String, LinkedHashMap<String, Node>> nodeTree = new TreeMap<>();
    CircleOptions optionButtonsBuilder;
    private Circle activeCircle = null;
    private boolean isDrawing = false;
    private double mouseStartPointX = 0, mouseStartPointY = 0;
    private int shapeCounter = 0;

    public CircleButtonTool(GlobalDrawPaneConfig config) {
        super("Circle", config);
        optionButtonsBuilder = new CircleOptions(config);
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
            LinkedHashMap<String, Node> staticGlobalOptions = optionButtonsBuilder.getNodes(OptionButtonsBuilder.GLOBAL_NODE_OPTIONS);
            boolean shouldFill = ((ToggleButton) staticGlobalOptions.get("fill_toggle_button")).isSelected();
            circle.setFill(shouldFill ? config.getForegroundColor() : null);
            circle.setStrokeWidth(config.getStrokeWidth());
            boolean shouldStroke = ((ToggleButton) staticGlobalOptions.get("stroke_toggle_button")).isSelected();
            circle.setStroke(shouldStroke ? config.getForegroundColor() : null);
            activeCircle = circle;

            LinkedHashMap<String, Node> nodeMap = nodeTree.get(PRIMARY);
            nodeMap.put(SHAPE_NAMESPACE + shapeCounter, circle);
            renderTree.get(PRIMARY).put(SHAPE_NAMESPACE + shapeCounter, circle);
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

        Node xCoordSpinnerSpinner = getOptions().getWidthSpinner();
        ((Spinner<Double>) xCoordSpinnerSpinner).getValueFactory().setValue(deltaX);
        Node yCoordSpinner = getOptions().getHeightSpinner();
        ((Spinner<Double>) yCoordSpinner).getValueFactory().setValue(deltaY);
        Node radiusSpinner = getOptions().getRadiusSpinner();
        ((Spinner<Double>) radiusSpinner).getValueFactory().setValue(radius);
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
    public void addClickListener(DrawableButtonTool prevSelectedButton) {
        super.addClickListener(prevSelectedButton);
    }

    public final class CircleOptions extends OptionButtonsBuilder{

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

        Spinner<Double> getWidthSpinner () {
            return (Spinner<Double>) (((HBox) getOptions().getNodes(getId()).get("x_coordinate_box")).getChildren().get(1));
        }

        Spinner<Double> getHeightSpinner () {
            return (Spinner<Double>) (((HBox) getOptions().getNodes(getId()).get("y_coordinate_box")).getChildren().get(1));
        }

        Spinner<Double> getRadiusSpinner() {
            return (Spinner<Double>) (((HBox) getOptions().getNodes(getId()).get("radius_box")).getChildren().get(1));
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
