package app;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

public class MainApp{

    private static Logger log = Logger.getLogger(MainApp.class.getName());

    public static void main(String[] args) {
        BasicConfigurator.configure();
        log.info("Simulation");
        Graph graph = new S("Tutorial 1");
        Node a1 = graph.addNode("A");
        graph.addNode("B" );
        graph.addNode("C" );
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("XC", "B", "C");
        graph.addEdge("CA", "C", "A");
        graph.addAttribute("ui.stylesheet", "graph { fill-color: red; }");
        Node a = graph.getNode("A");
        Node b = graph.getNode("B");
        Node c = graph.getNode("C");
        a.addAttribute("layout.frozen");
        b.addAttribute("layout.frozen");
        c.addAttribute("layout.frozen");
        a.addAttribute("xyz", 0, 0, 2);
        b.addAttribute("xyz", 1, 1, -2);
        c.addAttribute("xyz", 4, 0, 0);

        graph.display();
    }
}
