package combOpt.graph;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class ReadableDirectedWeightedGraphImpl implements
		ReadableDirectedWeightedGraph {

	private ReadableDirectedGraph graph;
	private Function<? super DirectedEdge, ? extends Double> edgeWeights;

	public ReadableDirectedWeightedGraphImpl(ReadableDirectedGraph graph,
			Function<? super DirectedEdge, ? extends Double> edgeWeights) {
		this.graph = graph;
		this.edgeWeights = edgeWeights;
	}

	public ReadableDirectedWeightedGraphImpl(ReadableDirectedGraph graph,
			Map<DirectedEdge, Double> edgeWeights) {
		this(graph, Functions.forMap(edgeWeights));

	}

	@Override
	public Map<Node, DirectedEdge> getOutgoingEdges(Node source) {
		return graph.getOutgoingEdges(source);
	}

	@Override
	public Map<Node, DirectedEdge> getIncomingEdges(Node target) {
		return graph.getIncomingEdges(target);
	}

	@Override
	public Collection<DirectedEdge> edges() {
		return graph.edges();
	}

	@Override
	public List<Node> nodes() {
		return graph.nodes();
	}

	@Override
	public double getWeight(DirectedEdge edge) {
		return edgeWeights.apply(edge);
	}

	@Override
	public ReadableDirectedGraph getGraph() {
		return this.graph;
	}

}
