package model;

import org.graphstream.graph.implementations.AbstractGraph;
import org.javatuples.Triplet;

public class InteriorNode extends GraphNode {

    public static final String INTERIOR_SYMBOL = "I";

    private final Triplet<Vertex, Vertex, Vertex> interiorsVertexes;

    private boolean R;

    public InteriorNode(AbstractGraph graph, String id, Vertex v1, Vertex v2, Vertex v3){
        super(graph, id, INTERIOR_SYMBOL, );
        interiorsVertexes = new Triplet<>(v1, v2, v3);
    }

}
