import model.*;
import org.javatuples.Pair;
import org.junit.Test;
import transformation.Transformation;
import transformation.TransformationP2;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransformationP2Test extends AbstractTransformationTest {
    private Transformation transformation = new TransformationP2();

    @Test
    public void conditionPassWithCorrectTriangle(){
        ModelGraph graph = createCorrectTriangleGraph(true);
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionWithRotatedTriangle(){
        ModelGraph graph = createRotatedTriangleGraph(true);
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWhenPartitioningIsNotNeeded(){
        ModelGraph graph = createCorrectTriangleGraph(false);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithIsoscelesTriangle(){
        ModelGraph graph = createIsoscelesTriangle(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithGraphWithHangingNode(){
        ModelGraph graph = createTriangleGraphWithHangingNode(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithImproperBorderEdges(){
        ModelGraph graph = createTriangleGraphWithIncorrectBorder(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void transformationProducesTwoInteriorNodes(){
        ModelGraph graph = createCorrectTriangleGraph(true);
        assertEquals(2, transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)).getInteriors().size());
    }

    @Test
    public void transformationProducesOneNewVertex(){
        ModelGraph graph = createCorrectTriangleGraph(true);
        assertEquals(4, transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)).getVertices().size());
    }

    @Test
    public void transformationProducesOneNewHangingVertex(){
        ModelGraph graph = createCorrectTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        assertEquals(VertexType.HANGING_NODE, graph.getVertex(interior.getId()).orElseThrow(AssertionError::new).getVertexType());
    }

    @Test
    public void transformationProducesTheNewVertexOnTheEdge(){
        ModelGraph graph = createCorrectTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex v1 = graph.getVertex("v1").orElseThrow(AssertionError::new);
        Vertex v2 = graph.getVertex("v2").orElseThrow(AssertionError::new);
        assertEquals(Point3d.middlePoint(v1.getCoordinates(), v2.getCoordinates()), newVertex.getCoordinates());
    }

    @Test
    public void transformationProducesNewInteriorNodesWithCorrectParams(){
        ModelGraph graph = createCorrectTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);

        transformation.transformGraph(graph, interior);
        InteriorNode interior1 = (InteriorNode) graph.getInteriors().toArray()[0];
        InteriorNode interior2 = (InteriorNode) graph.getInteriors().toArray()[1];

        assertTrue(!interior1.isPartitionRequired() && !interior2.isPartitionRequired());
    }

    @Test
    public void transformationProducesNewEdges(){
        ModelGraph graph = createCorrectTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        ModelGraph transformed = transformation.transformGraph(graph, interior);
        assertEquals(11, transformed.getEdges().size());
    }

    @Test
    public void transformationProducesNewOppositeEdgesWithCorrectLength(){
        ModelGraph graph = createCorrectTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        Vertex v1 = graph.getVertex("v1").orElseThrow(AssertionError::new);
        Vertex v2 = graph.getVertex("v2").orElseThrow(AssertionError::new);
        GraphEdge oldOpposite = graph.getEdge(v1, v2).orElseThrow(AssertionError::new);

        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        GraphEdge opposite1 = graph.getEdge(v1, newVertex).orElseThrow(AssertionError::new);
        GraphEdge opposite2 = graph.getEdge(newVertex, v2).orElseThrow(AssertionError::new);

        assertEquals(oldOpposite.getL(), opposite1.getL() + opposite2.getL());
    }

    @Test
    public void transformationProducesNewInternalEdgeWithCorrectParams(){
        ModelGraph graph = createCorrectTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex h = graph.getVertex("v3").orElseThrow(AssertionError::new);
        GraphEdge internalEdge = graph.getEdge(h, newVertex).orElseThrow(AssertionError::new);

        assertFalse(internalEdge.getB());
    }

    @Test
    public void transformationProducesNewInternalEdgeWithCorrectLength(){
        ModelGraph graph = createCorrectTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex h = graph.getVertex("v3").orElseThrow(AssertionError::new);
        GraphEdge internalEdge = graph.getEdge(h, newVertex).orElseThrow(AssertionError::new);

        assertEquals(Point3d.distance(newVertex.getCoordinates(), h.getCoordinates()), internalEdge.getL());
    }


    private ModelGraph createCorrectTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(6.0, 6.0, 0.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), false);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), true);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }

    private ModelGraph createRotatedTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(6.0, 20.0, 0.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v2, v3), false);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v3, v1), false);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v1, v2), false);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }

    private ModelGraph createTriangleGraphWithHangingNode(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.HANGING_NODE, new Point3d(0.0, 0.0, 12.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, -50.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(1.0, 6.0, 0.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), false);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), false);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v1, v1), false);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }

    private ModelGraph createTriangleGraphWithIncorrectBorder(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, -1.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(1.0, 6.0, 1.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), true);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }

    private ModelGraph createIsoscelesTriangle(boolean needsPartitioning) {     //L1==L3
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(6.0, 8.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), false);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), true);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }
}
