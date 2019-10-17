package app;

import model.InteriorNode;
import model.ModelGraph;
import model.Vertex;
import model.VertexType;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class MainApp {

    private static Logger log = Logger.getLogger(MainApp.class.getName());

    public static void main(String[] args) {
        BasicConfigurator.configure();
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, 0.0, 0.0, 0.0);
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, 2.0, 0.0, 0.0);
        Vertex v3 = graph.insertVertex("v3", VertexType.HANGING_NODE, 1.0, 1.0, 0.0);
        graph.insertEdge("e1", v1, v2, true, 1.0);
        graph.insertEdge("e2", v2, v3, true, 1.0);
        graph.insertEdge("e3", v3, v1, true, 1.0);
        InteriorNode in = graph.insertInterior("i1", v1, v2, v3);
        in.setPartitionRequired(true);
        graph.display();
    }
}
