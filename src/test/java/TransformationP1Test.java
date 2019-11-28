import model.*;
import org.javatuples.Pair;
import org.junit.Test;
import transformation.Transformation;
import transformation.TransformationP1;


import static org.junit.jupiter.api.Assertions.*;


public class TransformationP1Test extends AbstractTransformationTest {
    private Transformation transformation = new TransformationP1();

    // TODO: Should transformations allow to be done if the condition fails?

    @Test
    public void conditionPassesWithAcuteTriangle() {
        ModelGraph graph = createAcuteTriangleGraph(true);
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionPassesWithObtuseTriangle() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWhenPartitioningIsNotNeeded() {
        ModelGraph graph = createObtuseTriangleGraph(false);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionPassesWithRightTriangle() {
        ModelGraph graph = createRightTriangleGraph(true);
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionPassesWithoutBorderEdges() {
        ModelGraph graph = createInternalObtuseTriangleGraph(true);
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionPassesWithImproperBorderEdges() {
        ModelGraph graph = createSemiInternalObtuseTriangleGraph(true);
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void transformationProducesTwoInteriorNodes() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        assertEquals(2, transformation.transformGraph(graph,
                graph.getInterior("i1").orElseThrow(AssertionError::new)).getInteriors().size());
    }

    @Test
    public void transformationProducesOneNewVertex() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        assertEquals(4, transformation.transformGraph(graph,
                graph.getInterior("i1").orElseThrow(AssertionError::new)).getVertices().size());
    }

    @Test
    public void transformationProducesOneNewSimpleVertex() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        assertEquals(VertexType.SIMPLE_NODE, graph.getVertex(interior.getId()).orElseThrow(AssertionError::new).getVertexType());
    }

    @Test
    public void transformationProducesTheNewVertexOnTheEdge() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex v1 = graph.getVertex("v1").orElseThrow(AssertionError::new);
        Vertex v2 = graph.getVertex("v2").orElseThrow(AssertionError::new);
        assertEquals(Point3d.middlePoint(v1.getCoordinates(), v2.getCoordinates()), newVertex.getCoordinates());
    }

    @Test
    public void transformationProducesNewInteriorNodesWithCorrectParams() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);

        transformation.transformGraph(graph, interior);
        InteriorNode interior1 = (InteriorNode) graph.getInteriors().toArray()[0];
        InteriorNode interior2 = (InteriorNode) graph.getInteriors().toArray()[1];

        assertTrue(!interior1.isPartitionRequired() && !interior2.isPartitionRequired());
    }


    @Test
    public void transformationProducesNewEdges() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        ModelGraph transformed = transformation.transformGraph(graph, interior);
        assertEquals(11, transformed.getEdges().size()); // TODO: Should we remove the old interior node and edges when transforming the graph??
    }

    @Test
    public void transformationProducesNewOppositeEdgesWithCorrectParams() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        Vertex v1 = graph.getVertex("v1").orElseThrow(AssertionError::new);
        Vertex v2 = graph.getVertex("v2").orElseThrow(AssertionError::new);
        GraphEdge oldOpposite = graph.getEdge(v1, v2).orElseThrow(AssertionError::new);

        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        GraphEdge opposite1 = graph.getEdge(v1, newVertex).orElseThrow(AssertionError::new);
        GraphEdge opposite2 = graph.getEdge(newVertex, v2).orElseThrow(AssertionError::new);

        assertTrue(opposite1.getB() == opposite2.getB() == oldOpposite.getB());
    }

    @Test
    public void transformationProducesNewOppositeEdgesWithCorrectLength() {
        ModelGraph graph = createObtuseTriangleGraph(true);
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
    public void transformationProducesNewInternalEdgeWithCorrectParams() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex h = graph.getVertex("v3").orElseThrow(AssertionError::new);
        GraphEdge internalEdge = graph.getEdge(h, newVertex).orElseThrow(AssertionError::new);

        assertFalse(internalEdge.getB());
    }

    @Test
    public void transformationProducesNewInternalEdgeWithCorrectLength() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex h = graph.getVertex("v3").orElseThrow(AssertionError::new);
        GraphEdge internalEdge = graph.getEdge(h, newVertex).orElseThrow(AssertionError::new);

        assertEquals(Point3d.distance(newVertex.getCoordinates(), h.getCoordinates()), internalEdge.getL());
    }


    @Test
    public void transformationProduceNewTerrain() {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, -42.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 100.0, -42.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(100.0, 0.0, -42.0));
        Vertex v4 = new Vertex(graph, "v4", VertexType.SIMPLE_NODE, new Point3d(100.0, 100.0, -42.0));
        Vertex v5 = new Vertex(graph, "v5", VertexType.SIMPLE_NODE, new Point3d(200.0, 0.0, -42.0));
        Vertex v6 = new Vertex(graph, "v6", VertexType.SIMPLE_NODE, new Point3d(200.0, 100.0, -42.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), false);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), false);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), true);
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v2, v4), false);
        GraphEdge e5 = new GraphEdge("e5", "E", new Pair<>(v4, v3), false);
        GraphEdge e6 = new GraphEdge("e6", "E", new Pair<>(v3, v6), false);
        GraphEdge e7 = new GraphEdge("e7", "E", new Pair<>(v4, v6), false);
        GraphEdge e8 = new GraphEdge("e8", "E", new Pair<>(v3, v5), true);
        GraphEdge e9 = new GraphEdge("e9", "E", new Pair<>(v5, v6), false);

        populateTransformationGraph(graph, v1, v2, v3, e1, e2, e3, true, "i1");
        populateTransformationGraph(graph, v2, v3, v4, e2, e4, e5, true, "i2");
        populateTransformationGraph(graph, v3, v4, v6, e5, e6, e7, true, "i3");
        populateTransformationGraph(graph, v3, v5, v6, e6, e8, e9, true, "i4");


        InteriorNode interior1 = graph.getInterior("i1").orElseThrow(AssertionError::new);
        assertTrue(transformation.isConditionCompleted(graph, interior1));
        if (transformation.isConditionCompleted(graph, interior1))
            transformation.transformGraph(graph, interior1);
        InteriorNode interior2 = graph.getInterior("i2").orElseThrow(AssertionError::new);
        assertFalse(transformation.isConditionCompleted(graph, interior2));
        if (transformation.isConditionCompleted(graph, interior2))
            transformation.transformGraph(graph, interior2);
        InteriorNode interior3 = graph.getInterior("i3").orElseThrow(AssertionError::new);
        assertTrue(transformation.isConditionCompleted(graph, interior3));
        if (transformation.isConditionCompleted(graph, interior3))
            transformation.transformGraph(graph, interior3);
        InteriorNode interior4 = graph.getInterior("i4").orElseThrow(AssertionError::new);
        assertFalse(transformation.isConditionCompleted(graph, interior4));
        if (transformation.isConditionCompleted(graph, interior4))
            transformation.transformGraph(graph, interior4);

        assertEquals(graph.getVertices().size(), 8);
        assertEquals(graph.getInteriors().size(), 6);
        assertEquals(graph.getEdges().size(), 6 * 3 + 7 + 4 + 2);//interiors + original+ divide + new one

        assertEquals(graph.getVertexBetween(v2, v3).get().getCoordinates(),
                Point3d.middlePoint(new Point3d(0.0, 100.0, -42.0), new Point3d(100.0, 0.0, -42.0)));
        assertEquals(graph.getVertexBetween(v3, v6).get().getCoordinates(),
                Point3d.middlePoint(new Point3d(100.0, 0.0, -42.0), new Point3d(200.0, 100.0, -42.0)));
        assertEquals(graph.getEdgeBetweenNodes(graph.getVertexBetween(v3, v6).get(), v3).get().getL(),
                100 * Math.sqrt(2.0) / 2, 0.001);
        assertEquals(graph.getEdgeBetweenNodes(graph.getVertexBetween(v3, v6).get(), v6).get().getL(),
                100 * Math.sqrt(2.0) / 2, 0.001);

        assertEquals(graph.getEdgeBetweenNodes(graph.getVertexBetween(v2, v3).get(), v1).get().getL(),
                100 * Math.sqrt(2.0) / 2, 0.001);

        assertEquals(graph.getInteriors().stream().map(InteriorNode::isPartitionRequired).filter(x -> x).count(), 2L);

        assertEquals(graph.getEdges().stream().map(GraphEdge::getB).filter(x -> x).count(), 2L);


    }

    public ModelGraph populateTransformationGraph(ModelGraph graph, Vertex ve1, Vertex ve2, Vertex ve3, GraphEdge ge1,
                                                  GraphEdge ge2, GraphEdge ge3, boolean partitionRequired, String name) {

        Vertex v1 = graph.getVertex(ve1.getId()).orElseGet(() -> graph.insertVertex(ve1));
        Vertex v2 = graph.getVertex(ve2.getId()).orElseGet(() -> graph.insertVertex(ve2));
        Vertex v3 = graph.getVertex(ve3.getId()).orElseGet(() -> graph.insertVertex(ve3));
        if (graph.getEdge(ge1.getId()) == null)
            graph.insertEdge(ge1);
        if (graph.getEdge(ge2.getId()) == null)
            graph.insertEdge(ge2);
        if (graph.getEdge(ge3.getId()) == null)
            graph.insertEdge(ge3);
        InteriorNode in1 = graph.insertInterior(name, v1, v2, v3);
        in1.setPartitionRequired(partitionRequired);

        return graph;
    }


    private ModelGraph createAcuteTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, -42.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, -42.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(6.0, 6.0, -42.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), true);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }

    private ModelGraph createObtuseTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.2, 0.0, -6.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, 2.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(1.0, 6.0, -8.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), true);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }

    private ModelGraph createInternalObtuseTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 12.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, -50.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(1.0, 6.0, 0.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), false);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), false);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), false);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }

    private ModelGraph createSemiInternalObtuseTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, -1.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(1.0, 6.0, 1.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), false);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), true);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }

    private ModelGraph createRightTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(8.0, 10.0, 0.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), true);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }
}
