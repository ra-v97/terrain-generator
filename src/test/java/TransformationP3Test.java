import model.*;
import org.javatuples.Triplet;
import org.junit.Test;
import transformation.Transformation;
import transformation.TransformationP3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransformationP3Test extends AbstractTransformationTest {
    private Transformation transformation = new TransformationP3();

    @Test
    public void conditionPassesWithObtuseTriangle() {
        // Arrange
        Triplet<ModelGraph, InteriorNode, Vertex> graph = createGraph();

        // Act/Assert
        assertTrue(transformation.isConditionCompleted(graph.getValue0(), graph.getValue1()));
    }

    @Test
    public void transformationProducesTwoInteriorNodes() {
        // Arrange
        Triplet<ModelGraph, InteriorNode, Vertex> graph = createGraph();
        assertEquals(1, graph.getValue0().getInteriors().size());

        // Act
        ModelGraph result = transformation.transformGraph(graph.getValue0(), graph.getValue1());

        // Assert
        assertEquals(2, result.getInteriors().size());
    }

    @Test
    public void hangingBecomesSimpleNode() {
        // Arrange
        Triplet<ModelGraph, InteriorNode, Vertex> graph = createGraph();
        assertEquals(1, graph.getValue0().getInteriors().size());

        // Act
        transformation.transformGraph(graph.getValue0(), graph.getValue1());

        // Assert
        assertEquals(VertexType.SIMPLE_NODE, graph.getValue2().getVertexType());
    }

    @Test
    public void hasNoHangingNode() {
        // Arrange
        Triplet<ModelGraph, InteriorNode, Vertex> graph = createGraph();
        assertEquals(1, graph.getValue0().getInteriors().size());

        // Act
        ModelGraph result = transformation.transformGraph(graph.getValue0(), graph.getValue1());

        // Assert
        assertEquals(0, result.getVertices().stream()
                .filter(e -> e.getVertexType().equals(VertexType.HANGING_NODE))
                .count());
    }

    private Triplet<ModelGraph, InteriorNode, Vertex> createGraph() {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));

        GraphEdge v1_h4 = graph.insertEdge("e1", v1, h4, true);
        GraphEdge v1_v3 = graph.insertEdge("e2", v1, v3, true);
        GraphEdge h4_v2 = graph.insertEdge("e3", h4, v2, true);
        GraphEdge v2_v3 = graph.insertEdge("e4", v2, v3, true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3);
        return new Triplet<>(graph, in1, h4);
    }
}
