import model.*;
import org.javatuples.Triplet;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import transformation.Transformation;
import transformation.TransformationP3;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransformationP3Test extends AbstractTransformationTest {
    private Transformation transformation = new TransformationP3();

    @ParameterizedTest
    @MethodSource("graphs")
    public void transformationProducesTwoInteriorNodes(Triplet<ModelGraph, InteriorNode, Vertex> graph) {
        // Arrange
        assertEquals(1, graph.getValue0().getInteriors().size());

        // Act
        ModelGraph result = transformation.transformGraph(graph.getValue0(), graph.getValue1());

        // Assert
        assertEquals(2, result.getInteriors().size());
    }

    @ParameterizedTest
    @MethodSource("graphs")
    public void hangingBecomesSimpleNode(Triplet<ModelGraph, InteriorNode, Vertex> graph) {
        // Arrange
        assertEquals(1, graph.getValue0().getInteriors().size());

        // Act
        transformation.transformGraph(graph.getValue0(), graph.getValue1());

        // Assert
        assertEquals(VertexType.SIMPLE_NODE, graph.getValue2().getVertexType());
    }

    @ParameterizedTest
    @MethodSource("graphs")
    public void hasNoHangingNode(Triplet<ModelGraph, InteriorNode, Vertex> graph) {
        // Arrange
        assertEquals(1, graph.getValue0().getInteriors().size());

        // Act
        ModelGraph result = transformation.transformGraph(graph.getValue0(), graph.getValue1());

        // Assert
        assertEquals(0, result.getVertices().stream()
                .filter(e -> e.getVertexType().equals(VertexType.HANGING_NODE))
                .count());
    }

    private static Stream<Triplet<ModelGraph, InteriorNode, Vertex>> graphs()
    {
        return Stream.of(
                createGraph(),
                createGraph1()
        );
    }

    private static Triplet<ModelGraph, InteriorNode, Vertex> createGraph() {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.5, 0.5, 0.0));

        GraphEdge v2_h4 = graph.insertEdge("e1", v2, h4, true);
        GraphEdge v1_v3 = graph.insertEdge("e2", v1, v3, true);
        GraphEdge h4_v3 = graph.insertEdge("e3", h4, v3, true);
        GraphEdge v2_v3 = graph.insertEdge("e4", v2, v3, true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3);
        return new Triplet<>(graph, in1, h4);
    }

    private static Triplet<ModelGraph, InteriorNode, Vertex> createGraph1() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(5.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(0.0, 5.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(2.5, 2.5, 0.0));

        GraphEdge v2_h4 = graph.insertEdge("e1", v2, h4, true);
        GraphEdge v1_v3 = graph.insertEdge("e2", v1, v3, true);
        GraphEdge h4_v3 = graph.insertEdge("e3", h4, v3, true);
        GraphEdge v1_v2 = graph.insertEdge("e5", v1, v2, true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3);
        return new Triplet<>(graph, in1, h4);
    }


}
