package model;

import common.ElementAttributes;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.javatuples.Triplet;
import org.junit.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class ModelGraph extends MultiGraph {

    private Map<String, Vertex> vertexes = new HashMap<>();

    private Map<String, InteriorNode> interiors = new HashMap<>();

    private Map<String, GraphEdge> edges = new HashMap<>();

    public ModelGraph(String id) {
        super(id);
    }

    public Optional<GraphEdge> getEdgeBetweenNodes(Vertex v1, Vertex v2) {
        Edge edge = v1.getEdgeBetween(v2);
        Assert.assertNotNull("No edge between nodes: " + v1.getId() +" and: "+ v2.getId(), edge);
        return getEdgeById(edge.getId());
    }

    public Vertex insertVertex(Vertex vertex) {
        Node node = this.addNode(vertex.getId());
        node.addAttribute("ui.class", vertex.getVertexType().getSymbol());
        //node.addAttribute("ui.label", vertex.getId());
        node.setAttribute(ElementAttributes.FROZEN_LAYOUT);
        node.setAttribute(ElementAttributes.XYZ, vertex.getXCoordinate(), vertex.getYCoordinate(), vertex.getZCoordinate());
        vertexes.put(vertex.getId(), vertex);
        return vertex;
    }

    public Vertex insertVertex(String id, VertexType vertexType, Point3d coordinates) {
        Vertex vertex = new Vertex.VertexBuilder(this, id)
                .setVertexType(vertexType)
                .setCoordinates(coordinates)
                .build();
        insertVertex(vertex);
        return vertex;
    }

    public Optional<Vertex> getVertex(String id) {
        return Optional.ofNullable(vertexes.get(id));
    }

    public Collection<Vertex> getVertices() {
        return vertexes.values();
    }

    public Optional<Vertex> removeVertex(String id) {
        Vertex vertex = vertexes.remove(id);
        if (vertex != null) {
            this.removeVertex(id);
            interiors.entrySet().stream()
                    .filter(interior -> interior.getValue().getTriangleVertexes().contains(vertex))
                    .forEach(result -> removeInterior(result.getKey()));
            edges.values().stream()
                    .filter(graphEdge -> graphEdge.getEdgeNodes().contains(vertex))
                    .map(GraphEdge::getId)
                    .forEach(this::removeEdge);
            return Optional.of(vertex);
        }
        return Optional.empty();
    }

    public InteriorNode insertInterior(String id, Vertex v1, Vertex v2, Vertex v3, Vertex... associatedNodes) {
        InteriorNode interiorNode = new InteriorNode(this, id, v1, v2, v3, associatedNodes);
        Node node = this.addNode(interiorNode.getId());
        node.setAttribute(ElementAttributes.FROZEN_LAYOUT);
        node.setAttribute(ElementAttributes.XYZ, interiorNode.getXCoordinate(), interiorNode.getYCoordinate(), interiorNode.getZCoordinate());
        node.addAttribute("ui.class", "important");
        interiors.put(id, interiorNode);
        insertEdge(id.concat(v1.getId()), interiorNode, v1);
        insertEdge(id.concat(v2.getId()), interiorNode, v2);
        insertEdge(id.concat(v3.getId()), interiorNode, v3);
        return interiorNode;
    }

    public Optional<InteriorNode> getInterior(String id) {
        return Optional.ofNullable(interiors.get(id));
    }

    public Collection<InteriorNode> getInteriors() {
        return interiors.values();
    }

    public void removeInterior(String id) {
        List<String> edgesToRemove = edges.values().stream()
                .filter(graphEdge -> graphEdge.getEdgeNodes().contains(interiors.get(id)))
                .map(GraphEdge::getId)
                .collect(Collectors.toList());
        edgesToRemove
                .forEach(this::deleteEdge);
        interiors.remove(id);
        this.removeNode(id);
    }

    public GraphEdge insertEdge(String id, GraphNode n1, GraphNode n2) {
        GraphEdge graphEdge = new GraphEdge.GraphEdgeBuilder(id, n1, n2).build();
        this.addEdge(graphEdge.getId(), n1, n2);
        edges.put(graphEdge.getId(), graphEdge);
        return graphEdge;
    }

    public GraphEdge insertEdge(String id, GraphNode n1, GraphNode n2, boolean B) {
        GraphEdge graphEdge = new GraphEdge.GraphEdgeBuilder(id, n1, n2)
                .setB(B)
                .build();
        this.addEdge(graphEdge.getId(), n1, n2);
        edges.put(graphEdge.getId(), graphEdge);
        return graphEdge;
    }

    public void deleteEdge(GraphNode n1, GraphNode n2) {
        Edge edge = n1.getEdgeBetween(n2);
        deleteEdge(edge.getId());
    }

    public void deleteEdge(String edgeId){
        edges.remove(edgeId);
        this.removeEdge(edgeId);
    }

    public Optional<GraphEdge> getEdgeById(String id) {
        return Optional.ofNullable(edges.get(id));
    }

    public List<Vertex> getVertexesBetween(Vertex beginning, Vertex end) {
        if(beginning.getEdgeBetween(end) != null){
            return new LinkedList<>();
        }
        return this.vertexes
                .values()
                .stream()
                .filter(v -> isVertexBetween(v, beginning, end))
                .collect(Collectors.toList());
    }

    public Optional<Vertex> getVertexBetween(Vertex beginning, Vertex end) {
        return this.getVertexesBetween(beginning, end).stream().findFirst();
    }

    public GraphEdge getTriangleLongestEdge(InteriorNode interiorNode){
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangle();
        Vertex v1 = triangle.getValue0();
        Vertex v2 = triangle.getValue1();
        Vertex v3 = triangle.getValue2();

        GraphEdge edge1 = getEdgeBetweenNodes(v1, v2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge edge2 = getEdgeBetweenNodes(v2, v3)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge edge3 = getEdgeBetweenNodes(v1, v3)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));

        if(edge1.getL() >= edge2.getL() && edge1.getL() >= edge3.getL()) {
            return edge1;
        }else if(edge2.getL() >= edge3.getL()) {
            return edge2;
        }
        return edge3;
    }

    private boolean isVertexBetween(Vertex v, Vertex beginning, Vertex end) {
        double epsilon = .001;
        double xd = Math.abs(calculateInlineMatrixDeterminant(v, beginning, end));
        if(isVertexSameAs(v, beginning) || isVertexSameAs(v,end)){
            return false;
        } else return areCoordinatesMatching(v, beginning, end)
                && Math.abs(calculateInlineMatrixDeterminant(v, beginning, end)) < epsilon;
    }

    private boolean isVertexSameAs(Vertex a, Vertex b){
        return a.getCoordinates().equals(b.getCoordinates());
    }

    private boolean areCoordinatesMatching(Vertex v, Vertex beginning, Vertex end){
        return v.getXCoordinate() <= Math.max(beginning.getXCoordinate(), end.getXCoordinate())
                && v.getXCoordinate() >= Math.min(beginning.getXCoordinate(), end.getXCoordinate())
                && v.getYCoordinate() <= Math.max(beginning.getYCoordinate(), end.getYCoordinate())
                && v.getYCoordinate() >= Math.min(beginning.getYCoordinate(), end.getYCoordinate());
    }

    /*
    Basic matrix calculation to check if points are in line with each other
    The matrix looks like this:
    | a.x, a.y, a.z |
    | b.x, b.y, b.z |
    | c.x, c.y, c.z |

    so if we calculate det of that matrix and it is equal to 0 it means that all points are in straight line
     */
    private double calculateInlineMatrixDeterminant(Vertex v, Vertex beginning, Vertex end) {
        Point3d a = v.getCoordinates();
        Point3d b = beginning.getCoordinates();
        Point3d c = end.getCoordinates();

        return a.getX()*b.getY()*c.getZ()
                + a.getY()*b.getZ()*c.getX()
                + a.getZ()*b.getX()*c.getY()
                - a.getZ()*b.getY()*c.getX()
                - a.getX()*b.getZ()*c.getY()
                - a.getY()*b.getX()*c.getZ();
    }

    public Optional<GraphEdge> getEdge(Vertex v1, Vertex v2) {
        return Optional.ofNullable(edges.get(v1.getEdgeBetween(v2).getId()));
    }

    public Collection<GraphEdge> getEdges() {
        return edges.values();
    }

    public GraphEdge insertEdge(GraphEdge ge) {
        return insertEdge(ge.getId(), ge.getNode0(), ge.getNode1(), ge.getB());
    }

}
