package models;

import appcomponent.SVGTagMaker;
import com.sun.javafx.geom.Path2D;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Path extends ShapeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String pathData;

    public Path(Node path) {
        super(path);
        javafx.scene.shape.Path pathShape = (javafx.scene.shape.Path) path;
        setX(pathShape.getTranslateX());
        setY(pathShape.getTranslateY());
        Optional.ofNullable(pathShape.getFill()).ifPresent(fill -> setFill(fill.toString()));
        setStroke(pathShape.getStroke().toString());
        setStrokeWidth(pathShape.getStrokeWidth());
        setPathData(new SVGTagMaker().constructSvgElement(pathShape, "path"));
    }

    public Path(double x, double y) {
        super(x, y);
    }

    public void setPathData(String pathData) {
        this.pathData = pathData;
    }

    @Override
    public Shape createShape() {
        javafx.scene.shape.Path path = new javafx.scene.shape.Path();
        path.setTranslateX(getX());
        path.setTranslateY(getY());
        getFill().ifPresent(fill -> path.setFill(Paint.valueOf(fill)));
        path.setStroke(Paint.valueOf(getStroke()));
        path.setStrokeWidth(getStrokeWidth());
        Path2D path2D = new Path2D();
        //path2D.getPathIterator(BaseTransform.IDENTITY_TRANSFORM).currentSegment();
        System.out.println("Setting up pattern");
        Pattern pattern = Pattern.compile("d=\"(.*?)\"");
        Matcher matcher = pattern.matcher(pathData);
        boolean found = matcher.find();
        if(!found) return path;
        String firstGroupString = matcher.group(1);
        System.out.println(firstGroupString);
        pattern = Pattern.compile("[a-zA-Z][^a-zA-Z]*");
        matcher = pattern.matcher(firstGroupString);
        while(matcher.find()){
            path.getElements().add(createPathElement(matcher.group()));
        }
        return path;
    }

    private PathElement createPathElement(String data) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(data);
        double[] values = new double[7];
        int count = 0;
        try {
            while (matcher.find()) {
                values[count++] = Double.parseDouble(matcher.group());
            }
            if (Stream.of('M', 'm').anyMatch(c -> c == data.charAt(0))) return new MoveTo(values[0], values[1]);
            else if (data.charAt(0) == 'l') return new LineTo(values[0], values[1]);
            else if (data.charAt(0) == 'q') return new QuadCurveTo(values[0], values[1], values[2], values[3]);
            else if (data.charAt(0) == 'c')
                return new CubicCurveTo(values[0], values[1], values[2], values[3], values[4], values[5]);
            else if (data.charAt(0) == 'a')
                return new ArcTo(values[0], values[1], values[2], values[5], values[6], values[3] == 1, values[4] == 1);
            else return new ClosePath();
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ">>check values array");
        }
        return new ClosePath();
    }
}
