import model.*;
import org.javatuples.Pair;
import org.junit.Ignore;
import org.junit.Test;
import transformation.Transformation;
import transformation.TransformationP4;

import static org.junit.jupiter.api.Assertions.*;

public class TransformationP5Test extends AbstractTransformationTest {

    //todo Change P4 to P5 once it's implemented

    private Transformation transformation = new TransformationP4();

    @Ignore @Test
    public void conditionPassesWithObtuseTriangle() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Ignore @Test
    public void transformationProducesTwoInteriorNodes() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        assertEquals(2, transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)).getInteriors().size());
    }

    @Ignore @Test
    public void conditionFailsWithAcuteTriangle() {
        ModelGraph graph = createAcuteTriangleGraph(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Ignore @Test
    public void conditionFailsWhenPartitioningIsNotNeeded() {
        ModelGraph graph = createObtuseTriangleGraph(false);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Ignore @Test
    public void conditionFailsWithRightTriangle() {
        ModelGraph graph = createRightTriangleGraph(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Ignore @Test
    public void conditionFailsWithIsoscelesTriangle() {
        ModelGraph graph = createIsoscelesTriangleGraph(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Ignore @Test
    public void conditionFailsWithoutBorderEdges() {
        ModelGraph graph = createInternalObtuseTriangleGraph(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Ignore @Test
    public void conditionFailsWithImproperBorderEdges() {
        ModelGraph graph = createSemiInternalObtuseTriangleGraph(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Ignore @Test
    public void transformationProducesOneNewVertex() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        assertEquals(4, transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)).getVertices().size());
    }

    @Ignore @Test
    public void transformationProducesOneNewSimpleVertex() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        assertEquals(VertexType.SIMPLE_NODE, graph.getVertex(interior.getId()).orElseThrow(AssertionError::new).getVertexType());
    }

    @Ignore @Test
    public void transformationProducesTheNewVertexOnTheEdge() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex v1 = graph.getVertex("v1").orElseThrow(AssertionError::new);
        Vertex v3 = graph.getVertex("v3").orElseThrow(AssertionError::new);
        assertEquals(Point3d.middlePoint(v1.getCoordinates(), v3.getCoordinates()), newVertex.getCoordinates());
    }

    @Ignore @Test
    public void transformationProducesNewInteriorNodesWithCorrectParams() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);

        transformation.transformGraph(graph, interior);
        InteriorNode interior1 = (InteriorNode) graph.getInteriors().toArray()[0];
        InteriorNode interior2 = (InteriorNode) graph.getInteriors().toArray()[1];

        assertTrue(!interior1.isPartitionRequired() && !interior2.isPartitionRequired());
    }


    @Ignore @Test
    public void transformationProducesNewEdges() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        ModelGraph transformed = transformation.transformGraph(graph, interior);
        System.out.println(transformed.getEdges());
        assertEquals(12, transformed.getEdges().size());
    }

    @Ignore @Test
    public void transformationProducesNewOppositeEdgesWithCorrectParams() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        Vertex v1 = graph.getVertex("v1").orElseThrow(AssertionError::new);
        Vertex v3 = graph.getVertex("v3").orElseThrow(AssertionError::new);
        GraphEdge oldOpposite = graph.getEdge(v1, v3).orElseThrow(AssertionError::new);

        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        GraphEdge opposite1 = graph.getEdge(v1, newVertex).orElseThrow(AssertionError::new);
        GraphEdge opposite2 = graph.getEdge(newVertex, v3).orElseThrow(AssertionError::new);

        assertTrue(opposite1.getB() == opposite2.getB() == oldOpposite.getB());
    }

    @Ignore @Test
    public void transformationProducesNewOppositeEdgesWithCorrectLength() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        Vertex v1 = graph.getVertex("v1").orElseThrow(AssertionError::new);
        Vertex v3 = graph.getVertex("v3").orElseThrow(AssertionError::new);
        GraphEdge oldOpposite = graph.getEdge(v1, v3).orElseThrow(AssertionError::new);

        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        GraphEdge opposite1 = graph.getEdge(v1, newVertex).orElseThrow(AssertionError::new);
        GraphEdge opposite2 = graph.getEdge(newVertex, v3).orElseThrow(AssertionError::new);

        assertEquals(oldOpposite.getL(), opposite1.getL() + opposite2.getL());
    }

    @Ignore @Test
    public void transformationProducesNewInternalEdgeWithCorrectParams() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex h = graph.getVertex("v2").orElseThrow(AssertionError::new);
        GraphEdge internalEdge = graph.getEdge(h, newVertex).orElseThrow(AssertionError::new);

        assertFalse(internalEdge.getB());
    }

    @Ignore @Test
    public void transformationProducesNewInternalEdgeWithCorrectLength() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex h = graph.getVertex("v3").orElseThrow(AssertionError::new);
        GraphEdge internalEdge = graph.getEdge(h, newVertex).orElseThrow(AssertionError::new);

        assertEquals(Point3d.distance(newVertex.getCoordinates(), h.getCoordinates()), internalEdge.getL());
    }

    private ModelGraph createObtuseTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(10.0, 0.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(10.0, 10.0, 0.0));

        Vertex h = new Vertex(graph, "h1", VertexType.HANGING_NODE, new Point3d(6.0, 0.0, 0.0));

        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, h), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(h, v2), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v2, v3), true);
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v3, v1), false);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3, h);

        return populateTestGraphWithLists(graph, new Vertex[]{v1, v2, v3}, new GraphEdge[]{e1, e2, e3, e4}, in1, needsPartitioning);
    }

    private ModelGraph createAcuteTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 20.0, 0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(6.0, 6.0, 0));

        Vertex h = new Vertex(graph, "h1", VertexType.HANGING_NODE, new Point3d(3.0, 3.0, 0.0));

        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, h), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(h, v2), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v2, v3), true);
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v3, v1), false);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3, h);

        return populateTestGraphWithLists(graph, new Vertex[]{v1, v2, v3}, new GraphEdge[]{e1, e2, e3, e4}, in1, needsPartitioning);
    }

    private ModelGraph createInternalObtuseTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 12.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, -50.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(0.0, 6.0, 0.0));

        Vertex h = new Vertex(graph, "h1", VertexType.HANGING_NODE, new Point3d(3.0, -25.0, 0.0));

        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, h), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(h, v2), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v2, v3), true);
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v3, v1), false);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3, h);

        return populateTestGraphWithLists(graph, new Vertex[]{v1, v2, v3}, new GraphEdge[]{e1, e2, e3, e4}, in1, needsPartitioning);
    }

    private ModelGraph createSemiInternalObtuseTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(1.0, 6.0, 1.0));

        Vertex h = new Vertex(graph, "h1", VertexType.HANGING_NODE, new Point3d(0.0, 5.0, 0.0));

        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, h), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(h, v2), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v2, v3), true);
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v3, v1), false);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3, h);

        return populateTestGraphWithLists(graph, new Vertex[]{v1, v2, v3}, new GraphEdge[]{e1, e2, e3, e4}, in1, needsPartitioning);
    }

    private ModelGraph createRightTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(8.0, 10.0, 0.0));

        Vertex h = new Vertex(graph, "h1", VertexType.HANGING_NODE, new Point3d(4.0, 10.0, 0.0));

        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, h), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(h, v2), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v2, v3), true);
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v3, v1), false);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3, h);

        return populateTestGraphWithLists(graph, new Vertex[]{v1, v2, v3}, new GraphEdge[]{e1, e2, e3, e4}, in1, needsPartitioning);
    }

    private ModelGraph createIsoscelesTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(10.0, 0.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(5.0, 10.0, 0.0));

        Vertex h = new Vertex(graph, "h1", VertexType.HANGING_NODE, new Point3d(5.0, 0.0, 0.0));

        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, h), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(h, v2), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v2, v3), true);
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v3, v1), false);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3, h);

        return populateTestGraphWithLists(graph, new Vertex[]{v1, v2, v3}, new GraphEdge[]{e1, e2, e3, e4}, in1, needsPartitioning);
    }

}
