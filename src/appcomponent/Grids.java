package appcomponent;

import java.util.ArrayList;
import java.util.HashMap;

public enum Grids {
    NO_GRID(1, 1),

    GRID_1x2(1, 2),
    GRID_1x3(1, 3),
    GRID_1x4(1, 4),

    GRID_2x1(2, 1),
    GRID_2x2(2, 2),
    GRID_2x3(2, 3),
    GRID_2x4(2, 4),

    GRID_3x1(3, 1),
    GRID_3x2(3, 2),
    GRID_3x3(3, 3),
    GRID_3x4(3, 4),

    GRID_4x1(4, 1),
    GRID_4x2(4, 2),
    GRID_4x3(4, 3),
    GRID_4x4(4, 4);

    private final HashMap<String, ArrayList<Double>> XYCoordinates = new HashMap<>();
    private final int rows;
    private final int columns;

    Grids(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        XYCoordinates.put("x", new ArrayList<>());
        XYCoordinates.put("y", new ArrayList<>());
    }

    public HashMap<String, ArrayList<Double>> computeCoordinates(double width, double height) {
        XYCoordinates.get("x").clear();
        XYCoordinates.get("y").clear();
        double columnWidth = width / columns;
        double rowHeight = height / rows;
        for (int c = 1; c < columns; c++) XYCoordinates.get("x").add(c * columnWidth);
        for (int r = 1; r < rows; r++) XYCoordinates.get("y").add(r * rowHeight);
        return XYCoordinates;
    }
}
