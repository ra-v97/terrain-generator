import model.*;
import org.junit.Test;
import transformation.Transformation;
import transformation.TransformationP2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransformationP2Test {
    private Transformation transformation = new TransformationP2();

    @Test
    public void conditionNotCompletedWhenNoHangingNode(){
        ModelGraph graph  = createSingleTriangle();
        InteriorNode interior = graph.getInterior("i1").get();
        assertFalse(transformation.isConditionCompleted(graph,interior));
    }

    @Test
    public void conditionCompletedWhenHangingNodePresent(){
        ModelGraph graph  = createSingeTriangleWithHangingNode();
        InteriorNode interior = graph.getInterior("i1").get();
        assertTrue(transformation.isConditionCompleted(graph,interior));
    }

    @Test
    public void conditionNotCompletedWithIncorrectEdgeLengths(){
        ModelGraph graph = createSingeTriangleWithHangingNodeOnShortEdge();
        InteriorNode interior = graph.getInterior("i1").get();
        assertFalse(transformation.isConditionCompleted(graph, interior));
    }

    @Test
    public void conditionsNotMetForSeveralTrianglesInGraph(){
        List<Integer> trianglesToTransform = Arrays.stream(new int[] {3,6}).boxed().collect(Collectors.toList());
        List<Integer> correctTriangles = Arrays.stream(new int[] {1,2,4,5}).boxed().collect(Collectors.toList());

        ModelGraph graph = createFullGraphP2();
        for(Integer i: trianglesToTransform){
            InteriorNode interior = graph.getInterior("i"+i).get();
            assertTrue(transformation.isConditionCompleted(graph,interior));
        }

        for(Integer i: correctTriangles){
            InteriorNode interior = graph.getInterior("i"+i).get();
            assertFalse(transformation.isConditionCompleted(graph,interior));
        }
    }

    @Test
    public void simpleTriangleTransformation(){
        ModelGraph graph  = createSingeTriangleWithHangingNode();
        InteriorNode interior = graph.getInterior("i1").get();
        transformation.transformGraph(graph,interior);
        assertTrue(graph.getEdgeBetweenNodes(graph.getVertex("v4").get(),graph.getVertex("v3").get()).isPresent());
    }

    @Test
    public void newEdgeLengthTest(){
        ModelGraph graph  = createSingeTriangleWithHangingNode();
        InteriorNode interior = graph.getInterior("i1").get();
        transformation.transformGraph(graph,interior);
        GraphEdge e = graph.getEdgeBetweenNodes(graph.getVertex("v4").get(),graph.getVertex("v3").get()).get();
        assertEquals(e.getL(), 50);
    }

    @Test
    public void newEdgeBoundaryTest(){
        ModelGraph graph  = createSingeTriangleWithHangingNode();
        InteriorNode interior = graph.getInterior("i1").get();
        transformation.transformGraph(graph, interior);
        GraphEdge e = graph.getEdgeBetweenNodes(graph.getVertex("v4").get(),graph.getVertex("v3").get()).get();
        assertFalse(e.getB());
    }

    @Test
    public void newTrianglesInteriorAddedTest(){
        ModelGraph graph  = createSingeTriangleWithHangingNode();
        InteriorNode interior = graph.getInterior("i1").get();
        transformation.transformGraph(graph, interior);
        List<InteriorNode> i1 = new ArrayList<>(graph.getInteriors());
        assertEquals(2, i1.size());
    }

    @Test
    public void newTrianglesRefineTest(){
        ModelGraph graph  = createSingeTriangleWithHangingNode();
        InteriorNode interior = graph.getInterior("i1").get();
        transformation.transformGraph(graph, interior);
        List<InteriorNode> i1 = new ArrayList<>(graph.getInteriors());
        assertFalse(i1.get(0).isPartitionRequired());
        assertFalse(i1.get(1).isPartitionRequired());
    }

    @Test
    public void transformationKeepsEdgesBoundary() {
        ModelGraph graph  = createSingeTriangleWithHangingNodeAndChangingBoundary();
        InteriorNode interior = graph.getInterior("i1").get();
        transformation.transformGraph(graph, interior);
        Vertex v1 = graph.getVertex("v1").get();
        Vertex v2 = graph.getVertex("v2").get();
        Vertex v3 = graph.getVertex("v3").get();
        Vertex v4 = graph.getVertex("v4").get();
        assertTrue(graph.getEdge(v1, v4).get().getB());
        assertFalse(graph.getEdge(v2, v4).get().getB());
        assertTrue(graph.getEdge(v2, v3).get().getB());
        assertFalse(graph.getEdge(v1, v3).get().getB());
    }

    @Test
    public void fullTriangleTransormation(){
        List<Integer> trianglesToTransform = Arrays.stream(new int[] {3,6}).boxed().collect(Collectors.toList());

        ModelGraph graph = createFullGraphP2();
        for(Integer i: trianglesToTransform){
            InteriorNode interior = graph.getInterior("i"+i).get();
            transformation.transformGraph(graph,interior);
        }
        assertTrue(graph.getEdgeBetweenNodes(graph.getVertex("v4").get(),graph.getVertex("v7").get()).isPresent());
        assertTrue(graph.getEdgeBetweenNodes(graph.getVertex("v5").get(),graph.getVertex("v3").get()).isPresent());
    }

    private ModelGraph createSingleTriangle(){
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(100.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(50.0, 50.0, 0.0));
        graph.insertEdge("e1", v1, v2, true);
        graph.insertEdge("e2", v2, v3, true);
        graph.insertEdge("e3", v3, v1, true);
        graph.insertInterior("i1", v1, v2, v3);
        return graph;
    }

    private ModelGraph createSingeTriangleWithHangingNode(){
        ModelGraph graph = new ModelGraph("testGraph2");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(100.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(50.0, 50.0, 0.0));
        Vertex v4 = graph.insertVertex("v4", VertexType.HANGING_NODE, new Point3d(50.0, 0.0, 0.0));
        graph.insertEdge("e1", v1, v4, true);
        graph.insertEdge("e2", v4, v2, true);
        graph.insertEdge("e3", v2, v3, true);
        graph.insertEdge("e4", v3, v1, true);
        graph.insertInterior("i1", v1, v2, v3);
        return graph;
    }

    private ModelGraph createSingeTriangleWithHangingNodeAndChangingBoundary(){
        ModelGraph graph = new ModelGraph("testGraph2");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(100.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(50.0, 50.0, 0.0));
        Vertex v4 = graph.insertVertex("v4", VertexType.HANGING_NODE, new Point3d(50.0, 0.0, 0.0));
        graph.insertEdge("e1", v1, v4, true);
        graph.insertEdge("e2", v4, v2, false);
        graph.insertEdge("e3", v2, v3, true);
        graph.insertEdge("e4", v3, v1, false);
        graph.insertInterior("i1", v1, v2, v3);
        return graph;
    }

    private ModelGraph createSingeTriangleWithHangingNodeOnShortEdge(){
        ModelGraph graph = new ModelGraph("testGraph2");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(100.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(50.0, 50.0, 0.0));
        Vertex v4 = graph.insertVertex("v4", VertexType.HANGING_NODE, new Point3d(25.0, 25.0, 0.0));
        graph.insertEdge("e1", v1, v4, true);
        graph.insertEdge("e2", v4, v3, true);
        graph.insertEdge("e3", v1, v2, true);
        graph.insertEdge("e4", v2, v3, true);
        graph.insertInterior("i1", v1, v2, v3);
        return graph;
    }

    private ModelGraph createFullGraphP2(){
        // interior i3 and i6 need some transformations

        ModelGraph graph = new ModelGraph("testGraph3");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(100.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(200.0, 0.0, 0.0));
        Vertex v4 = graph.insertVertex("v4", VertexType.HANGING_NODE, new Point3d(50.0, 50.0, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.HANGING_NODE, new Point3d(150.0, 50.0, 0.0));
        Vertex v6 = graph.insertVertex("v6", VertexType.SIMPLE_NODE, new Point3d(0.0, 100.0, 0.0));
        Vertex v7 = graph.insertVertex("v7", VertexType.SIMPLE_NODE, new Point3d(100.0, 100.0, 0.0));
        Vertex v8 = graph.insertVertex("v8", VertexType.SIMPLE_NODE, new Point3d(200.0, 100.0, 0.0));

        graph.insertEdge("e1", v1, v2, true);
        graph.insertEdge("e2", v2, v3, true);
        graph.insertEdge("e3", v1, v4, false);
        graph.insertEdge("e4", v2, v4, false);
        graph.insertEdge("e5", v2, v7, false);
        graph.insertEdge("e6", v2, v5, false);
        graph.insertEdge("e7", v3, v8, true);
        graph.insertEdge("e8", v1, v6, true);
        graph.insertEdge("e9", v4, v6, false);
        graph.insertEdge("e10", v5, v7, false);
        graph.insertEdge("e11", v5, v8, false);
        graph.insertEdge("e12", v6, v7, true);
        graph.insertEdge("e13", v7, v8, true);

        List<InteriorNode> ints = new ArrayList<>();
        ints.add(graph.insertInterior("i1", v1, v2, v4));
        ints.add(graph.insertInterior("i2", v1, v4, v6));
        ints.add(graph.insertInterior("i3", v2, v6, v7));
        ints.add(graph.insertInterior("i4", v2, v5, v7));
        ints.add(graph.insertInterior("i5", v5, v7, v8));
        ints.add(graph.insertInterior("i6", v2, v3, v8));
        return graph;
    }
}
