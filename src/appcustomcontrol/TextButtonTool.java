package appcustomcontrol;

import appcomponent.DrawPane;
import apputil.GlobalDrawPaneConfig;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;

public abstract class TextButtonTool extends DrawableButtonTool {

    public static final String SHAPE_NAMESPACE = "text_";
    private final TextAreaInterface textAreaInterface;
    private boolean textMouseEventFlag = false;
    private Rectangle activeText = null;
    private boolean isDrawing = false;
    private double mouseStartPointX = 0, mouseStartPointY = 0;
    private int shapeCounter = 0;

    public TextButtonTool(GlobalDrawPaneConfig config) {
        super("Text", config);
        textAreaInterface = new TextAreaInterface(Font.font(
                "Arial", 20), 0, 0);
        nodeTree.put("primary", new LinkedHashMap<>());
        nodeTree.put("secondary", new LinkedHashMap<>());
    }

    @Override
    public <T extends InputEvent> TreeMap<String, LinkedHashMap<String, Node>> draw(EventType<T> eventType, T event) {
        if (eventType != MouseEvent.MOUSE_PRESSED) return null;
        TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
        renderTree.put("primary", new LinkedHashMap<>());
        renderTree.put("secondary", new LinkedHashMap<>());

        textMouseEventFlag = !textMouseEventFlag;
        if (textMouseEventFlag) {
            textAreaInterface.x = ((MouseEvent) event).getX();
            textAreaInterface.y = ((MouseEvent) event).getY();
            TextArea textArea = getTextArea();
            textArea.setTranslateX(textAreaInterface.x);
            textArea.setTranslateY(textAreaInterface.y);
            nodeTree.get("secondary").put(textArea.getId(), textArea);
        } else {
            String textString = textAreaInterface.getTextArea().getText();
            if(textString.isEmpty()) return null;
            Text text = new Text(textString);
            text.setTranslateX(textAreaInterface.x);
            text.setTranslateY(textAreaInterface.y);
            text.setFont(textAreaInterface.font);
            textAreaInterface.getTextArea().setText("");
            textMouseEventFlag = !textMouseEventFlag;
            nodeTree.get("secondary").put(SHAPE_NAMESPACE + shapeCounter, text);
        }
        return renderTree;
    }

    @Override
    public OptionButtonsBuilder getOptions() {
        return optionButtonsBuilder;
    }

    @Override
    public <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> unDraw(EventType<T> eventType, T event){
        if (eventType != MouseEvent.MOUSE_PRESSED) return null;
        if (textMouseEventFlag) return null;
        TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
        LinkedHashMap<String, Node> nodeMap = new LinkedHashMap<>();
        TextArea textArea = textAreaInterface.getTextArea();
        nodeMap.put(textArea.getId(), textArea);
        renderTree.put("primary", nodeMap);
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

}
