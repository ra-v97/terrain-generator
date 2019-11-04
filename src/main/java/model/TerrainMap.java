package model;

import org.javatuples.Triplet;

import java.util.LinkedList;
import java.util.List;

public class TerrainMap {

    private static final double EPSILON = Double.MIN_VALUE;

    private static final int MAP_HIGH = 100;

    private static final int MAP_WIGHT = 100;

    private final Point3d[][] terrainMap = new Point3d[MAP_HIGH][MAP_WIGHT];

    public List<Point3d> getAllPointsInTriangleArea(InteriorNode triangle){
        List<Point3d> trianglePoints = new LinkedList<>();
        for(int i = 0 ; i < MAP_HIGH ; i++){
            for(int j = 0 ; j < MAP_WIGHT ; j++) {
                if(checkIfPointIsInsideTriangle(triangle, terrainMap[i][j])){
                    trianglePoints.add(terrainMap[i][j]);
                }
            }
        }
        return trianglePoints;
    }

    private boolean checkIfPointIsInsideTriangle(InteriorNode triangle, Point3d point){
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

    private static double calculate2DTriangleArenaForXY(Point3d v1, Point3d v2, Point3d v3){
       return Math.abs(v1.getX()*(v2.getY()-v3.getY()) + v2.getX() * (v3.getY()-v1.getY()) + v3.getX() * (v1.getY()-v2.getY()) / 2.0);
    }
}
