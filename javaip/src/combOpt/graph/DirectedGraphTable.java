package combOpt.graph;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

public class DirectedGraphTable implements ReadableDirectedGraph {

	private List<Node> nodes;

	private Table<Node, Node, DirectedEdge> edges;

	public DirectedGraphTable() {
		this.nodes = Lists.newArrayList();
		this.edges = HashBasedTable.create();
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

	void addNode(Node node) {
		nodes.add(node);
	}

	public DirectedEdge addEdge(Node source, Node target) {
		if (source == target) {
			throw new RuntimeException(
					"Illegal edge, endpoints must be distinct");
		}
		DirectedEdge answer = new DirectedEdge(source, target);
		edges.put(source, target, answer);
		return answer;
	}

	@Override
	public Collection<DirectedEdge> edges() {
		return this.edges.values();
	}

	@Override
	public List<Node> nodes() {
		return this.nodes;
	}

	@Override
	public Map<Node, DirectedEdge> getOutgoingEdges(Node source) {
		return edges.row(source);
	}

	@Override
	public Map<Node, DirectedEdge> getIncomingEdges(Node target) {
		return edges.column(target);
	}

}
