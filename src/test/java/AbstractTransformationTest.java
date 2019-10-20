import model.*;

public class AbstractTransformationTest {

    public ModelGraph populateTestGraph(ModelGraph graph, Vertex ve1, Vertex ve2, Vertex ve3, GraphEdge ge1, GraphEdge ge2, GraphEdge ge3, boolean partitionRequired){
        Vertex v1 = graph.insertVertex(ve1);
        Vertex v2 = graph.insertVertex(ve2);
        Vertex v3 = graph.insertVertex(ve3);
        graph.insertEdge(ge1);
        graph.insertEdge(ge2);
        graph.insertEdge(ge3);
        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3);
        in1.setPartitionRequired(partitionRequired);

        return graph;
    }

    public ModelGraph populateTestGraphWithLists(ModelGraph graph, Vertex[] vs, GraphEdge[] es, InteriorNode in, boolean partitionRequired){
        for (Vertex v : vs) {
            graph.insertVertex(v);
        }
        for (GraphEdge e : es) {
            graph.insertEdge(e);
        }
        in.setPartitionRequired(partitionRequired);

        return graph;
    }

    public ModelGraph createEmptyGraph() {
        return new ModelGraph("testGraph");
    }
}
