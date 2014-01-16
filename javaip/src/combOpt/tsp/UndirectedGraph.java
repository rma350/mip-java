package combOpt.tsp;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class UndirectedGraph {

	private List<Node> nodes;

	private Table<Node, Node, Edge> edges;
	private Set<Edge> edgeSet;

	public UndirectedGraph() {
		this.nodes = Lists.newArrayList();
		this.edges = HashBasedTable.create();
		edgeSet = Sets.newHashSet();
	}

	public Node addNode() {
		Node answer = new Node(nodes.size());
		this.nodes.add(answer);
		return answer;
	}

	public void addNodes(int numNodes) {
		for (int i = 0; i < numNodes; i++) {
			addNode();
		}
	}

	public Edge addEdge(Node endPoint, Node otherEndPoint, double weight) {
		if (endPoint == otherEndPoint) {
			throw new RuntimeException(
					"Illegal edge, endpoints must be distinct");
		}
		Edge answer = new Edge(ImmutableSet.of(endPoint, otherEndPoint), weight);
		edges.put(endPoint, otherEndPoint, answer);
		edges.put(otherEndPoint, endPoint, answer);
		edgeSet.add(answer);
		return answer;
	}

	public Map<Node, Edge> getAdjacentEdges(Node endPoint) {
		return edges.row(endPoint);
	}

	public Set<Edge> edgeSet() {
		return this.edgeSet;
	}

	public List<Node> vertexSet() {
		return this.nodes;
	}

}
