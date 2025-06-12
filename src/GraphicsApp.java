import appcomponent.AppMenuBar;
import appcomponent.DrawPane;
import appcomponent.SubToolsPanel;
import appcomponent.ToolsPanel;
import apputil.GlobalDrawPaneConfig;
import apputil.NewProjectModelConsumer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GraphicsApp extends Application {
    private static final Double WIDTH;
    private static final Double HEIGHT;
    static {
        Rectangle2D screenRect = Screen.getPrimary().getVisualBounds();
        WIDTH = screenRect.getWidth();
        HEIGHT = screenRect.getHeight();
    }

    @Override
    public void start(Stage primaryStage){
        VBox vBox = new VBox();
        Scene scene = new Scene(vBox, 800, 600);
        GlobalDrawPaneConfig drawPaneConfig = GlobalDrawPaneConfig.getInstance();
        AppMenuBar menuBar = new AppMenuBar();
        SubToolsPanel toolOptionsPanel = new SubToolsPanel();
        ToolsPanel sideToolsPanel = new ToolsPanel(drawPaneConfig, toolOptionsPanel);
        DrawPane drawingArea = new DrawPane(drawPaneConfig, WIDTH, HEIGHT);
        menuBar.getConsumer().setDrawPane(drawingArea);

        /*scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            drawingArea.setPrefWidth((Double) newValue);
        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            drawingArea.setPrefHeight((Double) newValue);
        });*/

        Button generateSvg = new Button("Generate SVG");
        Text text = new Text();
        generateSvg.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            text.setText(drawingArea.generateSVGHTML());
        });
        Spinner<Integer> spinner = new Spinner<>(0, 10, 5, 1);
        VBox rightSidePanel = new VBox(generateSvg, text, spinner);
        rightSidePanel.setPrefWidth(180);
        rightSidePanel.setMaxWidth(240);

        HBox hBox = new HBox();
        VBox drawAreaVBox = new VBox(toolOptionsPanel, drawingArea);
        HBox.setHgrow(drawAreaVBox, Priority.ALWAYS);
        VBox.setVgrow(drawingArea, Priority.ALWAYS);
        hBox.getChildren().addAll(sideToolsPanel, drawAreaVBox, rightSidePanel);


        ObservableList<Node> rootChildren = vBox.getChildren();
        rootChildren.addAll(menuBar, hBox);

        scene.setFill(Color.color(1, 1, 0.9));

        primaryStage.setScene(scene);
        primaryStage.setTitle("Vectore");
        primaryStage.show();
    }

    public static void main(String[] args) throws NoSuchMethodException {
        launch(args);
    }

}
