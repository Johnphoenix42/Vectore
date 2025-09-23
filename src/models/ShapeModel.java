package models;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

public class ShapeModel {

    private static final long serialVersionUID = 1L;

    private double x, y;
    private double strokeWidth;
    private Paint stroke;
    private Paint fill;

    public <T extends Shape> ShapeModel(T shape){}

    public ShapeModel(double x, double y) {
        this.x = x;
        this.y = y;
    }

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

    public void setFill(Paint fill) {
        this.fill = fill;
    }

    public void setStroke(Paint stroke) {
        this.stroke = stroke;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
}
