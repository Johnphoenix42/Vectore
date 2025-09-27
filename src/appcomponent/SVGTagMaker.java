package appcomponent;

import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

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
                "\" fill=\"" + (circle.getFill() != null ? colorConvertToHashHexadecimal(circle.getFill()) : "transparent") +
                (circle.getStroke() != null ? "\" stroke=\"" + colorConvertToHashHexadecimal(circle.getStroke()) : "") +
                "\" />";
    }

    public String constructSvgElement(Path path, String tagName) {
        StringBuilder pathData = new StringBuilder();
        HashMap<String, BiFunction<PathElement, StringBuilder, StringBuilder>> pathElementHandlers = getPathElementHandlers();
        for (PathElement pathElement : path.getElements()) {
            pathData = pathElementHandlers.get(pathElement.getClass().getSimpleName()).apply(pathElement, pathData);
        }
        return "<" + tagName + " d=\"" + pathData +
                "\" fill=\"" + (path.getFill() != null ? colorConvertToHashHexadecimal(path.getFill()) : "transparent") +
                (path.getStroke() != null ? "\" stroke=\"" + colorConvertToHashHexadecimal(path.getStroke()) : "") +
                "\" />";
    }

    private HashMap<String, BiFunction<PathElement, StringBuilder, StringBuilder>> getPathElementHandlers() {
        HashMap<String, BiFunction<PathElement, StringBuilder, StringBuilder>> pathElementHandlers = new HashMap<>();
        pathElementHandlers.put(MoveTo.class.getSimpleName(), (pathElement, pData) -> {
            MoveTo moveTo = (MoveTo) pathElement;
            return pData.append("m").append(parse(moveTo.getX())).append(" ").append(parse(moveTo.getY()));
        });
        pathElementHandlers.put(LineTo.class.getSimpleName(), (pathElement, pData) -> {
            LineTo lineTo = (LineTo) pathElement;
            return pData.append("l").append(parse(lineTo.getX())).append(" ").append(parse(lineTo.getY()));
        });
        pathElementHandlers.put(VLineTo.class.getSimpleName(), (pathElement, pData) -> {
            VLineTo lineTo = (VLineTo) pathElement;
            return pData.append("v").append(" ").append(parse(lineTo.getY()));
        });
        pathElementHandlers.put(HLineTo.class.getSimpleName(), (pathElement, pData) -> {
            HLineTo lineTo = (HLineTo) pathElement;
            return pData.append("h").append(" ").append(parse(lineTo.getX()));
        });
        pathElementHandlers.put(QuadCurveTo.class.getSimpleName(), (pathElement, pData) -> {
            QuadCurveTo quadTo = (QuadCurveTo) pathElement;
            return pData.append("q").append(parse(quadTo.getControlX())).append(" ").append(parse(quadTo.getControlY())).append(" ").append(parse(quadTo.getX())).append(" ").append(parse(quadTo.getY()));
        });
        pathElementHandlers.put(CubicCurveTo.class.getSimpleName(), (pathElement, pData) -> {
            CubicCurveTo cubicTo = (CubicCurveTo) pathElement;
            return pData.append("c").append(parse(cubicTo.getControlX1())).append(" ").append(parse(cubicTo.getControlY1())).append(" ").append(parse(cubicTo.getControlX2())).append(" ").append(parse(cubicTo.getControlY2())).append(" ").append(parse(cubicTo.getX())).append(" ").append(parse(cubicTo.getY()));
        });
        pathElementHandlers.put(ArcTo.class.getSimpleName(), (pathElement, pData) -> {
            ArcTo arcTo = (ArcTo) pathElement;
            return pData.append("a").append(parse(arcTo.getRadiusX())).append(" ").append(parse(arcTo.getRadiusY())).append(" ").append(parse(arcTo.getXAxisRotation())).append(" ").append(arcTo.isLargeArcFlag()).append(" ").append(parse(arcTo.getX())).append(" ").append(parse(arcTo.getY()));
        });
        pathElementHandlers.put(ClosePath.class.getSimpleName(), (pathElement, pData) -> pData.append("z"));
        return pathElementHandlers;
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
