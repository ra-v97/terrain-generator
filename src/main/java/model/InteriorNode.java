package model;

import org.graphstream.graph.implementations.AbstractGraph;
import org.javatuples.Triplet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InteriorNode extends GraphNode {

    private static final String INTERIOR_SYMBOL = "I";

    private final Triplet<Vertex, Vertex, Vertex> triangle;

    private final List<Vertex> associatedNodes = new LinkedList<>();

    private boolean R;

    public InteriorNode(AbstractGraph graph, String id, Vertex v1, Vertex v2, Vertex v3) {
        super(graph, id, INTERIOR_SYMBOL, getInteriorPosition(v1, v2, v3));
        triangle = new Triplet<>(v1, v2, v3);
        R = false;
    }

    public InteriorNode(AbstractGraph graph, String id, Vertex v1, Vertex v2, Vertex v3, Vertex... associatedNodes) {
        this(graph, id, v1, v2, v3);
        this.associatedNodes.addAll(Arrays.asList(associatedNodes));
    }

    public Triplet<Vertex, Vertex, Vertex> getTriangle(){
        return triangle;
    }

    public void setPartitionRequired(boolean partitionRequired) {
        R = partitionRequired;
    }

    public boolean isPartitionRequired() {
        return R;
    }

    public Triplet<Vertex, Vertex, Vertex> getTriangleVertexes() {
        return triangle;
    }

    public List<Vertex> getAssociatedNodes() {
        ModelGraph graph = (ModelGraph) this.getGraph();
        List<Vertex> between0and1 = graph.getVertexesBetween(this.triangle.getValue0(), this.triangle.getValue1());
        List<Vertex> between0and2 = graph.getVertexesBetween(this.triangle.getValue0(), this.triangle.getValue2());
        List<Vertex> between1and2 = graph.getVertexesBetween(this.triangle.getValue1(), this.triangle.getValue2());

        return Stream.of(between0and1, between0and2, between1and2)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static Point3d getInteriorPosition(Vertex v1, Vertex v2, Vertex v3) {
        return new Point3d(getInteriorXCoordinate(v1, v2, v3), getInteriorYCoordinate(v1, v2, v3), getInteriorZCoordinate(v1, v2, v3));
    }

    private static double getInteriorXCoordinate(Vertex v1, Vertex v2, Vertex v3) {
        return (v1.getXCoordinate() + v2.getXCoordinate() + v3.getXCoordinate()) / 3d;
    }

    private static double getInteriorYCoordinate(Vertex v1, Vertex v2, Vertex v3) {
        return (v1.getYCoordinate() + v2.getYCoordinate() + v3.getYCoordinate()) / 3d;
    }

    private static double getInteriorZCoordinate(Vertex v1, Vertex v2, Vertex v3) {
        return (v1.getZCoordinate() + v2.getZCoordinate() + v3.getZCoordinate()) / 3d;
    }
}
