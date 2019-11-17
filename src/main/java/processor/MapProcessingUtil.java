package processor;

import model.*;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MapProcessingUtil {

    /**
     * Creates model graph consisting of four points on the corners of a terrainMap.
     * The resulting rectangle is split in half by edges so that two triangles (interiors) are formed.
     */
    public static ModelGraph spanGraphOverTerrain(TerrainMap terrainMap) {
        ModelGraph graph = new ModelGraph("G");

        // add corners clockwise
        Vertex[] corners = new Vertex[4];
        corners[0] = graph.insertVertex("v1", VertexType.SIMPLE_NODE, terrainMap.getPoints()[0][0]);
        corners[1] = graph.insertVertex("v2", VertexType.SIMPLE_NODE, terrainMap.getPoints()[0][terrainMap.getSizeY() - 1]);
        corners[2] = graph.insertVertex("v3", VertexType.SIMPLE_NODE, terrainMap.getPoints()[terrainMap.getSizeX() - 1][terrainMap.getSizeY() - 1]);
        corners[3] = graph.insertVertex("v4", VertexType.SIMPLE_NODE, terrainMap.getPoints()[terrainMap.getSizeX() - 1][0]);

        // add border edges
        for (int i = 0; i < corners.length; i++) {
            int next = (i + 1) % corners.length;
            String edgeId = corners[i].getId().concat(corners[next].getId());
            graph.insertEdge(edgeId, corners[i], corners[next], true);
        }

        // add diagonal
        String diagonalId = corners[0].getId().concat(corners[2].getId());
        graph.insertEdge(diagonalId, corners[0], corners[2], false);

        // add interiors
        String idI012 = String.join("", corners[0].getId(), corners[1].getId(), corners[2].getId());
        graph.insertInterior(idI012, corners[0], corners[1], corners[2]);
        String idI230 = String.join("", corners[2].getId(), corners[3].getId(), corners[0].getId());
        graph.insertInterior(idI230, corners[2], corners[3], corners[0]);

        return graph;
    }

    public static List<InteriorNode> markTrianglesForRefinement(ModelGraph graph, TerrainMap terrainMap, double errorEps) {
        List<InteriorNode> trianglesForRefinement = graph.getInteriors().stream()
                .filter(i -> calculateTerrainApproximationError(i, terrainMap) > errorEps)
                .collect(Collectors.toList());
        trianglesForRefinement.forEach(i -> i.setPartitionRequired(true));
        return trianglesForRefinement;
    }

    public static double calculateTerrainApproximationError(InteriorNode interiorNode, TerrainMap terrainMap) {
        Triplet<Vertex, Vertex, Vertex> triangleVertexes = interiorNode.getTriangle();

        Plane plane = new Plane(triangleVertexes.getValue0().getCoordinates(),
                triangleVertexes.getValue1().getCoordinates(),
                triangleVertexes.getValue2().getCoordinates());

        double sumOfSquaresOfDifferenceBetweenEstimatedAndRealPoints = terrainMap.getAllPointsInTriangleArea(interiorNode).stream()
                .map(point -> (point.getZ() - plane.getZCoordinate(point.getX(), point.getY())) * (point.getZ() - plane.getZCoordinate(point.getX(), point.getY())))
                .mapToDouble(Double::doubleValue)
                .sum();

        double sumOfRealPointsSquares = terrainMap.getAllPointsInTriangleArea(interiorNode).stream()
                .map(point -> point.getZ() * point.getZ())
                .mapToDouble(Double::doubleValue)
                .sum();
        if (sumOfRealPointsSquares == 0.0) {
            throw new RuntimeException("Divide by zero");
        }
        return sumOfSquaresOfDifferenceBetweenEstimatedAndRealPoints / sumOfRealPointsSquares;
    }

    public static final class Plane {
        //PLane equation
        // ax + by + cz = d
        private final double coefficientA;
        private final double coefficientB;
        private final double coefficientC;
        private final double coefficientD;

        private Plane(Point3d p1, Point3d p2, Point3d p3) {
            Vector3d v1 = new Vector3d(p1, p2);
            Vector3d v2 = new Vector3d(p1, p3);
            Vector3d crossProduct = v1.cross(v2);

            coefficientA = crossProduct.getVx();
            coefficientB = crossProduct.getVy();
            coefficientC = crossProduct.getVz();
            coefficientD = crossProduct.dot(p3);
        }

        private double getZCoordinate(double x0, double y0) {
            if (coefficientC == 0) {
                return 0;
            }
            return (coefficientD - coefficientA * x0 - coefficientB * y0) / coefficientC;
        }
    }

    private static final class Vector3d {

        private final double vx;

        private final double vy;

        private final double vz;

        private Vector3d(Point3d p1, Point3d p2) {
            vx = p2.getX() - p1.getX();
            vy = p2.getY() - p1.getY();
            vz = p2.getZ() - p1.getZ();
        }

        private Vector3d(double vx, double vy, double vz) {
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
        }

        private double getVx() {
            return vx;
        }

        private double getVy() {
            return vy;
        }

        private double getVz() {
            return vz;
        }

        private Vector3d cross(Vector3d vector) {
            double newVx = vy * vector.getVz() - vz * vector.getVy();

            double newVy = vz * vector.getVx() - vx * vector.getVz();

            double newVz = vx * vector.getVy() - vy * vector.getVx();

            return new Vector3d(newVx, newVy, newVz);
        }

        private double dot(Point3d point) {
            return vx * point.getX() + vy * point.getY() + vz * point.getZ();
        }
    }
}
