package transformation;

import model.*;
import org.javatuples.Triplet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformationP6 implements Transformation {

    private static final String SIMPLE_VERTEX_1 = "vertex1";
    private static final String SIMPLE_VERTEX_3 = "vertex3";
    private static final String SIMPLE_VERTEX_5 = "vertex5";
    private static final String HANGING_NODE_2 = "node2";
    private static final String HANGING_NODE_4 = "node4";

    private static final String SIMPLE_VERTEX_2 = "vertex2";


    private Map<String, Vertex> mapVerticesToModel(ModelGraph graph, InteriorNode interiorNode){
        Map<String, Vertex> verticesMap = new HashMap<>();

        Triplet<Vertex, Vertex, Vertex> triangleSimple = interiorNode.getTriangleVertexes();
        Vertex vertexA = triangleSimple.getValue0();
        Vertex vertexB = triangleSimple.getValue1();
        Vertex vertexC = triangleSimple.getValue2();

        Vertex vertexD = graph.getVertexBetween(vertexA, vertexB).orElse(null);
        Vertex vertexE = graph.getVertexBetween(vertexB, vertexC).orElse(null);
        Vertex vertexF = graph.getVertexBetween(vertexA, vertexC).orElse(null);

        if(vertexD == null){
            verticesMap.put(SIMPLE_VERTEX_1, vertexA);
            verticesMap.put(SIMPLE_VERTEX_5, vertexB);
            verticesMap.put(SIMPLE_VERTEX_3, vertexC);
        }

        if(vertexE == null){
            verticesMap.put(SIMPLE_VERTEX_1, vertexB);
            verticesMap.put(SIMPLE_VERTEX_5, vertexC);
            verticesMap.put(SIMPLE_VERTEX_3, vertexA);
        }

        if(vertexF == null){
            verticesMap.put(SIMPLE_VERTEX_1, vertexA);
            verticesMap.put(SIMPLE_VERTEX_5, vertexC);
            verticesMap.put(SIMPLE_VERTEX_3, vertexB);
        }

        List<Vertex> hangingNodes = interiorNode.getAssociatedNodes();

        Vertex hanging2;
        Vertex hanging4;

        if(verticesMap.get(SIMPLE_VERTEX_1).getEdgeBetween(hangingNodes.get(0)) != null){
            hanging2 = hangingNodes.get(0);
            hanging4 = hangingNodes.get(1);
        }
        else{
            hanging2 = hangingNodes.get(1);
            hanging4 = hangingNodes.get(0);
        }

        verticesMap.put(HANGING_NODE_2, hanging2);
        verticesMap.put(HANGING_NODE_4, hanging4);

        return verticesMap;
    }

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode){
        List<Vertex> hangingNodes = interiorNode.getAssociatedNodes();

        if(hangingNodes.size() != 2){
            return false;
        }
        Map<String, Vertex> verticesMap = this.mapVerticesToModel(graph, interiorNode);

        if(verticesMap.size() != 5){
            System.out.println(verticesMap.size());
            return false;
        }

        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        int hanging_nodes = interiorNode.getAssociatedNodes().size();

        if (getSimpleVertexCount(triangle) != 3 || hanging_nodes != 2) {
            return false;
        }

        GraphEdge e1 = graph.getEdgeBetweenNodes(verticesMap.get(SIMPLE_VERTEX_1), verticesMap.get(HANGING_NODE_2))
                .orElseThrow(() -> new IllegalStateException("Unknown vertices"));

        GraphEdge e2 = graph.getEdgeBetweenNodes(verticesMap.get(HANGING_NODE_2), verticesMap.get(SIMPLE_VERTEX_3))
                .orElseThrow(() -> new IllegalStateException("Unknown vertices"));

        GraphEdge e3 = graph.getEdgeBetweenNodes(verticesMap.get(SIMPLE_VERTEX_3), verticesMap.get(HANGING_NODE_4))
                .orElseThrow(() -> new IllegalStateException("Unknown vertices"));

        GraphEdge e4 = graph.getEdgeBetweenNodes(verticesMap.get(HANGING_NODE_4), verticesMap.get(SIMPLE_VERTEX_5))
                .orElseThrow(() -> new IllegalStateException("Unknown vertices"));

        GraphEdge e5 = graph.getEdgeBetweenNodes(verticesMap.get(SIMPLE_VERTEX_1), verticesMap.get(SIMPLE_VERTEX_5))
                .orElseThrow(() -> new IllegalStateException("Unknown vertices"));

        return (e1.getL() + e2.getL() >= (e3.getL() + e4.getL())) && (e1.getL() + e2.getL() >= e5.getL());
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {

        Map<String, Vertex> verticesMap = this.mapVerticesToModel(graph, interiorNode);

        if(this.isConditionCompleted(graph, interiorNode)){

            graph.removeInterior(interiorNode.getId());

            //change type of node 2
            Vertex node2 = verticesMap.get(HANGING_NODE_2);
            node2.setVertexType(VertexType.SIMPLE_NODE);
            verticesMap.remove(HANGING_NODE_2);
            verticesMap.put(SIMPLE_VERTEX_2, node2);

            graph.insertEdge("edge6", verticesMap.get(SIMPLE_VERTEX_2), verticesMap.get(SIMPLE_VERTEX_5), false);

            //insert new interiors
            String leftInteriorId = verticesMap.get(SIMPLE_VERTEX_1).getId() + verticesMap.get(SIMPLE_VERTEX_2).getId() + verticesMap.get(SIMPLE_VERTEX_5).getId();
            String rightInteriorId = verticesMap.get(SIMPLE_VERTEX_2).getId() + verticesMap.get(SIMPLE_VERTEX_3).getId() + verticesMap.get(SIMPLE_VERTEX_5).getId();

            InteriorNode leftInteriorNode = graph.insertInterior(leftInteriorId, verticesMap.get(SIMPLE_VERTEX_1), verticesMap.get(SIMPLE_VERTEX_2), verticesMap.get(SIMPLE_VERTEX_5));
            leftInteriorNode.setPartitionRequired(false);

            InteriorNode rightInteriorNode = graph.insertInterior(rightInteriorId, verticesMap.get(SIMPLE_VERTEX_2), verticesMap.get(SIMPLE_VERTEX_3), verticesMap.get(SIMPLE_VERTEX_5), verticesMap.get(HANGING_NODE_4));
            rightInteriorNode.setPartitionRequired(false);
        }

        return graph;
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