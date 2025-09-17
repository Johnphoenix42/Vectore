package appcomponent;

import apputil.GlobalDrawPaneConfig;
import apputil.NewProjectModelConsumer;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Popup;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class AppMenuBar extends MenuBar {

    private final Map<String, LinkedHashMap<String, MenuItem[]>> menuTreeMap = new LinkedHashMap<>();
    private final GlobalDrawPaneConfig globalConfig;
    private NewProjectModelConsumer consumer;

    public AppMenuBar(GlobalDrawPaneConfig config){
        super();
        this.globalConfig = config;

        menuTreeMap.put("File", new LinkedHashMap<>());
        menuTreeMap.put("Edit", new LinkedHashMap<>());
        menuTreeMap.put("Help", new LinkedHashMap<>());
        populateFileMap();

        Set<Map.Entry<String, LinkedHashMap<String, MenuItem[]>>> firstLevelEntries =  menuTreeMap.entrySet();
        for(Map.Entry<String, LinkedHashMap<String, MenuItem[]>> firstLevelEntry : firstLevelEntries){
            Menu menu = new Menu(firstLevelEntry.getKey());
            Set<Map.Entry<String, MenuItem[]>> secondLevelEntries =  firstLevelEntry.getValue().entrySet();
            for(Map.Entry<String, MenuItem[]> secondLevelEntry : secondLevelEntries){
                MenuItem menuItem;
                if(secondLevelEntry.getValue().length == 1) {
                    menuItem = secondLevelEntry.getValue()[0];
                    if (menuItem.getText().equals("New")) {
                        createNewProject();
                    } else if (menuItem.getText().equals("Open")) {
                        openFileChooser();
                    }
                } else {
                    menuItem = new Menu(secondLevelEntry.getKey());
                    for(MenuItem thirdLevelItem : secondLevelEntry.getValue()){
                        thirdLevelItem.setOnAction(event -> System.out.println("Secondary Action executed"));
                        ((Menu) menuItem).getItems().add(thirdLevelItem);
                    }
                }
                menu.getItems().add(menuItem);
            }
            getMenus().add(menu);
        }
    }

    private void openFileChooser() {
        MenuItem menuItem = menuTreeMap.get("File").get("Open")[0];

        menuItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("svg", "*.svg"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png", "*.png"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("jpg", "*.jpg"));
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("svg", "*.svg"));
            File openedFile = fileChooser.showOpenDialog(getScene().getWindow());
            final Desktop desktop = Desktop.getDesktop();
            try {
                if (Desktop.isDesktopSupported()) desktop.open(openedFile);
                Scanner scanner = new Scanner(openedFile);
                System.out.println("scanner.hasNext() = " + scanner.hasNext());
            } catch (IOException | NullPointerException e) {
                System.out.printf(">> Error: NullPointer or IOException:\t%s", e.getMessage());
            }
        });
    }

    private void populateFileMap(){
        LinkedHashMap<String, MenuItem[]> fileSubMap = menuTreeMap.get("File");
        fileSubMap.put("New", new MenuItem[]{new MenuItem("New")});
        fileSubMap.put("Open", new MenuItem[]{new MenuItem("Open")});
        fileSubMap.put("Recent", new MenuItem[]{new MenuItem("This"), new MenuItem("That"), new MenuItem("These"), new MenuItem("Those")});
        fileSubMap.put("Exit", new MenuItem[]{new MenuItem("Exit")});
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

    public void createNewProject() {
        MenuItem menuItem = menuTreeMap.get("File").get("New")[0];

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        Dialog<NewProjectModel> dialog = new Dialog<>();
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
                return new NewProjectModel(name, width, height);
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

    public NewProjectModelConsumer getConsumer() {
        return consumer;
    }


    public static class NewProjectModel {

        private final String projectName;
        private int width, height;
        private boolean none = true;

        NewProjectModel(String name, int width, int height) {
            projectName = name;
            this.width = width;
            this.height = height;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public void setNone(boolean none) {
            this.none = none;
        }

        public boolean isNone() {
            return none;
        }
    }

}
