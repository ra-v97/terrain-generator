package transformation;

import model.GraphEdge;
import model.InteriorNode;
import model.ModelGraph;
import model.Point3d;
import model.Vertex;
import model.VertexType;
import org.javatuples.Triplet;

import java.util.HashMap;
import java.util.Map;

public class TransformationP1 implements Transformation {

    private static final String VERTEX_MAP_SIMPLE_VERTEX_1_KEY = "simpleVertex1";

    private static final String VERTEX_MAP_SIMPLE_VERTEX_2_KEY = "simpleVertex2";

    private static final String VERTEX_MAP_SIMPLE_VERTEX_OPPOSITE_LONGEST_EDGE_KEY = "oppositeLongestEdgeVertex3";

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        if (!interiorNode.isPartitionRequired()) {
            return false;
        }
        if (getSimpleVertexCount(triangle) != 3 || interiorNode.getAssociatedNodes().size() != 0) {
            return false;
        }

        Map<String, Vertex> model = mapTriangleVertexesToModel(graph, interiorNode);

        Vertex simpleVertex1 = model.get(VERTEX_MAP_SIMPLE_VERTEX_1_KEY);
        Vertex simpleVertex2 = model.get(VERTEX_MAP_SIMPLE_VERTEX_2_KEY);
        Vertex oppositeLongestEdgeVertex = model.get(VERTEX_MAP_SIMPLE_VERTEX_OPPOSITE_LONGEST_EDGE_KEY);

        if (simpleVertex1 == null || simpleVertex2 == null || oppositeLongestEdgeVertex == null) {
            throw new RuntimeException("Transformation error");
        }

        GraphEdge longestEdge = graph.getEdgeBetweenNodes(simpleVertex1, simpleVertex2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge shortEdge1 = graph.getEdgeBetweenNodes(oppositeLongestEdgeVertex, simpleVertex1)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge shortEdge2 = graph.getEdgeBetweenNodes(oppositeLongestEdgeVertex, simpleVertex2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));

        return (longestEdge.getL() >= shortEdge1.getL() && longestEdge.getL() >= shortEdge2.getL());
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        Map<String, Vertex> model = mapTriangleVertexesToModel(graph, interiorNode);
        Vertex simpleVertex1 = model.get(VERTEX_MAP_SIMPLE_VERTEX_1_KEY);
        Vertex simpleVertex2 = model.get(VERTEX_MAP_SIMPLE_VERTEX_2_KEY);
        Vertex oppositeLongestEdgeVertex = model.get(VERTEX_MAP_SIMPLE_VERTEX_OPPOSITE_LONGEST_EDGE_KEY);

        if (simpleVertex1 == null || simpleVertex2 == null || oppositeLongestEdgeVertex == null) {
            throw new RuntimeException("Transformation error");
        }

        GraphEdge longestEdge = graph.getEdgeBetweenNodes(simpleVertex1, simpleVertex2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));

        //transformation process
        graph.removeInterior(interiorNode.getId());
        graph.deleteEdge(simpleVertex1, simpleVertex2);

        Vertex insertedVertex = graph.insertVertex(interiorNode.getId(),
                VertexType.SIMPLE_NODE,
                Point3d.middlePoint(simpleVertex1.getCoordinates(), simpleVertex2.getCoordinates()));

        String newEdge1Id = simpleVertex1.getId().concat(insertedVertex.getId());
        String newEdge2Id = simpleVertex2.getId().concat(insertedVertex.getId());
        String newEdge3Id = oppositeLongestEdgeVertex.getId().concat(insertedVertex.getId());

        GraphEdge insertedEdge1 = graph.insertEdge(newEdge1Id, simpleVertex1, insertedVertex);
        insertedEdge1.setB(longestEdge.getB());

        GraphEdge insertedEdge2 = graph.insertEdge(newEdge2Id, simpleVertex2, insertedVertex);
        insertedEdge2.setB(longestEdge.getB());

        GraphEdge insertedEdge3 = graph.insertEdge(newEdge3Id, oppositeLongestEdgeVertex, insertedVertex);
        insertedEdge3.setB(false);

        String insertedInterior1Id = oppositeLongestEdgeVertex.getId().concat(simpleVertex1.getId()).concat(insertedVertex.getId());
        String insertedInterior2Id = oppositeLongestEdgeVertex.getId().concat(simpleVertex2.getId()).concat(insertedVertex.getId());
        graph.insertInterior(insertedInterior1Id, oppositeLongestEdgeVertex, simpleVertex1, insertedVertex);
        graph.insertInterior(insertedInterior2Id, oppositeLongestEdgeVertex, simpleVertex2, insertedVertex);
        return graph;
    }

    private static Map<String, Vertex> mapTriangleVertexesToModel(ModelGraph modelGraph, InteriorNode interiorNode) {
        Map<String, Vertex> triangleModel = new HashMap<>();

        GraphEdge triangleLongestEdge = modelGraph.getTraingleLongestEdge(interiorNode);
        triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_1_KEY, triangleLongestEdge.getNode0());
        triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_2_KEY, triangleLongestEdge.getNode1());

        Triplet<Vertex, Vertex, Vertex> triangleVertexes = interiorNode.getTriangleVertexes();

        for (Object o : triangleVertexes) {
            Vertex v = (Vertex) o;
            if (v != triangleLongestEdge.getNode0() && v != triangleLongestEdge.getNode1()) {
                triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_OPPOSITE_LONGEST_EDGE_KEY, v);
                break;
            }
        }
        return triangleModel;
    }

    private static int getSimpleVertexCount(Triplet<Vertex, Vertex, Vertex> triangle) {
        int count = 0;
        for (Object o : triangle) {
            Vertex v = (Vertex) o;
            if (v.getVertexType() == VertexType.SIMPLE_NODE) {
                count++;
            }
        }
        return count;
    }
}
