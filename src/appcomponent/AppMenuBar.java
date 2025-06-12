package appcomponent;

import apputil.NewProjectModelConsumer;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import java.util.*;
import java.util.function.Consumer;

public class AppMenuBar extends MenuBar {

    private final Map<String, LinkedHashMap<String, String[]>> menuTreeMap = new LinkedHashMap<>();
    private final NewProjectModelConsumer consumer;

    public AppMenuBar(){
        super();

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        Dialog<NewProjectModel> dialog = new Dialog<>();
        dialog.setHeaderText("Create new Project");
        ObservableList<ButtonType> buttonTypeList = dialog.getDialogPane().getButtonTypes();
        buttonTypeList.add(cancelButtonType);
        buttonTypeList.add(createButtonType);

        consumer = new NewProjectModelConsumer();

        menuTreeMap.put("File", new LinkedHashMap<>());
        menuTreeMap.put("Edit", new LinkedHashMap<>());
        menuTreeMap.put("Help", new LinkedHashMap<>());
        populateFileMap();

        Set<Map.Entry<String, LinkedHashMap<String, String[]>>> firstLevelEntries =  menuTreeMap.entrySet();
        for(Map.Entry<String, LinkedHashMap<String, String[]>> firstLevelEntry : firstLevelEntries){
            Menu menu = new Menu(firstLevelEntry.getKey());
            Set<Map.Entry<String, String[]>> secondLevelEntries =  firstLevelEntry.getValue().entrySet();
            for(Map.Entry<String, String[]> secondLevelEntry : secondLevelEntries){
                MenuItem menuItem;
                if(secondLevelEntry.getValue().length == 1)
                    menuItem = new MenuItem(secondLevelEntry.getValue()[0]);
                else {
                    menuItem = new Menu(secondLevelEntry.getKey());
                    for(String menuName : secondLevelEntry.getValue()){
                        MenuItem thirdLevelItem = new MenuItem(menuName);
                        thirdLevelItem.setOnAction(event -> System.out.println("Secondary Action executed"));
                        ((Menu) menuItem).getItems().add(new MenuItem(menuName));
                    }
                }
                menuItem.setOnAction(event -> {
                    TextField widthTextField = new TextField();
                    HBox widthBox = new HBox(new Text("Width"), widthTextField);
                    widthBox.setAlignment(Pos.CENTER);
                    widthBox.setSpacing(25);
                    widthBox.setPadding(new Insets(10));
                    widthBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

                    TextField heightTextField = new TextField();
                    HBox heightBox = new HBox(new Text("Height"), heightTextField);
                    heightBox.setAlignment(Pos.CENTER);
                    heightBox.setSpacing(25);
                    heightBox.setPadding(new Insets(10));
                    heightBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

                    VBox borderPane = new VBox(widthBox, heightBox);
                    dialog.getDialogPane().setContent(borderPane);
                    dialog.setResultConverter(param -> {
                        int width = 0;
                        int height = 0;
                        try {
                            width = Integer.parseInt(widthTextField.getText());
                            height = Integer.parseInt(heightTextField.getText());
                        } catch (NumberFormatException e) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setContentText("Only numbers can be entered.");
                            alert.showAndWait().ifPresent(response -> {
                                widthTextField.clear();
                                heightTextField.clear();
                            });
                        }
                        return new NewProjectModel(width, height);
                    });

                    dialog.showAndWait()
                            .filter(response -> response.getWidth() > 1 && response.getHeight() > 1)
                            .ifPresent(consumer);

                    /*final Button btOk = (Button) dialog.getDialogPane().lookupButton(createButtonType);
                    btOk.addEventFilter(ActionEvent.ACTION, e -> {
                        if (!validateAndStore()) {
                            e.consume();
                        }
                        DrawPane.createCanvas()
                        System.out.println("Dialog closed successfully");
                    });
                    dialog.show();*/
                });
                menu.getItems().add(menuItem);
            }
            getMenus().add(menu);
        }
    }

    private void populateFileMap(){
        LinkedHashMap<String, String[]> fileSubMap = menuTreeMap.get("File");
        fileSubMap.put("New", new String[]{"New"});
        fileSubMap.put("Open", new String[]{"Open"});
        fileSubMap.put("Recent", new String[]{"This", "That", "These", "Those"});
        fileSubMap.put("Exit", new String[]{"Exit"});
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

    public NewProjectModelConsumer getConsumer() {
        return consumer;
    }

    public static class NewProjectModel {

        private int width, height;

        NewProjectModel(int width, int height) {
            this.width = width;
            this.height = height;
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
    }

}
