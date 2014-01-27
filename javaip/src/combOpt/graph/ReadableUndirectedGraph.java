package combOpt.graph;

import java.util.Map;
import java.util.Set;

public interface ReadableUndirectedGraph {

	public Map<Node, Edge> getAdjacentEdges(Node endPoint);

	public Set<Edge> edgeSet();

	public Iterable<Node> vertexSet();

}
