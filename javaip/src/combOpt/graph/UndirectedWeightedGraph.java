package combOpt.graph;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

public class UndirectedWeightedGraph {

	private UndirectedGraphTable graph;
	private Map<Edge, Double> weights;

	private UndirectedWeightedGraph(UndirectedGraphTable graph,
			Map<Edge, Double> edgeWeights) {
		this.graph = graph;
		this.weights = edgeWeights;
	}

	public UndirectedWeightedGraph() {
		this(new UndirectedGraphTable(), Maps.<Edge, Double> newHashMap());
	}

	public Node addNode() {
		return graph.addNode();
	}

	public void addNodes(int numNodes) {
		graph.addNodes(numNodes);
	}

	public Edge addEdge(Node endPoint, Node otherEndPoint, double weight) {
		Edge ans = graph.addEdge(endPoint, otherEndPoint);
		weights.put(ans, weight);
		return ans;
	}

	public Map<Node, Edge> getAdjacentEdges(Node endPoint) {
		return graph.getAdjacentEdges(endPoint);
	}

	public Set<Edge> edgeSet() {
		return graph.edgeSet();
	}

	public List<Node> vertexSet() {
		return graph.vertexSet();
	}

	public double getWeight(Edge edge) {
		return weights.get(edge);
	}

	public UndirectedGraphTable getGraph() {
		return this.graph;
	}

	public static UndirectedWeightedGraph fromUndirectedGraph(
			UndirectedGraphTable graph, Map<Edge, Double> edgeWeights) {
		if (!graph.edgeSet().equals(edgeWeights.keySet())) {
			throw new RuntimeException(
					"Edges in graph and weights of edges must be the same");
		}
		return new UndirectedWeightedGraph(graph, edgeWeights);
	}

}
