package transformation;

import model.*;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.LinkedList;
import java.util.List;

public class TransformationP4 implements Transformation {

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

//    private Pair<List<GraphEdge>, List<Vertex>>
//    convertToProductionModel(ModelGraph graph, InteriorNode interiorNode) throws InvalidProduction {
//        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangle();
//        Vertex v0 = triangle.getValue0();
//        Vertex v2 = triangle.getValue1();
//        Vertex v4 = triangle.getValue2();
//        Vertex v1 = this.getMiddleVertex(graph, v0, v2);
//        Vertex v3 = this.getMiddleVertex(graph, v2, v4);
//
//        LinkedList<Vertex> vertices = new LinkedList<>();
//        vertices.add(v0);
//        vertices.add(v1);
//        vertices.add(v2);
//        vertices.add(v3);
//        vertices.add(v4);
//
//        GraphEdge e0 = getEdgeBetween(graph, v0, v1);
//        GraphEdge e1 = getEdgeBetween(graph, v1, v2);
//        GraphEdge e2 = getEdgeBetween(graph, v2, v3);
//        GraphEdge e3 = getEdgeBetween(graph, v3, v4);
//        GraphEdge e4 = getEdgeBetween(graph, v4, v0);
//
//
//        LinkedList<GraphEdge> edges = new LinkedList<>();
//        edges.add(e0);
//        edges.add(e1);
//        edges.add(e2);
//        edges.add(e3);
//        edges.add(e4);
//
//        return Pair.with(edges, vertices);
//    }

    private long numberOfVertexesBetween(ModelGraph graph, Vertex begin, Vertex end) {
        return graph.getVertexesBetween(begin, end).stream().filter(x -> x.getVertexType()
                .equals(VertexType.HANGING_NODE)).count();
    }

    private double edgeLengthBetween(Vertex v1, Vertex v2) {
        return Math.sqrt(Math.pow(v1.getXCoordinate() - v2.getXCoordinate(), 2.0) +
                Math.pow(v1.getYCoordinate() - v2.getYCoordinate(), 2.0) +
                Math.pow(v1.getZCoordinate() - v2.getZCoordinate(), 2.0));
    }

    private Pair<List<GraphEdge>, List<Vertex>>
    convertToProductionModel(ModelGraph graph, InteriorNode interiorNode) throws InvalidProduction {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangle();
        Vertex v0p = triangle.getValue0();
        Vertex v2p = triangle.getValue1();
        Vertex v4p = triangle.getValue2();
//        Vertex v1p = this.getMiddleVertex(graph, v0p, v2p);
//        Vertex v3p = this.getMiddleVertex(graph, v2p, v4p);

        Vertex v0 = null, v1= null, v2= null, v3= null, v4= null, v5= null;

        if(numberOfVertexesBetween(graph, v0p, v2p) == 1 && numberOfVertexesBetween(graph, v0p, v4p) == 1) {
            v2 = v0p;
            if(edgeLengthBetween(v2, v2p) < edgeLengthBetween(v2, v4p)){
                v4 = v2p;
                v0 = v4p;
            }
            else{
                v4 = v4p;
                v0 = v2p;
            }


        }
        else if(numberOfVertexesBetween(graph, v2p, v0p) == 1 && numberOfVertexesBetween(graph, v2p, v4p) == 1) {
            v2 = v2p;
            if(edgeLengthBetween(v2, v4p) < edgeLengthBetween(v2, v0p)){
                v4 = v4p;
                v0 = v0p;
            }
            else{
                v4 = v0p;
                v0 = v4p;
            }
        }

        else if(numberOfVertexesBetween(graph, v4p, v0p) == 1 && numberOfVertexesBetween(graph, v4p, v2p) == 1) {
            v2 = v4p;
            if(edgeLengthBetween(v2, v0p) < edgeLengthBetween(v2, v2p)){
                v4 = v0p;
                v0 = v2p;
            }
            else{
                v4 = v2p;
                v0 = v0p;
            }

        }
        else throw new InvalidProduction();


        v1 = this.getMiddleVertex(graph, v0, v2);
        v3 = this.getMiddleVertex(graph, v2, v4);

        LinkedList<Vertex> vertices = new LinkedList<>();
        vertices.add(v0);
        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);
        vertices.add(v4);

        GraphEdge e0 = getEdgeBetween(graph, v0, v1);
        GraphEdge e1 = getEdgeBetween(graph, v1, v2);
        GraphEdge e2 = getEdgeBetween(graph, v2, v3);
        GraphEdge e3 = getEdgeBetween(graph, v3, v4);
        GraphEdge e4 = getEdgeBetween(graph, v4, v0);


        LinkedList<GraphEdge> edges = new LinkedList<>();
        edges.add(e0);
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);
        edges.add(e4);

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
        InteriorNode v0v1v4 = graph.insertInterior(this.generateId(v[0], v[1], v[4]), v[0], v[1], v[4]);
        InteriorNode v1v2v4 = graph.insertInterior(this.generateId(v[1], v[2], v[4]), v[1], v[2], v[4]);

        v0v1v4.setPartitionRequired(false);
        v1v2v4.setPartitionRequired(false);

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