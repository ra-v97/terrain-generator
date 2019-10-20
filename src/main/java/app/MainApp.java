package app;

import model.InteriorNode;
import model.ModelGraph;
import model.Point3d;
import model.Vertex;
import model.VertexType;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import transformation.Transformation;
import transformation.TransformationP1;

public class MainApp {

    private static Logger log = Logger.getLogger(MainApp.class.getName());

    public static void main(String[] args) {
        BasicConfigurator.configure();
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.HANGING_NODE, new Point3d(1.0, 1.0, 0.0));
        graph.insertEdge("e1", v1, v2, true);
        graph.insertEdge("e2", v2, v3, true);
        graph.insertEdge("e3", v3, v1, true);
        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3);
        in1.setPartitionRequired(true);
        Transformation t1 = new TransformationP1();
        log.info(String.format("Condition state for transformation P1: %b", t1.isConditionCompleted(graph, in1)));

        graph.display();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.transformGraph(graph, in1);
    }
}
