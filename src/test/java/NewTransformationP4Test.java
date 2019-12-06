import model.*;
import org.graphstream.graph.Edge;
import org.junit.Test;
import transformation.Transformation;
import new_transformation.TransformationP4;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NewTransformationP4Test extends AbstractTransformationTest {
    private Transformation transformation = new TransformationP4();

    @Test
    public void testGraphShouldBeCorrectlyTransformed(){
        ModelGraph graph = createTestGraph();
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i2").orElseThrow(AssertionError::new)));
        List<Vertex> vertices = new LinkedList<>(graph.getVertices());
        List<Edge> edges = new LinkedList<>(graph.getEdges());
        List<InteriorNode> interiors = new LinkedList<>(graph.getInteriors());

        interiors.forEach(interior -> transformation.transformGraph(graph, interior));

        assertEquals(vertices.size(), graph.getVertices().size());
        assertEquals(interiors.size() + 1, graph.getInteriors().size());
        assertEquals(edges.size() + 4, graph.getEdges().size());

        assertFalse(graph.getInterior("i2").isPresent());
        assertTrue(graph.getInterior("i2i1").isPresent());
        assertTrue(graph.getInterior("i2i2").isPresent());
        assertTrue(graph.getEdgeById("i2e1").isPresent());

        Edge newEdge = graph.getEdgeById("i2e1").orElseThrow(AssertionError::new);
        Edge edgeInGraph = graph.getEdgeBetweenNodes(
                graph.getVertex("v4").orElseThrow(AssertionError::new),
                graph.getVertex("v3").orElseThrow(AssertionError::new)
        ).orElseThrow(AssertionError::new);

        assertEquals(newEdge, edgeInGraph);
    }

    private ModelGraph createTestGraph() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 100.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(50.0, 50.0, 0.0));
        Vertex v4 = graph.insertVertex("v4", VertexType.SIMPLE_NODE, new Point3d(100.0, 100.0, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(100.0, 50.0, 0.0));
        Vertex v6 = graph.insertVertex("v6", VertexType.SIMPLE_NODE, new Point3d(100.0, 0.0, 0.0));
        Vertex v7 = graph.insertVertex("v7", VertexType.SIMPLE_NODE, new Point3d(150.0, 50.0, 0.0));
        Vertex v8 = graph.insertVertex("v8", VertexType.SIMPLE_NODE, new Point3d(200.0, 0.0, 0.0));
        Vertex v9 = graph.insertVertex("v9", VertexType.SIMPLE_NODE, new Point3d(250.0, 50.0, 0.0));
        Vertex v10 = graph.insertVertex("v10", VertexType.SIMPLE_NODE, new Point3d(300.0, 100.0, 0.0));
        Vertex v11 = graph.insertVertex("v11", VertexType.SIMPLE_NODE, new Point3d(300.0, 0.0, 0.0));

        graph.insertEdge("e1", v1, v4,  false);
        graph.insertEdge("e2", v4, v10,  false);
        graph.insertEdge("e3", v10, v11,  false);
        graph.insertEdge("e4", v11, v6,  false);
        graph.insertEdge("e5", v6, v2,  false);
        graph.insertEdge("e6", v2, v1,  false);

        graph.insertEdge("e8", v1, v3,  false);
        graph.insertEdge("e9", v3, v6,  false);
        graph.insertEdge("e10", v2, v3,  false);
        graph.insertEdge("e11", v4, v7,  false);
        graph.insertEdge("e12", v5, v7,  false);
        graph.insertEdge("e13", v6, v7,  false);
        graph.insertEdge("e14", v7, v8,  false);
        graph.insertEdge("e15", v8, v9,  false);
        graph.insertEdge("e16", v9, v10,  false);
        graph.insertEdge("e17", v9, v11,  false);
        graph.insertEdge("e18", v5, v6,  false);
        graph.insertEdge("e19", v4, v5,  false);

        graph.insertInterior("i1", v1, v2, v3);
        graph.insertInterior("i2", v1, v4, v6, v3, v5);
        graph.insertInterior("i3", v2, v3, v6);
        graph.insertInterior("i4", v4, v5, v7);
        graph.insertInterior("i5", v5, v6, v7);
        graph.insertInterior("i6", v6, v7, v8);
        graph.insertInterior("i7", v8, v9, v11);
        graph.insertInterior("i8", v9, v10, v11);
        graph.insertInterior("i9", v4, v8, v10, v7, v9);
        return graph;
    }
}
