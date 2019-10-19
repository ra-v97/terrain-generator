package common;

import model.GraphNode;
import org.javatuples.Triplet;

public class Utils {
    public static double distance(GraphNode n1, GraphNode n2) { // Move to GraphNode?
        double dx = n1.getXCoordinate() - n2.getXCoordinate();
        double dy = n1.getYCoordinate() - n2.getYCoordinate();
        double dz = n1.getZCoordinate() - n2.getZCoordinate();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static Triplet<Double, Double, Double> middlePoint(GraphNode n1, GraphNode n2) { // Move to GraphNode?
        double xs = n1.getXCoordinate() + n2.getXCoordinate();
        double ys = n1.getYCoordinate() + n2.getYCoordinate();
        double zs = n1.getZCoordinate() + n2.getZCoordinate();
        return new Triplet<>(xs / 2d, ys / 2d, zs / 2d);
    }
}
