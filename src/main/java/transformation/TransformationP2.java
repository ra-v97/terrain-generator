package transformation;

import model.*;
import org.javatuples.Triplet;

import java.util.HashMap;
import java.util.Map;

public class TransformationP2 implements Transformation {

    private static final String VERTEX_MAP_SIMPLE_VERTEX_3_KEY = "simpleVertex3";

    private static final String VERTEX_MAP_SIMPLE_VERTEX_1_KEY = "simpleVertex1";

    private static final String VERTEX_MAP_SIMPLE_VERTEX_2_KEY = "simpleVertex2";

    private static Map<String, Vertex> mapTriangleVertexesToModel(Triplet<Vertex, Vertex, Vertex> triangle) {
        Map<String, Vertex> triangleModel = new HashMap<>();
        for (Object o : triangle) {
            Vertex v = (Vertex) o;
            if (triangleModel.get(VERTEX_MAP_SIMPLE_VERTEX_1_KEY) == null) {
                triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_1_KEY, v);
            } else if (triangleModel.get(VERTEX_MAP_SIMPLE_VERTEX_2_KEY) == null) {
                triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_2_KEY, v);
            } else {
                triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_3_KEY, v);
            }
        }
        return triangleModel;
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

    private static int getHangingVertexCount(Triplet<Vertex, Vertex, Vertex> triangle) {
        int count = 0;
        for (Object o : triangle) {
            Vertex v = (Vertex) o;
            if (v.getVertexType() == VertexType.HANGING_NODE) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        if (!isGraphValidForTransformation(interiorNode, triangle)) return false;

        Map<String, Vertex> model = mapTriangleVertexesToModel(triangle);
        Vertex simpleVertex1 = model.get(VERTEX_MAP_SIMPLE_VERTEX_1_KEY);
        Vertex simpleVertex2 = model.get(VERTEX_MAP_SIMPLE_VERTEX_2_KEY);
        Vertex simpleVertex3 = model.get(VERTEX_MAP_SIMPLE_VERTEX_3_KEY);

        if (simpleVertex1 == null || simpleVertex2 == null || simpleVertex3 == null) {
            throw new RuntimeException("Transformation error");
        }

        GraphEdge edge1 = graph.getEdgeBetweenNodes(simpleVertex1, simpleVertex2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge edge2 = graph.getEdgeBetweenNodes(simpleVertex2, simpleVertex3)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge edge3 = graph.getEdgeBetweenNodes(simpleVertex3, simpleVertex2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));

        return isConditionFulfilled(edge1, edge2, edge3);
    }

    private boolean isGraphValidForTransformation(InteriorNode interiorNode, Triplet<Vertex, Vertex, Vertex> triangle) {
        if (!interiorNode.isPartitionRequired()) {
            return false;
        }
        if (getSimpleVertexCount(triangle) != 3 || getHangingVertexCount(triangle) != 0) {
            return false;
        }
        return true;
    }

    private boolean isConditionFulfilled(GraphEdge edge1, GraphEdge edge2, GraphEdge edge3) {
        return !edge1.getB() && isEdgeLengthConditionFulfilled(edge1, edge2, edge3)
                && !(!edge2.getB() || edge1.getL() != edge2.getL()) && (!edge3.getB() || edge3.getL() != edge1.getL());
    }

    private boolean isEdgeLengthConditionFulfilled(GraphEdge edge1, GraphEdge edge2, GraphEdge edge3) {
        return !(edge1.getL() < edge2.getL()) && !(edge1.getL() < edge3.getL());
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        Map<String, Vertex> model = mapTriangleVertexesToModel(interiorNode.getTriangleVertexes());

        Vertex simpleVertex1 = model.get(VERTEX_MAP_SIMPLE_VERTEX_1_KEY);
        Vertex simpleVertex2 = model.get(VERTEX_MAP_SIMPLE_VERTEX_2_KEY);
        Vertex simpleVertex3 = model.get(VERTEX_MAP_SIMPLE_VERTEX_3_KEY);

        GraphEdge edge1 = graph.getEdgeBetweenNodes(simpleVertex1, simpleVertex2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));

        //transformation process
        graph.removeInterior(interiorNode.getId());
        graph.deleteEdge(simpleVertex1, simpleVertex2);

        Vertex insertedVertex = graph.insertVertex(interiorNode.getId(),
                VertexType.HANGING_NODE,
                Point3d.middlePoint(simpleVertex1.getCoordinates(), simpleVertex2.getCoordinates()));

        String newEdge1Id = simpleVertex1.getId().concat(insertedVertex.getId());
        String newEdge2Id = simpleVertex2.getId().concat(insertedVertex.getId());
        String newEdge3Id = simpleVertex3.getId().concat(insertedVertex.getId());

        GraphEdge insertedEdge1 = graph.insertEdge(newEdge1Id, simpleVertex1, insertedVertex);
        insertedEdge1.setB(edge1.getB());

        GraphEdge insertedEdge2 = graph.insertEdge(newEdge2Id, simpleVertex2, insertedVertex);
        insertedEdge2.setB(edge1.getB());

        GraphEdge insertedEdge3 = graph.insertEdge(newEdge3Id, simpleVertex3, insertedVertex);
        insertedEdge3.setB(false);

        String insertedInterior1Id = simpleVertex3.getId().concat(simpleVertex1.getId()).concat(insertedVertex.getId());
        String insertedInterior2Id = simpleVertex3.getId().concat(simpleVertex2.getId()).concat(insertedVertex.getId());
        graph.insertInterior(insertedInterior1Id, simpleVertex3, simpleVertex1, insertedVertex);
        graph.insertInterior(insertedInterior2Id, simpleVertex3, simpleVertex2, insertedVertex);
        return graph;
    }
}
