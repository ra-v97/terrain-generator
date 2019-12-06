package transformation;

import model.*;
import org.javatuples.Triplet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TransformationP2 implements Transformation {

    private static final String VERTEX_MAP_SIMPLE_VERTEX_1_KEY = "simpleVertex1";
    private static final String VERTEX_MAP_SIMPLE_VERTEX_2_KEY = "simpleVertex2";
    private static final String VERTEX_MAP_OPPOSITE_LONGEST_SIDE = "simpleVertex3";
    private static final String VERTEX_MAP_ON_LONGEST_SIDE = "simpleVertex4";

    private static Map<String, Vertex> mapTriangleVertexesToModel(ModelGraph graph, Triplet<Vertex, Vertex, Vertex> triangle) {

        Map<String, Vertex> triangleModel = new HashMap<>();

        Vertex v1 = triangle.getValue0();
        Vertex v2 = triangle.getValue1();
        Vertex v3 = triangle.getValue2();
        Vertex v4 = null;
        Optional<Vertex> v = getHangingVertexBetween(v1, v2, graph);
        if (v.isPresent()) {
            v4 = v.get();
            triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_1_KEY, v1);
            triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_2_KEY, v2);
            triangleModel.put(VERTEX_MAP_OPPOSITE_LONGEST_SIDE, v3);
        }
        v = getHangingVertexBetween(v1, v3, graph);
        if (v.isPresent()) {
            if (v4 != null) {
                throw new IllegalStateException();
            }
            v4 = v.get();
            triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_1_KEY, v1);
            triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_2_KEY, v3);
            triangleModel.put(VERTEX_MAP_OPPOSITE_LONGEST_SIDE, v2);
        }
        v = getHangingVertexBetween(v3, v2, graph);
        if (v.isPresent()) {
            if (v4 != null) {
                throw new IllegalStateException();
            }
            v4 = v.get();
            triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_1_KEY, v2);
            triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_2_KEY, v3);
            triangleModel.put(VERTEX_MAP_OPPOSITE_LONGEST_SIDE, v1);
        }
        if (v4 == null) {
            throw new IllegalStateException();
        }
        triangleModel.put(VERTEX_MAP_ON_LONGEST_SIDE, v4);
        return triangleModel;
    }

    private static Optional<Vertex> getHangingVertexBetween(Vertex v1, Vertex v2, ModelGraph graph) {
        if (v1.getEdgeBetween(v2) != null) return Optional.empty();

        List<Vertex> between = graph.getVertexesBetween(v1, v2);

        return between.stream().filter(e -> e.getVertexType() == VertexType.HANGING_NODE).findAny();
    }
    private static boolean hasHangingVertexBetween(Vertex v1, Vertex v2){
        return v1.getEdgeBetween(v2) != null;
    }

    private static boolean hasHangingVertex(InteriorNode interiorNode){
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        return hasHangingVertexBetween(triangle.getValue0(),triangle.getValue1()) ||
         hasHangingVertexBetween(triangle.getValue1(),triangle.getValue2()) ||
         hasHangingVertexBetween(triangle.getValue2(),triangle.getValue0());
    }

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        if (!hasHangingVertex(interiorNode)) {
            return false;
        }

        Map<String, Vertex> model = null;
        try {
            model = mapTriangleVertexesToModel(graph, triangle);
        } catch (IllegalStateException e) {
            return false; // more than one broken edge found in triangle
        }
        if (model.get(VERTEX_MAP_ON_LONGEST_SIDE).getVertexType() != VertexType.HANGING_NODE) {
            return false;
        }
        GraphEdge shortEdge1 = graph.getEdge(model.get(VERTEX_MAP_OPPOSITE_LONGEST_SIDE), model.get(VERTEX_MAP_SIMPLE_VERTEX_1_KEY))
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge shortEdge2 = graph.getEdge(model.get(VERTEX_MAP_OPPOSITE_LONGEST_SIDE), model.get(VERTEX_MAP_SIMPLE_VERTEX_2_KEY))
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge longEdge1 = graph.getEdge(model.get(VERTEX_MAP_SIMPLE_VERTEX_1_KEY), model.get(VERTEX_MAP_ON_LONGEST_SIDE))
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge longEdge2 = graph.getEdge(model.get(VERTEX_MAP_SIMPLE_VERTEX_2_KEY), model.get(VERTEX_MAP_ON_LONGEST_SIDE))
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        double lengthShort1 = shortEdge1.getL();
        double lengthShort2 = shortEdge2.getL();
        double lengthLong = longEdge1.getL() + longEdge2.getL();
        if (lengthLong < lengthShort1 || lengthLong < lengthShort2) {
            return false;
        }
        return true;
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        Map<String, Vertex> model;
        try {
            model = mapTriangleVertexesToModel(graph, triangle);
        } catch (IllegalStateException e) {
            throw new RuntimeException("Transformation error");
        }
        graph.removeInterior(interiorNode.getId());

        Vertex vertexOppositeLongest = model.get(VERTEX_MAP_OPPOSITE_LONGEST_SIDE);
        Vertex vertexOnLongest = model.get(VERTEX_MAP_ON_LONGEST_SIDE);
        String centralEdgeId = vertexOppositeLongest.getId().concat(vertexOnLongest.getId());
        GraphEdge centralEdge = graph.insertEdge(centralEdgeId, vertexOppositeLongest, vertexOnLongest);
        centralEdge.setB(false);

        Vertex v1 = model.get(VERTEX_MAP_SIMPLE_VERTEX_1_KEY);
        Vertex v2 = model.get(VERTEX_MAP_SIMPLE_VERTEX_2_KEY);
        String interior1Id = v1.getId().concat(vertexOnLongest.getId()).concat(vertexOppositeLongest.getId());
        InteriorNode in1 = graph.insertInterior(interior1Id, v1, vertexOnLongest, vertexOppositeLongest);
        in1.setPartitionRequired(false);
        String interior2Id = v2.getId().concat(vertexOnLongest.getId()).concat(vertexOppositeLongest.getId());
        InteriorNode in2 = graph.insertInterior(interior2Id, v2, vertexOnLongest, vertexOppositeLongest);
        in2.setPartitionRequired(false);

        return graph;
    }
}
