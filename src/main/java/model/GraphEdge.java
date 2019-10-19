package model;

import common.Utils;
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
        return Utils.distance(edgeNodes.getValue0(), edgeNodes.getValue1());
    }

    public void setB(boolean b) {
        B = b;
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
