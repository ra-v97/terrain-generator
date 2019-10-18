package transformation;

import model.GraphEdge;
import model.InteriorNode;
import model.ModelGraph;
import model.Vertex;
import model.VertexType;
import org.javatuples.Triplet;

public class TransformationP1 implements Transformation {

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        if(!interiorNode.isPartitionRequired()){
            return false;
        }
        if(getSimpleVertexCount(triangle) != 2 || getHangingVertexCount(triangle) !=1){
            return false;
        }
        Vertex hangingVertex = null;
        Vertex simpleVertex1 = null;
        Vertex simpleVertex2 = null;
        for (Object o : triangle){
            Vertex v = (Vertex)o;
            if(v.getVertexType() == VertexType.HANGING_NODE){
                hangingVertex = v;
            }else if(simpleVertex1 == null){
                simpleVertex1 = v;
            }else{
                simpleVertex2 = v;
            }
        }

        GraphEdge oppositeToHangingNodeEdge = graph.getEdgeById(simpleVertex1.getEdgeBetween(simpleVertex2).getId())
                .orElseThrow(()->new RuntimeException("Unknown edge id"));
        GraphEdge hangingNodeAdjacentEdge1 = graph.getEdgeById(hangingVertex.getEdgeBetween(simpleVertex1).getId())
                .orElseThrow(()->new RuntimeException("Unknown edge id"));
        GraphEdge hangingNodeAdjacentEdge2 = graph.getEdgeById(hangingVertex.getEdgeBetween(simpleVertex2).getId())
                .orElseThrow(()->new RuntimeException("Unknown edge id"));

        if(!oppositeToHangingNodeEdge.isB()){
            return false;
        }

        if(oppositeToHangingNodeEdge.getL() < hangingNodeAdjacentEdge1.getL() || oppositeToHangingNodeEdge.getL() < hangingNodeAdjacentEdge2.getL()){
            return false;
        }
        return true;
    }

    @Override
    public void transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        Vertex hangingVertex = null;
        Vertex simpleVertex1 = null;
        Vertex simpleVertex2 = null;
        for (Object o : triangle){
            Vertex v = (Vertex)o;
            if(v.getVertexType() == VertexType.HANGING_NODE){
                hangingVertex = v;
            }else if(simpleVertex1 == null){
                simpleVertex1 = v;
            }else{
                simpleVertex2 = v;
            }
        }

        GraphEdge oppositeToHangingNodeEdge = graph.getEdgeById(simpleVertex1.getEdgeBetween(simpleVertex2).getId())
                .orElseThrow(()->new RuntimeException("Unknown edge id"));

        //transformation process
        graph.removeInterior(interiorNode.getId());
        graph.deleteEdge(simpleVertex1, simpleVertex2);

        double newVertexX = (simpleVertex1.getXCoordinate()+simpleVertex2.getXCoordinate())/2.0;
        double newVertexY = (simpleVertex1.getYCoordinate()+simpleVertex2.getYCoordinate())/2.0;
        double newVertexZ = (simpleVertex1.getZCoordinate()+simpleVertex2.getZCoordinate())/2.0;

        Vertex insertedVertex = graph.insertVertex(interiorNode.getId(),VertexType.SIMPLE_NODE, newVertexX, newVertexY, newVertexZ);

        String newEdge1Id = simpleVertex1.getId().concat(insertedVertex.getId());
        String newEdge2Id = simpleVertex2.getId().concat(insertedVertex.getId());
        String newEdge3Id = hangingVertex.getId().concat(insertedVertex.getId());

        GraphEdge insertedEdge1 = graph.insertEdge(newEdge1Id, simpleVertex1, insertedVertex);
        insertedEdge1.setB(oppositeToHangingNodeEdge.isB());
        insertedEdge1.setL(oppositeToHangingNodeEdge.getL() / 2.0);

        GraphEdge insertedEdge2 = graph.insertEdge(newEdge2Id, simpleVertex2, insertedVertex);
        insertedEdge2.setB(oppositeToHangingNodeEdge.isB());
        insertedEdge2.setL(oppositeToHangingNodeEdge.getL() / 2.0);

        GraphEdge insertedEdge3 = graph.insertEdge(newEdge3Id, hangingVertex, insertedVertex);
        insertedEdge3.setB(false);
        double insertedEdge3Length = Math.sqrt(
                (hangingVertex.getXCoordinate()-insertedVertex.getXCoordinate())*(hangingVertex.getXCoordinate()-insertedVertex.getXCoordinate())
                +(hangingVertex.getYCoordinate()-insertedVertex.getYCoordinate())*(hangingVertex.getYCoordinate()-insertedVertex.getYCoordinate())
                +(hangingVertex.getZCoordinate()-insertedVertex.getZCoordinate())*(hangingVertex.getZCoordinate()-insertedVertex.getZCoordinate()));
        insertedEdge3.setL(insertedEdge3Length);

        String insertedInterior1Id = hangingVertex.getId().concat(simpleVertex1.getId()).concat(insertedVertex.getId());
        String insertedInterior2Id = hangingVertex.getId().concat(simpleVertex2.getId()).concat(insertedVertex.getId());
        graph.insertInterior(insertedInterior1Id, hangingVertex, simpleVertex1,  insertedVertex);
        graph.insertInterior(insertedInterior2Id, hangingVertex, simpleVertex2,  insertedVertex);
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
