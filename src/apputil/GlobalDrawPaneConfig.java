package apputil;

import appcomponent.DrawPane;
import appcustomcontrol.DrawableButtonTool;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.*;

/**
 * A convenience class that hold state or current configuration values like strokeWidth, color, e.t.c.
 * Classes and methods that affect states get a static instance of this class and sets it values, which
 * other class can then access when they need to be notified or consider state update.
 */
public final class GlobalDrawPaneConfig implements Serializable {

    private Paint foregroundColor = Color.BLACK;
    private String curveType = "Quadratic";
    private double strokeWidth = 1;
    private DrawableButtonTool currentTool;
    private DrawableButtonTool prevSelectedTool;
    private Node selectedNode; // The one node on canvas that is active or currently selected.
    private static final GlobalDrawPaneConfig globalDrawPaneConfig = new GlobalDrawPaneConfig();
    private DrawPane.CanvasActionMode canvasActionMode;
    private Paint strokeColor = Color.BLACK;

    public static GlobalDrawPaneConfig getInstance(){
        return globalDrawPaneConfig;
    }

    public void setForegroundColor(Paint foregroundColor){
        this.foregroundColor = foregroundColor;
    }

    public Paint getForegroundColor(){
        return this.foregroundColor;
    }

    public void setCurveType(String curveType) {
        this.curveType = curveType;
    }

    public String getCurveType() {
        return curveType;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public DrawableButtonTool getCurrentTool() {
        return currentTool;
    }

    public void setCurrentTool(DrawableButtonTool currentTool) {
        this.currentTool = currentTool;
    }

    public void setPreviousTool(DrawableButtonTool prevSelectedButton) {
        this.prevSelectedTool = prevSelectedButton;
    }

    public DrawableButtonTool getPrevSelectedTool(){
        return prevSelectedTool;
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(Node selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void setActionMode(DrawPane.CanvasActionMode canvasActionMode) {
        this.canvasActionMode = canvasActionMode;
    }

    public DrawPane.CanvasActionMode getCanvasActionMode() {
        return canvasActionMode;
    }

    public Paint getStrokeColor() {
        return this.strokeColor;
    }

    public void setStrokeColor(Paint strokeColor) {
        this.strokeColor = strokeColor;
    }
}
