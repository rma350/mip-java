package combOpt.graph;

import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;

public class DirectedView {

	private ReadableUndirectedGraph undirectedGraph;
	private DirectedGraphTable directedGraph;
	private Map<DirectedEdge, Edge> toOriginalEdges;

	public DirectedView(ReadableUndirectedGraph graph) {
		this.undirectedGraph = graph;
		this.directedGraph = new DirectedGraphTable();
		this.toOriginalEdges = Maps.newHashMap();
		for (Node node : graph.vertexSet()) {
			directedGraph.addNode(node);
		}
		for (Edge edge : graph.edgeSet()) {
			Iterator<Node> nodes = edge.getEndpoints().iterator();
			Node first = nodes.next();
			Node second = nodes.next();
			toOriginalEdges.put(directedGraph.addEdge(first, second), edge);
			toOriginalEdges.put(directedGraph.addEdge(second, first), edge);
		}
	}

	public ReadableUndirectedGraph getUndirectedGraph() {
		return undirectedGraph;
	}

	public DirectedGraphTable getDirectedGraph() {
		return directedGraph;
	}

	public Map<DirectedEdge, Edge> getToOriginalEdges() {
		return toOriginalEdges;
	}

	public ReadableDirectedWeightedGraph createDirectedWeightedView(
			Map<Edge, Double> edgeWeights) {
		return new ReadableDirectedWeightedGraphImpl(this.directedGraph,
				Functions.compose(Functions.forMap(edgeWeights),
						Functions.forMap(toOriginalEdges)));
	}

}
