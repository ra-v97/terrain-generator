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
    public void conditionPassesWithObtuseTriangle(){
        ModelGraph graph = createObtuseTriangleGraph(true);
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void transformationProducesTwoInteriorNodes(){
        ModelGraph graph = createObtuseTriangleGraph(true);
        assertEquals(2, transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)).getInteriors().size());
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
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v3, v1), true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3, h);

        return populateTestGraphWithLists(graph, new Vertex[]{v1, v2, v3}, new GraphEdge[]{e1, e2, e3, e4}, in1, needsPartitioning);
    }

}
