package combOpt.graph;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Subgraphs {

	public static ReadableUndirectedSubgraphView onlyEdges(
			ReadableUndirectedGraph graph, Predicate<? super Edge> includedEdges) {
		return new ReadableUndirectedSubgraphView(graph,
				Predicates.alwaysTrue(), includedEdges);
	}

	public static class ReadableUndirectedSubgraphView implements
			ReadableUndirectedGraph {

		private ReadableUndirectedGraph graph;
		private final Predicate<? super Edge> includedEdges;
		private final Predicate<? super Node> includedNodes;
		private Predicate<Entry<Node, Edge>> includedEntries;

		/**
		 * Warning: unexpected behavior may occur if vertices are removed, but
		 * edges incident to these vertices are not.
		 * 
		 * @param graph
		 * @param includedNodes
		 * @param includedEdges
		 */
		public ReadableUndirectedSubgraphView(ReadableUndirectedGraph graph,
				Predicate<? super Node> includedNodes,
				Predicate<? super Edge> includedEdges) {
			super();
			this.graph = graph;
			this.includedEdges = includedEdges;
			this.includedNodes = includedNodes;
			includedEntries = new Predicate<Entry<Node, Edge>>() {
				@Override
				public boolean apply(Entry<Node, Edge> entry) {
					return ReadableUndirectedSubgraphView.this.includedNodes
							.apply(entry.getKey())
							&& ReadableUndirectedSubgraphView.this.includedEdges
									.apply(entry.getValue());
				}
			};
		}

		@Override
		public Map<Node, Edge> getAdjacentEdges(Node endPoint) {
			return Maps.filterEntries(graph.getAdjacentEdges(endPoint),
					includedEntries);
		}

		@Override
		public Set<Edge> edgeSet() {
			return Sets.filter(graph.edgeSet(), includedEdges);
		}

		@Override
		public Iterable<Node> vertexSet() {
			return Iterables.filter(graph.vertexSet(), includedNodes);
		}

	}

}
