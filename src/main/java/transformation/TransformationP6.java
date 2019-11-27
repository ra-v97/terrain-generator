package transformation;

import model.*;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.LinkedList;
import java.util.List;

public class TransformationP6 implements Transformation {

    private static class InvalidProduction extends IllegalStateException {
    }

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        try {
            convertToProductionModel(graph, interiorNode);
        } catch (InvalidProduction invalidProduction) {
            return false;
        }

        return true;
    }

    private Pair<List<GraphEdge>, List<Vertex>>
    convertToProductionModel(ModelGraph graph, InteriorNode interiorNode) throws InvalidProduction {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangle();
        Vertex v0 = triangle.getValue0();
        Vertex v2 = triangle.getValue1();
        Vertex v4 = triangle.getValue2();
        Vertex v1 = this.getMiddleVertex(graph, v0, v2);
        Vertex v3 = this.getMiddleVertex(graph, v2, v4);
        Vertex v5 = this.getMiddleVertex(graph, v4, v0);

        LinkedList<Vertex> vertices = new LinkedList<>();
        vertices.add(v0);
        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);
        vertices.add(v4);
        vertices.add(v5);

        GraphEdge e0 = getEdgeBetween(graph, v0, v1);
        GraphEdge e1 = getEdgeBetween(graph, v1, v2);
        GraphEdge e2 = getEdgeBetween(graph, v2, v3);
        GraphEdge e3 = getEdgeBetween(graph, v3, v4);
        GraphEdge e4 = getEdgeBetween(graph, v4, v5);
        GraphEdge e5 = getEdgeBetween(graph, v5, v0);

        LinkedList<GraphEdge> edges = new LinkedList<>();
        edges.add(e0);
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);
        edges.add(e4);
        edges.add(e5);

        double base0 = e0.getL() + e1.getL();
        double base1 = e2.getL() + e3.getL();
        double base2 = e4.getL() + e5.getL();

        if (base1 > base0 && base1 > base2) {
            vertices.addLast(vertices.removeFirst());
            vertices.addLast(vertices.removeFirst());
            edges.addLast(edges.removeFirst());
            edges.addLast(edges.removeFirst());

        } else if (base2 > base0 && base2 > base1) {
            vertices.addFirst(vertices.removeLast());
            vertices.addFirst(vertices.removeLast());
            edges.addFirst(edges.removeLast());
            edges.addFirst(edges.removeLast());
        }

        return Pair.with(edges, vertices);
    }

    private static GraphEdge getEdgeBetween(ModelGraph modelGraph, Vertex begin, Vertex end) {
        return modelGraph.getEdgeBetweenNodes(begin, end).orElseThrow(InvalidProduction::new);
    }

    private Vertex getMiddleVertex(ModelGraph graph, Vertex begin, Vertex end) throws InvalidProduction {
        return graph.getVertexesBetween(begin, end)
                .stream()
                .filter(v -> v.getVertexType() == VertexType.HANGING_NODE)
                .findAny()
                .orElseThrow(InvalidProduction::new);
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) throws InvalidProduction {
        Vertex[] v = this.convertToProductionModel(graph, interiorNode).getValue1().toArray(new Vertex[0]);

        graph.removeInterior(interiorNode.getId());
        graph.insertInterior(this.generateId(v[0], v[1], v[4]), v[0], v[1], v[4]);
        graph.insertInterior(this.generateId(v[1], v[2], v[4]), v[1], v[2], v[4]);

        GraphEdge edge = graph.insertEdge(this.generateId(v[1], v[4]), v[1], v[4]);
        edge.setB(false);
        v[1].setVertexType(VertexType.SIMPLE_NODE);

        return graph;
    }

    private String generateId(Vertex v1, Vertex v2, Vertex v3) {
        return v1.getId().concat(v2.getId()).concat(v3.getId());
    }

    private String generateId(Vertex v1, Vertex v2) {
        return v1.getId().concat(v2.getId());
    }
}