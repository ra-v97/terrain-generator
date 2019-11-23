package transformation;

import model.*;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransformationP5 implements Transformation {

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Optional<Triplet<Vertex, Vertex, Vertex>> triangle = getOrderedTriangle(interiorNode.getTriangleVertexes(), graph, interiorNode);
        Optional<Triplet<Vertex, Vertex, Vertex>> triangle2 = triangle.map(e -> getOrderedTriage2(e, graph));

        return triangle2.isPresent() && areAllVertexType(triangle2.get()) &&
                isTransformationConditionFulfilled(graph, triangle2.get(), interiorNode);
    }

    private Triplet<Vertex, Vertex, Vertex> getOrderedTriage2(Triplet<Vertex, Vertex, Vertex> triangle, ModelGraph graph) {
        GraphEdge E1 = graph.getEdgeBetweenNodes(triangle.getValue0(), triangle.getValue2()).orElseThrow(() -> new RuntimeException("Edge not found"));
        GraphEdge E2 = graph.getEdgeBetweenNodes(triangle.getValue1(), triangle.getValue2()).orElseThrow(() -> new RuntimeException("Edge not found"));


        return E1.getL() > E2.getL()
                ? triangle
                : new Triplet<>(triangle.getValue1(), triangle.getValue0(), triangle.getValue2());
    }

    private boolean isTransformationConditionFulfilled(ModelGraph graph, Triplet<Vertex, Vertex, Vertex> triangle, InteriorNode interiorNode) {
        Vertex v1 = triangle.getValue0();
        Vertex v2 = triangle.getValue1();
        Vertex v3 = triangle.getValue2();
        Vertex h4 = getHangingVertexBetweenOp(v1, v2, graph, interiorNode).get();

        GraphEdge L1 = getEdgeBetween(graph, v1, h4);
        GraphEdge L2 = getEdgeBetween(graph, h4, v2);
        GraphEdge L3 = getEdgeBetween(graph, v2, v3);
        GraphEdge L4 = getEdgeBetween(graph, v3, v1);

        final double eps = .0001;
        return (!L4.getB() &&
                (L4.getL() > (L1.getL() + L2.getL())) &&
                (L4.getL() >= L3.getL()) &&
                !(L3.getB() && (Math.abs(L3.getL() - L4.getL()) < eps)));
    }

    private GraphEdge getEdgeBetween(ModelGraph graph, Vertex v1, Vertex v2) {
        return graph.getEdgeById(v1.getEdgeBetween(v2).getId()).orElseThrow(() -> new RuntimeException("Unknown edge id"));
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        if (!isConditionCompleted(graph, interiorNode)) {
            return graph;
        }

        Triplet<Vertex, Vertex, Vertex> trianglePre = getOrderedTriangle(interiorNode.getTriangleVertexes(), graph, interiorNode).get();
        Triplet<Vertex, Vertex, Vertex> triangle = getOrderedTriage2(trianglePre, graph);

        Vertex v1 = triangle.getValue0();
        Vertex v2 = triangle.getValue1();
        Vertex v3 = triangle.getValue2();
        Vertex h4 = getHangingVertexBetweenOp(v1, v2, graph, interiorNode).get();

        GraphEdge v1_h4 = getEdgeBetween(graph, v1, h4);
        GraphEdge h4_v2 = getEdgeBetween(graph, h4, v2);
        GraphEdge v2_v3 = getEdgeBetween(graph, v2, v3);
        GraphEdge v3_v1 = getEdgeBetween(graph, v3, v1);

        // remove old
        graph.removeInterior(interiorNode.getId());
        graph.removeEdge(v1, v3);

        // new vertex
        Vertex h5 = graph.insertVertex(interiorNode.getId(),
                VertexType.HANGING_NODE,
                Point3d.middlePoint(v1.getCoordinates(), v3.getCoordinates()));

        // new edges
        addEdgeBetween(graph, v1, h5, v3_v1.getB());
        addEdgeBetween(graph, h5, v3, v3_v1.getB());
        addEdgeBetween(graph, h5, v2, false);

        // new interior
        String leftInteriorId = v1.getId().concat(v2.getId()).concat(h5.getId());
        InteriorNode left = graph.insertInterior(leftInteriorId, v1, v2, h5);
        left.setPartitionRequired(false);

        String rightInteriorId = h5.getId().concat(v2.getId()).concat(v3.getId());
        InteriorNode right = graph.insertInterior(rightInteriorId, h5, v2, v3);
        right.setPartitionRequired(false);

        return graph;
    }

    private void addEdgeBetween(ModelGraph graph, Vertex v1, Vertex h6, boolean b) {
        String edgeId = v1.getId().concat(h6.getId());
        graph.insertEdge(edgeId, v1, h6, b);
    }

    private Optional<Triplet<Vertex, Vertex, Vertex>> getOrderedTriangle(Triplet<Vertex, Vertex, Vertex> v, ModelGraph graph, InteriorNode interiorNode){
        List<Vertex> hanging =
                interiorNode
                        .getAssociatedNodes()
                        .stream()
                        .filter(e -> e.getVertexType() == VertexType.HANGING_NODE)
                        .collect(Collectors.toList());

        if (hanging.size() != 1) {
            return Optional.empty();
        }

        Vertex hangingVertex = hanging.get(0);
        if(graph.getEdgeBetweenNodes(hangingVertex, v.getValue0()).isPresent()
                && graph.getEdgeBetweenNodes(hangingVertex, v.getValue1()).isPresent()){
            return Optional.of(v);
        } else if(graph.getEdgeBetweenNodes(hangingVertex, v.getValue0()).isPresent()
                && graph.getEdgeBetweenNodes(hangingVertex, v.getValue2()).isPresent()){
            return Optional.of(new Triplet<>(v.getValue2(), v.getValue0(), v.getValue1()));
        } else if(graph.getEdgeBetweenNodes(hangingVertex, v.getValue1()).isPresent()
                && graph.getEdgeBetweenNodes(hangingVertex, v.getValue2()).isPresent()){
            return Optional.of(new Triplet<>(v.getValue1(), v.getValue2(), v.getValue0()));
        }

        return Optional.empty();
    }

    private Optional<Vertex> getHangingVertexBetweenOp(Vertex v1, Vertex v2, ModelGraph graph, InteriorNode interiorNode) {
        List<Vertex> available = graph.getVertexesBetween(v1, v2);

        return available
                .stream()
                .filter(vertex -> vertex.getVertexType() == VertexType.HANGING_NODE)
                .findAny();
    }

    private boolean areAllVertexType(Triplet<Vertex, Vertex, Vertex> triangle) {
        return triangle.toList().stream().map(e -> {
            Vertex v = (Vertex) e;
            return v.getVertexType() == VertexType.SIMPLE_NODE;
        }).reduce(true, (acc, e) -> acc && e);
    }
}
