package model;

import org.graphstream.graph.implementations.AbstractGraph;
import org.javatuples.Triplet;

public class InteriorNode extends GraphNode {

    private static final String INTERIOR_SYMBOL = "I";

    private final Triplet<Vertex, Vertex, Vertex> interiorsVertexes;

    private boolean R;

    public InteriorNode(AbstractGraph graph, String id, Vertex v1, Vertex v2, Vertex v3) {
        super(graph, id, INTERIOR_SYMBOL, getInteriorXCoordinate(v1, v2, v3), getInteriorYCoordinate(v1, v2, v3), getInteriorZCoordinate(v1, v2, v3));
        interiorsVertexes = new Triplet<>(v1, v2, v3);
        R = false;
    }

    public void setPartitionRequired(boolean required) {
        R = required;
    }

    public boolean isPartitionRequired() {
        return R;
    }

    public Triplet<Vertex, Vertex, Vertex> getTriangleVertexes() {
        return interiorsVertexes;
    }

    private static double getInteriorXCoordinate(Vertex v1, Vertex v2, Vertex v3) {
        return (v1.getXCoordinate() + v2.getXCoordinate() + v3.getXCoordinate()) / 3.0;
    }

    private static double getInteriorYCoordinate(Vertex v1, Vertex v2, Vertex v3) {
        return (v1.getYCoordinate() + v2.getYCoordinate() + v3.getYCoordinate()) / 3.0;
    }

    private static double getInteriorZCoordinate(Vertex v1, Vertex v2, Vertex v3) {
        return (v1.getZCoordinate() + v2.getZCoordinate() + v3.getZCoordinate()) / 3.0;
    }
}
