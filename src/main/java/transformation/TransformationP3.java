package transformation;

import model.*;
import org.javatuples.Triplet;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransformationP3 implements Transformation {
    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Optional<Triplet<Vertex, Vertex, Vertex>> triangle = getOrderedTriangle(interiorNode.getTriangleVertexes(), graph, interiorNode);

        return triangle.isPresent() && areAllVertexType(triangle.get()) &&
                isTransformationConditionFulfilled(graph, triangle.get());
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = getOrderedTriangle(interiorNode.getTriangleVertexes(), graph, interiorNode)
                .orElseThrow(() -> new RuntimeException("Configuration not found"));

        Vertex first = triangle.getValue0();
        Vertex second = triangle.getValue1();
        Vertex third = triangle.getValue2();
        Vertex hanging = getHangingVertexBetween(first, second, graph);
        
        GraphEdge firstToHanging = graph.getEdgeById(first.getEdgeBetween(hanging).getId()).orElseThrow(()->new RuntimeException("Unknown edge id"));
        GraphEdge hangingToSecond = graph.getEdgeById(hanging.getEdgeBetween(second).getId()).orElseThrow(()->new RuntimeException("Unknown edge id"));

        //remove old
        graph.removeInterior(interiorNode.getId());
        graph.deleteEdge(first, hanging);
        graph.deleteEdge(hanging, second);

        //create new vertex
        Vertex inserted = hanging.setVertexType(VertexType.SIMPLE_NODE);

        //connect it to other
        String edgeFromOneToVertexId = first.getId().concat(inserted.getId());
        GraphEdge fromOneToVertex = graph.insertEdge(edgeFromOneToVertexId, first, inserted);
        fromOneToVertex.setB(firstToHanging.getB());

        String edgeFromVertexToTwoId = inserted.getId().concat(second.getId());
        GraphEdge fromVertexToTwo = graph.insertEdge(edgeFromVertexToTwoId, inserted, second);
        fromVertexToTwo.setB(hangingToSecond.getB());

        String edgeFromVertexToThird = inserted.getId().concat(third.getId());
        GraphEdge fromVertexToThird = graph.insertEdge(edgeFromVertexToThird, inserted, third);
        fromVertexToThird.setB(false);

        //Rebuild interior
        String leftInteriorId = first.getId().concat(inserted.getId()).concat(third.getId());
        InteriorNode left = graph.insertInterior(leftInteriorId, first, inserted, third);
        left.setPartitionRequired(false);

        String rightInteriorId = inserted.getId().concat(second.getId()).concat(third.getId());
        InteriorNode right = graph.insertInterior(rightInteriorId, inserted, second, third);
        right.setPartitionRequired(false);
        return graph;
    }

    private Optional<Vertex> getHangingVertexBetweenOp(Vertex v1, Vertex v2, ModelGraph graph){
        List<Vertex> available = graph.getVertexesBetween(v1, v2);

        return available
                .stream()
                .filter(vertex -> vertex.getVertexType() == VertexType.HANGING_NODE)
                .findAny();
    }

    private Vertex getHangingVertexBetween(Vertex v1, Vertex v2, ModelGraph graph){
        return getHangingVertexBetweenOp(v1, v2, graph)
                .orElseThrow(() -> new RuntimeException("Hanging vertex between " + v1.getId() +" and " + v2.getId() + " not found"));
    }

    private boolean isTransformationConditionFulfilled(ModelGraph graph, Triplet<Vertex, Vertex, Vertex> triangle){
        Vertex first = triangle.getValue0();
        Vertex second = triangle.getValue1();
        Vertex third = triangle.getValue2();
        Vertex hanging = getHangingVertexBetween(first, second, graph);

        GraphEdge L1 = graph.getEdgeById(first.getEdgeBetween(hanging).getId()).orElseThrow(()->new RuntimeException("Unknown edge id"));
        GraphEdge L2 = graph.getEdgeById(hanging.getEdgeBetween(second).getId()).orElseThrow(()->new RuntimeException("Unknown edge id"));
        GraphEdge L3 = graph.getEdgeById(second.getEdgeBetween(third).getId()).orElseThrow(()->new RuntimeException("Unknown edge id"));
        GraphEdge L4 = graph.getEdgeById(third.getEdgeBetween(first).getId()).orElseThrow(()->new RuntimeException("Unknown edge id"));

        return (L1.getL() + L2.getL()) >= L3.getL() && (L1.getL() + L2.getL()) >= L4.getL();
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

    private boolean areAllVertexType(Triplet<Vertex, Vertex, Vertex> triangle) {
        return triangle.toList().stream().map(e -> {
            Vertex v = (Vertex)e;
            return isVertexType(v);
        }).reduce(true, (acc, e) -> acc && e);
    }

    private boolean isVertexType(Vertex v){
        return v.getVertexType() == VertexType.SIMPLE_NODE;
    }
}
