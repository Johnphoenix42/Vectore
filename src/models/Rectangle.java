package models;

import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.io.Serializable;
import java.util.Optional;

public class Rectangle extends ShapeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private double width, height;

    public Rectangle(Node rectangle) {
        super(rectangle);
        //serializing
        javafx.scene.shape.Rectangle rectangleShape = (javafx.scene.shape.Rectangle) rectangle;
        setX(rectangleShape.getX());
        setY(rectangleShape.getY());
        setWidth(rectangleShape.getWidth());
        setHeight(rectangleShape.getHeight());
        Optional.ofNullable(rectangleShape.getFill()).ifPresent(fill -> setFill(fill.toString()));
        setStroke(rectangleShape.getStroke().toString());
        setStrokeWidth(rectangleShape.getStrokeWidth());
    }

    public Rectangle(double x, double y) {
        super(x, y);
    }

    @Override
    public Shape createShape() {
        //deserializing
        javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle();
        rect.setX(getX());
        rect.setY(getY());
        rect.setWidth(getWidth());
        rect.setHeight(getHeight());
        getFill().ifPresent(fill -> rect.setFill(Paint.valueOf(fill)));
        rect.setStroke(Paint.valueOf(getStroke()));
        rect.setStrokeWidth(getStrokeWidth());
        return rect;
    }

    public Rectangle() {
        super(0, 0);
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
