package transformation;

import model.GraphEdge;
import model.InteriorNode;
import model.ModelGraph;
import model.Point3d;
import model.Vertex;
import model.VertexType;
import org.javatuples.Triplet;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TransformationP1 implements Transformation {

    private static final String longestEdgeVertex1 = "simpleVertex1";
    private static final String longestEdgeVertex2 = "simpleVertex2";
    private static final String oppositeVertex = "oppositeLongestEdgeVertex3";

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        boolean conditionMet = true;
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        if(!interiorNode.isPartitionRequired()) conditionMet = false;
        if(hangingVertexExists(graph, triangle)) conditionMet = false;

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

        Triplet<Vertex, Vertex, Vertex> triangleVertexes = interiorNode.getTriangleVertexes();

        Vertex v1 = triangleVertexes.getValue0();
        Vertex v2 = triangleVertexes.getValue1();
        Vertex v3 = triangleVertexes.getValue2();

        GraphEdge edge1;
        try {
            edge1 = modelGraph.getEdgeById(v1.getId() + v2.getId()).orElseThrow(() -> new RuntimeException("Unknown edge id"));
        }catch (RuntimeException e){
            edge1 = modelGraph.getEdgeById(v2.getId() + v1.getId()).orElseThrow(() -> new RuntimeException("Unknown edge id"));
        }

        GraphEdge edge2;
        try {
            edge2 = modelGraph.getEdgeBetweenNodes(v2, v3).orElseThrow(() -> new RuntimeException("Unknown edge id"));
        }catch (RuntimeException e){
            edge2 = modelGraph.getEdgeBetweenNodes(v3, v2).orElseThrow(() -> new RuntimeException("Unknown edge id"));
        }

        GraphEdge edge3;
        try {
            edge3 = modelGraph.getEdgeBetweenNodes(v1, v3).orElseThrow(() -> new RuntimeException("Unknown edge id"));
        }catch (RuntimeException e){
            edge3 = modelGraph.getEdgeBetweenNodes(v3, v1).orElseThrow(() -> new RuntimeException("Unknown edge id"));
        }

        if (edge1.getL() >= edge2.getL() && edge1.getL() >= edge3.getL()){
            triangleModel.put(longestEdgeVertex1, v1);
            triangleModel.put(longestEdgeVertex2, v2);
            triangleModel.put(oppositeVertex, v3);
        } else if (edge2.getL() >= edge1.getL() && edge2.getL() >= edge3.getL()) {
            triangleModel.put(longestEdgeVertex1, v2);
            triangleModel.put(longestEdgeVertex2, v3);
            triangleModel.put(oppositeVertex, v1);
        } else if (edge3.getL() >= edge1.getL() && edge3.getL() >= edge2.getL()) {
            triangleModel.put(longestEdgeVertex1, v1);
            triangleModel.put(longestEdgeVertex2, v3);
            triangleModel.put(oppositeVertex, v2);
        }

        return triangleModel;
    }

    private static boolean hangingVertexExists(ModelGraph modelGraph, Triplet<Vertex, Vertex, Vertex> triangle) {
        AtomicBoolean hangingVertexExist = new AtomicBoolean(false);
        for (Object o : triangle) {
            Vertex v = (Vertex)o;
            if(v.getVertexType() == VertexType.HANGING_NODE){
                hangingVertexExist.set(true);
                break;
            }
        }

        List<Vertex> vertexes = Arrays.asList(triangle.getValue0(), triangle.getValue1(), triangle.getValue2());
        for (List<Vertex> pair: Arrays.asList(Arrays.asList(vertexes.get(0), vertexes.get(1)),
                Arrays.asList(vertexes.get(0), vertexes.get(2)),
                Arrays.asList(vertexes.get(1), vertexes.get(2)))) {
                modelGraph.getEdgeById(pair.get(0).getId() + pair.get(1).getId()).
                        ifPresentOrElse((e) -> {},
                                () -> modelGraph.getEdgeById(pair.get(1).getId() + pair.get(0).getId())
                                        .ifPresentOrElse((e) -> {},
                                                () -> hangingVertexExist.set(true)));
        }
        return hangingVertexExist.get();
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
