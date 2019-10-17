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
    public ModelGraph transformGraph(ModelGraph graph) {
        return null;
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
