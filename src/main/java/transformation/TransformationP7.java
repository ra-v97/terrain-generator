package transformation;

import model.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class TransformationP7 implements Transformation {

    private static final class OrientedInterior {
        public InteriorNode i;

        public Vertex v1;
        public Vertex v3;
        public Vertex v5;

        public Vertex h2;
        public Vertex h4;

        public GraphEdge e1;
        public GraphEdge e2;
        public GraphEdge e3;
        public GraphEdge e4;
        public GraphEdge e5;

        public GraphEdge eiv1;
        public GraphEdge eiv3;
        public GraphEdge eiv5;
    }

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Optional<OrientedInterior> oi = orient(graph, interiorNode);
        return oi.filter(i -> i.e5.getB())
                .filter(i -> i.e5.getL() > i.e1.getL() + i.e2.getL())
                .filter(i -> i.e5.getL() > i.e3.getL() + i.e4.getL())
                .isPresent();
    }

    private Optional<OrientedInterior> orient(ModelGraph graph, InteriorNode interiorNode) {

        List<Vertex> hangingNodes = interiorNode.getAssociatedNodes();

        // check for the correct number and type of hanging nodes
        if (hangingNodes.size() != 2
                || hangingNodes.stream().anyMatch(v -> v.getVertexType() != VertexType.HANGING_NODE)) {
            return Optional.empty();
        }

        // unpack a tuple in a language without pattern matching...
        Vertex v0 = interiorNode.getTriangleVertexes().getValue0();
        Vertex v1 = interiorNode.getTriangleVertexes().getValue1();
        Vertex v2 = interiorNode.getTriangleVertexes().getValue2();

        // check for the correct number and type of triangle vertices
        if (Stream.of(v0, v1, v2)
                .anyMatch(v -> v.getVertexType() != VertexType.SIMPLE_NODE)) {
            return Optional.empty();
        }

        OrientedInterior i = new OrientedInterior();
        i.i = interiorNode;

        // find the only one direct edge between triangle vertices and orient them
        if (v0.getEdgeBetween(v1) != null) {
            i.v1 = v0;
            i.v3 = v2;
            i.v5 = v1;
        } else if (v1.getEdgeBetween(v2) != null) {
            i.v1 = v1;
            i.v3 = v0;
            i.v5 = v2;
        } else {
            i.v1 = v2;
            i.v3 = v1;
            i.v5 = v0;
        }

        // orient the hanging nodes
        if (hangingNodes.get(0).getEdgeBetween(i.v1) != null) {
            i.h2 = hangingNodes.get(0);
            i.h4 = hangingNodes.get(1);
        } else {
            i.h2 = hangingNodes.get(1);
            i.h4 = hangingNodes.get(0);
        }

        // assert that all the necessary edges exist and so we're properly oriented
        i.e1 = graph.getEdgeById(i.v1.getEdgeBetween(i.h2).getId()).orElseThrow(() -> new RuntimeException("Unknown edge id"));
        i.e2 = graph.getEdgeById(i.h2.getEdgeBetween(i.v3).getId()).orElseThrow(() -> new RuntimeException("Unknown edge id"));
        i.e3 = graph.getEdgeById(i.v3.getEdgeBetween(i.h4).getId()).orElseThrow(() -> new RuntimeException("Unknown edge id"));
        i.e4 = graph.getEdgeById(i.h4.getEdgeBetween(i.v5).getId()).orElseThrow(() -> new RuntimeException("Unknown edge id"));
        i.e5 = graph.getEdgeById(i.v1.getEdgeBetween(i.v5).getId()).orElseThrow(() -> new RuntimeException("Unknown edge id"));
        i.eiv1 = graph.getEdgeById(i.v1.getEdgeBetween(i.i).getId()).orElseThrow(() -> new RuntimeException("Unknown edge id"));
        i.eiv3 = graph.getEdgeById(i.v3.getEdgeBetween(i.i).getId()).orElseThrow(() -> new RuntimeException("Unknown edge id"));
        i.eiv5 = graph.getEdgeById(i.v5.getEdgeBetween(i.i).getId()).orElseThrow(() -> new RuntimeException("Unknown edge id"));

        Stream<GraphEdge> allEdges = Stream.of(
                i.e1,
                i.e2,
                i.e3,
                i.e4,
                i.e5,
                i.eiv1,
                i.eiv3,
                i.eiv5
        );

        if (allEdges.anyMatch(Objects::isNull)) {
            return Optional.empty();
        }

        return Optional.of(i);
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        Optional<OrientedInterior> oi = orient(graph, interiorNode);
        OrientedInterior i = oi.orElseThrow(() -> new RuntimeException("Transformation error"));

        // delete the split border edge
        graph.deleteEdge(i.v1, i.v5);

        // remove the split interior
        graph.removeInterior(interiorNode.getId());

        // insert the new vertex on the midpoint of the split edge
        Point3d midpointV1V3 = Point3d.middlePoint(i.v1.getCoordinates(), i.v5.getCoordinates());
        Vertex newV = new Vertex(graph, interiorNode.getId(), VertexType.SIMPLE_NODE, midpointV1V3);
        graph.insertVertex(newV);

        // connect the new vertex with adjacent triangle vertices
        // and mark the appropriate edges as the border
        graph.insertEdge(i.v1.getId().concat(newV.getId()), i.v1, newV, true);
        graph.insertEdge(i.v3.getId().concat(newV.getId()), i.v3, newV, false);
        graph.insertEdge(i.v5.getId().concat(newV.getId()), i.v5, newV, true);

        // insert the new interiors without partition required
        graph.insertInterior(
                String.join("", i.v1.getId(), i.v3.getId(), newV.getId()),
                i.v1,
                i.v3,
                newV,
                i.h2
        ).setPartitionRequired(false);
        graph.insertInterior(
                String.join("", i.v3.getId(), i.v5.getId(), newV.getId()),
                i.v3,
                i.v5,
                newV,
                i.h4
        ).setPartitionRequired(false);

        return graph;
    }
}
