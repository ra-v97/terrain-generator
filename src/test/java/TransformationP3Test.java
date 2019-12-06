import model.*;
import org.graphstream.graph.Node;
import org.junit.Test;
import transformation.Transformation;
import transformation.TransformationP3;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TransformationP3Test extends AbstractTransformationTest {
    // TransformationP2 seems to work as TransformationP3 form http://home.agh.edu.pl/~paszynsk/GG/ProjektGG2019.pdf
    // Renamed to TransformationP3
    private Transformation transformation = new TransformationP3();

    @Test
    public void testComplexGraphTransformation() throws Exception {
        // this test uses terrain form point 3b introduced in http://home.agh.edu.pl/~paszynsk/GG/ProjektGG2019.pdf
        ModelGraph transformedGraph = getComplexGraphAfterTransformation(true);
        List<Vertex> hangingNodes = transformedGraph.getVertices().stream()
                .filter(v -> v.getVertexType() == VertexType.HANGING_NODE)
                .collect(Collectors.toList());

        assertEquals(4, hangingNodes.size());
        assertEquals(8, transformedGraph.getInteriors().size());
    }

    private ModelGraph getComplexGraphAfterTransformation(boolean showGraph) throws InterruptedException {
        ModelGraph graph = createEmptyGraph();
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0, 100, 0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(100, 100, 0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(250, 100, 0));
        Vertex v4 = graph.insertVertex("v4", VertexType.SIMPLE_NODE, new Point3d(100, 50, 0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(150, 50, 0));
        Vertex v6 = graph.insertVertex("v6", VertexType.SIMPLE_NODE, new Point3d(0, 0, 0));
        Vertex v7 = graph.insertVertex("v7", VertexType.SIMPLE_NODE, new Point3d(100, 0, 0));
        Vertex v8 = graph.insertVertex("v8", VertexType.SIMPLE_NODE, new Point3d(200, 0, 0));

        Vertex h1 = graph.insertVertex("h1", VertexType.HANGING_NODE, Point3d.middlePoint(v2.getCoordinates(), v7.getCoordinates()));
        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, Point3d.middlePoint(v2.getCoordinates(), v8.getCoordinates()));

        // interior nodes (should be for each triangle)
        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v7);
        InteriorNode in2 = graph.insertInterior("i2", v2, v3, v8);
        InteriorNode in3 = graph.insertInterior("i3", v1, v7, v6);
        InteriorNode in4 = graph.insertInterior("i4", v2, v5, v4);
        InteriorNode in5 = graph.insertInterior("i5", v4, v5, v7);
        InteriorNode in6 = graph.insertInterior("i6", v5, v8, v7);

        // mark partition required for each node
        graph.getInteriors().forEach(interiorNode -> interiorNode.setPartitionRequired(true));

        // EDGES:
        // from v1
        graph.insertEdge("e1", v1, v2, true);
        graph.insertEdge("e2", v1, v7, true);
        graph.insertEdge("e3", v1, v6, true);
        // from v2
        graph.insertEdge("e4", v2, v3, true);
        graph.insertEdge("e5", v2, v5, true);
        graph.insertEdge("e6", v2, v4, true);
        graph.insertEdge("e6_1", v2, v7, true); // FIXME - this edge should not be added. It is only to prevent null pointer exception when calling getEdgeBetweenNodes for v2 and v7
        graph.insertEdge("e6_2", v2, v8, true); // FIXME - this edge should not be added. It is only to prevent null pointer exception when calling getEdgeBetweenNodes for v2 and v8
        // from v3
        graph.insertEdge("e7", v3, v8, true);
        // from v4
        graph.insertEdge("e8", v4, v5, true);
        graph.insertEdge("e9", v4, v7, true);
        // from v5
        graph.insertEdge("e10", v5, v8, true);
        graph.insertEdge("e11", v5, v7, true);
        // from v6
        graph.insertEdge("e12", v6, v7, true);
        // from v7
        graph.insertEdge("e13", v7, v8, true);
        // edges between hanging nodes
        graph.insertEdge("e14", v2, h1, true);
        graph.insertEdge("e15", h1, v7, true);
        graph.insertEdge("e16", h2, v2, true);
        graph.insertEdge("e17", h2, v8, true);



        // show before transformation
        if (showGraph) showGraph(graph);

        // FIXME - transformations that do not change the graph are executed first due to the error in getEdgeBetweenNodes
        //  method which throws nullPointer if edge contains hanging node. This error should be resolved by other team.
        //  After fixing this error refactor the code and replace with graph.getInteriors().forEach ..
        // transformations that do not change the graph
        transformation.transformGraph(graph, in3);
        transformation.transformGraph(graph, in4);
        transformation.transformGraph(graph, in5);
        transformation.transformGraph(graph, in6);
        // transformations that change the graph
        transformation.transformGraph(graph, in1);
        transformation.transformGraph(graph, in2);

        // show after transformation
        if (showGraph) showGraph(graph);

        return graph;
    }

    private void showGraph(ModelGraph graph) throws InterruptedException {
        // add node labels for better visualization
        for (Node node : graph) node.addAttribute("ui.label", node.getId());
        graph.display();
        TimeUnit.SECONDS.sleep(5);
    }

}
