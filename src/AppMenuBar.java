import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

import java.util.*;

public class AppMenuBar extends javafx.scene.control.MenuBar {

    private Map<String, LinkedHashMap<String, String[]>> menuTreeMap = new LinkedHashMap<>();

    public AppMenuBar(){
        super();

        Popup popup = new Popup();
        menuTreeMap.put("File", new LinkedHashMap<>());
        menuTreeMap.put("Edit", new LinkedHashMap<>());
        menuTreeMap.put("Help", new LinkedHashMap<>());
        populateFileMap();

        Set<Map.Entry<String, LinkedHashMap<String, String[]>>> firstLevelEntries =  menuTreeMap.entrySet();
        for(Map.Entry<String, LinkedHashMap<String, String[]>> firstLevelEntry : firstLevelEntries){
            Menu menu = new Menu(firstLevelEntry.getKey());
            Set<Map.Entry<String, String[]>> secondLevelEntries =  firstLevelEntry.getValue().entrySet();
            for(Map.Entry<String, String[]> secondLevelEntry : secondLevelEntries){
                MenuItem menuItem = null;
                if(secondLevelEntry.getValue().length == 1)
                    menuItem = new MenuItem(secondLevelEntry.getValue()[0]);
                else {
                    menuItem = new Menu(secondLevelEntry.getKey());
                    for(String menuName : secondLevelEntry.getValue()){
                        MenuItem thirdLevelItem = new MenuItem(menuName);
                        thirdLevelItem.setOnAction(event -> {
                            System.out.println("Secondary Action executed");
                        });
                        ((Menu) menuItem).getItems().add(new MenuItem(menuName));
                    }
                }
                menuItem.setOnAction(event -> {
                    if(popup.isShowing()) popup.hide();
                    BorderPane borderPane = new BorderPane(new Button("this"));
                    borderPane.setMinSize(400, 200);
                    borderPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
                    popup.getContent().add(borderPane);
                    popup.setWidth(400);
                    popup.setWidth(400);

                    popup.show(getScene().getWindow());
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

}
