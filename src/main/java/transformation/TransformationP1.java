package transformation;

import model.GraphEdge;
import model.InteriorNode;
import model.ModelGraph;
import model.Point3d;
import model.Vertex;
import model.VertexType;
import org.javatuples.Triplet;

import java.util.HashMap;
import java.util.Map;

public class TransformationP1 implements Transformation {

    private static final String VERTEX_MAP_HANGING_NODE_KEY = "hangingNode";

    private static final String VERTEX_MAP_SIMPLE_VERTEX_1_KEY = "simpleVertex1";

    private static final String VERTEX_MAP_SIMPLE_VERTEX_2_KEY = "simpleVertex2";

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        if(!interiorNode.isPartitionRequired()){
            return false;
        }
        if(getSimpleVertexCount(triangle) != 2 || getHangingVertexCount(triangle) !=1){
            return false;
        }

        Map<String, Vertex> model = mapTriangleVertexesToModel(interiorNode.getTriangleVertexes());
        Vertex simpleVertex1 = model.get(VERTEX_MAP_SIMPLE_VERTEX_1_KEY);
        Vertex simpleVertex2 = model.get(VERTEX_MAP_SIMPLE_VERTEX_2_KEY);
        Vertex hangingVertex = model.get(VERTEX_MAP_HANGING_NODE_KEY);

        if(simpleVertex1 == null || simpleVertex2 == null || hangingVertex == null){
            throw new RuntimeException("Transformation error");
        }

        GraphEdge oppositeToHangingNodeEdge = graph.getEdgeBetweenNodes(simpleVertex1, simpleVertex2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge hangingNodeAdjacentEdge1 = graph.getEdgeBetweenNodes(hangingVertex, simpleVertex1)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge hangingNodeAdjacentEdge2 = graph.getEdgeBetweenNodes(hangingVertex, simpleVertex2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));

        if(!oppositeToHangingNodeEdge.getB()){
            return false;
        }

        if(oppositeToHangingNodeEdge.getL() < hangingNodeAdjacentEdge1.getL() || oppositeToHangingNodeEdge.getL() < hangingNodeAdjacentEdge2.getL()){
            return false;
        }
        return true;
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        Map<String, Vertex> model = mapTriangleVertexesToModel(interiorNode.getTriangleVertexes());
        Vertex simpleVertex1 = model.get(VERTEX_MAP_SIMPLE_VERTEX_1_KEY);
        Vertex simpleVertex2 = model.get(VERTEX_MAP_SIMPLE_VERTEX_2_KEY);
        Vertex hangingVertex = model.get(VERTEX_MAP_HANGING_NODE_KEY);

        if(simpleVertex1 == null || simpleVertex2 == null || hangingVertex == null){
            throw new RuntimeException("Transformation error");
        }

        GraphEdge oppositeToHangingNodeEdge = graph.getEdgeBetweenNodes(simpleVertex1, simpleVertex2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));

        //transformation process
        graph.removeInterior(interiorNode.getId());
        graph.deleteEdge(simpleVertex1, simpleVertex2);

        Vertex insertedVertex = graph.insertVertex(interiorNode.getId(),
                VertexType.SIMPLE_NODE,
                Point3d.middlePoint(simpleVertex1.getCoordinates(), simpleVertex2.getCoordinates()));

        String newEdge1Id = simpleVertex1.getId().concat(insertedVertex.getId());
        String newEdge2Id = simpleVertex2.getId().concat(insertedVertex.getId());
        String newEdge3Id = hangingVertex.getId().concat(insertedVertex.getId());

        GraphEdge insertedEdge1 = graph.insertEdge(newEdge1Id, simpleVertex1, insertedVertex);
        insertedEdge1.setB(oppositeToHangingNodeEdge.getB());

        GraphEdge insertedEdge2 = graph.insertEdge(newEdge2Id, simpleVertex2, insertedVertex);
        insertedEdge2.setB(oppositeToHangingNodeEdge.getB());

        GraphEdge insertedEdge3 = graph.insertEdge(newEdge3Id, hangingVertex, insertedVertex);
        insertedEdge3.setB(false);

        String insertedInterior1Id = hangingVertex.getId().concat(simpleVertex1.getId()).concat(insertedVertex.getId());
        String insertedInterior2Id = hangingVertex.getId().concat(simpleVertex2.getId()).concat(insertedVertex.getId());
        graph.insertInterior(insertedInterior1Id, hangingVertex, simpleVertex1,  insertedVertex);
        graph.insertInterior(insertedInterior2Id, hangingVertex, simpleVertex2,  insertedVertex);
        return graph;
    }

    private static Map<String, Vertex> mapTriangleVertexesToModel(Triplet<Vertex, Vertex, Vertex> triangle){
        Map<String, Vertex> triangleModel = new HashMap<>();
        for (Object o : triangle){
            Vertex v = (Vertex)o;
            if(v.getVertexType() == VertexType.HANGING_NODE){
                triangleModel.put(VERTEX_MAP_HANGING_NODE_KEY, v);
            }else if(triangleModel.get(VERTEX_MAP_SIMPLE_VERTEX_1_KEY) == null){
                triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_1_KEY, v);
            }else{
                triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_2_KEY, v);
            }
        }
        return triangleModel;
    }

    private static int getSimpleVertexCount(Triplet<Vertex, Vertex, Vertex> triangle) {
        int count = 0;
        for (Object o : triangle) {
            Vertex v = (Vertex)o;
            if(v.getVertexType() == VertexType.SIMPLE_NODE){
                count++;
            }
        }
        return count;
    }

    private static int getHangingVertexCount(Triplet<Vertex, Vertex, Vertex> triangle) {
        int count = 0;
        for (Object o : triangle) {
            Vertex v = (Vertex)o;
            if(v.getVertexType() == VertexType.HANGING_NODE){
                count++;
            }
        }
        return count;
    }
}
