package model;

import common.ElementAttributes;
import org.graphstream.graph.implementations.AbstractGraph;

public class Vertex extends GraphNode {

    private VertexType vertexType;

    private Vertex(AbstractGraph graph, String id, String symbol, VertexType vertexType, double x, double y, double z){
        super(graph, id, vertexType.getSybmbol(), x, y, z);
        this.vertexType = vertexType;
    }

    public static class VertexBuilder {

        private final AbstractGraph graph;

        private final String id;

        private String symbol;

        private VertexType vertexType;

        private double xCoordinate;

        private double yCoordinate;

        private double zCoordinate;

        public VertexBuilder(AbstractGraph graph, String id) {
            this.graph = graph;
            this.id = id;
        }

        public VertexBuilder setSymbol(String symbol){
            this.symbol = symbol;
            return this;
        }

        public VertexBuilder setVertexType(VertexType vertexType){
            this.vertexType = vertexType;
            return this;
        }

        public VertexBuilder setXCoordinate(double xCoordinate) {
            this.xCoordinate = xCoordinate;
            return this;
        }

        public VertexBuilder setYCoordinate(double yCoordinate) {
            this.yCoordinate = yCoordinate;
            return this;
        }

        public VertexBuilder setZCoordinate(double zCoordinate) {
            this.zCoordinate = zCoordinate;
            return this;
        }

        public Vertex build() {
            if (symbol == null) {
                symbol = id;
            }
            if(vertexType == null){
                vertexType = VertexType.SIMPLE_NODE;
            }
            Vertex vertex = new Vertex(graph, id, symbol, vertexType, xCoordinate, yCoordinate, zCoordinate);
            vertex.addAttribute(ElementAttributes.FROZEN_LAYOUT);
            vertex.addAttribute("xyz", xCoordinate, yCoordinate, zCoordinate);
            return vertex;
        }
    }
}
