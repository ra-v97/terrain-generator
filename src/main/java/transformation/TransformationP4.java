package transformation;

import model.*;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import java.util.Optional;

public class TransformationP4 implements Transformation {

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {

        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        int hang = interiorNode.getAssociatedNodes().size();

        if (!interiorNode.isPartitionRequired()) {
            return false;
        }
        if (getSimpleVertexCount(triangle) != 3 || hang != 1) {
            return false;
        }

        Vertex hangingNode = interiorNode.getAssociatedNodes().get(0);

        Pair<Triplet<Vertex, Vertex, Vertex>,
                Quartet<GraphEdge, GraphEdge, GraphEdge, GraphEdge>> orientedFigure = orientFigure(graph, hangingNode, triangle);

        GraphEdge v1h = orientedFigure.getValue1().getValue0();
        GraphEdge v2h = orientedFigure.getValue1().getValue1();
        GraphEdge v2v3 = orientedFigure.getValue1().getValue2();
        GraphEdge v1v3 = orientedFigure.getValue1().getValue3();

        return (v1v3.getB() && (v1h.getL() + v2h.getL() < v1v3.getL()) && (v1v3.getL() >= v2v3.getL()));
    }

    private Optional<Quartet<GraphEdge, GraphEdge, GraphEdge, GraphEdge>> verifyRotation(ModelGraph graph,
                                                                                         Vertex hangingNode,
                                                                                         Triplet<Vertex, Vertex, Vertex> triangle) {

        GraphEdge v1h, v2h, v2v3, v1v3;

        if (graph.getEdgeBetweenNodes(triangle.getValue0(), hangingNode).isPresent() &&
                graph.getEdgeBetweenNodes(triangle.getValue1(), hangingNode).isPresent()) {

            v1h = graph.getEdgeBetweenNodes(triangle.getValue0(), hangingNode)
                    .orElseThrow(() -> new RuntimeException("Unknown edge id"));
            v2h = graph.getEdgeBetweenNodes(triangle.getValue1(), hangingNode)
                    .orElseThrow(() -> new RuntimeException("Unknown edge id"));
            v1v3 = graph.getEdgeBetweenNodes(triangle.getValue0(), triangle.getValue2())
                    .orElseThrow(() -> new RuntimeException("Unknown edge id"));
            v2v3 = graph.getEdgeBetweenNodes(triangle.getValue1(), triangle.getValue2())
                    .orElseThrow(() -> new RuntimeException("Unknown edge id"));

            return Optional.of(new Quartet<>(v1h, v2h, v2v3, v1v3));
        } else {
            return Optional.empty();
        }
    }

    private Pair<Triplet<Vertex, Vertex, Vertex>,
            Quartet<GraphEdge, GraphEdge, GraphEdge, GraphEdge>> orientFigure(ModelGraph graph,
                                                                              Vertex hangingNode,
                                                                              Triplet<Vertex, Vertex, Vertex> triangle) {

        Optional<Quartet<GraphEdge, GraphEdge, GraphEdge, GraphEdge>> rotatedEgdes = verifyRotation(graph, hangingNode, triangle);
        Triplet<Vertex, Vertex, Vertex> rotatedTri = triangle;

        if (!rotatedEgdes.isPresent()) {
            rotatedTri = new Triplet<>(triangle.getValue1(), triangle.getValue2(), triangle.getValue0());
            rotatedEgdes = verifyRotation(graph, hangingNode, rotatedTri);

            if (!rotatedEgdes.isPresent()) {
                rotatedTri = new Triplet<>(triangle.getValue2(), triangle.getValue0(), triangle.getValue1());
                rotatedEgdes = verifyRotation(graph, hangingNode, rotatedTri);
            }
        }

        if (rotatedEgdes.isPresent()) {
            Quartet<GraphEdge, GraphEdge, GraphEdge, GraphEdge> edges = rotatedEgdes.get();

            if (edges.getValue2().getL() > edges.getValue3().getL()) {
                return new Pair<>(new Triplet<>(rotatedTri.getValue1(), rotatedTri.getValue0(), rotatedTri.getValue2()),
                        new Quartet<>(edges.getValue1(), edges.getValue0(), edges.getValue3(), edges.getValue2()));
            } else {
                return new Pair<>(rotatedTri, rotatedEgdes.get());
            }

        } else {
            throw new RuntimeException("Transformation error");
        }
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        Vertex hangingNode = interiorNode.getAssociatedNodes().get(0);

        Pair<Triplet<Vertex, Vertex, Vertex>,
                Quartet<GraphEdge, GraphEdge, GraphEdge, GraphEdge>> rotatedFigure = orientFigure(graph, hangingNode, triangle);

        Vertex v1 = rotatedFigure.getValue0().getValue0();
        Vertex v2 = rotatedFigure.getValue0().getValue1();
        Vertex v3 = rotatedFigure.getValue0().getValue2();

        GraphEdge v1v3 = rotatedFigure.getValue1().getValue3();

        //transformation process
        graph.removeInterior(interiorNode.getId());
        graph.deleteEdge(v1, v3);

        Vertex insertedVertex = graph.insertVertex(interiorNode.getId(),
                VertexType.SIMPLE_NODE,
                Point3d.middlePoint(v1.getCoordinates(), v3.getCoordinates()));

        String newEdge1Id = v1.getId().concat(insertedVertex.getId());
        String newEdge2Id = v3.getId().concat(insertedVertex.getId());
        String newEdge3Id = v2.getId().concat(insertedVertex.getId());

        GraphEdge insertedEdge1 = graph.insertEdge(newEdge1Id, v1, insertedVertex);
        insertedEdge1.setB(v1v3.getB());

        GraphEdge insertedEdge2 = graph.insertEdge(newEdge2Id, v3, insertedVertex);
        insertedEdge2.setB(v1v3.getB());

        GraphEdge insertedEdge3 = graph.insertEdge(newEdge3Id, v2, insertedVertex);
        insertedEdge3.setB(false);

        String insertedInterior1Id = v1.getId().concat(v2.getId()).concat(insertedVertex.getId());
        String insertedInterior2Id = v2.getId().concat(v3.getId()).concat(insertedVertex.getId());
        graph.insertInterior(insertedInterior1Id, v1, v2, insertedVertex);
        graph.insertInterior(insertedInterior2Id, v2, v3, insertedVertex);

        return graph;
    }

    private static int getSimpleVertexCount(Triplet<Vertex, Vertex, Vertex> triangle) {
        int count = 0;
        for (Object o : triangle) {
            Vertex v = (Vertex) o;
            if (v.getVertexType() == VertexType.SIMPLE_NODE) {
                count++;
            }
        }
        return count;
    }
}
