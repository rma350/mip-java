package mipSolveJava;

import java.util.Comparator;

public class DiveNodeSelector implements Comparator<Node> {

	public static final DiveNodeSelector INSTANCE = new DiveNodeSelector();

	private DiveNodeSelector() {
	}

	@Override
	public int compare(Node node0, Node node1) {
		return Long.signum(node0.getId() - node1.getId());
	}

}
