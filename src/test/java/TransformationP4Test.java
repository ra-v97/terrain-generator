import model.*;
import org.javatuples.Pair;
import org.junit.Test;
import transformation.Transformation;
import transformation.TransformationP4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TransformationP4Test extends AbstractTransformationTest {
    private Transformation transformation = new TransformationP4();

    @Test
    public void simpleGraphHangingNode() {
        Pair<ModelGraph, InteriorNode> simplestGraph = this.createSimplestGraph();
        ModelGraph graph = simplestGraph.getValue0();
        InteriorNode interiorNode = simplestGraph.getValue1();

        assertTrue(this.transformation.isConditionCompleted(graph, interiorNode));
        assertEquals(2, getHangingNodeSize(graph));
        this.transformation.transformGraph(graph, simplestGraph.getValue1());
        assertEquals(1, getHangingNodeSize(graph));
    }

    private static int getHangingNodeSize(ModelGraph graph) {
        return (int) graph.getVertices()
                .stream()
                .filter(vertex -> vertex.getVertexType() == VertexType.HANGING_NODE)
                .count();
    }

    @Test
    public void simpleGraphInteriorNumber() {
        Pair<ModelGraph, InteriorNode> simplestGraph = this.createSimplestGraph();
        ModelGraph graph = simplestGraph.getValue0();
        InteriorNode interiorNode = simplestGraph.getValue1();

        assertTrue(this.transformation.isConditionCompleted(graph, interiorNode));
        assertEquals(1, graph.getInteriors().size());
        this.transformation.transformGraph(graph, simplestGraph.getValue1());
        assertEquals(2, graph.getInteriors().size());
    }

    private Pair<ModelGraph, InteriorNode> createSimplestGraph() {
        ModelGraph graph = new ModelGraph("simplestGraphTest");

        Vertex v0 = graph.insertVertex("v0", VertexType.SIMPLE_NODE, new Point3d(0., 0., 0.));
        Vertex v1 = graph.insertVertex("v1", VertexType.HANGING_NODE, new Point3d(50., 0., 0.));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(100., 0., 0.));
        Vertex v3 = graph.insertVertex("v3", VertexType.HANGING_NODE, new Point3d(75., 43., 0.));
        Vertex v4 = graph.insertVertex("v4", VertexType.SIMPLE_NODE, new Point3d(50., 86., 0.));

        graph.insertEdge("e0", v0, v1);
        graph.insertEdge("e1", v1, v2);
        graph.insertEdge("e2", v2, v3);
        graph.insertEdge("e3", v3, v4);
        graph.insertEdge("e4", v4, v0);


        InteriorNode interiorNode = graph.insertInterior("i1", v0, v2, v4);
        return Pair.with(graph, interiorNode);
    }

    private ModelGraph createEnvelopeGraph() {
        ModelGraph graph = new ModelGraph("envelopeGraphTest");
        //TODO
        return graph;
    }
}