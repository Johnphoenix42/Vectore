package models;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.io.Serializable;

public class Rectangle extends ShapeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private double x, y;
    private double width, height;
    private Paint fill;
    private Paint stroke;

    public <T extends Shape> Rectangle(T rectangle) {
        super((javafx.scene.shape.Rectangle) rectangle);
        javafx.scene.shape.Rectangle rectangleShape = (javafx.scene.shape.Rectangle) rectangle;
        setX(rectangleShape.getX());
        setY(rectangleShape.getY());
        setWidth(rectangleShape.getWidth());
        setHeight(rectangleShape.getHeight());
    }

    public Rectangle(double x, double y) {
        super(x, y);
        this.x = x;
        this.y = y;
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
