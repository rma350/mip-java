package mipSolveJava;

import java.util.Comparator;

import lpSolveBase.ObjectiveSense;

public class BestBoundNodeSelector implements Comparator<Node> {

	public static final BestBoundNodeSelector MINIMIZATION = new BestBoundNodeSelector(
			ObjectiveSense.MIN);
	public static final BestBoundNodeSelector MAXIMIZATION = new BestBoundNodeSelector(
			ObjectiveSense.MAX);

	public static final BestBoundNodeSelector getBestBoundNodeSelector(
			ObjectiveSense objectiveSense) {
		if (objectiveSense == ObjectiveSense.MIN) {
			return MINIMIZATION;
		} else if (objectiveSense == ObjectiveSense.MAX) {
			return MAXIMIZATION;
		} else {
			throw new RuntimeException("Unexpected objective sense: "
					+ objectiveSense);
		}
	}

	private ObjectiveSense objectiveSense;

	private BestBoundNodeSelector(ObjectiveSense objectiveSense) {
		this.objectiveSense = objectiveSense;
	}

	@Override
	public int compare(Node node0, Node node1) {
		if (!node0.getBestBound().isPresent()) {
			return -1;
		}
		if (!node1.getBestBound().isPresent()) {
			return 1;
		}
		if (objectiveSense == ObjectiveSense.MIN) {
			return Double.compare(node0.getBestBound().get(), node1
					.getBestBound().get());
		} else if (objectiveSense == ObjectiveSense.MAX) {
			return Double.compare(node1.getBestBound().get(), node0
					.getBestBound().get());
		} else {
			throw new RuntimeException("Unexpected objective sense: "
					+ objectiveSense);
		}
	}

}
