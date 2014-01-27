package combOpt.graph.algorithms;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import combOpt.graph.Edge;
import combOpt.graph.Node;
import combOpt.graph.ReadableUndirectedGraph;

public class ConnectedComponents {

	public static Set<Node> connectedComponent(ReadableUndirectedGraph graph,
			Node start) {
		Set<Node> explored = Sets.newHashSet();
		Set<Node> frontier = Sets.newHashSet(start);
		while (!frontier.isEmpty()) {
			Node top = frontier.iterator().next();
			frontier.remove(top);
			explored.add(top);
			for (Map.Entry<Node, Edge> neighbor : graph.getAdjacentEdges(top)
					.entrySet()) {
				if (!explored.contains(neighbor.getKey())) {
					frontier.add(neighbor.getKey());
				}
			}
		}
		return explored;
	}

	public static List<Set<Node>> connectedComponents(
			ReadableUndirectedGraph graph) {
		List<Set<Node>> ans = Lists.newArrayList();
		Set<Node> unused = Sets.newHashSet(graph.vertexSet());
		while (!unused.isEmpty()) {
			Node first = unused.iterator().next();
			Set<Node> component = connectedComponent(graph, first);
			ans.add(component);
			unused.removeAll(component);
		}
		return ans;
	}

}
