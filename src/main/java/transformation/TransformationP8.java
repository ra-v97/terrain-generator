package transformation;

import model.*;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TransformationP8 implements Transformation{

	private static final String SIMPLE_VERTEX_1 = "vertex1";
	private static final String SIMPLE_VERTEX_3 = "vertex3";
	private static final String SIMPLE_VERTEX_5 = "vertex5";
	private static final String HANGING_NODE_2 = "node2";
	private static final String HANGING_NODE_4 = "node4";

	private static final String HANGING_NODE_6 = "node6";

	private Map<String, Vertex> mapVerticesToModel(ModelGraph graph){
		Map<String, Vertex> verticesMap = new HashMap<>();
		Collection<GraphEdge> edges = graph.getEdges();
		GraphEdge wholeEdge = null;

		for(GraphEdge edge: edges){
			Pair<GraphNode, GraphNode> edgeNodes = edge.getEdgeNodes();
			GraphNode graphNode0 = edgeNodes.getValue0();
			GraphNode graphNode1 = edgeNodes.getValue1();

			if(graphNode0 instanceof Vertex && graphNode1 instanceof Vertex){
				Vertex node0 = (Vertex) graphNode0;
				Vertex node1 = (Vertex) graphNode1;

				if(node0.getVertexType() == VertexType.SIMPLE_NODE && node1.getVertexType() == VertexType.SIMPLE_NODE){
					verticesMap.put(SIMPLE_VERTEX_1, node0);
					verticesMap.put(SIMPLE_VERTEX_5, node1);
					wholeEdge = edge;
					break;
				}
			}

		}

		for(GraphEdge edge: edges){
			try {
				if (edge.equals(wholeEdge)) {
					continue;
				}
			}
			catch (NullPointerException e){
				return new HashMap<>();
			}
			Pair<GraphNode, GraphNode> edgeNodes = edge.getEdgeNodes();
			GraphNode graphNode0 = edgeNodes.getValue0();
			GraphNode graphNode1 = edgeNodes.getValue1();

			if(graphNode0 instanceof Vertex && graphNode1 instanceof Vertex) {
				Vertex node0 = (Vertex) graphNode0;
				Vertex node1 = (Vertex) graphNode1;

				if (node0 == verticesMap.get(SIMPLE_VERTEX_1)) verticesMap.put(HANGING_NODE_2, node1);
				if (node1 == verticesMap.get(SIMPLE_VERTEX_1)) verticesMap.put(HANGING_NODE_2, node0);

				if (node0 == verticesMap.get(SIMPLE_VERTEX_5)) verticesMap.put(HANGING_NODE_4, node1);
				if (node1 == verticesMap.get(SIMPLE_VERTEX_5)) verticesMap.put(HANGING_NODE_4, node0);
			}
		}

		for(GraphNode node: graph.getVertices()){
			if(node != verticesMap.get(SIMPLE_VERTEX_1) &&
					node != verticesMap.get(SIMPLE_VERTEX_5) &&
					node != verticesMap.get(HANGING_NODE_2) &&
					node != verticesMap.get(HANGING_NODE_4)) verticesMap.put(SIMPLE_VERTEX_3, (Vertex) node);
		}

		return verticesMap;
	}

	@Override
	public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode){

		Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
		int hanging_nodes = interiorNode.getAssociatedNodes().size();

		if (getSimpleVertexCount(triangle) != 3 || hanging_nodes != 2) {
			return false;
		}

		Map<String, Vertex> verticesMap = this.mapVerticesToModel(graph);

		if(verticesMap.size() != 5){
			return false;
		}

		Vertex simpleVertex1 = verticesMap.get(SIMPLE_VERTEX_1);
		Vertex hangingNode2 = verticesMap.get(HANGING_NODE_2);
		Vertex simpleVertex3 = verticesMap.get(SIMPLE_VERTEX_3);
		Vertex hangingNode4 = verticesMap.get(HANGING_NODE_4);
		Vertex simpleVertex5 = verticesMap.get(SIMPLE_VERTEX_5);


		GraphEdge e1 = graph.getEdgeBetweenNodes(simpleVertex1, hangingNode2)
				.orElseThrow(() -> new RuntimeException("Unknown edge id"));
		GraphEdge e2 = graph.getEdgeBetweenNodes(hangingNode2, simpleVertex3)
				.orElseThrow(() -> new RuntimeException("Unknown edge id"));
		GraphEdge e3 = graph.getEdgeBetweenNodes(simpleVertex3, hangingNode4)
				.orElseThrow(() -> new RuntimeException("Unknown edge id"));
		GraphEdge e4 = graph.getEdgeBetweenNodes(hangingNode4, simpleVertex5)
				.orElseThrow(() -> new RuntimeException("Unknown edge id"));
		GraphEdge e5 = graph.getEdgeBetweenNodes(simpleVertex5, simpleVertex1)
				.orElseThrow(() -> new RuntimeException("Unknown edge id"));

		return !e5.getB() && e5.getL() > e1.getL() + e2.getL() && e5.getL() > e3.getL() + e4.getL();
	}

	@Override
	public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {

		Map<String, Vertex> verticesMap = this.mapVerticesToModel(graph);

		if(this.isConditionCompleted(graph, interiorNode)){

			// remove old interior
			graph.removeInterior(interiorNode.getId());

			// add third hanging node
			Vertex vertex1 = verticesMap.get(SIMPLE_VERTEX_1);
			Vertex vertex5 = verticesMap.get(SIMPLE_VERTEX_5);
			Vertex node6 = graph.insertVertex(interiorNode.getId(), VertexType.HANGING_NODE, new Point3d(
					(vertex1.getXCoordinate() + vertex5.getXCoordinate())/2,
					(vertex1.getYCoordinate() + vertex5.getYCoordinate())/2,
					(vertex1.getZCoordinate() + vertex5.getZCoordinate())/2));

			verticesMap.put(HANGING_NODE_6, node6);

			graph.insertEdge("e6", verticesMap.get(HANGING_NODE_6), verticesMap.get(SIMPLE_VERTEX_3), false);

			//insert new interiors
			String interior2Id = verticesMap.get(SIMPLE_VERTEX_1).getId() + verticesMap.get(SIMPLE_VERTEX_3).getId() + verticesMap.get(HANGING_NODE_6).getId();
			String interior3Id = verticesMap.get(SIMPLE_VERTEX_3).getId() + verticesMap.get(SIMPLE_VERTEX_5).getId() + verticesMap.get(HANGING_NODE_6).getId();

			InteriorNode interior2Node = graph.insertInterior(interior2Id, verticesMap.get(SIMPLE_VERTEX_1), verticesMap.get(SIMPLE_VERTEX_3), verticesMap.get(HANGING_NODE_6));
			interior2Node.setPartitionRequired(false);

			InteriorNode interior3Node = graph.insertInterior(interior3Id, verticesMap.get(SIMPLE_VERTEX_3), verticesMap.get(SIMPLE_VERTEX_5), verticesMap.get(HANGING_NODE_6));
			interior3Node.setPartitionRequired(false);

		}

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
