package models;

import javafx.scene.shape.Shape;

import java.io.Serializable;

public class Circle extends ShapeModel implements Serializable {

    private double radius;

    public <T extends Shape> Circle(Shape circle) {
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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
