package model;

import org.graphstream.graph.implementations.SingleGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModelGraph extends SingleGraph {

    private Map<String, Vertex> vertexes = new HashMap<>();

    private Map<String, InteriorNode> interiors = new HashMap<>();

    public ModelGraph(String id, boolean strictChecking, boolean autoCreate, int initialNodeCapacity, int initialEdgeCapacity) {
        super(id, strictChecking, autoCreate, initialNodeCapacity, initialEdgeCapacity);
    }

    public ModelGraph(String id, boolean strictChecking, boolean autoCreate) {
        super(id, strictChecking, autoCreate);
    }

    public ModelGraph(String id) {
        super(id);
    }

    public void insertVertex(Vertex vertex){
        this.addNode(vertex.getId());
        vertexes.put(vertex.getId(), vertex);
    }

    public Vertex insertVertex(String id, String symbol, VertexType vertexType, double x, double y, double z){
        Vertex vertex = new Vertex.VertexBuilder(this, id)
                .setSymbol(symbol)
                .setVertexType(vertexType)
                .setXCoordinate(x)
                .setYCoordinate(y)
                .setZCoordinate(z)
                .build();
        insertVertex(vertex);
        return vertex;
    }

    public Optional<Vertex> getVertex(String id){
        return Optional.ofNullable(vertexes.get(id));
    }

    public InteriorNode insertInterior(String ){

    }

    public Optional<InteriorNode> getInterior(String id){
        return Optional.ofNullable(interiors.get(id));
    }
}
