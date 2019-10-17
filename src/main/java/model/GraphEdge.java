package model;

import org.graphstream.graph.implementations.AbstractEdge;
import org.javatuples.Pair;

public class GraphEdge extends AbstractEdge {

    private static final String EDGE_SYMBOL = "E";

    private final String symbol;

    private final Pair<GraphNode, GraphNode> edgeNodes;

    private boolean B;

    private double L;

    private GraphEdge(String id, String symbol, Pair<GraphNode, GraphNode> edgeNodes, boolean B, double L) {
        super(id, edgeNodes.getValue0(), edgeNodes.getValue1(), false);
        this.edgeNodes = edgeNodes;
        this.symbol = symbol;
        this.L = L;
        this.B = B;
    }

    public String getSymbol() {
        return symbol;
    }

    public Pair<GraphNode, GraphNode> getEdgeNodes() {
        return edgeNodes;
    }

    public boolean isB() {
        return B;
    }

    public double getL() {
        return L;
    }

    public void setB(boolean b) {
        B = b;
    }

    public void setL(double l) {
        L = l;
    }

    public static class GraphEdgeBuilder {

        private final String symbol;

        private final Pair<GraphNode, GraphNode> edgeNodes;

        private final String id;

        private boolean B;

        private double L;

        public GraphEdgeBuilder(String id, GraphNode source, GraphNode target) {
            this.symbol = EDGE_SYMBOL;
            this.id = id;
            this.edgeNodes = new Pair<>(source, target);
        }

        public GraphEdgeBuilder setB(boolean b) {
            B = b;
            return this;
        }

        public GraphEdgeBuilder setL(double l) {
            L = l;
            return this;
        }

        public GraphEdge build() {
            return new GraphEdge(id, symbol, edgeNodes, B, L);
        }
    }
}
