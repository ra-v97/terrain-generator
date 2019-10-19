package model;

import org.graphstream.graph.implementations.AbstractGraph;

public class Vertex extends GraphNode {

    private VertexType vertexType;

    public Vertex(AbstractGraph graph, String id, VertexType vertexType, double x, double y, double z) {
        super(graph, id, vertexType.getSymbol(), x, y, z);
        this.vertexType = vertexType;
    }

    public VertexType getVertexType() {
        return vertexType;
    }

    public static class VertexBuilder {

        private final AbstractGraph graph;

        private final String id;

        private VertexType vertexType;

        private double xCoordinate;

        private double yCoordinate;

        private double zCoordinate;

        public VertexBuilder(AbstractGraph graph, String id) {
            this.graph = graph;
            this.id = id;
        }

        public VertexBuilder setVertexType(VertexType vertexType) {
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
            if (vertexType == null) {
                vertexType = VertexType.SIMPLE_NODE;
            }
            return new Vertex(graph, id, vertexType, xCoordinate, yCoordinate, zCoordinate);
        }
    }
}
