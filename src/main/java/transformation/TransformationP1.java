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

    private static final String longestEdgeVertex1 = "simpleVertex1";
    private static final String longestEdgeVertex2 = "simpleVertex2";
    private static final String oppositeVertex = "oppositeLongestEdgeVertex3";

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        boolean conditionMet = true;
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        if(!interiorNode.isPartitionRequired()) conditionMet = false;
        if(getHangingVertexCount(triangle) != 0) conditionMet = false;

        return conditionMet;
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        Map<String, Vertex> model = mapTriangleVertexesToModel(graph, interiorNode);
        Vertex vertex1 = model.get(longestEdgeVertex1);
        Vertex vertex2 = model.get(longestEdgeVertex2);
        Vertex opposite = model.get(oppositeVertex);

        GraphEdge longestEdge = graph.getEdgeBetweenNodes(vertex1, vertex2)
                .orElseThrow(() -> new RuntimeException("Edge doesn't exist in the graph."));

        graph.removeInterior(interiorNode.getId());
        graph.deleteEdge(longestEdge.getId());

        Vertex insertedVertex = graph.insertVertex(interiorNode.getId(),
                VertexType.SIMPLE_NODE,
                Point3d.middlePoint(vertex1.getCoordinates(), vertex2.getCoordinates()));

        insertEdge(graph, vertex1, insertedVertex, longestEdge.getB());
        insertEdge(graph, vertex2, insertedVertex, longestEdge.getB());
        insertEdge(graph, opposite, insertedVertex, false);

        insertInterior(graph, opposite, vertex1, insertedVertex);
        insertInterior(graph, opposite, vertex2, insertedVertex);

        return graph;
    }

    private static Map<String, Vertex> mapTriangleVertexesToModel(ModelGraph modelGraph, InteriorNode interiorNode){
        Map<String, Vertex> triangleModel = new HashMap<>();

        GraphEdge triangleLongestEdge = modelGraph.getTraingleLongestEdge(interiorNode);
        triangleModel.put(longestEdgeVertex1, triangleLongestEdge.getNode0());
        triangleModel.put(longestEdgeVertex2, triangleLongestEdge.getNode1());

        Triplet<Vertex, Vertex, Vertex> triangleVertexes = interiorNode.getTriangleVertexes();

        for (Object o : triangleVertexes){
            Vertex v = (Vertex)o;
            if (v != triangleLongestEdge.getNode0() && v != triangleLongestEdge.getNode1()) {
                triangleModel.put(oppositeVertex, v);
                break;
            }
        }
        return triangleModel;
    }

    private static int getHangingVertexCount(Triplet<Vertex, Vertex, Vertex> triangle) {
        int count = 0;
        for (Object o : triangle) {
            Vertex v = (Vertex)o;
            if(v.getVertexType() == VertexType.HANGING_NODE){
                count++;
            }
        }
        return count;
    }

    private static void insertEdge(ModelGraph graph, Vertex v1, Vertex v2, boolean boundary){
        String newEdgeId = v1.getId().concat(v2.getId());
        GraphEdge edge = graph.insertEdge(newEdgeId, v1, v2);
        edge.setB(boundary);
    }

    private static void insertInterior(ModelGraph graph, Vertex v1, Vertex v2, Vertex v3){
        String insertedInteriorId = v1.getId().concat(v2.getId()).concat(v3.getId());
        graph.insertInterior(insertedInteriorId, v1, v2, v3);
    }
}
