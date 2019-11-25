import model.*;
import org.junit.Ignore;
import org.junit.Test;
import transformation.Transformation;
import transformation.TransformationP6;

import static org.junit.jupiter.api.Assertions.*;

@Ignore
public class TransformationP6Test extends AbstractTransformationTest {
	private Transformation transformation = new TransformationP6();

	@Test
	public void conditionFailsWithIncorrectLengths(){
		ModelGraph graph = createIncorrectLengthsGraph();
		assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
	}

	@Test
	public void conditionFailsWithTooFewHangingNodes(){
		ModelGraph graph = createTooFewHangingNodesGraph();
		assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
	}

	@Test
	public void conditionFailsWithTooMuchHangingNodes(){
		ModelGraph graph = createTooMuchHangingNodesGraph();
		assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
	}

	@Test
	public void conditionFailsWithoutHangingNodeOnLongestSide(){
		ModelGraph graph = createIncorrectHangingNodePositionGraph();
		assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
	}

	@Test
	public void conditionPassesWithCorrectGraph(){
		ModelGraph graph = createCorrectGraph();
		assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
	}

	@Test
	public void transformationProducesTwoInteriorNodes(){
		ModelGraph graph = createCorrectGraph();
		assertEquals(2, transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)).getInteriors().size());
	}

	@Test
	public void transformationProducesNoNewVertices(){
		ModelGraph graph = createCorrectGraph();
		int vertices = graph.getVertices().size();
		assertEquals(vertices, transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)).getVertices().size());
	}

	@Test
	public void transformationChangesVertexType(){
		ModelGraph graph = createCorrectGraph();
		Vertex vertex = graph.getVertex("h2").orElseThrow(AssertionError::new);
		assertEquals(VertexType.HANGING_NODE, vertex.getVertexType());
		transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new));
		assertEquals(VertexType.SIMPLE_NODE, vertex.getVertexType());
	}

	@Test
	public void transformationProducesCorrectVertexTypes(){
		ModelGraph graph = createCorrectGraph();
		transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new));
		int hanging_nodes = 0, simple_nodes = 0;
		for(Vertex v: graph.getVertices()){
			if(v.getVertexType() == VertexType.SIMPLE_NODE){
				simple_nodes++;
			}
			else{
				hanging_nodes++;
			}
		}
		Vertex vertex = graph.getVertex("h2").orElseThrow(AssertionError::new);
		assertEquals(4,simple_nodes);
		assertEquals(1, hanging_nodes);
	}

	@Test
	public void transformationProducesNewInteriorNodesWithCorrectParams(){
		ModelGraph graph = createCorrectGraph();
		InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);

		transformation.transformGraph(graph, interior);
		InteriorNode interior1 = (InteriorNode) graph.getInteriors().toArray()[0];
		InteriorNode interior2 = (InteriorNode) graph.getInteriors().toArray()[1];

		assertTrue(!interior1.isPartitionRequired() && !interior2.isPartitionRequired());
	}

	@Test
	public void transformationProducesNewEdges(){
		ModelGraph graph = createCorrectGraph();
		InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
		ModelGraph transformed = transformation.transformGraph(graph, interior);
		assertEquals(12, transformed.getEdges().size());
	}

	@Test
	public void transformationProducesNewInternalEdgeWithCorrectParams(){
		ModelGraph graph = createCorrectGraph();
		transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new));
		Vertex v = graph.getVertex("v5").orElseThrow(AssertionError::new);
		Vertex h = graph.getVertex("h2").orElseThrow(AssertionError::new);
		GraphEdge internalEdge = graph.getEdge(v, h).orElseThrow(AssertionError::new);

		assertFalse(internalEdge.getB());
	}

	@Test
	public void transformationProducesNewInternalEdgeWithCorrectLength(){
		ModelGraph graph = createCorrectGraph();
		transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new));
		Vertex v = graph.getVertex("v5").orElseThrow(AssertionError::new);
		Vertex h = graph.getVertex("h2").orElseThrow(AssertionError::new);
		GraphEdge internalEdge = graph.getEdge(v, h).orElseThrow(AssertionError::new);

		assertEquals(Point3d.distance(v.getCoordinates(), h.getCoordinates()), internalEdge.getL());
	}


	private ModelGraph createIncorrectLengthsGraph() {
		ModelGraph graph = new ModelGraph("testGraph");
		Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0,0.0));
		Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
		Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
		Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.5, 1.0, 0.0));
		Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 2.0, 0.0));

		graph.insertEdge("e1", v1, h2, true);
		graph.insertEdge("e2", h2, v3, true);
		graph.insertEdge("e3", v3, h4, true);
		graph.insertEdge("e4", h4, v5, true);
		graph.insertEdge("e5", v1, v5, true);

		graph.insertInterior("i1", v1, v3, v5, h2, h4);
		return graph;
	}

	private ModelGraph createTooFewHangingNodesGraph() {
		ModelGraph graph = new ModelGraph("testGraph");
		Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0,0.0));
		Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
		Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
		Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));

		graph.insertEdge("e1", v1, h2, true);
		graph.insertEdge("e2", h2, v3, true);
		graph.insertEdge("e3", v3, v5, true);
		graph.insertEdge("e5", v1, v5, true);

		graph.insertInterior("i1", v1, v3, v5, h2);
		return graph;
	}

	private ModelGraph createTooMuchHangingNodesGraph() {
		ModelGraph graph = new ModelGraph("testGraph");
		Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0,0.0));
		Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
		Vertex v3 = graph.insertVertex("v3", VertexType.HANGING_NODE, new Point3d(2.0, 0.0, 0.0));
		Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.5, 0.5, 0.0));
		Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));

		graph.insertEdge("e1", v1, h2, true);
		graph.insertEdge("e2", h2, v3, true);
		graph.insertEdge("e3", v3, h4, true);
		graph.insertEdge("e4", h4, v5, true);
		graph.insertEdge("e5", v1, v5, true);

		graph.insertInterior("i1", v1, v3, v5, h2, h4);
		return graph;
	}

	private ModelGraph createIncorrectHangingNodePositionGraph() {
		ModelGraph graph = new ModelGraph("testGraph");
		Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0,0.0));
		Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
		Vertex h3 = graph.insertVertex("h3", VertexType.HANGING_NODE, new Point3d(1.5, 1.0, 0.0));
		Vertex v4 = graph.insertVertex("v4", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
		Vertex h5 = graph.insertVertex("h5", VertexType.HANGING_NODE, new Point3d(0.5, 0.5, 0.0));

		graph.insertEdge("e1", v1, v2, true);
		graph.insertEdge("e2", v2, h3, true);
		graph.insertEdge("e3", h3, v4, true);
		graph.insertEdge("e4", v4, h5, true);
		graph.insertEdge("e5", h5, v1, true);

		graph.insertInterior("i1", v1, v2, v4, h3, h5);
		return graph;
	}

	private ModelGraph createCorrectGraph() {
		ModelGraph graph = new ModelGraph("testGraph");
		Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0,0.0));
		Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
		Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
		Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.5, 0.5, 0.0));
		Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));

		graph.insertEdge("e1", v1, h2, true);
		graph.insertEdge("e2", h2, v3, true);
		graph.insertEdge("e3", v3, h4, true);
		graph.insertEdge("e4", h4, v5, true);
		graph.insertEdge("e5", v1, v5, true);

		graph.insertInterior("i1", v1, v3, v5, h2, h4);
		return graph;
	}

}
