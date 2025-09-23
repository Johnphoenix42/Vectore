package models;

import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.io.Serializable;

public class Path extends ShapeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    public Path(Node path) {
        super((javafx.scene.shape.Path) path);
        javafx.scene.shape.Path pathShape = (javafx.scene.shape.Path) path;
        setX(pathShape.getTranslateX());
        setY(pathShape.getTranslateY());
        setFill(pathShape.getFill().toString());
        setStroke(pathShape.getStroke().toString());
        setStrokeWidth(pathShape.getStrokeWidth());
    }

    public Path(double x, double y) {
        super(x, y);
    }

    @Override
    public Shape createShape() {
        javafx.scene.shape.Path path = new javafx.scene.shape.Path();
        path.setTranslateX(getX());
        path.setTranslateY(getY());
        path.setFill(Paint.valueOf(getFill()));
        path.setStroke(Paint.valueOf(getStroke()));
        path.setStrokeWidth(getStrokeWidth());
        return path;
    }

}
