package transformation;

import model.*;
import org.javatuples.Triplet;

import java.util.List;
import java.util.Optional;

public class TransformationP8 implements Transformation {

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = getOrderedTriage(interiorNode.getTriangleVertexes(), graph);

        return areAllVertexType(triangle) &&
                isTransformationConditionFulfilled(graph, triangle);
    }

    private boolean isTransformationConditionFulfilled(ModelGraph graph, Triplet<Vertex, Vertex, Vertex> triangle) {
        Vertex v1 = triangle.getValue0();
        Vertex v3 = triangle.getValue1();
        Vertex v5 = triangle.getValue2();
        Vertex h2 = getHangingVertexBetween(v1, v3, graph);
        Vertex h4 = getHangingVertexBetween(v3, v5, graph);

        GraphEdge L1 = getEdgeBetween(graph, v1, h2);
        GraphEdge L2 = getEdgeBetween(graph, h2, v3);
        GraphEdge L3 = getEdgeBetween(graph, v3, h4);
        GraphEdge L4 = getEdgeBetween(graph, h4, v5);
        GraphEdge L5 = getEdgeBetween(graph, v5, v1);

        return (!L5.getB() && (L5.getL() > (L1.getL() + L2.getL())) && (L5.getL() > (L3.getL() + L4.getL())));
    }

    private GraphEdge getEdgeBetween(ModelGraph graph, Vertex v1, Vertex v2)
    {
        return graph.getEdgeById(v1.getEdgeBetween(v2).getId()).orElseThrow(() -> new RuntimeException("Unknown edge id"));
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = getOrderedTriage(interiorNode.getTriangleVertexes(), graph);

        Vertex v1 = triangle.getValue0();
        Vertex v3 = triangle.getValue1();
        Vertex v5 = triangle.getValue2();
        Vertex h2 = getHangingVertexBetween(v1, v3, graph);
        Vertex h4 = getHangingVertexBetween(v3, v5, graph);

        GraphEdge v1_h2 = getEdgeBetween(graph, v1, h2);
        GraphEdge h2_v3 = getEdgeBetween(graph, h2, v3);
        GraphEdge v3_h4 = getEdgeBetween(graph, v3, h4);
        GraphEdge h4_v5 = getEdgeBetween(graph, h4, v5);
        GraphEdge v1_v5 = getEdgeBetween(graph, v1, v5);

        // remove old
        graph.removeInterior(interiorNode.getId());
        graph.removeEdge(v1, v5);

        // new vertex
        Vertex h6 = graph.insertVertex(interiorNode.getId(),
                VertexType.HANGING_NODE,
                Point3d.middlePoint(v1.getCoordinates(), v5.getCoordinates()));

        // new edges
        String v1_h6Id = v1.getId().concat(h6.getId());
        graph.insertEdge(v1_h6Id, v1, h6, v1_v5.getB());

        String h6_v5Id = h6.getId().concat(v5.getId());
        graph.insertEdge(h6_v5Id, h6, v5, v1_v5.getB());

        String h6_v3Id = h6.getId().concat(v3.getId());
        graph.insertEdge(h6_v3Id, h6, v3, false);

        // new interior
        String leftInteriorId = v1.getId().concat(v3.getId()).concat(h6.getId());
        InteriorNode left = graph.insertInterior(leftInteriorId, v1, v3, h6);
        left.setPartitionRequired(false);

        String rightInteriorId = h6.getId().concat(v3.getId()).concat(v5.getId());
        InteriorNode right = graph.insertInterior(rightInteriorId, h6, v3, v5);
        right.setPartitionRequired(false);

        return graph;
    }

    private Triplet<Vertex, Vertex, Vertex> getOrderedTriage(Triplet<Vertex, Vertex, Vertex> v, ModelGraph graph){
        if(getHangingVertexBetweenOp(v.getValue0(), v.getValue1(), graph).isPresent() &&
                getHangingVertexBetweenOp(v.getValue1(), v.getValue2(), graph).isPresent()){
            return v;
        } else if (getHangingVertexBetweenOp(v.getValue2(), v.getValue0(), graph).isPresent() &&
                getHangingVertexBetweenOp(v.getValue0(), v.getValue1(), graph).isPresent()){
            return new Triplet<>(v.getValue2(), v.getValue0(), v.getValue1());
        } else if(getHangingVertexBetweenOp(v.getValue1(), v.getValue2(), graph).isPresent() &&
                getHangingVertexBetweenOp(v.getValue2(), v.getValue0(), graph).isPresent()) {
            return new Triplet<>(v.getValue1(), v.getValue2(), v.getValue0());
        }
        throw new RuntimeException("Configuration with hanging vertex between 2 vertexes was not found");
    }

    private Optional<Vertex> getHangingVertexBetweenOp(Vertex v1, Vertex v2, ModelGraph graph){
        List<Vertex> between = graph.getVertexBetween(v1, v2);

        return between.stream().filter(e -> e.getVertexType() == VertexType.HANGING_NODE).findAny();
    }

    private Vertex getHangingVertexBetween(Vertex v1, Vertex v2, ModelGraph graph){
        return getHangingVertexBetweenOp(v1, v2, graph)
                .orElseThrow(() -> new RuntimeException("Hanging vertex between " + v1.getId() +" and " + v2.getId() + " not found"));
    }

    private boolean areAllVertexType(Triplet<Vertex, Vertex, Vertex> triangle) {
        return triangle.toList().stream().map(e -> {
            Vertex v = (Vertex)e;
            return v.getVertexType() == VertexType.SIMPLE_NODE;
        }).reduce(true, (acc, e) -> acc && e);
    }
}
