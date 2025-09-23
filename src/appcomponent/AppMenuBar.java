package appcomponent;

import appcustomcontrol.DrawableButtonTool;
import apputil.GlobalDrawPaneConfig;
import apputil.NewProjectModelConsumer;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import models.*;

import java.io.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppMenuBar extends MenuBar {

    private final Map<String, LinkedHashMap<String, MenuItem[]>> menuTreeMap = new LinkedHashMap<>();
    private final GlobalDrawPaneConfig globalConfig;
    private NewProjectModelConsumer consumer;
    private TabPane drawingTabbedPane;

    public AppMenuBar(GlobalDrawPaneConfig config){
        super();
        this.globalConfig = config;
        LinkedHashMap<String, Shape> systemShapes = new LinkedHashMap<>();
        LinkedHashMap<String, ShapeModel> shapeModels = new LinkedHashMap<>();
        systemShapes.put("rect_0", new javafx.scene.shape.Rectangle(10, 10, 50, 50));
        mapShapesToModels(systemShapes, shapeModels);
        System.out.println(shapeModels);
        menuTreeMap.put("File", new LinkedHashMap<>());
        menuTreeMap.put("Edit", new LinkedHashMap<>());
        menuTreeMap.put("Help", new LinkedHashMap<>());
        populateFileMap();

        HashMap<String, Consumer<MenuItem>> methRef = new HashMap<>();
        methRef.put("New", this::createNewProject);
        methRef.put("Open", this::openFileChooser);
        methRef.put("Recent", menuItem -> menuItem.setOnAction(e -> System.out.println("Recent menu pressed")));
        methRef.put("Save", this::saveProject);
        methRef.put("Exit", menuItem -> menuItem.setOnAction(e -> System.exit(0)));

        Set<Map.Entry<String, LinkedHashMap<String, MenuItem[]>>> firstLevelEntries =  menuTreeMap.entrySet();
        for(Map.Entry<String, LinkedHashMap<String, MenuItem[]>> firstLevelEntry : firstLevelEntries){
            Menu menu = new Menu(firstLevelEntry.getKey());
            Set<Map.Entry<String, MenuItem[]>> secondLevelEntries =  firstLevelEntry.getValue().entrySet();
            for(Map.Entry<String, MenuItem[]> secondLevelEntry : secondLevelEntries){
                String menuName = secondLevelEntry.getKey();
                MenuItem menuItem = new MenuItem(menuName);
                MenuItem[] subMenus = secondLevelEntry.getValue();
                if(subMenus != null) {
                    menuItem = new Menu(menuName);
                    for (MenuItem subMenu : subMenus) {
                        ((Menu) menuItem).getItems().add(subMenu);
                    }
                }
                Consumer<MenuItem> menuExecutor = methRef.get(menuName);
                menuExecutor.accept(menuItem);
                menu.getItems().add(menuItem);
            }
            getMenus().add(menu);
        }
    }

    private void populateFileMap(){
        LinkedHashMap<String, MenuItem[]> fileSubMap = menuTreeMap.get("File");
        fileSubMap.put("New", null);
        fileSubMap.put("Open", null);
        fileSubMap.put("Recent", new MenuItem[]{new MenuItem("This"), new MenuItem("That"), new MenuItem("These"), new MenuItem("Those")});
        fileSubMap.put("Save", null);
        fileSubMap.put("Exit", null);
    }

    private void openFileChooser(MenuItem menuItem) {
        menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+O"));
        menuItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("vectore file", "*.vct"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("svg", "*.svg"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png", "*.png"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("jpg", "*.jpg"));
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("svg", "*.svg"));
            File openedFile = fileChooser.showOpenDialog(getScene().getWindow());
            //final Desktop desktop = Desktop.getDesktop();
            ObjectInputStream objectInputStream = null;
            try {
                //if (Desktop.isDesktopSupported()) desktop.open(openedFile);
                objectInputStream = new ObjectInputStream(new FileInputStream(openedFile));
                if (consumer == null) {
                    consumer = new NewProjectModelConsumer(globalConfig);
                }
                while (true) {
                    VectoreProject vectoreProject = (VectoreProject) objectInputStream.readObject();
                    LinkedHashMap<String, Node> elementsList = vectoreProject.getCanvasElementsList();
                    System.out.println(elementsList.get("rectangle_0"));
                    /*((Rectangle) elementsList.get("rectangle_0")).setRectHeight(100);*/

                    final DrawPane drawingArea = new DrawPane(globalConfig, 500, 200);
                    LinkedHashMap<String, LinkedHashMap<String, Node>> canvasTree = new LinkedHashMap<>();
                    canvasTree.put(DrawableButtonTool.PRIMARY, elementsList);
                    drawingArea.setFocusTraversable(true);
                    Pane canvasPane = drawingArea.createCanvas(vectoreProject.getWidth(), vectoreProject.getHeight());
                    drawingArea.getChildren().add(canvasPane);
                    drawingArea.addCoordinateText();
                    drawingArea.foreignRender(canvasTree);
                    drawingArea.addEventListeners(canvasPane);
                    if (drawingTabbedPane != null) {
                        Tab tab = new Tab(openedFile.getName(), drawingArea);
                        drawingTabbedPane.getTabs().add(tab);
                        drawingTabbedPane.getSelectionModel().select(tab);
                    }
                    GlobalDrawPaneConfig newConfig = (GlobalDrawPaneConfig) objectInputStream.readObject();
                    globalConfig.setSelectedNode(newConfig.getSelectedNode());
                }
            } catch (EOFException e) {
                System.out.println("File has been read into program");
            } catch (IOException | NullPointerException e) {
                System.out.printf(">> Error: NullPointer or IOException:\t%s", e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.WARNING, ex.getMessage());
                    }
                }
            }
        });
    }

    private void saveProject(MenuItem menuItem) {
        menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));

        menuItem.setOnAction(event -> {
            if (consumer.getVectoreProject() == null) return;
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(consumer.getVectoreProject().getProjectName());
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(".vct", "*.vct"));
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(".vct", "*.vct"));
            File savedFile = fileChooser.showSaveDialog(getScene().getWindow());
            if (savedFile == null) return;
            ObjectOutputStream objectOutputStream = null;
            try {
                objectOutputStream = new ObjectOutputStream(new FileOutputStream(savedFile));
                VectoreProject vectoreProject = consumer.getVectoreProject();
                DrawPane drawingPane = (DrawPane) drawingTabbedPane.getSelectionModel().getSelectedItem().getContent();
                vectoreProject.getCanvasElementsList().putAll(drawingPane.getGlobalPrimaryElements());
                objectOutputStream.writeObject(vectoreProject);
            } catch (FileNotFoundException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ">> No file found");
            } catch (NotSerializableException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ">> Input/Output error " + e.getMessage());
            } catch (IOException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
            } finally {
                if (objectOutputStream != null) {
                    try {
                        objectOutputStream.close();
                    } catch (IOException e) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, ">> Input/Output error on close");
                    }
                }
            }
        });
    }

    private static <T extends Shape, R extends ShapeModel> void mapShapesToModels(LinkedHashMap<String, T> from, LinkedHashMap<String, ShapeModel> to) {
        HashMap<String, Function<T, ? extends ShapeModel>> constructorMap = new HashMap<>();
        constructorMap.put("Rectangle", Rectangle::new);
        constructorMap.put("Circle", Circle::new);
        constructorMap.put("Path", Path::new);
        constructorMap.put("Text", Text::new);

        for (Map.Entry<String, T> entry: from.entrySet()) {
            T shape = entry.getValue();
            Function<T, ? extends ShapeModel> constructor = constructorMap.get(shape.getClass().getSimpleName());
            ShapeModel shapeModel = to.getOrDefault(entry.getKey(), constructor.apply(shape));
            System.out.println(shapeModel.getX());
        }
    }

    private void createAndShowPopup(){
        Popup popup = new Popup();
        if(popup.isShowing()) popup.hide();
        BorderPane borderPane = new BorderPane(new Button("this"));
        borderPane.setMinSize(400, 200);
        borderPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        popup.getContent().add(borderPane);
        popup.setWidth(400);
        popup.setWidth(400);

        popup.show(getScene().getWindow());
    }

    public void createNewProject(MenuItem menuItem) {
        menuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+N"));

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        Dialog<VectoreProject> dialog = new Dialog<>();
        dialog.setHeaderText("Create new Project");
        ObservableList<ButtonType> buttonTypeList = dialog.getDialogPane().getButtonTypes();
        buttonTypeList.add(cancelButtonType);
        buttonTypeList.add(createButtonType);

        consumer = new NewProjectModelConsumer(globalConfig);

        menuItem.setOnAction(event -> {
            TextField projectNameField = new TextField();
            Label projectNameLabel = new Label("Project name");
            projectNameLabel.setLabelFor(projectNameField);
            HBox nameBox = new HBox(projectNameLabel, projectNameField);
            nameBox.setAlignment(Pos.CENTER);
            nameBox.setSpacing(25);
            nameBox.setPadding(new Insets(10));
            nameBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

            Spinner<Integer> widthField = new Spinner<>(24, 5000, 400, 12);
            Label widthLabel = new Label("Width");
            widthLabel.setLabelFor(widthField);
            HBox widthBox = new HBox(widthLabel, widthField);
            widthBox.setAlignment(Pos.CENTER);
            widthBox.setSpacing(25);
            widthBox.setPadding(new Insets(10));
            widthBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

            Spinner<Integer> heightField = new Spinner<>(24, 5000, 400, 12);
            Label heightLabel = new Label("Height");
            heightLabel.setLabelFor(heightField);
            HBox heightBox = new HBox(heightLabel, heightField);
            heightBox.setAlignment(Pos.CENTER);
            heightBox.setSpacing(25);
            heightBox.setPadding(new Insets(10));
            heightBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

            VBox borderPane = new VBox(nameBox, widthBox, heightBox);
            dialog.getDialogPane().setContent(borderPane);
            dialog.setResultConverter(param -> {
                String name = "";
                int width = 0;
                int height = 0;
                try {
                    name = projectNameField.getText();
                    width = widthField.getValue();
                    height = heightField.getValue();
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Only numbers can be entered.");
                    alert.showAndWait().ifPresent(response -> {
                        widthField.getEditor().clear();
                        heightField.getEditor().clear();
                    });
                }
                return new VectoreProject(name, width, height);
            });

            final Button btOk = (Button) dialog.getDialogPane().lookupButton(createButtonType);
            btOk.setOnAction(e -> {
                dialog.getResult().setNone(false);
            });
            dialog.showAndWait()
                    .filter(response -> !response.isNone())
                    .filter(response -> response.getWidth() > 1 && response.getHeight() > 1)
                    .ifPresent(consumer);

            //dialog.show();
        });
    }

    public void setDrawingTabbedPane(TabPane drawingTabbedPane) {
        this.drawingTabbedPane = drawingTabbedPane;
    }

    public NewProjectModelConsumer getConsumer() {
        return consumer;
    }

}
