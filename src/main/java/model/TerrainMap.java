package model;

import org.javatuples.Triplet;

import java.util.LinkedList;
import java.util.List;

public class TerrainMap {

    private static final double EPSILON = Double.MIN_VALUE;

    private final int sizeX;
    private final int sizeY;
    private final Point3d[][] points;

    public TerrainMap(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        points = new Point3d[this.sizeX][this.sizeY];
    }

    public void fillMapWithExampleData() {
        for (int i = 0; i < this.sizeX; i++) {
            for (int j = 0; j < this.sizeY; j++) {
                points[i][j] = new Point3d(i, j, 1.0);
            }
        }
    }

    public List<Point3d> getAllPointsInTriangleArea(InteriorNode triangle) {
        List<Point3d> trianglePoints = new LinkedList<>();
        int startIndexI;
        int startIndexJ;

        TriangleSquareBorder triangleSquareBorder = new TriangleSquareBorder(triangle);

        for (startIndexI = 1; startIndexI < sizeX && points[startIndexI][0].getY() < triangleSquareBorder.getMinY(); startIndexI++){}
        startIndexI--;

        for (startIndexJ = 1; startIndexJ < sizeY && points[0][startIndexJ].getX() < triangleSquareBorder.getMinX(); startIndexJ++){}
        startIndexJ--;

        for (int i = startIndexI; i < this.sizeX && points[i][0].getY() < triangleSquareBorder.getMaxY(); i++) {
            for (int j = startIndexJ; j < this.sizeY && points[0][j].getX() < triangleSquareBorder.getMaxX(); j++) {
                if (checkIfPointIsInsideTriangle(triangle, points[i][j])) {
                    trianglePoints.add(points[i][j]);
                }
            }
        }
        return trianglePoints;
    }

    private boolean checkIfPointIsInsideTriangle(InteriorNode triangle, Point3d point) {
        Triplet<Vertex, Vertex, Vertex> triangleVertexes = triangle.getTriangle();
        Point3d v1 = triangleVertexes.getValue0().getCoordinates();
        Point3d v2 = triangleVertexes.getValue1().getCoordinates();
        Point3d v3 = triangleVertexes.getValue2().getCoordinates();

        double arena = calculate2DTriangleArenaForXY(v1, v2, v3);
        double arena1 = calculate2DTriangleArenaForXY(point, v2, v3);
        double arena2 = calculate2DTriangleArenaForXY(point, v1, v3);
        double arena3 = calculate2DTriangleArenaForXY(point, v1, v2);
        return Math.abs(arena - (arena1 + arena2 + arena3)) < EPSILON;
    }

    private static double calculate2DTriangleArenaForXY(Point3d v1, Point3d v2, Point3d v3) {
        return Math.abs(v1.getX() * (v2.getY() - v3.getY()) + v2.getX() * (v3.getY() - v1.getY()) + v3.getX() * (v1.getY() - v2.getY()) / 2.0);
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public Point3d[][] getPoints() {
        return points;
    }

    public void addPoint(int x, int y, Point3d val) {
        points[x][y] = val;
    }

    public static final class TriangleSquareBorder {
        private final double minX;
        private final double maxX;
        private final double minY;
        private final double maxY;

        private TriangleSquareBorder(InteriorNode interiorNode) {
            Triplet<Vertex, Vertex, Vertex> triangleVertexes = interiorNode.getTriangleVertexes();
            Vertex v1 = triangleVertexes.getValue0();
            Vertex v2 = triangleVertexes.getValue1();
            Vertex v3 = triangleVertexes.getValue2();

            minX = Math.min(Math.min(v1.getXCoordinate(), v2.getXCoordinate()), v3.getXCoordinate());
            maxX = Math.max(Math.max(v1.getXCoordinate(), v2.getXCoordinate()), v3.getXCoordinate());
            minY = Math.min(Math.min(v1.getYCoordinate(), v2.getYCoordinate()), v3.getYCoordinate());
            maxY = Math.max(Math.max(v1.getYCoordinate(), v2.getYCoordinate()), v3.getYCoordinate());
        }

        public double getMinX() {
            return minX;
        }

        public double getMaxX() {
            return maxX;
        }

        public double getMinY() {
            return minY;
        }

        public double getMaxY() {
            return maxY;
        }
    }
}
