package transformation;

import model.*;
import org.javatuples.Triplet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformationP8 implements Transformation {

	private static final String SIMPLE_VERTEX_1 = "vertex1";
	private static final String SIMPLE_VERTEX_3 = "vertex3";
	private static final String SIMPLE_VERTEX_5 = "vertex5";
	private static final String HANGING_NODE_2 = "node2";
	private static final String HANGING_NODE_4 = "node4";

	private static final String HANGING_NODE_6 = "node6";

	private Map<String, Vertex> mapVerticesToModel(InteriorNode interiorNode) {
		Map<String, Vertex> verticesMap = new HashMap<>();

		Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();

		Vertex v0 = interiorNode.getTriangleVertexes().getValue0();
		Vertex v1 = interiorNode.getTriangleVertexes().getValue1();
		Vertex v2 = interiorNode.getTriangleVertexes().getValue2();

		List<Vertex> hanging_nodes = interiorNode.getAssociatedNodes();

		if (getSimpleVertexCount(triangle) != 3 || hanging_nodes.size() != 2) {
			return new HashMap<>();
		}

		if (v0.getEdgeBetween(v1) != null) {
			verticesMap.put(SIMPLE_VERTEX_1, v0);
			verticesMap.put(SIMPLE_VERTEX_5, v1);
			verticesMap.put(SIMPLE_VERTEX_3, v2);
		} else if (v1.getEdgeBetween(v2) != null) {
			verticesMap.put(SIMPLE_VERTEX_1, v1);
			verticesMap.put(SIMPLE_VERTEX_5, v2);
			verticesMap.put(SIMPLE_VERTEX_3, v0);
		} else {
			verticesMap.put(SIMPLE_VERTEX_1, v0);
			verticesMap.put(SIMPLE_VERTEX_5, v2);
			verticesMap.put(SIMPLE_VERTEX_3, v1);
		}

		if (verticesMap.get(SIMPLE_VERTEX_1).getEdgeBetween(hanging_nodes.get(0)) != null) {
			verticesMap.put(HANGING_NODE_2, hanging_nodes.get(0));
			verticesMap.put(HANGING_NODE_4, hanging_nodes.get(1));
		} else {
			verticesMap.put(HANGING_NODE_2, hanging_nodes.get(1));
			verticesMap.put(HANGING_NODE_4, hanging_nodes.get(0));
		}

		return verticesMap;
	}

	@Override
	public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {

		Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
		int hanging_nodes = interiorNode.getAssociatedNodes().size();

		if (getSimpleVertexCount(triangle) != 3 || hanging_nodes != 2) {
			return false;
		}

		Map<String, Vertex> verticesMap = this.mapVerticesToModel(interiorNode);

		if (verticesMap.size() != 5) {
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

		if (this.isConditionCompleted(graph, interiorNode)) {

			Map<String, Vertex> verticesMap = this.mapVerticesToModel(interiorNode);

			// remove old interior
			graph.removeInterior(interiorNode.getId());

			// add third hanging node
			Vertex vertex1 = verticesMap.get(SIMPLE_VERTEX_1);
			Vertex vertex5 = verticesMap.get(SIMPLE_VERTEX_5);
			Vertex node6 = graph.insertVertex(interiorNode.getId(), VertexType.HANGING_NODE, new Point3d(
					(vertex1.getXCoordinate() + vertex5.getXCoordinate()) / 2,
					(vertex1.getYCoordinate() + vertex5.getYCoordinate()) / 2,
					(vertex1.getZCoordinate() + vertex5.getZCoordinate()) / 2));

			verticesMap.put(HANGING_NODE_6, node6);

			// remove old edge
			graph.deleteEdge(verticesMap.get(SIMPLE_VERTEX_1), verticesMap.get(SIMPLE_VERTEX_5));

			// add new edges
			graph.insertEdge("e6", verticesMap.get(HANGING_NODE_6), verticesMap.get(SIMPLE_VERTEX_5), false);
			graph.insertEdge("e7", verticesMap.get(SIMPLE_VERTEX_1), verticesMap.get(HANGING_NODE_6), false);
			graph.insertEdge("e8", verticesMap.get(HANGING_NODE_6), verticesMap.get(SIMPLE_VERTEX_3), false);

			// insert new interiors
			String interior2Id = verticesMap.get(SIMPLE_VERTEX_1).getId() + verticesMap.get(SIMPLE_VERTEX_3).getId() + verticesMap.get(HANGING_NODE_6).getId();
			String interior3Id = verticesMap.get(SIMPLE_VERTEX_3).getId() + verticesMap.get(SIMPLE_VERTEX_5).getId() + verticesMap.get(HANGING_NODE_6).getId();

			InteriorNode interior2Node = graph.insertInterior(interior2Id, verticesMap.get(SIMPLE_VERTEX_1), verticesMap.get(SIMPLE_VERTEX_3), verticesMap.get(HANGING_NODE_6), verticesMap.get(HANGING_NODE_2));
			interior2Node.setPartitionRequired(false);

			InteriorNode interior3Node = graph.insertInterior(interior3Id, verticesMap.get(SIMPLE_VERTEX_3), verticesMap.get(SIMPLE_VERTEX_5), verticesMap.get(HANGING_NODE_6), verticesMap.get(HANGING_NODE_4));
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
