package shapes;

import javafx.scene.paint.Paint;

import java.io.Serializable;

public class Rectangle extends javafx.scene.shape.Rectangle implements Serializable {

    private static final long serialVersionUID = 1L;

    private double x, y;
    private double width, height;
    private Paint fill;
    private Paint stroke;

    public Rectangle(double x, double y) {
        super(x, y);
        this.x = x;
        this.y = y;
    }

    public Rectangle() {}

    public void setRectX(double x) {
        super.setX(x);
        this.x = super.getX();
    }

    public void setRectY(double y) {
        super.setY(y);
        this.y = super.getY();
    }

    public void setRectWidth(double width) {
        super.setWidth(width);
        this.width = super.getWidth();
    }

    public void setRectHeight(double height) {
        super.setHeight(height);
        this.height = super.getHeight();
    }

    public double getRectX() {
        return super.getX();
    }

    public double getRectY() {
        return super.getY();
    }

    public double getRectWidth() {
        return super.getWidth();
    }

    public double getRectHeight() {
        return super.getHeight();
    }
}
