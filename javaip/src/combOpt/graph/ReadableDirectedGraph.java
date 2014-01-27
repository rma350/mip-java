package combOpt.graph;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ReadableDirectedGraph {

	public Map<Node, DirectedEdge> getOutgoingEdges(Node source);

	public Map<Node, DirectedEdge> getIncomingEdges(Node target);

	public Collection<DirectedEdge> edges();

	public List<Node> nodes();

}
