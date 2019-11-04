package processor;

import model.InteriorNode;
import model.Point3d;
import model.TerrainMap;
import model.Vertex;
import org.javatuples.Triplet;

public final class MapProcessingUtil {

    public static double calculateTerrainApproximationError(InteriorNode interiorNode, TerrainMap terrainMap){
        Triplet<Vertex, Vertex, Vertex> triangleVertexes = interiorNode.getTriangle();

        Plane plane = new Plane(triangleVertexes.getValue0().getCoordinates(),
                triangleVertexes.getValue1().getCoordinates(),
                triangleVertexes.getValue2().getCoordinates());

        double sumOfSquaresOfDifferenceBetweenEstimatedAndRealPoints =  terrainMap.getAllPointsInTriangleArea(interiorNode).stream()
                .map(point -> (point.getZ() - plane.getZCoordinate(point.getX(), point.getY())) * (point.getZ() - plane.getZCoordinate(point.getX(), point.getY())))
                .mapToDouble(Double::doubleValue)
                .sum();

        double  sumOfRealPointsSquares =  terrainMap.getAllPointsInTriangleArea(interiorNode).stream()
                .map(point -> point.getZ() * point.getZ())
                .mapToDouble(Double::doubleValue)
                .sum();
        if(sumOfRealPointsSquares == 0.0){
            throw new RuntimeException("Divide by zero");
        }
        return sumOfSquaresOfDifferenceBetweenEstimatedAndRealPoints / sumOfRealPointsSquares;
    }

    public static final class Plane{
       //PLane equation
       // ax + by + cz = d
        private final double coefficientA;
        private final double coefficientB;
        private final double coefficientC;
        private final double coefficientD;

        private Plane(Point3d p1, Point3d p2, Point3d p3){
            Vector3d v1 = new Vector3d(p1, p2);
            Vector3d v2 = new Vector3d(p1, p3);
            Vector3d crossProduct = v1.cross(v2);

            coefficientA = crossProduct.getVx();
            coefficientB = crossProduct.getVy();
            coefficientC = crossProduct.getVz();
            coefficientD = crossProduct.dot(p3);
        }

        private double getZCoordinate(double x0, double y0){
            if(coefficientC == 0){
                return 0;
            }
            return (coefficientD - coefficientA * x0 - coefficientB * y0) / coefficientC;
        }
    }

    private static final class Vector3d {

        private final double vx;

        private final double vy;

        private final double vz;

        private Vector3d(Point3d p1, Point3d p2){
            vx = p2.getX() - p1.getX();
            vy = p2.getY() - p1.getY();
            vz = p2.getZ() - p1.getZ();
        }

        private Vector3d(double vx, double vy, double vz){
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

        private Vector3d cross(Vector3d vector){
            double newVx = vy * vector.getVz() - vz * vector.getVy();

            double newVy = vz * vector.getVx() - vx * vector.getVz();

            double newVz = vx * vector.getVy() - vy * vector.getVx();

            return new Vector3d(newVx, newVy, newVz);
        }

        private double dot(Point3d point){
            return vx * point.getX() + vy * point.getY() + vx * point.getZ();
        }
    }
}
