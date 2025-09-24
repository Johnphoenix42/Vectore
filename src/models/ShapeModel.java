package models;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.io.Serializable;
import java.util.Optional;

public abstract class ShapeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private double x, y;
    private double strokeWidth;
    private String stroke;
    private String fill;

    public ShapeModel(Node shape){}

    public ShapeModel(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public abstract Shape createShape();

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public void setStroke(String stroke) {
        this.stroke = stroke;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getStroke() {
        return stroke;
    }

    public Optional<String> getFill() {
        return Optional.ofNullable(fill);
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }
}
