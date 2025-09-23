package models;

import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.io.Serializable;

public class Circle extends ShapeModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private double radius;

    public Circle(Node circle) {
        super(circle);
        javafx.scene.shape.Circle circleShape = (javafx.scene.shape.Circle) circle;
        setX(circleShape.getCenterX());
        setY(circleShape.getCenterY());
        setRadius(circleShape.getRadius());
    }

    public Circle(double x, double y) {
        super(x, y);
        this.radius = 5;
    }

    @Override
    public Shape createShape() {
        javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(getX(), getY(), getRadius());
        circle.setFill(Paint.valueOf(getFill()));
        circle.setStroke(Paint.valueOf(getStroke()));
        circle.setStrokeWidth(getStrokeWidth());
        return circle;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
