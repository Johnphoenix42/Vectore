package shapes;

import java.io.Serializable;

public class Circle extends javafx.scene.shape.Circle implements Serializable {
    public Circle(double x, double y, double r) {
        super(x, y, r);
    }

    public Circle(double r) {
        super(r);
    }

}
