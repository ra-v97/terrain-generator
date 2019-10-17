package model;

import common.ElementAttributes;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.SingleNode;

public abstract class GraphNode extends SingleNode {

    private final String symbol;

    private double xCoordinate;

    private double yCoordinate;

    private double zCoordinate;

    protected GraphNode(AbstractGraph graph, String id, String symbol, double xCoordinate, double yCoordinate, double zCoordinate) {
        super(graph, id);
        this.symbol = symbol;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.zCoordinate = zCoordinate;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getXCoordinate() {
        return xCoordinate;
    }

    public double getYCoordinate() {
        return yCoordinate;
    }

    public double getZCoordinate() {
        return zCoordinate;
    }

    public void setXCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
        this.setAttribute(ElementAttributes.X, xCoordinate);
    }

    public void setYCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
        this.setAttribute(ElementAttributes.Y, yCoordinate);
    }

    public void setZCoordinate(double zCoordinate) {
        this.zCoordinate = zCoordinate;
        this.setAttribute(ElementAttributes.Z, zCoordinate);
    }
}
