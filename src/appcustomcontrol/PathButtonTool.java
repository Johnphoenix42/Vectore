package appcustomcontrol;

import appcomponent.DrawPane;
import appcomponent.SubToolsPanel;
import apputil.AppLogger;
import apputil.GlobalDrawPaneConfig;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import javax.vecmath.Vector2d;
import java.util.*;

public class PathButtonTool extends DrawableButtonTool {

    public static final String SHAPE_NAMESPACE = "path_";

    // A global reference of renderTree so it does not get garbage-collected;
    TreeMap<String, LinkedHashMap<String, Node>> globalRenderTree;

    ArrayList<Node> pathElementControls; //Exists to maintain reference to control_0 and control_1.
    private final PathOptions optionButtonsBuilder;
    private Path activePath = null;
    private int shapeCounter = 0;
    private int breakPointCounter = 0;

    // true as soon as a click or mouse-press is recorded with path tool active.
    private boolean isPathDrawing = false;

    // true when new path is ready to open. Without it, path open exactly where others close.
    private boolean isPathOpenReady = true;
    private double mousePointX = 0;
    private double mousePointY = 0;
    private boolean isLastPathElementRemoved = false;
    private boolean skipDrawBreakpoint = false;

    public PathButtonTool(GlobalDrawPaneConfig config, SubToolsPanel toolOptionsPanel) {
        super("Path", config, toolOptionsPanel);
        optionButtonsBuilder = new PathOptions(config);
        nodeTree = new TreeMap<>();
        nodeTree.put(PRIMARY, new LinkedHashMap<>());
        nodeTree.put(SECONDARY, new LinkedHashMap<>());
        globalRenderTree = new TreeMap<>();
        globalRenderTree.put(PRIMARY, new LinkedHashMap<>());
        globalRenderTree.put(SECONDARY, new LinkedHashMap<>());
        pathElementControls = new ArrayList<>(2);
    }

    <K,V> void merge(Map<K,V> mainMap, Map<K,V> auxilliaryMap){
        auxilliaryMap.forEach((k, v) -> {
            boolean containsKey = mainMap.containsKey(k);
            if(containsKey) return;
            mainMap.put(k, v);
        });
    }

    @Override
    public <T extends InputEvent> TreeMap<String, LinkedHashMap<String, Node>> draw(EventType<T> eventType, T event) {
        TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
        renderTree.put(PRIMARY, new LinkedHashMap<>());
        renderTree.put(SECONDARY, new LinkedHashMap<>());
        merge(renderTree.get(SECONDARY), globalRenderTree.get(SECONDARY));
        if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
            drawOnMousePressed((MouseEvent) event, renderTree);
            globalRenderTree.get(PRIMARY).clear();
            globalRenderTree.get(SECONDARY).clear();
        } else if (eventType == MouseEvent.MOUSE_DRAGGED) {
            drawOnMouseDragged((MouseEvent) event, renderTree);
        } else if (eventType == MouseEvent.MOUSE_MOVED) {
            drawOnMouseMoved((MouseEvent) event, renderTree);
        } else if (eventType == MouseEvent.MOUSE_RELEASED) {
            drawOnMouseReleased((MouseEvent) event, renderTree);
        }
        return renderTree;
    }

    @Override
    public <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> unDraw(EventType<T> eventType, T event){
        if (eventType == MouseEvent.MOUSE_PRESSED) {
            TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
            renderTree.put(PRIMARY, new LinkedHashMap<>());
            renderTree.put(SECONDARY, new LinkedHashMap<>());
            LinkedHashMap<String, Node> anchorsMap = nodeTree.get(SECONDARY);

            //isPathDrawing at this point means path is not drawing or not.
            if (isPathDrawing && isPathOpenReady) {

                /*
                This next block checks if a new Path has been created and the last path's control points
                remain active. If they are, it clears them all except for the first control point
                 */
                if (anchorsMap.containsKey("point_" + (shapeCounter - 1) + "_0")) {
                    Node newestNodeBreakPoint = anchorsMap.remove("point_" + shapeCounter + "_0");
                    renderTree.get(SECONDARY).putAll(anchorsMap);
                    anchorsMap.clear();
                    anchorsMap.put("point_" + shapeCounter + "_0", newestNodeBreakPoint);
                }
            }
            if (!isPathDrawing && isPathOpenReady) {
                for (int count = 0; count < pathElementControls.size(); count++) {
                    renderTree.get(SECONDARY).put("control_" + count, pathElementControls.get(count));
                }
            }
            return renderTree;
        }
        return null;
    }

    @Override
    public DrawableButtonTool.OptionButtonsBuilder getOptions() {
        return optionButtonsBuilder;
    }

    @Override
    public void addClickListener(DrawableButtonTool prevSelectedButton) {
        super.addClickListener(prevSelectedButton);
        TreeMap<String, LinkedHashMap<String, Node>> renderTree = new TreeMap<>();
        renderTree.put(PRIMARY, new LinkedHashMap<>());
        renderTree.put(SECONDARY, new LinkedHashMap<>());
        LinkedHashMap<String, Node> breakPointsMap = prevSelectedButton.nodeTree.get(SECONDARY);

        renderTree.get(SECONDARY).putAll(breakPointsMap);
        breakPointsMap.clear();
        DrawPane.removeSecondaryNodeFromShapes(renderTree);
        config.setSelectedNode(activePath);
    }

    private void drawOnMousePressed(MouseEvent ev, TreeMap<String, LinkedHashMap<String, Node>> renderTree){
        double x = ev.getX();
        double y = ev.getY();
        if(!isPathOpenReady) {
            isPathOpenReady = true;
            return;
        }
        LinkedHashMap<String, Node> nodeMap = nodeTree.get(PRIMARY);
        skipDrawBreakpoint = false;

        if(!isPathDrawing) {
            Path pathShape = new Path();
            pathShape.setId(SHAPE_NAMESPACE + shapeCounter);
            pathShape.setTranslateX(0);
            pathShape.setTranslateY(0);
            ObservableList<PathElement> pathElements = pathShape.getElements();
            pathElements.add(new MoveTo(x, y));
            LinkedHashMap<String, Node> staticGlobalOptions = optionButtonsBuilder.getNodes(OptionButtonsBuilder.GLOBAL_NODE_OPTIONS);
            boolean shouldFill = ((ToggleButton) staticGlobalOptions.get("fill_toggle_button")).isSelected();
            pathShape.setFill(shouldFill ? config.getForegroundColor() : null);
            boolean shouldStroke = ((ToggleButton) staticGlobalOptions.get("stroke_toggle_button")).isSelected();
            pathShape.setStroke(shouldStroke ? config.getForegroundColor(): null);
            pathShape.setStrokeWidth(config.getStrokeWidth());
            activePath = pathShape;
            nodeMap.put(SHAPE_NAMESPACE + shapeCounter, pathShape);
            pathElements.add(new LineTo(x, y));
            renderTree.get(PRIMARY).put(SHAPE_NAMESPACE + shapeCounter, pathShape);
            isPathDrawing = true;
            isLastPathElementRemoved = false;
            shapeCounter++;
            breakPointCounter = 0;
            config.setSelectedNode(pathShape);
            pathShape.setOnMouseClicked(event -> {
                config.setActionMode(DrawPane.CanvasActionMode.SELECT_MODE);
                //activePath = pathShape;
            });
        }else {
            Path pathShape = activePath;
            ObservableList<PathElement> pathElements = pathShape.getElements();
            PathElement latestPathElement = pathElements.get(pathElements.size() - 1);
            setPathElementPointXAndY(latestPathElement, x, y);
            pathElements.add(new LineTo(x, y));
            PathElement anchorPathElement = pathElements.get(0);
            if (anchorPathElement instanceof MoveTo) {
                MoveTo moveTo = (MoveTo) anchorPathElement;
                if (inRange(new Point2D(moveTo.getX(), moveTo.getY()), new Point2D(x, y))){
                    // code here simply checks if the mouse press occurs inside the break point circle of the first Move to.
                    // in other words, it ends the drawing of a path;
                    skipDrawBreakpoint = true;
                }
            }
            isLastPathElementRemoved = false;
        }
        mousePointX = x;
        mousePointY = y;

        Circle circle = createAnchorPoint(x, y, renderTree);
        if (skipDrawBreakpoint) {
            return;
        }
        nodeTree.get(SECONDARY).putIfAbsent(circle.getId(), circle);
        renderTree.get(SECONDARY).putIfAbsent(circle.getId(), circle);
        nodeTree.get(SECONDARY).replace(circle.getId(), circle);
        renderTree.get(SECONDARY).replace(circle.getId(), circle);
    }

    public static boolean inRange(Point2D origin, Point2D test){
        Vector2d vec = new Vector2d(origin.getX() - test.getX(), origin.getY() - test.getY());
        return vec.length() < 4;
    }

    /**
     * Create a path breakpoint node, i.e. a circle-represented point that received a click event,
     * and set it's onMousePressed event. Each breakpoint circle is also given an
     * id consisting of the  shapeCounter (how many paths this path tool has drawn),
     * and breakpointCounter variable, the latter of which is the zero-based index
     * of breakpoints drawn since the path started. The breakpoints id are in this format:
     * "point_0_0".
     * @param x the x pos to draw the breakpoint node.
     * @param y the y pos to draw the breakpoint node.
     * @param renderTree this node tree this tool gives draw pane to render.
     * @return the circle object which is the breakpoint node.
     */
    private Circle createAnchorPoint(double x, double y, Map<String, LinkedHashMap<String, Node>> renderTree) {
        Circle breakPoint = new Circle(x, y, config.getStrokeWidth()+4);
        breakPoint.setFill(Color.TRANSPARENT);
        breakPoint.setStroke(Color.GRAY);
        breakPoint.setId("point_" + shapeCounter + "_" + breakPointCounter);
        breakPointCounter++;
        breakPoint.setOnMousePressed(event -> {
            if(isPathDrawing) {
                if (breakPoint == nodeTree.get(SECONDARY).get("point_" + shapeCounter + "_0")){
                    isPathDrawing = false;
                    skipDrawBreakpoint = false;
                    //close the path.
                    ClosePath closePath = new ClosePath();
                    Path pathShape = activePath;
                    ObservableList<PathElement> pathShapeElementList = pathShape.getElements();
                    if(!isLastPathElementRemoved){
                        if(pathShapeElementList.get(pathShapeElementList.size() - 1) instanceof MoveTo) return;
                        //PathElement removedPathElement = pathShapeElementList.get(pathShapeElementList.size() - 1);
                        //pathShapeElementList.removeAll(removedPathElement);
                        PathElement newLastPathElement = pathShapeElementList.get(pathShapeElementList.size() - 1);
                        setPathElementPointXAndY(newLastPathElement, x, y);
                        //todo: add newLastPathElement into some kind of history for undo and redo operations
                        isLastPathElementRemoved = true;
                        pathShapeElementList.add(closePath);

                        //renderTree.get("secondary").get(circle.getId(), circle);
                    }
                } else { //circle clicked is not the first breakpoint
                    System.out.println("circle clicked is not the first breakpoint");
                }
            }
            else {
                ObservableList<PathElement> pathElements = activePath.getElements();
                short index = Short.parseShort(breakPoint.getId().split("_")[2]);
                try {
                    PathElement pathElement = pathElements.get(index);
                    LinkedHashMap<String, Node> secondaryMainTree = nodeTree.get(SECONDARY);
                    LinkedHashMap<String, Node> secondaryRenderTree = globalRenderTree.get(SECONDARY);
                    ///getting a reference to last controls before I replace them
                    pathElementControls.add(secondaryMainTree.get("control_0"));
                    pathElementControls.add(secondaryMainTree.get("control_1"));
                    secondaryRenderTree.put("control_0", null);
                    secondaryRenderTree.put("control_1", null);
                    secondaryMainTree.put("control_0", null);
                    secondaryMainTree.put("control_1", null);
                    if (pathElement instanceof MoveTo) {
                        pathElement = pathElements.get(pathElements.size() - 2);
                    }
                    if (pathElement instanceof QuadCurveTo) {
                        Circle controlPoint = getControlPoint((QuadCurveTo) pathElement);
                        secondaryRenderTree.replace("control_0", controlPoint);
                        secondaryMainTree.replace("control_0", controlPoint);
                    } else if (pathElement instanceof CubicCurveTo) {
                        CubicCurveTo cubicCurveTo = (CubicCurveTo) pathElement;
                        Circle controlPoint1 = getControlPoint(cubicCurveTo, new Point2D(cubicCurveTo.getControlX1(), cubicCurveTo.getControlY1()));
                        Circle controlPoint2 = getControlPoint(cubicCurveTo, new Point2D(cubicCurveTo.getControlX2(), cubicCurveTo.getControlY2()));
                        secondaryRenderTree.replace("control_0", controlPoint1);
                        secondaryRenderTree.replace("control_1", controlPoint2);
                        secondaryMainTree.replace("control_0", controlPoint1);
                        secondaryMainTree.replace("control_1", controlPoint2);
                    } else if (pathElement instanceof ArcTo) {
                        Circle controlPoint = getControlPoint((ArcTo) pathElement, 45, true, true);
                        secondaryRenderTree.replace("control_0", controlPoint);
                        secondaryMainTree.replace("control_0", controlPoint);
                    }
                }catch (IndexOutOfBoundsException e) {
                    AppLogger.log(getClass(), 221, "index = " + 4 + " is >= " + pathElements.size());
                }
                //event.consume();
            }
            isPathOpenReady = false;
        });
        breakPoint.setOnMouseDragged(event -> {
            if(isPathDrawing && !isPathOpenReady){
                AppLogger.log(getClass(), 282, "drag not executing");
                return;
            }
            double eventX = event.getX();
            double eventY = event.getY();
            breakPoint.setCenterX(event.getX());
            breakPoint.setCenterY(event.getY());
            Path pathShape = activePath;
            ObservableList<PathElement> pathShapeElementList = pathShape.getElements();
            short index = Short.parseShort(breakPoint.getId().split("_")[2]);
            if(!isLastPathElementRemoved) {
                PathElement pathElement = pathShapeElementList.get(index);
                setPathElementPointXAndY(pathElement, eventX, eventY);
                if (pathElement instanceof MoveTo){
                    PathElement secondToLastElement = pathShapeElementList.get(pathShapeElementList.size() - 2);
                    setPathElementPointXAndY(secondToLastElement, eventX, eventY);
                }
            }
        });
        breakPoint.setOnMouseEntered(event -> {
            breakPoint.setCursor(Cursor.CROSSHAIR);
        });
        return breakPoint;
    }

    private <T extends PathElement> void setPathElementPointXAndY(T pathElement, double x, double y){
        if (pathElement instanceof MoveTo) {
            ((MoveTo) pathElement).setX(x);
            ((MoveTo) pathElement).setY(y);
        }else if (pathElement instanceof LineTo){
            ((LineTo) pathElement).setX(x);
            ((LineTo) pathElement).setY(y);
        } else if (pathElement instanceof QuadCurveTo) {
            ((QuadCurveTo) pathElement).setX(x);
            ((QuadCurveTo) pathElement).setY(y);
        } else if (pathElement instanceof CubicCurveTo) {
            ((CubicCurveTo) pathElement).setX(x);
            ((CubicCurveTo) pathElement).setY(y);
        } else if (pathElement instanceof ArcTo) {
            ((ArcTo) pathElement).setX(x);
            ((ArcTo) pathElement).setY(y);
        }
    }

    private void drawOnMouseMoved(MouseEvent event, TreeMap<String, LinkedHashMap<String, Node>> renderTree) {
        double x = event.getX();
        double y = event.getY();
        if (!isPathDrawing) return;
        Path pathShape = activePath;
        ObservableList<PathElement> pathElements = pathShape.getElements();
        PathElement pathElement = pathElements.get(pathElements.size() - 1);
        setPathElementPointXAndY(pathElement, x, y);
    }

    private void drawOnMouseDragged(MouseEvent ev, TreeMap<String, LinkedHashMap<String, Node>> renderTree){
        if(!isPathDrawing) {
            return;
        }
        double x = ev.getX();
        double y = ev.getY();
        Path pathShape = activePath;
        ObservableList<PathElement> pathShapeElementList = pathShape.getElements();
        if(!isLastPathElementRemoved){
            if(pathShapeElementList.get(pathShapeElementList.size() - 1) instanceof MoveTo) return;
            boolean pathElement = switchPathElementCurveType(pathShapeElementList.size() - 1, x, y);
        }else {
            PathElement curvePathElement = pathShapeElementList.get(pathShapeElementList.size() - 1);
            if (curvePathElement instanceof QuadCurveTo){
                ((QuadCurveTo) curvePathElement).setX(x);
                ((QuadCurveTo) curvePathElement).setY(y);
                ((QuadCurveTo) curvePathElement).setControlX(x);
                ((QuadCurveTo) curvePathElement).setControlY(y);
            }else if (curvePathElement instanceof  CubicCurveTo) {
                ((CubicCurveTo) curvePathElement).setX(x);
                ((CubicCurveTo) curvePathElement).setY(y);
                ((CubicCurveTo) curvePathElement).setControlX1(x);
                ((CubicCurveTo) curvePathElement).setControlY1(y);
                ((CubicCurveTo) curvePathElement).setControlX2((x + mousePointX) / 2);
                ((CubicCurveTo) curvePathElement).setControlY2((y + mousePointY) / 2);
            }else if (curvePathElement instanceof  ArcTo) {
                ((ArcTo) curvePathElement).setX(x);
                ((ArcTo) curvePathElement).setY(y);
                ((ArcTo) curvePathElement).setRadiusX(Math.abs(mousePointX - x));
                ((ArcTo) curvePathElement).setRadiusY(Math.abs(mousePointY - y));
            }
        }
    }

    private boolean switchPathElementCurveType (int index, double ...coord) {
        ObservableList<PathElement> pathShapeElementList = activePath.getElements();
        pathShapeElementList.remove(index, pathShapeElementList.size());
        //todo: add pathElement into some kind of history for undo and redo operations
        isLastPathElementRemoved = true;
        PathElement curvePathElement = null;
        if(Objects.equals(config.getCurveType(), "Quadratic")) {
            curvePathElement = new QuadCurveTo(coord[0], coord[1], mousePointX, mousePointY);
        }else if(Objects.equals(config.getCurveType(), "Cubic")){
            curvePathElement = new CubicCurveTo(coord[0], coord[1], (coord[0] + mousePointX) / 2, (coord[1] + mousePointY) / 2, mousePointX, mousePointY);
        }else {
            curvePathElement = new ArcTo(Math.abs(mousePointX - coord[0]), Math.abs(mousePointY - coord[1]),
                    45, mousePointX, mousePointY, true, false);
        }
        return activePath.getElements().add(curvePathElement);
    }

    private void drawOnMouseReleased(MouseEvent ev, TreeMap<String, LinkedHashMap<String, Node>> renderTree){
        isLastPathElementRemoved = false;
    }

    private Circle getControlPoint(QuadCurveTo quadCurveTo) {
        Circle controlPoint = new Circle(3);
        controlPoint.setCenterX(Math.max(0, quadCurveTo.getControlX()));
        controlPoint.setCenterY(Math.max(0, quadCurveTo.getControlY()));
        controlPoint.setCenterX(Math.min(DrawPane.getPane().getWidth(), quadCurveTo.getControlX()));
        controlPoint.setCenterY(Math.min(DrawPane.getPane().getHeight(), quadCurveTo.getControlY()));

        controlPoint.setOnMousePressed(Event::consume);
        controlPoint.setOnMouseMoved(Event::consume);
        controlPoint.setOnMouseDragged(controlPointEvent -> {
            float eventX = (float) controlPointEvent.getX();
            float eventY = (float) controlPointEvent.getY();
            controlPoint.setCenterX(eventX);
            controlPoint.setCenterY(eventY);
            quadCurveTo.setControlX(eventX);
            quadCurveTo.setControlY(eventY);
            controlPointEvent.consume();
        });
        controlPoint.setOnMouseReleased(controlPointEvent -> {
            System.out.println(controlPointEvent.getY() + ", "+
                    controlPointEvent.getSceneY() + ", " + controlPointEvent.getScreenY());
        });
        return controlPoint;
    }

    private Circle getControlPoint(CubicCurveTo cubicCurveTo, Point2D p) {
        Circle controlPoint = new Circle(3);
        controlPoint.setCenterX(Math.max(0, p.getX()));
        controlPoint.setCenterY(Math.max(0, p.getY()));
        controlPoint.setCenterX(Math.min(DrawPane.getPane().getWidth(), p.getX()));
        controlPoint.setCenterY(Math.min(DrawPane.getPane().getHeight(), p.getY()));

        controlPoint.setOnMousePressed(Event::consume);
        controlPoint.setOnMouseMoved(Event::consume);
        controlPoint.setOnMouseDragged(controlPointEvent -> {
            float eventX = (float) controlPointEvent.getX();
            float eventY = (float) controlPointEvent.getY();
            controlPoint.setCenterX(eventX);
            controlPoint.setCenterY(eventY);
            cubicCurveTo.setControlX1(eventX);
            cubicCurveTo.setControlY1(eventY);
            controlPointEvent.consume();
        });
        return controlPoint;
    }

    private Circle getControlPoint(ArcTo arcTo, float xAxisRotation, boolean sweepFlag, boolean largeArcFlag) {
        Circle controlPoint = new Circle(3);
        controlPoint.setCenterX(Math.max(0, arcTo.getRadiusX()));
        controlPoint.setCenterY(Math.max(0, arcTo.getRadiusY()));
        controlPoint.setCenterX(Math.min(DrawPane.getPane().getWidth(), arcTo.getRadiusX()));
        controlPoint.setCenterY(Math.min(DrawPane.getPane().getHeight(), arcTo.getRadiusY()));

        controlPoint.setOnMousePressed(Event::consume);
        controlPoint.setOnMouseMoved(Event::consume);
        controlPoint.setOnMouseDragged(controlPointEvent -> {
            float eventX = (float) controlPointEvent.getX();
            float eventY = (float) controlPointEvent.getY();
            controlPoint.setCenterX(eventX);
            controlPoint.setCenterY(eventY);
            arcTo.setRadiusX(eventX);
            arcTo.setRadiusY(eventY);
            controlPointEvent.consume();
        });
        return controlPoint;
    }

    @Override
    public void setCurrentToolbarOptions(DrawableButtonTool tool) {
        getOptions().switchToolOptions(toolOptionsPanel.getItems(), getId());
    }

    public final class PathOptions extends OptionButtonsBuilder{

        private PathOptions(GlobalDrawPaneConfig config){
            super(config);
            config.setForegroundColor(config.getForegroundColor());
            config.setStrokeWidth(config.getStrokeWidth());
            LinkedHashMap<String, Node> optionNodes = createOptions();
            nodeMap.put(getId(), optionNodes);
        }

        private LinkedHashMap<String, Node> createOptions(){
            ComboBox<String> curveType = new ComboBox<>();
            curveType.getStyleClass().add("button");
            curveType.getItems().addAll("Arc", "Quadratic", "Cubic");
            curveType.getSelectionModel().select(1);
            curveType.setPrefSize(BUTTON_WIDTH + 20, BUTTON_HEIGHT - 10);
            curveType.setOnAction(event -> {
                String selectedItem = curveType.getSelectionModel().getSelectedItem();
                config.setCurveType(selectedItem);
            });

            /*ToggleButton strokeToggleButton = new ToggleButton("Stroke");
            strokeToggleButton.setOnAction(event -> {
                if (activePath == null) return;
                if (strokeToggleButton.isSelected()){
                    activePath.setStroke(config.getForegroundColor());
                }else{
                    activePath.setStroke(null);
                }
            });*/
            // ToggleGroup toggleGroup = new ToggleGroup();

            // fillToggleButton.setToggleGroup(toggleGroup);

            LinkedHashMap<String, Node> nodeList = nodeMap.getOrDefault(getId(), new LinkedHashMap<>());
            nodeList.put("curve_type", curveType);
            //nodeList.put("stroke_toggle_button", strokeToggleButton);
            return nodeList;
        }

        @Override
        public void switchToolOptions(ObservableList<Node> items, String newID) {
            super.switchToolOptions(items, newID);
        }
    }
}
