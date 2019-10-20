package model;

import org.graphstream.graph.implementations.AbstractEdge;
import org.javatuples.Pair;

public class GraphEdge extends AbstractEdge {

    private static final String EDGE_SYMBOL = "E";

    private final String symbol;

    private final Pair<GraphNode, GraphNode> edgeNodes;

    private boolean B;

    public GraphEdge(String id, String symbol, Pair<GraphNode, GraphNode> edgeNodes, boolean B) {
        super(id, edgeNodes.getValue0(), edgeNodes.getValue1(), false);
        this.edgeNodes = edgeNodes;
        this.symbol = symbol;
        this.B = B;
    }

    public GraphEdge(String id, Pair<GraphNode, GraphNode> edgeNodes, boolean B) {
        this(id, EDGE_SYMBOL, edgeNodes, B);
    }

    public String getSymbol() {
        return symbol;
    }

    public Pair<GraphNode, GraphNode> getEdgeNodes() {
        return edgeNodes;
    }

    public boolean getB() {
        return B;
    }

    public void setB(boolean b) {
        B = b;
    }

    public double getL() {
        return Point3d.distance(edgeNodes.getValue0().getCoordinates(), edgeNodes.getValue1().getCoordinates());
    }

    public static class GraphEdgeBuilder {

        private final String symbol;

        private final Pair<GraphNode, GraphNode> edgeNodes;

        private final String id;

        private boolean B;

        public GraphEdgeBuilder(String id, GraphNode source, GraphNode target) {
            this.symbol = EDGE_SYMBOL;
            this.id = id;
            this.edgeNodes = new Pair<>(source, target);
        }

        public GraphEdgeBuilder setB(boolean b) {
            B = b;
            return this;
        }

        public GraphEdge build() {
            return new GraphEdge(id, symbol, edgeNodes, B);
        }
    }
}
