package model;

import common.ElementAttributes;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.SingleNode;

public abstract class GraphNode extends SingleNode {

    private final String symbol;

    private final Point3d coordinates;

    protected GraphNode(AbstractGraph graph, String id, String symbol, double xCoordinate, double yCoordinate, double zCoordinate) {
        this(graph, id, symbol, new Point3d(xCoordinate, yCoordinate, zCoordinate));
    }

    protected GraphNode(AbstractGraph graph, String id, String symbol, Point3d coordinates) {
        super(graph, id);
        super.setAttribute(ElementAttributes.FROZEN_LAYOUT);
        this.symbol = symbol;
        this.coordinates = coordinates;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getXCoordinate() {
        return coordinates.getX();
    }

    public double getYCoordinate() {
        return coordinates.getY();
    }

    public double getZCoordinate() {
        return coordinates.getZ();
    }

    public Point3d getCoordinates(){
        return  coordinates;
    }
}
