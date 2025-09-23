package models;

import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.io.Serializable;

public class Text extends ShapeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String labelString;
    private String fontName;
    private String fontFamily;
    private String style;
    private double fontSize;

    public Text(Node text) {
        super(text);
        javafx.scene.text.Text textShape = (javafx.scene.text.Text) text;
        setX(textShape.getX());
        setY(textShape.getY());
        Font font = textShape.getFont();
        setFontName(font.getName());
        setFontFamily(font.getFamily());
        setStyle(font.getStyle());
        setFontSize(font.getSize());
    }

    public Text(double x, double y){
        super(x, y);
        this.labelString = "";
    }

    @Override
    public Shape createShape() {
        javafx.scene.text.Text text = new javafx.scene.text.Text(getX(), getY(), getLabelString());
        text.setFont(Font.font(fontFamily, fontSize));
        return text;
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

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }
}
