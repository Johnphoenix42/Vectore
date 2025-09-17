import appcomponent.AppMenuBar;
import appcomponent.DrawPane;
import appcomponent.SubToolsPanel;
import appcomponent.ToolsPanel;
import apputil.GlobalDrawPaneConfig;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * While every effort is made to accurately reflect the behavior of the program, Vectore is under continuous development and the code may have changed since the time of this writing.
 * Todo: Change the stroke toggle button to a Spinner that define the stroke-width with 0 as min and 10 as max.
 *      When the color is set a dialog or popup should appear with at least 3 options to choose which property - fill, stroke or none, the
 *      color change is for or should affect.
 * Todo: switchPathElementCurveType() in PathButtonTool, shouldn't be adding to path element, should maybe be setting.
 *      also should not be using Combo box, <code>Spinner<String></code> is better.
 * Todo: focus on TextButtonTool and CircleButtonTool and after that, add a Select button.
 * Todo: Show color palette so users can switch between different color options they've chosen for their project (maybe with shortcuts too).
 * Todo: An export and import (svg) menu button.
 */
public class GraphicsApp extends Application {

    @Override
    public void start(Stage primaryStage){
        VBox vBox = new VBox();
        Scene scene = new Scene(vBox, 800, 600);
        TabPane drawingTabbedPane = new TabPane();
        drawingTabbedPane.autosize();
        drawingTabbedPane.setBackground(new Background(new BackgroundFill(Color.grayRgb(50), null, null)));
        drawingTabbedPane.setTabMaxHeight(Double.MAX_VALUE);
        GlobalDrawPaneConfig drawPaneConfig = GlobalDrawPaneConfig.getInstance();
        AppMenuBar menuBar = new AppMenuBar(drawPaneConfig);
        SubToolsPanel toolOptionsPanel = new SubToolsPanel();
        toolOptionsPanel.setDrawingTabbedPane(drawingTabbedPane);
        ToolsPanel sideToolsPanel = new ToolsPanel(drawPaneConfig, toolOptionsPanel);
        //sideToolsPanel.setTabbedPane(drawingTabbedPane);
        sideToolsPanel.setFocusTraversable(true);
        menuBar.getConsumer().setTabbedPane(drawingTabbedPane);

        /*scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            drawingArea.setPrefWidth((Double) newValue);
        });*/

        Button generateSvg = new Button("Generate SVG");
        TextArea htmlEditor = new TextArea();
        generateSvg.setOnAction(event -> {
            DrawPane drawPane = (DrawPane) drawingTabbedPane.getSelectionModel().getSelectedItem().getContent();
            String svgCode = drawPane.generateSVGHTML();
            htmlEditor.setText(svgCode);
        });
        VBox rightSidePanel = new VBox(generateSvg, htmlEditor);
        rightSidePanel.setPrefWidth(200);
        rightSidePanel.setMaxWidth(300);
        rightSidePanel.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        SplitPane splitPane = new SplitPane();
        VBox drawAreaVBox = new VBox(toolOptionsPanel, drawingTabbedPane);
        HBox.setHgrow(drawAreaVBox, Priority.ALWAYS);
        VBox.setVgrow(drawingTabbedPane, Priority.ALWAYS);
        splitPane.getItems().addAll(sideToolsPanel, drawAreaVBox, rightSidePanel);

        VBox.setVgrow(splitPane, Priority.ALWAYS);
        ObservableList<Node> rootChildren = vBox.getChildren();
        rootChildren.addAll(menuBar, splitPane);

        scene.setFill(Color.color(1, 1, 0.9));

        primaryStage.setScene(scene);
        primaryStage.setTitle("Vectore");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
