package app;

import adaptation.Adaptation;
import model.GraphEdge;
import model.InteriorNode;
import model.ModelGraph;
import model.Point3d;
import model.Vertex;
import model.VertexType;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import processor.MapProcessingUtil;
import transformation.*;

import java.util.*;
import java.util.stream.Stream;

public class MainApp {

    private static Logger log = Logger.getLogger(MainApp.class.getName());

    private static Pair<ModelGraph, InteriorNode> task1() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
        graph.insertEdge("e1", v1, v2, true);
        graph.insertEdge("e2", v2, v3, true);
        graph.insertEdge("e3", v3, v1, true);
        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3);
        in1.setPartitionRequired(true);

        return new Pair<>(graph, in1);
    }

    private static Pair<ModelGraph, InteriorNode> task5() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));

        GraphEdge v1_h4 = graph.insertEdge("e1", v1, h4, true);
        GraphEdge v1_v3 = graph.insertEdge("e2", v1, v3, true);
        GraphEdge h4_v2 = graph.insertEdge("e3", h4, v2, true);
        GraphEdge v2_v3 = graph.insertEdge("e4", v2, v3, true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3);
        return new Pair<>(graph, in1);
    }

    private static Pair<ModelGraph, InteriorNode> task11() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.5, 0.5, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));

        GraphEdge v1_h2 = graph.insertEdge("e1", v1, h2, true);
        GraphEdge h2_v3 = graph.insertEdge("e2", h2, v3, true);
        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, true);
        GraphEdge h3_v5 = graph.insertEdge("e4", h4, v5, true);
        GraphEdge v1_h5 = graph.insertEdge("e5", v1, v5, true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h2, h4);
        return new Pair<>(graph, in1);
    }

    private static Pair<ModelGraph, InteriorNode> task15(){
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.75, 1.0, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.5, 2.0, 0.0));

        GraphEdge v1_h2 = graph.insertEdge("e1", v1, h2, false);
        GraphEdge h2_v3 = graph.insertEdge("e2", h2, v3, false);
        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, false);
        GraphEdge h3_v5 = graph.insertEdge("e4", h4, v5, false);
        GraphEdge v1_h5 = graph.insertEdge("e5", v1, v5, false);

        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h2, h4);
        return new Pair<>(graph, in1);
    }

    private static Pair<ModelGraph, InteriorNode> task17() {
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

        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5);
        return new Pair<>(graph, in1);
    }

    private static Pair<ModelGraph, InteriorNode> task13() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(1.0, 0.0, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(2.0, 1.0, 0.0));
        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, Point3d.middlePoint(v1.getCoordinates(), v3.getCoordinates()));
        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, Point3d.middlePoint(v3.getCoordinates(), v5.getCoordinates()));

        GraphEdge v1_h2 = graph.insertEdge("e1", v1, h2, false);
        GraphEdge h2_v3 = graph.insertEdge("e2", h2, v3, false);
        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, false);
        GraphEdge h4_v5 = graph.insertEdge("e4", h4, v5, false);
        GraphEdge v1_v5 = graph.insertEdge("e6", v1, v5, true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h2, h4);
        return new Pair<>(graph, in1);
    }

    private static Pair<ModelGraph, Collection<InteriorNode>> task25() {

        ModelGraph graph = new ModelGraph("testGraph");

        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(4.0, 0.0, 0.0));
        Vertex v4 = graph.insertVertex("v4", VertexType.SIMPLE_NODE, new Point3d(0.0, 1.0, 0.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
        Vertex v6 = graph.insertVertex("v6", VertexType.SIMPLE_NODE, new Point3d(3.0, 1.0, 0.0));
        Vertex v7 = graph.insertVertex("v7", VertexType.SIMPLE_NODE, new Point3d(4.0, 1.0, 0.0));
        Vertex v8 = graph.insertVertex("v8", VertexType.SIMPLE_NODE, new Point3d(0.0, 2.0, 0.0));
        Vertex v9 = graph.insertVertex("v9", VertexType.SIMPLE_NODE, new Point3d(2.0, 2.0, 0.0));
        Vertex v10 = graph.insertVertex("v10", VertexType.SIMPLE_NODE, new Point3d(4.0, 2.0, 0.0));
        Vertex v11 = graph.insertVertex("v11", VertexType.SIMPLE_NODE, new Point3d(0.0, 4.0, 0.0));
        Vertex v12 = graph.insertVertex("v12", VertexType.SIMPLE_NODE, new Point3d(4.0, 4.0, 0.0));

        GraphEdge v1_v2 = graph.insertEdge("e1", v1, v2, false);
        GraphEdge v2_v3 = graph.insertEdge("e2", v2, v3, false);
        GraphEdge v1_v4 = graph.insertEdge("e3", v1, v4, false);
        GraphEdge v1_v5 = graph.insertEdge("e4", v1, v5, false);
        GraphEdge v2_v5 = graph.insertEdge("e5", v2, v5, false);
        GraphEdge v2_v6 = graph.insertEdge("e6", v2, v6, false);
        GraphEdge v3_v6 = graph.insertEdge("e7", v3, v6, false);
        GraphEdge v3_v7 = graph.insertEdge("e8", v3, v7, false);
        GraphEdge v4_v5 = graph.insertEdge("e9", v4, v5, false);
        GraphEdge v5_v6 = graph.insertEdge("e10", v5, v6, false);
        GraphEdge v6_v7 = graph.insertEdge("e11", v6, v7, false);
        GraphEdge v4_v8 = graph.insertEdge("e12", v4, v8, false);
        GraphEdge v5_v8 = graph.insertEdge("e13", v5, v8, false);
        GraphEdge v5_v9 = graph.insertEdge("e14", v5, v9, false);
        GraphEdge v6_v9 = graph.insertEdge("e15", v6, v9, false);
        GraphEdge v6_v10 = graph.insertEdge("e16", v6, v10, false);
        GraphEdge v7_v10 = graph.insertEdge("e17", v7, v10, false);
        GraphEdge v8_v9 = graph.insertEdge("e18", v8, v9, false);
        GraphEdge v9_v10 = graph.insertEdge("e19", v9, v10, false);
        GraphEdge v8_v11 = graph.insertEdge("e20", v8, v11, false);
        GraphEdge v9_v11 = graph.insertEdge("e21", v9, v11, false);
        GraphEdge v9_v12 = graph.insertEdge("e22", v9, v12, false);
        GraphEdge v10_v12 = graph.insertEdge("e23", v10, v12, false);
        GraphEdge v11_v12 = graph.insertEdge("e24", v11, v12, false);

        List<InteriorNode> interiorNodes = new LinkedList<InteriorNode>();

        InteriorNode in1 = graph.insertInterior("i1", v1, v4, v5);
        InteriorNode in2 = graph.insertInterior("i2", v1, v2, v5);
        InteriorNode in3 = graph.insertInterior("i3", v2, v5, v6);
        InteriorNode in4 = graph.insertInterior("i4", v2, v6, v3);
        InteriorNode in5 = graph.insertInterior("i5", v3, v6, v7);
        InteriorNode in6 = graph.insertInterior("i6", v4, v5, v8);
        InteriorNode in7 = graph.insertInterior("i7", v5, v8, v9);
        InteriorNode in8 = graph.insertInterior("i8", v5, v6, v9);
        InteriorNode in9 = graph.insertInterior("i9", v6, v9, v10);
        InteriorNode in10 = graph.insertInterior("i10", v6, v7, v10);
        InteriorNode in11 = graph.insertInterior("i11", v8, v9, v11);
        InteriorNode in12 = graph.insertInterior("i12", v9, v11, v12);
        InteriorNode in13 = graph.insertInterior("i13", v9, v10, v12);

        interiorNodes.add(in1);
        interiorNodes.add(in2);
        interiorNodes.add(in3);
        interiorNodes.add(in4);
        interiorNodes.add(in5);
        interiorNodes.add(in6);
        interiorNodes.add(in7);
        interiorNodes.add(in8);
        interiorNodes.add(in9);
        interiorNodes.add(in10);
        interiorNodes.add(in11);
        interiorNodes.add(in12);
        interiorNodes.add(in13);

        in10.setPartitionRequired(true);

        return new Pair<>(graph, interiorNodes);
    }

    public static void main(String[] args) throws InterruptedException {
        BasicConfigurator.configure();

        Pair<ModelGraph, Collection<InteriorNode>> task = task25();
        ModelGraph graph = task.getValue0();
        graph.addAttribute("ui.stylesheet", "url('file:src/main/resources/styles.css')");
        Collection<InteriorNode> interiorNodes = task.getValue1();
        boolean endFlag = true;

        Transformation t1 = new TransformationP1();
        Transformation t2 = new TransformationP2();
        Transformation t3 = new TransformationP3();
        Transformation t4 = new TransformationP4();
        Transformation t5 = new TransformationP5();
        Transformation t6 = new TransformationP6();
        Transformation t7 = new TransformationP7();
        Transformation t8 = new TransformationP8();
        Transformation t9 = new TransformationP9();

        graph.display();
        Thread.sleep(2000);

        log.debug("Result of transformation " + t1.getClass().getSimpleName() + ": " + Adaptation.transform(graph, t1, interiorNodes));
        log.debug("Result of transformation " + t2.getClass().getSimpleName() + ": " + Adaptation.transform(graph, t2, interiorNodes));

        log.info("Beginning of transformations 3 to 9");

        while(endFlag){
            Boolean acc = false;
            for (Transformation transformation : Arrays.asList(t3, t4, t5, t6, t7, t8, t9)) {
                Boolean transform = Adaptation.transform(graph, transformation, interiorNodes);
                acc = acc || transform;
                log.debug("Result of transformation " + transformation.getClass().getSimpleName() + ": " + transform);
            }
            endFlag = acc;
        }

        log.info("End of transformations 3 to 9");
    }
}