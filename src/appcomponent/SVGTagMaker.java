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
        StringBuilder pathData = new StringBuilder();
        for (PathElement pathElement : path.getElements()) {
            if (pathElement instanceof MoveTo) {
                MoveTo moveTo = (MoveTo) pathElement;
                pathData.append("m").append(parse(moveTo.getX())).append(" ").append(parse(moveTo.getY()));
            } else if (pathElement instanceof LineTo) {
                LineTo lineTo = (LineTo) pathElement;
                pathData.append("l").append(parse(lineTo.getX())).append(" ").append(parse(lineTo.getY()));
            } else if (pathElement instanceof QuadCurveTo) {
                QuadCurveTo quadTo = (QuadCurveTo) pathElement;
                pathData.append("q").append(parse(quadTo.getControlX())).append(" ").append(parse(quadTo.getControlY())).append(" ").append(parse(quadTo.getX())).append(" ").append(parse(quadTo.getY()));
            } else if (pathElement instanceof CubicCurveTo) {
                CubicCurveTo cubicTo = (CubicCurveTo) pathElement;
                pathData.append("c").append(parse(cubicTo.getControlX1())).append(" ").append(parse(cubicTo.getControlY1())).append(" ").append(parse(cubicTo.getControlX2())).append(" ").append(parse(cubicTo.getControlY2())).append(" ").append(parse(cubicTo.getX())).append(" ").append(parse(cubicTo.getY()));
            } else if (pathElement instanceof ArcTo) {
                ArcTo arcTo = (ArcTo) pathElement;
                pathData.append("a").append(parse(arcTo.getRadiusX())).append(" ").append(parse(arcTo.getRadiusY())).append(" ").append(parse(arcTo.getXAxisRotation())).append(" ").append(arcTo.isLargeArcFlag()).append(" ").append(parse(arcTo.getX())).append(" ").append(parse(arcTo.getY()));
            } else if (pathElement instanceof ClosePath) {
                pathData.append("z");
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
