package models;

import javafx.scene.shape.Shape;

import java.io.Serializable;

public class Path extends ShapeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    public <T extends Shape> Path(T path) {
        super((javafx.scene.shape.Path) path);
        javafx.scene.shape.Path pathShape = (javafx.scene.shape.Path) path;
        setX(pathShape.getTranslateX());
        setY(pathShape.getTranslateY());
        setFill(pathShape.getFill());
        setStroke(pathShape.getStroke());
        setStrokeWidth(pathShape.getStrokeWidth());
    }

    public Path(double x, double y) {
        super(x, y);
    }

}
