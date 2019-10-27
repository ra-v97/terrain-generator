package transformation;

import model.*;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TransformationP6 implements Transformation {

    private static final String SIMPLE_VERTEX_1 = "vertex1";
    private static final String SIMPLE_VERTEX_3 = "vertex3";
    private static final String SIMPLE_VERTEX_5 = "vertex5";
    private static final String HANGING_NODE_2 = "node2";
    private static final String HANGING_NODE_4 = "node4";

    private static final String SIMPLE_VERTEX_2 = "vertex2";


    private Map<String, Vertex> mapVerticesToModel(ModelGraph graph){
        Map<String, Vertex> verticesMap = new HashMap<>();
        Collection<GraphEdge> edges = graph.getEdges();
        GraphEdge wholeEdge = null;

        for(GraphEdge edge: edges){
            Pair<GraphNode, GraphNode> edgeNodes = edge.getEdgeNodes();
            GraphNode graphNode0 = edgeNodes.getValue0();
            GraphNode graphNode1 = edgeNodes.getValue1();

            if(graphNode0 instanceof Vertex && graphNode1 instanceof Vertex){
                Vertex node0 = (Vertex) graphNode0;
                Vertex node1 = (Vertex) graphNode1;

                if(node0.getVertexType() == VertexType.SIMPLE_NODE && node1.getVertexType() == VertexType.SIMPLE_NODE){
                    verticesMap.put(SIMPLE_VERTEX_1, node0);
                    verticesMap.put(SIMPLE_VERTEX_5, node1);
                    wholeEdge = edge;
                }
            }

        }

        for(GraphEdge edge: edges){
            if(edge.equals(wholeEdge)){
                continue;
            }
            Pair<GraphNode, GraphNode> edgeNodes = edge.getEdgeNodes();
            GraphNode graphNode0 = edgeNodes.getValue0();
            GraphNode graphNode1 = edgeNodes.getValue1();

            if(graphNode0 instanceof Vertex && graphNode1 instanceof Vertex) {
                Vertex node0 = (Vertex) graphNode0;
                Vertex node1 = (Vertex) graphNode1;

                if (node0 == verticesMap.get(SIMPLE_VERTEX_1)) verticesMap.put(HANGING_NODE_2, node1);
                if (node1 == verticesMap.get(SIMPLE_VERTEX_1)) verticesMap.put(HANGING_NODE_2, node0);

                if (node0 == verticesMap.get(SIMPLE_VERTEX_5)) verticesMap.put(HANGING_NODE_4, node1);
                if (node1 == verticesMap.get(SIMPLE_VERTEX_5)) verticesMap.put(HANGING_NODE_4, node0);
            }
        }

        for(GraphNode node: graph.getVertices()){
            if(node != verticesMap.get(SIMPLE_VERTEX_1) &&
                    node != verticesMap.get(SIMPLE_VERTEX_5) &&
                    node != verticesMap.get(HANGING_NODE_2) &&
                    node != verticesMap.get(HANGING_NODE_4)) verticesMap.put(SIMPLE_VERTEX_3, (Vertex) node);
        }

        return verticesMap;
    }

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode){
        Map<String, Vertex> verticesMap = this.mapVerticesToModel(graph);

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

        Map<String, Vertex> verticesMap = this.mapVerticesToModel(graph);

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

            InteriorNode rightInteriorNode = graph.insertInterior(rightInteriorId, verticesMap.get(SIMPLE_VERTEX_2), verticesMap.get(SIMPLE_VERTEX_3), verticesMap.get(SIMPLE_VERTEX_5));
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