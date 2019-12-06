import model.*;
import org.javatuples.Pair;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import transformation.Transformation;
import transformation.TransformationP3;

import static org.junit.jupiter.api.Assertions.*;

public class TransformationP3Test extends AbstractTransformationTest {
    private Transformation transformation = new TransformationP3();

    private static ModelGraph[] graphs() {
        return new ModelGraph[]{
                createAcuteTriangleGraph(true),
                createRectangularTriangleGraph(true),
                createObtuseTriangleGraph(true)};
    }

    @ParameterizedTest
    @MethodSource("graphs")
    public void conditionPassesWithDifferentTriangleTypes(ModelGraph graph) {
        InteriorNode i1 = graph.getInterior("i1").orElseThrow(AssertionError::new);
        assertTrue(transformation.isConditionCompleted(graph, i1));
    }


    @ParameterizedTest
    @MethodSource("graphs")
    public void transformationProducesTwoInteriorNodesInDifferentTriangleTypes(ModelGraph graph) {
        InteriorNode i1 = graph.getInterior("i1").orElseThrow(AssertionError::new);
        assertEquals(2, transformation.transformGraph(graph, i1).getInteriors().size());
    }

    @Test
    public void conditionFailsWithRightTriangle() {
        ModelGraph graph = createRightTriangleGraph(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithoutBorderEdges() {
        ModelGraph graph = createInternalObtuseTriangleGraph(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithImproperBorderEdges() {
        ModelGraph graph = createSemiInternalObtuseTriangleGraph(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @ParameterizedTest
    @MethodSource("graphs")
    public void transformationProducesOneNewSimpleVertex(ModelGraph graph) {
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        VertexType vertexType = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new).getVertexType();
        assertEquals(VertexType.SIMPLE_NODE, vertexType);
    }

    @ParameterizedTest
    @MethodSource("graphs")
    public void transformationProducesTheNewVertexOnTheEdge(ModelGraph graph) {
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex v1 = graph.getVertex("v1").orElseThrow(AssertionError::new);
        Vertex v3 = graph.getVertex("v3").orElseThrow(AssertionError::new);
        assertEquals(Point3d.middlePoint(v1.getCoordinates(), v3.getCoordinates()), newVertex.getCoordinates());
    }

    @ParameterizedTest
    @MethodSource("graphs")
    public void transformationProducesNewInteriorNodesWithCorrectParams(ModelGraph graph) {
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);

        transformation.transformGraph(graph, interior);
        InteriorNode interior1 = (InteriorNode) graph.getInteriors().toArray()[0];
        InteriorNode interior2 = (InteriorNode) graph.getInteriors().toArray()[1];

        assertTrue(!interior1.isPartitionRequired() && !interior2.isPartitionRequired());
    }


    @ParameterizedTest
    @MethodSource("graphs")
    public void transformationProducesNewEdges() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        ModelGraph transformed = transformation.transformGraph(graph, interior);
        assertEquals(12, transformed.getEdges().size());
    }

    @ParameterizedTest
    @MethodSource("graphs")
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

    @ParameterizedTest
    @MethodSource("graphs")
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

    @ParameterizedTest
    @MethodSource("graphs")
    public void transformationProducesNewInternalEdgeWithCorrectParams() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex h = graph.getVertex("v2").orElseThrow(AssertionError::new);
        GraphEdge internalEdge = graph.getEdge(h, newVertex).orElseThrow(AssertionError::new);

        assertFalse(internalEdge.getB());
    }

    @ParameterizedTest
    @MethodSource("graphs")
    public void transformationProducesNewInternalEdgeWithCorrectLength() {
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex h = graph.getVertex("v3").orElseThrow(AssertionError::new);
        GraphEdge internalEdge = graph.getEdge(h, newVertex).orElseThrow(AssertionError::new);

        assertEquals(Point3d.distance(newVertex.getCoordinates(), h.getCoordinates()), internalEdge.getL());
    }

    private static ModelGraph createObtuseTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(10.0, 0.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(12.0, 10.0, 0.0));

        Vertex h = new Vertex(graph, "h1", VertexType.HANGING_NODE, new Point3d(5.0, 0.0, 0.0));

        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, h), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(h, v2), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v2, v3), true);
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v3, v1), true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3, h);

        return populateTestGraphWithLists(graph, new Vertex[]{v1, v2, v3, h}, new GraphEdge[]{e1, e2, e3, e4}, in1, needsPartitioning);
    }

    private static ModelGraph createAcuteTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(10.0, 0.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(6.0, 10.0, 0.0));

        Vertex h = new Vertex(graph, "h1", VertexType.HANGING_NODE, new Point3d(5.0, 0.0, 0.0));

        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, h), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(h, v2), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v2, v3), true);
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v3, v1), true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3, h);

        return populateTestGraphWithLists(graph, new Vertex[]{v1, v2, v3, h}, new GraphEdge[]{e1, e2, e3, e4}, in1, needsPartitioning);
    }

    private static ModelGraph createRectangularTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(10.0, 0.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(10.0, 10.0, 0.0));

        Vertex h = new Vertex(graph, "h1", VertexType.HANGING_NODE, new Point3d(5.0, 0.0, 0.0));

        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, h), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(h, v2), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v2, v3), true);
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v3, v1), true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3, h);

        return populateTestGraphWithLists(graph, new Vertex[]{v1, v2, v3, h}, new GraphEdge[]{e1, e2, e3, e4}, in1, needsPartitioning);
    }

    private ModelGraph createInternalObtuseTriangleGraph(boolean needsPartitioning) {  //todo 3d?
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 12.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, -50.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(0.0, 6.0, 0.0));

        Vertex h = new Vertex(graph, "h1", VertexType.HANGING_NODE, new Point3d(3.0, -25.0, 0.0));

        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, h), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(h, v2), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v2, v3), true);
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v3, v1), true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3, h);

        return populateTestGraphWithLists(graph, new Vertex[]{v1, v2, v3, h}, new GraphEdge[]{e1, e2, e3, e4}, in1, needsPartitioning);
    }

    private ModelGraph createSemiInternalObtuseTriangleGraph(boolean needsPartitioning) { //todo 3d?
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(1.0, 6.0, 1.0));

        Vertex h = new Vertex(graph, "h1", VertexType.HANGING_NODE, new Point3d(0.0, 5.0, 0.0));

        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, h), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(h, v2), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v2, v3), true);
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v3, v1), true);

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
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v3, v1), true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3, h);

        return populateTestGraphWithLists(graph, new Vertex[]{v1, v2, v3, h}, new GraphEdge[]{e1, e2, e3, e4}, in1, needsPartitioning);
    }

}
