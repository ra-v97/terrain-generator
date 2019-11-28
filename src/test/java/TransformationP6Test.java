import model.*;
import org.javatuples.Pair;
import org.junit.Test;
import transformation.Transformation;
import transformation.TransformationP6;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class TransformationP6Test extends AbstractTransformationTest {
    private Transformation transformation = new TransformationP6();

    @Test
    public void simpleGraphHangingNode() {
        Pair<ModelGraph, InteriorNode> simplestGraph = this.createSimplestGraph();
        ModelGraph graph = simplestGraph.getValue0();
        InteriorNode interiorNode = simplestGraph.getValue1();

        assertTrue(this.transformation.isConditionCompleted(graph, interiorNode));
        assertEquals(3, getHangingNodeSize(graph));
        this.transformation.transformGraph(graph, interiorNode);
        assertEquals(2, getHangingNodeSize(graph));
    }

    @Test
    public void simpleGraphInteriorNumber() {
        Pair<ModelGraph, InteriorNode> simplestGraph = this.createSimplestGraph();
        ModelGraph graph = simplestGraph.getValue0();
        InteriorNode interiorNode = simplestGraph.getValue1();

        assertTrue(this.transformation.isConditionCompleted(graph, interiorNode));
        assertEquals(1, graph.getInteriors().size());
        this.transformation.transformGraph(graph, interiorNode);
        assertEquals(2, graph.getInteriors().size());
    }

    @Test
    public void envelopeGraphConditionCompletion() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();

        for (Map.Entry<InteriorNode, Boolean> entry : graphPair.getValue1().entrySet()) {
            System.out.println("Testing for interior node with id: " + entry.getKey().getId());
            assertEquals(entry.getValue(), transformation.isConditionCompleted(graphPair.getValue0(), entry.getKey()));
        }
    }

    @Test
    public void envelopeGraphHangingNodesCount() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
        ModelGraph graph = graphPair.getValue0();

        assertEquals(3, getHangingNodeSize(graph));
        for (Map.Entry<InteriorNode, Boolean> entry : graphPair.getValue1().entrySet()) {
            InteriorNode iNode = entry.getKey();

            if (transformation.isConditionCompleted(graph, iNode)) {
                transformation.transformGraph(graph, iNode);
            }
        }
        assertEquals(2, getHangingNodeSize(graph));
    }

    @Test
    public void envelopeGraphInternalNodesCount() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
        ModelGraph graph = graphPair.getValue0();

        assertEquals(8, graph.getInteriors().size());
        for (Map.Entry<InteriorNode, Boolean> entry : graphPair.getValue1().entrySet()) {
            InteriorNode iNode = entry.getKey();

            if (transformation.isConditionCompleted(graph, iNode)) {
                transformation.transformGraph(graph, iNode);
            }
        }
        assertEquals(9, graph.getInteriors().size());
    }

    @Test
    public void envelopeGraphEdgesCount() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
        ModelGraph graph = graphPair.getValue0();

        assertEquals(41, graph.getEdges().size());
        for (Map.Entry<InteriorNode, Boolean> entry : graphPair.getValue1().entrySet()) {
            InteriorNode iNode = entry.getKey();

            if (transformation.isConditionCompleted(graph, iNode)) {
                transformation.transformGraph(graph, iNode);
            }
        }
        assertEquals(45, graph.getEdges().size());
    }

    @Test
    public void envelopeGraphNewEdgeProperties() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
        ModelGraph graph = graphPair.getValue0();
        InteriorNode iNode = graph.getInterior("i3").orElseThrow(IllegalStateException::new);

        Vertex v2 = getVertexIfExists(graph, "v2");
        Vertex v8 = getVertexIfExists(graph, "v8");

        Double edgeLength = Math.sqrt(Math.pow(v2.getXCoordinate() - v8.getXCoordinate(), 2.0) +
                Math.pow(v2.getYCoordinate() - v8.getYCoordinate(), 2.0) +
                Math.pow(v2.getZCoordinate() - v8.getZCoordinate(), 2.0));

        assertFalse(graph.getEdgeBetweenNodes(v2, v8).isPresent());
        transformation.transformGraph(graph, iNode);

        Optional<GraphEdge> edge = graph.getEdgeBetweenNodes(v2, v8);
        assertTrue(edge.isPresent());
        assertEquals(edgeLength, edge.get().getL());
    }

    private Pair<ModelGraph, InteriorNode> createSimplestGraph() {
        ModelGraph graph = new ModelGraph("simplestGraphTest");

        Vertex v0 = graph.insertVertex("v0", VertexType.SIMPLE_NODE, new Point3d(0., 0., 0.));
        Vertex v1 = graph.insertVertex("v1", VertexType.HANGING_NODE, new Point3d(50., 0., 0.));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(100., 0., 0.));
        Vertex v3 = graph.insertVertex("v3", VertexType.HANGING_NODE, new Point3d(75., 43., 0.));
        Vertex v4 = graph.insertVertex("v4", VertexType.SIMPLE_NODE, new Point3d(50., 86., 0.));
        Vertex v5 = graph.insertVertex("v5", VertexType.HANGING_NODE, new Point3d(25., 43., 0.));

        graph.insertEdge("e0", v0, v1);
        graph.insertEdge("e1", v1, v2);
        graph.insertEdge("e2", v2, v3);
        graph.insertEdge("e3", v3, v4);
        graph.insertEdge("e4", v4, v5);
        graph.insertEdge("e5", v5, v0);

        InteriorNode interiorNode = graph.insertInterior("i1", v0, v2, v4);
        return Pair.with(graph, interiorNode);
    }

    private Pair<ModelGraph, Map<InteriorNode, Boolean>> createEnvelopeGraph() {
        ModelGraph graph = new ModelGraph("envelopeGraphTest");

        // vertices top -> down; in the same level: left -> right
        Vertex v0 = graph.insertVertex("v0", VertexType.SIMPLE_NODE, new Point3d(150., 150., 0.));
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(100., 100., 0.));
        Vertex v2 = graph.insertVertex("v2", VertexType.HANGING_NODE, new Point3d(150., 100., 0.));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(250., 100., 0.));
        Vertex v4 = graph.insertVertex("v4", VertexType.SIMPLE_NODE, new Point3d(100., 50., 0.));
        Vertex v5 = graph.insertVertex("v5", VertexType.HANGING_NODE, new Point3d(150., 50., 0.));
        Vertex v6 = graph.insertVertex("v6", VertexType.HANGING_NODE, new Point3d(250., 50., 0.));
        Vertex v7 = graph.insertVertex("v7", VertexType.SIMPLE_NODE, new Point3d(100., 0., 0.));
        Vertex v8 = graph.insertVertex("v8", VertexType.SIMPLE_NODE, new Point3d(200., 0., 0.));
        Vertex v9 = graph.insertVertex("v9", VertexType.SIMPLE_NODE, new Point3d(250., 0., 0.));

        //edges
        graph.insertEdge("e0", v0, v1);
        graph.insertEdge("e1", v0, v2);
        graph.insertEdge("e2", v0, v3);
        graph.insertEdge("e3", v1, v2);
        graph.insertEdge("e4", v1, v4);
        graph.insertEdge("e5", v1, v5);
        graph.insertEdge("e6", v2, v3);
        graph.insertEdge("e7", v3, v6);
        graph.insertEdge("e8", v3, v9);
        graph.insertEdge("e9", v4, v5);
        graph.insertEdge("e10", v4, v7);
        graph.insertEdge("e11", v5, v7);
        graph.insertEdge("e12", v5, v8);
        graph.insertEdge("e13", v6, v8);
        graph.insertEdge("e14", v6, v9);
        graph.insertEdge("e15", v7, v8);
        graph.insertEdge("e16", v8, v9);

        // i-nodes
        Map<InteriorNode, Boolean> nodesWithFlag = new HashMap<>();
        nodesWithFlag.put(graph.insertInterior("i0", v0, v1, v2), false);
        nodesWithFlag.put(graph.insertInterior("i1", v0, v2, v3), false);
        nodesWithFlag.put(graph.insertInterior("i2", v1, v4, v5), false);
        nodesWithFlag.put(graph.insertInterior("i3", v1, v3, v8), true);  // <-- correct :D
        nodesWithFlag.put(graph.insertInterior("i4", v3, v6, v9), false);
        nodesWithFlag.put(graph.insertInterior("i5", v4, v5, v7), false);
        nodesWithFlag.put(graph.insertInterior("i6", v5, v7, v8), false);
        nodesWithFlag.put(graph.insertInterior("i7", v6, v8, v9), false);

        return Pair.with(graph, nodesWithFlag);
    }

    private int getHangingNodeSize(ModelGraph graph) {
        return (int) graph.getVertices()
                .stream()
                .filter(vertex -> vertex.getVertexType() == VertexType.HANGING_NODE)
                .count();
    }

    private Vertex getVertexIfExists(ModelGraph graph, String vertexId) {
        return graph.getVertex(vertexId)
                .orElseThrow(() -> new IllegalStateException("Cannot find vertex with id: " + vertexId));
    }
}
