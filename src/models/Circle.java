package models;

import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.io.Serializable;
import java.util.Optional;

public class Circle extends ShapeModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private double radius;

    public Circle(Node circle) {
        super(circle);
        //serializing
        javafx.scene.shape.Circle circleShape = (javafx.scene.shape.Circle) circle;
        setX(circleShape.getCenterX());
        setY(circleShape.getCenterY());
        setRadius(circleShape.getRadius());
        Optional.ofNullable(circleShape.getFill()).ifPresent(fill -> setFill(fill.toString()));
        setStroke(circleShape.getStroke().toString());
        setStrokeWidth(circleShape.getStrokeWidth());
    }

    public Circle(double x, double y) {
        super(x, y);
        this.radius = 5;
    }

    @Override
    public Shape createShape() {
        //deserializing
        javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(getX(), getY(), getRadius());
        getFill().ifPresent(fill -> circle.setFill(Paint.valueOf(fill)));
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
