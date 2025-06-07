import apputil.GlobalDrawPaneConfig;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class GraphicsApp extends Application {
    private static final Double WIDTH;
    private static final Double HEIGHT;
    public static DrawPane drawingArea;
    static {
        Rectangle2D screenRect = Screen.getPrimary().getVisualBounds();
        WIDTH = screenRect.getWidth();
        HEIGHT = screenRect.getHeight();
    }

    @Override
    public void start(Stage primaryStage) throws  Exception{
        Group root = new Group();
        VBox vBox = new VBox();
        Scene scene = new Scene(vBox, 800, 600);
        GlobalDrawPaneConfig drawPaneConfig = GlobalDrawPaneConfig.getInstance();
        AppMenuBar menuBar = new AppMenuBar();
        SubToolsPanel toolOptionsPanel = new SubToolsPanel();
        ToolsPanel<InputEvent> sideToolsPanel = new ToolsPanel<>(drawPaneConfig, toolOptionsPanel);
        drawingArea = new DrawPane(drawPaneConfig, WIDTH, HEIGHT);

        /*scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            drawingArea.setPrefWidth((Double) newValue);
        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            drawingArea.setPrefHeight((Double) newValue);
        });*/

        VBox rightSidePanel = new VBox();
        rightSidePanel.setPrefWidth(180);

        HBox hBox = new HBox();
        VBox drawAreaVBox = new VBox(toolOptionsPanel, drawingArea);
        HBox.setHgrow(drawAreaVBox, Priority.ALWAYS);
        VBox.setVgrow(drawingArea, Priority.ALWAYS);
        hBox.getChildren().addAll(sideToolsPanel, drawAreaVBox, rightSidePanel);


        ObservableList<Node> rootChildren = vBox.getChildren();
        rootChildren.addAll(menuBar, hBox);

        scene.setFill(Color.color(1, 1, 0.9));

        primaryStage.setScene(scene);
        primaryStage.setTitle("Sign in");
        primaryStage.show();
    }

    public static void main(String[] args) throws NoSuchMethodException {
        launch(args);
        /*System.out.println(trimAndReplace("Mr  John Smith Mr  John Smith Mr  John Smith Mr  John Smith Mr  John Smith Mr  John Smith    "));
        System.out.println(trimAndReplace2("Mr  John Smith Mr  John Smith Mr  John Smith Mr  John Smith Mr  John Smith Mr  John Smith    "));
        System.out.println(Arrays.toString(GraphicsApp.class.getMethods()));*/
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface MyAnno{
        String str() default "Custom Anno";
        int val() default 2;
    }

    @MyAnno(str = "Meta")
    static String trimAndReplace(String input){
        double startTime = System.currentTimeMillis();
        boolean spaceFound = false;
        char[] inputArray = input.toCharArray();
        ArrayList<Character> outputArray = new ArrayList<>();
        for (char value : inputArray) {
            if (value == ' ') {
                spaceFound = true;
            } else {
                if (spaceFound) {
                    outputArray.add('%');
                    outputArray.add('2');
                    outputArray.add('0');
                }
                outputArray.add(value);
                spaceFound = false;
            }
        }
        Object[] cA = outputArray.toArray();
        StringBuilder builder = new StringBuilder();
        for(Object c: cA) builder.append(c);
        System.out.println(System.currentTimeMillis() - startTime);
        return new String(builder);
    }

    static String trimAndReplace2(String input){
        double startTime = System.currentTimeMillis();
        boolean spaceFound = false;
        char[] inputArray = input.toCharArray();
        char[] outputArray = new char[input.length() * 3];
        for(int i = 0, o = 0; i < inputArray.length; i++, o++){
            if(inputArray[i] == ' ') {
                spaceFound = true;
                o--;
            }else{
                if(spaceFound){
                    outputArray[o++] = '%';
                    outputArray[o++] = '2';
                    outputArray[o++] = '0';
                }
                outputArray[o] = inputArray[i];
                spaceFound = false;
            }
        }
        System.out.println(System.currentTimeMillis() - startTime);
        return new String(outputArray).trim();
    }
}
