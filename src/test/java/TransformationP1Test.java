import model.*;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.junit.Test;
import transformation.Transformation;
import transformation.TransformationP1;

import static org.junit.jupiter.api.Assertions.*;


public class TransformationP1Test extends AbstractTransformationTest {
    private Transformation transformation = new TransformationP1();

    // TODO: Should transformations allow to be done if the condition fails?

    @Test
    public void conditionFailsWithAcuteTriangle(){
        ModelGraph graph = createAcuteTriangleGraph(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionPassesWithObtuseTriangle(){
        ModelGraph graph = createObtuseTriangleGraph(true);
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWhenPartitioningIsNotNeeded(){
        ModelGraph graph = createObtuseTriangleGraph(false);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithRightTriangle(){
        ModelGraph graph = createRightTriangleGraph(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithoutBorderEdges(){
        ModelGraph graph = createInternalObtuseTriangleGraph(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithImproperBorderEdges(){
        ModelGraph graph = createSemiInternalObtuseTriangleGraph(true);
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void transformationProducesTwoInteriorNodes(){
        ModelGraph graph = createObtuseTriangleGraph(true);
        assertEquals(2, transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)).getInteriors().size());
    }

    @Test
    public void transformationProducesOneNewVertex(){
        ModelGraph graph = createObtuseTriangleGraph(true);
        assertEquals(4, transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)).getVertices().size());
    }

    @Test
    public void transformationProducesOneNewSimpleVertex(){
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        assertEquals(VertexType.SIMPLE_NODE, graph.getVertex(interior.getId()).orElseThrow(AssertionError::new).getVertexType());
    }

    @Test
    public void transformationProducesTheNewVertexOnTheEdge(){
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex v1 = graph.getVertex("v1").orElseThrow(AssertionError::new);
        Vertex v2 = graph.getVertex("v2").orElseThrow(AssertionError::new);
        assertEquals(Point3d.middlePoint(v1.getCoordinates(), v2.getCoordinates()), newVertex.getCoordinates());
    }

    @Test
    public void transformationProducesNewInteriorNodesWithCorrectParams(){
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);

        transformation.transformGraph(graph, interior);
        InteriorNode interior1 = (InteriorNode) graph.getInteriors().toArray()[0];
        InteriorNode interior2 = (InteriorNode) graph.getInteriors().toArray()[1];

        assertTrue(!interior1.isPartitionRequired() && !interior2.isPartitionRequired());
    }


    @Test
    public void transformationProducesNewEdges(){
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        ModelGraph transformed = transformation.transformGraph(graph, interior);
        assertEquals(11, transformed.getEdges().size()); // TODO: Should we remove the old interior node and edges when transforming the graph??
    }

    @Test
    public void transformationProducesNewOppositeEdgesWithCorrectParams(){
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
    public void transformationProducesNewOppositeEdgesWithCorrectLength(){
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
    public void transformationProducesNewInternalEdgeWithCorrectParams(){
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex h = graph.getVertex("v3").orElseThrow(AssertionError::new);
        GraphEdge internalEdge = graph.getEdge(h, newVertex).orElseThrow(AssertionError::new);

        assertFalse(internalEdge.getB());
    }

    @Test
    public void transformationProducesNewInternalEdgeWithCorrectLength(){
        ModelGraph graph = createObtuseTriangleGraph(true);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        transformation.transformGraph(graph, interior);
        Vertex newVertex = graph.getVertex(interior.getId()).orElseThrow(AssertionError::new);
        Vertex h = graph.getVertex("v3").orElseThrow(AssertionError::new);
        GraphEdge internalEdge = graph.getEdge(h, newVertex).orElseThrow(AssertionError::new);

        assertEquals(Point3d.distance(newVertex.getCoordinates(), h.getCoordinates()), internalEdge.getL());
    }


    private ModelGraph createAcuteTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.HANGING_NODE, new Point3d(0.0, 0.0, -42.0));
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
        Vertex v3 = new Vertex(graph, "v3", VertexType.HANGING_NODE, new Point3d(1.0, 6.0, -8.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), true);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }

    private ModelGraph createInternalObtuseTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 12.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, -50.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.HANGING_NODE, new Point3d(1.0, 6.0, 0.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), false);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), false);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), false);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }

    private ModelGraph createSemiInternalObtuseTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, -1.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.HANGING_NODE, new Point3d(1.0, 6.0, 1.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), false);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), true);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }

    private ModelGraph createRightTriangleGraph(boolean needsPartitioning) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", VertexType.HANGING_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, 0.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(8.0, 10.0, 0.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), true);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }
}
