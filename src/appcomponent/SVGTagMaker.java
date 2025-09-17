package appcomponent;

import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

public class SVGTagMaker {

    public String constructSvgElement(Rectangle rect, String tagName) {
        return "<" + tagName + " x=\"" + parse(rect.getX()) + "\" y=\"" + parse(rect.getY()) +
                "\" width=\"" + parse(rect.getWidth()) + "\" height=\"" + parse(rect.getHeight()) +
                "\" fill=\"" + (rect.getFill() != null ? colorConvertToHashHexadecimal(rect.getFill()) : "transparent") +
                (rect.getStroke() != null ? "\" stroke=\"" + colorConvertToHashHexadecimal(rect.getStroke()) : "") +
                "\" />";
    }

    public String constructSvgElement(Circle circle, String tagName) {
        return "<" + tagName + " cx=\"" + parse(circle.getCenterX()) + "\" cy=\"" + parse(circle.getCenterY()) +
                "\" radius=\"" + parse(circle.getRadius()) +
                "\" />";
    }

    public String constructSvgElement(Path path, String tagName) {
        String pathData = "";
        for (PathElement pathElement : path.getElements()) {
            if (pathElement instanceof MoveTo) {
                MoveTo moveTo = (MoveTo) pathElement;
                pathData = "m" + parse(moveTo.getX()) + " " + parse(moveTo.getY());
            } else if (pathElement instanceof LineTo) {
                LineTo lineTo = (LineTo) pathElement;
                pathData = "l" + parse(lineTo.getX()) + " " + parse(lineTo.getY());
            } else if (pathElement instanceof QuadCurveTo) {
                QuadCurveTo quadTo = (QuadCurveTo) pathElement;
                pathData = "q" + parse(quadTo.getControlX()) + " " + parse(quadTo.getControlY()) + " " + parse(quadTo.getX()) + " " + parse(quadTo.getY());
            } else if (pathElement instanceof CubicCurveTo) {
                CubicCurveTo cubicTo = (CubicCurveTo) pathElement;
                pathData = "c" + parse(cubicTo.getControlX1()) + " " + parse(cubicTo.getControlY1()) + " " +
                        parse(cubicTo.getControlX2()) + " " + parse(cubicTo.getControlY2())+ " " + parse(cubicTo.getX()) + " " + parse(cubicTo.getY());
            } else if (pathElement instanceof ArcTo) {
                ArcTo arcTo = (ArcTo) pathElement;
                pathData = "a" + parse(arcTo.getRadiusX()) + " " + parse(arcTo.getRadiusY()) + " " + parse(arcTo.getXAxisRotation()) + " " +
                        arcTo.isLargeArcFlag() + " " + parse(arcTo.getX()) + " " + parse(arcTo.getY());
            } else if (pathElement instanceof ClosePath) {
                pathData = "z";
            }
        }
        return "<" + tagName + " d=\"" + pathData +
                "\" fill=\"" + (path.getFill() != null ? colorConvertToHashHexadecimal(path.getFill()) : "transparent") +
                (path.getStroke() != null ? "\" stroke=\"" + colorConvertToHashHexadecimal(path.getStroke()) : "") +
                "\" />";
    }

    public String constructSvgElement(Text text, String tagName) {
        return "<" + tagName + " x=\"" + parse(text.getX()) + "\"" + " y=\"" + parse(text.getY()) +
                "\" fill=\"" + (text.getFill() != null ? colorConvertToHashHexadecimal(text.getFill()) : "transparent") +
                (text.getStroke() != null ? "\" stroke=\"" + colorConvertToHashHexadecimal(text.getStroke()) : "") +
                //"\"" + (text.rotate)
                "\" >" + text.getText() + "</text>";
    }

    String colorConvertToHashHexadecimal(Paint color) {
        return color.toString().replaceFirst("0x", "#");
    }

    String parse(double value) {
        return String.valueOf((int) value);
    }
}
