import model.*;
import org.junit.Ignore;
import org.junit.Test;
import transformation.TransformationP9;

import static org.junit.jupiter.api.Assertions.*;

@Ignore
public class TransformationP9Test extends AbstractTransformationTest {
    private TransformationP9 transformation = new TransformationP9();

    @Test
    public void conditionPassesWithCorrectGraph(){
        ModelGraph graph = createCorrectGraph();
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionPassesForRotatedGraph(){
        ModelGraph graph = createRotatedGraph();
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithTooFewHangingNodes(){
        ModelGraph graph = createGraphWithTooFewHangingNodes();
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithGraphWithOnlyHangingNodes(){
        ModelGraph graph = createGraphWithOnlyHangingNodes();
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsWithGraphWithOnlySimpleNodes(){
        ModelGraph graph = createGraphWithOnlySimpleNodes();
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionFailsForTooManyNodes(){
        ModelGraph graph = createGraphWithTooManyNodes();
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void transformationProducesTwoInteriorNodes(){
        ModelGraph graph = createCorrectGraph();
        assertEquals(2, transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)).getInteriors().size());
    }

    @Test
    public void transformationProducesCorrectVertexTypes(){
        ModelGraph graph = createCorrectGraph();
        transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new));
        int hanging_nodes = 0;
        int simple_nodes = 0;
        for(Vertex v: graph.getVertices()){
            if(v.getVertexType() == VertexType.SIMPLE_NODE){
                simple_nodes++;
            }
            else{
                hanging_nodes++;
            }
        }
        assertEquals(4,simple_nodes);
        assertEquals(2, hanging_nodes);
    }

    @Test
    public void transformationCreatesCorrectNumberOfEdges(){
        ModelGraph graph = createCorrectGraph();
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        ModelGraph transformedGraph = transformation.transformGraph(graph, interior);
        assertEquals(13, transformedGraph.getEdges().size());
    }

    @Test
    public void conditionFailsForGraphWithNewInterior(){
        ModelGraph graph = createCorrectGraph();
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        ModelGraph transformedGraph = transformation.transformGraph(graph, interior);
        assertFalse(transformation.isConditionCompleted(transformedGraph, graph.getInterior("v1h2v5").orElseThrow(AssertionError::new)));
    }

    private ModelGraph createGraphWithTooManyNodes() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.5, 0.5, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
        Vertex h6 = graph.insertVertex("h6", VertexType.HANGING_NODE, new Point3d(0.5, 0.5, 0.0));
        Vertex h7 = graph.insertVertex("h7", VertexType.HANGING_NODE, new Point3d(0.75, 0.75, 0.0));


        GraphEdge v1_h2 = graph.insertEdge("e1", v1, h2, true);
        GraphEdge h2_v3 = graph.insertEdge("e2", h2, v3, true);
        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, true);
        GraphEdge h4_v5 = graph.insertEdge("e4", h4, v5, true);
        GraphEdge v5_h7 = graph.insertEdge("e5", v5, h7, true);
        GraphEdge h6_h7 = graph.insertEdge("e6", h6, h7, true);
        GraphEdge h6_v1 = graph.insertEdge("e7", h6, v1, true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h2, h4, h6, h7);
        return graph;
    }

    private ModelGraph createRotatedGraph() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(2.0, 1.0, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(2.0, 2.0, 0.0));
        Vertex h6 = graph.insertVertex("h6", VertexType.HANGING_NODE, new Point3d(1.41, 1.41, 0.0));

        GraphEdge v1_h2 = graph.insertEdge("e1", v1, h2, true);
        GraphEdge h2_v3 = graph.insertEdge("e2", h2, v3, true);
        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, true);
        GraphEdge h4_v5 = graph.insertEdge("e4", h4, v5, true);
        GraphEdge v5_h6 = graph.insertEdge("e5", v5, h6, true);
        GraphEdge h6_v1 = graph.insertEdge("e6", h6, v1, true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h2, h4, h6);
        return graph;
    }

    private ModelGraph createCorrectGraph() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.5, 0.5, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
        Vertex h6 = graph.insertVertex("h6", VertexType.HANGING_NODE, new Point3d(0.5, 0.5, 0.0));

        GraphEdge v1_h2 = graph.insertEdge("e1", v1, h2, true);
        GraphEdge h2_v3 = graph.insertEdge("e2", h2, v3, true);
        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, true);
        GraphEdge h4_v5 = graph.insertEdge("e4", h4, v5, true);
        GraphEdge v5_h6 = graph.insertEdge("e5", v5, h6, true);
        GraphEdge h6_v1 = graph.insertEdge("e6", h6, v1, true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h2, h4, h6);
        return graph;
    }

    private ModelGraph createGraphWithTooFewHangingNodes() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.5, 0.5, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
        Vertex h6 = graph.insertVertex("h6", VertexType.HANGING_NODE, new Point3d(0.5, 0.5, 0.0));

        GraphEdge v1_h2 = graph.insertEdge("e1", v1, v3, true);
        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, true);
        GraphEdge h4_v5 = graph.insertEdge("e4", h4, v5, true);
        GraphEdge v5_h6 = graph.insertEdge("e5", v5, h6, true);
        GraphEdge h6_v1 = graph.insertEdge("e6", h6, v1, true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h4, h6);
        return graph;
    }

    private ModelGraph createGraphWithOnlyHangingNodes() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.HANGING_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.HANGING_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.5, 0.5, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.HANGING_NODE, new Point3d(1.0, 1.0, 0.0));
        Vertex h6 = graph.insertVertex("h6", VertexType.HANGING_NODE, new Point3d(0.5, 0.5, 0.0));

        GraphEdge v1_h2 = graph.insertEdge("e1", v1, h2, true);
        GraphEdge h2_v3 = graph.insertEdge("e2", h2, v3, true);
        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, true);
        GraphEdge h4_v5 = graph.insertEdge("e4", h4, v5, true);
        GraphEdge v5_h6 = graph.insertEdge("e5", v5, h6, true);
        GraphEdge h6_v1 = graph.insertEdge("e6", h6, v1, true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h2, h4, h6);
        return graph;
    }

    private ModelGraph createGraphWithOnlySimpleNodes() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex h2 = graph.insertVertex("h2", VertexType.SIMPLE_NODE, new Point3d(1.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.SIMPLE_NODE, new Point3d(1.5, 0.5, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
        Vertex h6 = graph.insertVertex("h6", VertexType.SIMPLE_NODE, new Point3d(0.5, 0.5, 0.0));

        GraphEdge v1_h2 = graph.insertEdge("e1", v1, h2, true);
        GraphEdge h2_v3 = graph.insertEdge("e2", h2, v3, true);
        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, true);
        GraphEdge h4_v5 = graph.insertEdge("e4", h4, v5, true);
        GraphEdge v5_h6 = graph.insertEdge("e5", v5, h6, true);
        GraphEdge h6_v1 = graph.insertEdge("e6", h6, v1, true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h2, h4, h6);
        return graph;
    }

}
