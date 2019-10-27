package transformation;

import model.*;
import org.javatuples.Triplet;

import java.util.*;
import java.util.stream.Stream;

public class TransformationP9 implements Transformation {

    // original
    private static final String SIMPLE_NODE_1 = "v1";
    private static final String SIMPLE_NODE_3 = "v3";
    private static final String SIMPLE_NODE_5 = "v5";
    private static final String HANGING_NODE_2 = "h2";
    private static final String HANGING_NODE_4 = "h4";
    private static final String HANGING_NODE_6 = "h6";

    // added
    private static final String SIMPLE_NODE_2 = "v2";

    private Map<String, Vertex> mapVerticesToModel(ModelGraph graph, InteriorNode interiorNode) {
        Map<String, Vertex> vertexMap = new HashMap<>();

        Triplet<Vertex, Vertex, Vertex> triangleVertexes = interiorNode.getTriangleVertexes();

        Vertex simpleA = triangleVertexes.getValue0();
        Vertex simpleB = triangleVertexes.getValue1();
        Vertex simpleC = triangleVertexes.getValue2();

        Vertex hangingA = getHangingVertexBetween(graph, simpleB, simpleC).get();
        Vertex hangingB = getHangingVertexBetween(graph, simpleC, simpleA).get();
        Vertex hangingC = getHangingVertexBetween(graph, simpleA, simpleB).get();

        GraphEdge edgeAB = graph.getEdgeBetweenNodes(simpleA, hangingB).get();
        GraphEdge edgeAC = graph.getEdgeBetweenNodes(simpleA, hangingC).get();
        GraphEdge edgeBA = graph.getEdgeBetweenNodes(simpleB, hangingA).get();
        GraphEdge edgeBC = graph.getEdgeBetweenNodes(simpleB, hangingC).get();
        GraphEdge edgeCA = graph.getEdgeBetweenNodes(simpleC, hangingA).get();
        GraphEdge edgeCB = graph.getEdgeBetweenNodes(simpleC, hangingB).get();

        Double lA = edgeBA.getL() + edgeCA.getL();
        Double lB = edgeAB.getL() + edgeCB.getL();
        Double lC = edgeAC.getL() + edgeBC.getL();

        Double max = Stream.of(lA, lB, lC).max(Double::compare).get();

        if (max == lA) {
            vertexMap.put(SIMPLE_NODE_1, simpleB);
            vertexMap.put(SIMPLE_NODE_3, simpleC);
            vertexMap.put(SIMPLE_NODE_5, simpleA);
            vertexMap.put(HANGING_NODE_2, hangingA);
            vertexMap.put(HANGING_NODE_4, hangingB);
            vertexMap.put(HANGING_NODE_6, hangingC);
        } else if (max == lB) {
            vertexMap.put(SIMPLE_NODE_1, simpleC);
            vertexMap.put(SIMPLE_NODE_3, simpleA);
            vertexMap.put(SIMPLE_NODE_5, simpleB);
            vertexMap.put(HANGING_NODE_2, hangingB);
            vertexMap.put(HANGING_NODE_4, hangingC);
            vertexMap.put(HANGING_NODE_6, hangingA);
        } else if (max == lC) {
            vertexMap.put(SIMPLE_NODE_1, simpleA);
            vertexMap.put(SIMPLE_NODE_3, simpleB);
            vertexMap.put(SIMPLE_NODE_5, simpleC);
            vertexMap.put(HANGING_NODE_2, hangingC);
            vertexMap.put(HANGING_NODE_4, hangingA);
            vertexMap.put(HANGING_NODE_6, hangingB);
        }

        return vertexMap;
    }

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangleVertexes = interiorNode.getTriangleVertexes();

        Vertex vertexA = triangleVertexes.getValue0();
        Vertex vertexB = triangleVertexes.getValue1();
        Vertex vertexC = triangleVertexes.getValue2();

        if (!(isSimpleNode(vertexA) && isSimpleNode(vertexB) && isSimpleNode(vertexC))) return false;

        Optional<Vertex> hangingA = getHangingVertexBetween(graph, vertexB, vertexC);
        Optional<Vertex> hangingB = getHangingVertexBetween(graph, vertexC, vertexA);
        Optional<Vertex> hangingC = getHangingVertexBetween(graph, vertexA, vertexB);

        return hangingA.isPresent() && hangingB.isPresent() && hangingC.isPresent();
    }

    private Optional<Vertex> getHangingVertexBetween(ModelGraph graph, Vertex v1, Vertex v2) {
        return graph.getVertexBetween(v1, v2).filter(TransformationP9::isHangingNode);
    }

    private static boolean isSimpleNode(Vertex v) {
        return v.getVertexType() == VertexType.SIMPLE_NODE;
    }

    private static boolean isHangingNode(Vertex v) {
        return v.getVertexType() == VertexType.HANGING_NODE;
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        if (isConditionCompleted(graph, interiorNode)) {
            graph.removeInterior(interiorNode.getId());

            Map<String, Vertex> vertexMap = mapVerticesToModel(graph, interiorNode);

            Vertex node2 = vertexMap.get(HANGING_NODE_2);
            node2.setVertexType(VertexType.SIMPLE_NODE);

            graph.insertEdge("e7", vertexMap.get(SIMPLE_NODE_2), vertexMap.get(SIMPLE_NODE_5), false);

            String leftInteriorId = vertexMap.get(SIMPLE_NODE_1).getId()
                    .concat(vertexMap.get(SIMPLE_NODE_2).getId())
                    .concat(vertexMap.get(SIMPLE_NODE_5).getId());
            String rightInteriorId = vertexMap.get(SIMPLE_NODE_2).getId()
                    .concat(vertexMap.get(SIMPLE_NODE_3).getId())
                    .concat(vertexMap.get(SIMPLE_NODE_5).getId());
            graph.insertInterior(
                    leftInteriorId,
                    vertexMap.get(SIMPLE_NODE_1),
                    vertexMap.get(SIMPLE_NODE_2),
                    vertexMap.get(SIMPLE_NODE_5),
                    vertexMap.get(HANGING_NODE_6)
            );
            graph.insertInterior(
                    rightInteriorId,
                    vertexMap.get(SIMPLE_NODE_2),
                    vertexMap.get(SIMPLE_NODE_3),
                    vertexMap.get(SIMPLE_NODE_5),
                    vertexMap.get(HANGING_NODE_4)
            );
        }

        return graph;
    }
}
