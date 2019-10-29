import model.*;
import org.junit.Test;
import transformation.Transformation;
import transformation.TransformationP7;

import static org.junit.jupiter.api.Assertions.*;

public class TransformationP7Test {
    private Transformation transformation = new TransformationP7();

    @Test
    public void conditionPassesWithCorrectGraph() {
        ModelGraph graph = createCorrectGraph();
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithAllInsideEdges() {
        ModelGraph graph = createAllInsideEdgesGraph();
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithIncorrectLengths() {
        ModelGraph graph = createIncorrectLengthsGraph();
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithTooFewHangingNodes() {
        ModelGraph graph = createTooFewHangingNodesGraph();
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithTooManyHangingNodes() {
        ModelGraph graph = createTooManyHangingNodesGraph();
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void transformationResultsWithTwoInteriorNodes() {
        ModelGraph graph = createCorrectGraph();
        assertEquals(2, transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)).getInteriors().size());
    }

    @Test
    public void transformationProducesCorrectVertexTypes() {
        ModelGraph graph = createCorrectGraph();
        transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new));
        int hangingNodes = (int) graph.getVertices()
                .stream()
                .filter(v -> v.getVertexType() == VertexType.HANGING_NODE)
                .count();
        int simpleNodes = (int) graph.getVertices()
                .stream()
                .filter(v -> v.getVertexType() == VertexType.SIMPLE_NODE)
                .count();
        assertEquals(2, hangingNodes);
        assertEquals(4, simpleNodes);
    }

    @Test
    public void transformationProducesOneNewSimpleNode() {
        ModelGraph graph = createCorrectGraph();
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        assertEquals(VertexType.SIMPLE_NODE, graph.getVertex(interior.getId()).orElseThrow(AssertionError::new).getVertexType());
    }

    @Test
    public void transformationProducesOneNewNode() {
        ModelGraph graph = createCorrectGraph();
        int verticesNumber = graph.getVertices().size();
        assertEquals(verticesNumber + 1, transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)).getVertices().size());
    }

    @Test
    public void transformationProducesFiveNewEdges() {
        ModelGraph graph = createCorrectGraph();
        int edgesNumber = graph.getEdgeCount();
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        ModelGraph transformed = transformation.transformGraph(graph, interior);
        assertEquals(edgesNumber + 5, transformed.getEdges().size());
    }

    @Test
    public void transformationProducesCorrectEdges() {
        ModelGraph graph = createCorrectGraph();
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        Vertex v1 = graph.getVertex("v1").orElseThrow(AssertionError::new);
        Vertex v5 = graph.getVertex("v5").orElseThrow(AssertionError::new);
        Vertex v3 = graph.getVertex("v3").orElseThrow(AssertionError::new);

        transformation.transformGraph(graph, interior);

        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        GraphEdge edge1 = graph.getEdge(v1, newVertex).orElseThrow(AssertionError::new);
        GraphEdge edge2 = graph.getEdge(newVertex, v5).orElseThrow(AssertionError::new);
        GraphEdge edge3 = graph.getEdge(newVertex, v3).orElseThrow(AssertionError::new);

        assertTrue(edge1.getB() || edge2.getB() || !edge3.getB());
    }

    @Test
    public void transformationProducesNewExternalEdgesOfCorrectLength() {
        ModelGraph graph = createCorrectGraph();
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        Vertex v1 = graph.getVertex("v1").orElseThrow(AssertionError::new);
        Vertex v5 = graph.getVertex("v5").orElseThrow(AssertionError::new);
        GraphEdge oldEdge = graph.getEdge(v1, v5).orElseThrow(AssertionError::new);

        transformation.transformGraph(graph, interior);

        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        GraphEdge newEdge1 = graph.getEdge(v1, newVertex).orElseThrow(AssertionError::new);
        GraphEdge newEdge2 = graph.getEdge(newVertex, v5).orElseThrow(AssertionError::new);

        assertEquals(oldEdge.getL() / 2, newEdge1.getL(), newEdge2.getL());
    }

    @Test
    public void transformationProducesNewInternalEdgeOfCorrectLength() {
        ModelGraph graph = createCorrectGraph();
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);

        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex v3 = graph.getVertex("v3").orElseThrow(AssertionError::new);
        GraphEdge internalEdge = graph.getEdge(v3, newVertex).orElseThrow(AssertionError::new);

        assertEquals(Point3d.distance(newVertex.getCoordinates(), v3.getCoordinates()), internalEdge.getL());
    }

    @Test
    public void transformationProducesNewNodeOnProperEdge() {
        ModelGraph graph = createCorrectGraph();
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);

        Vertex vertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex v1 = graph.getVertex("v1").orElseThrow(AssertionError::new);
        Vertex v5 = graph.getVertex("v5").orElseThrow(AssertionError::new);
        assertEquals(Point3d.middlePoint(v1.getCoordinates(), v5.getCoordinates()), vertex.getCoordinates());
    }

    @Test
    public void transformationProducesNewInteriorNodesWithCorrectParams() {
        ModelGraph graph = createCorrectGraph();
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);

        transformation.transformGraph(graph, interior);
        InteriorNode interior1 = (InteriorNode) graph.getInteriors().toArray()[0];
        InteriorNode interior2 = (InteriorNode) graph.getInteriors().toArray()[1];

        assertFalse(interior1.isPartitionRequired() || interior2.isPartitionRequired());
    }

    private ModelGraph createAllInsideEdgesGraph() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(4.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(8.0, 0.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(7.0, 4.0, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(6.0, 8.0, 0.0));

        graph.insertEdge("e1", v1, h2, false);
        graph.insertEdge("e2", h2, v3, false);
        graph.insertEdge("e3", v3, h4, false);
        graph.insertEdge("e4", h4, v5, false);
        graph.insertEdge("e5", v1, v5, false);

        graph.insertInterior("i1", v1, v3, v5, h2, h4);
        return graph;
    }

    private ModelGraph createIncorrectLengthsGraph() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(4.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(8.0, 0.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(6.0, 1.0, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(2.0, 3.0, 0.0));

        graph.insertEdge("e1", v1, h2, false);
        graph.insertEdge("e2", h2, v3, false);
        graph.insertEdge("e3", v3, h4, false);
        graph.insertEdge("e4", h4, v5, false);
        graph.insertEdge("e5", v1, v5, true);

        graph.insertInterior("i1", v1, v3, v5, h2, h4);
        return graph;
    }

    private ModelGraph createCorrectGraph() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(4.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(8.0, 0.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(7.0, 4.0, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(6.0, 8.0, 0.0));

        graph.insertEdge("e1", v1, h2, false);
        graph.insertEdge("e2", h2, v3, false);
        graph.insertEdge("e3", v3, h4, false);
        graph.insertEdge("e4", h4, v5, false);
        graph.insertEdge("e5", v1, v5, true);

        graph.insertInterior("i1", v1, v3, v5, h2, h4);
        return graph;
    }

    private ModelGraph createTooFewHangingNodesGraph() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(4.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(8.0, 0.0, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(6.0, 8.0, 0.0));

        graph.insertEdge("e1", v1, h2, false);
        graph.insertEdge("e2", h2, v3, false);
        graph.insertEdge("e3", v3, v5, false);
        graph.insertEdge("e5", v1, v5, true);

        graph.insertInterior("i1", v1, v3, v5, h2);
        return graph;
    }

    private ModelGraph createTooManyHangingNodesGraph() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(4.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.HANGING_NODE, new Point3d(8.0, 0.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(7.0, 4.0, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(6.0, 8.0, 0.0));

        graph.insertEdge("e1", v1, h2, false);
        graph.insertEdge("e2", h2, v3, false);
        graph.insertEdge("e3", v3, h4, false);
        graph.insertEdge("e4", h4, v5, false);
        graph.insertEdge("e5", v1, v5, true);

        graph.insertInterior("i1", v1, v3, v5, h2, h4);
        return graph;
    }


}
