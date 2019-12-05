import model.*;
import org.javatuples.Pair;
import org.junit.Test;
import transformation.Transformation;
import transformation.TransformationP1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class TransformationP1Test extends AbstractTransformationTest {
    private Transformation transformation = new TransformationP1();

    @Test
    public void when_triangleHasHangingNode_then_conditionFails(){
        ModelGraph graph = createTriangle(true, VertexType.HANGING_NODE, VertexType.SIMPLE_NODE, VertexType.SIMPLE_NODE);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        assertFalse(transformation.isConditionCompleted(graph, interior));
    }

    @Test
    public void when_triangleDoesntHaveHangingNodesAndRequiresPartitioning_then_conditionPasses(){
        ModelGraph graph = createTriangle(true, VertexType.SIMPLE_NODE, VertexType.SIMPLE_NODE, VertexType.SIMPLE_NODE);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        assertTrue(transformation.isConditionCompleted(graph, interior));
    }

    @Test
    public void when_partitioningIsNotNeeded_then_conditionFails(){
        ModelGraph graph = createTriangle(false, VertexType.SIMPLE_NODE, VertexType.SIMPLE_NODE, VertexType.SIMPLE_NODE);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        assertFalse(transformation.isConditionCompleted(graph, interior));
    }

    @Test
    public void transformationProducesTwoInteriorNodes(){
        ModelGraph graph = createTriangle(true, VertexType.SIMPLE_NODE, VertexType.SIMPLE_NODE, VertexType.SIMPLE_NODE);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        assertEquals(2, transformation.transformGraph(graph, interior).getInteriors().size());
    }

    @Test
    public void transformationProducesOneNewVertex(){
        ModelGraph graph = createTriangle(true, VertexType.SIMPLE_NODE, VertexType.SIMPLE_NODE, VertexType.SIMPLE_NODE);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        int vertexCountBeforeTransformation = graph.getVertices().size();
        assertEquals(1, transformation.transformGraph(graph, interior).getVertices().size() - vertexCountBeforeTransformation);
    }

    @Test
    public void transformationProducesOnlySimpleVertices(){
        ModelGraph graph = createTriangle(true, VertexType.SIMPLE_NODE, VertexType.SIMPLE_NODE, VertexType.SIMPLE_NODE);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        Set<Vertex> oldVertices = new HashSet<>(graph.getVertices());
        transformation.transformGraph(graph, interior);
        Set<Vertex> newVertices = new HashSet<>(graph.getVertices());
        newVertices.removeAll(oldVertices);
        assertTrue(newVertices.stream().allMatch(v -> v.getVertexType() == VertexType.SIMPLE_NODE));
    }

    @Test
    public void transformationProducesNewInteriorNodesWithCorrectParams(){
        ModelGraph graph = createTriangle(true, VertexType.SIMPLE_NODE, VertexType.SIMPLE_NODE, VertexType.SIMPLE_NODE);
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);

        transformation.transformGraph(graph, interior);

        assertTrue(graph.getInteriors().stream().noneMatch(InteriorNode::isPartitionRequired));
    }

    private ModelGraph createTriangle(boolean needsPartitioning, VertexType vType1, VertexType vType2, VertexType vType3) {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = new Vertex(graph, "v1", vType1, new Point3d(0.0, 0.0, -42.0));
        Vertex v2 = new Vertex(graph, "v2", vType2, new Point3d(0.0, 10.0, -42.0));
        Vertex v3 = new Vertex(graph, "v3", vType3, new Point3d(6.0, 6.0, -42.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), true);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v2, v3), true);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), true);

        return populateTestGraph(graph, v1, v2, v3, e1, e2, e3, needsPartitioning);
    }

    @Test
    public void onePassThroughGraph(){
        ModelGraph graph = ultimateTestGenerator();

        graph.display();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Collection<InteriorNode> interiorNodes = new ArrayList<>(graph.getInteriors());
        for (InteriorNode i : interiorNodes) {
            if (!new HashSet<>(graph.getInteriors()).contains(i)) break;
            if (transformation.isConditionCompleted(graph, i)) {
                graph = transformation.transformGraph(graph, i);
            }
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ModelGraph ultimateTestGenerator() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(100.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(200.0, 0.0, 0.0));
        Vertex v4 = graph.insertVertex("v4", VertexType.SIMPLE_NODE, new Point3d(0.0, 100.0, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(100.0, 100.0, 0.0));
        Vertex v6 = graph.insertVertex("v6", VertexType.SIMPLE_NODE, new Point3d(200.0, 100.0, 0.0));

        GraphEdge v1v2 = graph.insertEdge("v1v2", v1, v2, true);
        GraphEdge v1v4 = graph.insertEdge("v1v4", v1, v4, true);
        GraphEdge v2v3 = graph.insertEdge("v2v3", v2, v3, true);
        GraphEdge v2v4 = graph.insertEdge("v2v4", v2, v4, false);
        GraphEdge v2v5 = graph.insertEdge("v2v5", v2, v5, false);
        GraphEdge v2v6 = graph.insertEdge("v2v6", v2, v6, false);
        GraphEdge v3v6 = graph.insertEdge("v3v6", v3, v6, true);
        GraphEdge v4v5 = graph.insertEdge("v4v5", v4, v5, true);
        GraphEdge v5v6 = graph.insertEdge("v5v6", v5, v6, true);

        InteriorNode in1 = graph.insertInterior("v1v2v4", v1, v2, v4);
        InteriorNode in2 = graph.insertInterior("v2v3v6", v2, v3, v6);
        InteriorNode in3 = graph.insertInterior("v2v4v5", v2, v4, v5);
        InteriorNode in4 = graph.insertInterior("v2v5v6", v2, v5, v6);

        in1.setPartitionRequired(true);
        in2.setPartitionRequired(true);
        in3.setPartitionRequired(true);
        in4.setPartitionRequired(true);

        return graph;
    }
}
