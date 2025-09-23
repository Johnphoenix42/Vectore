package appcomponent;

import models.ShapeModel;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class VectoreProject implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LinkedHashMap<String, ShapeModel> canvasElementsList = new LinkedHashMap<>();

    private final String projectName;
    private int width, height;
    private boolean none = true;

    public VectoreProject(String name, int width, int height) {
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

    public LinkedHashMap<String, ShapeModel> getCanvasElementsList() {
        return canvasElementsList;
    }
}
