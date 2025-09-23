package models;

import javafx.scene.shape.Shape;

import java.io.Serializable;

public class Text extends ShapeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String labelString;

    public <T extends Shape> Text(T text) {
        super(text);
        javafx.scene.text.Text textShape = (javafx.scene.text.Text) text;
        setX(textShape.getX());
        setY(textShape.getY());
    }

    public Text(double x, double y){
        super(x, y);
        this.labelString = "";
    }

    public Text(String labelString) {
        super(10, 10);
        this.labelString = labelString;
    }

    public String getLabelString() {
        return labelString;
    }

    public void setLabelString(String labelString) {
        this.labelString = labelString;
    }
}
