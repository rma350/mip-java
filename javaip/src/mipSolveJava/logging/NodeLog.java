package mipSolveJava.logging;

import lpSolveBase.SolutionStatus;

import com.google.common.base.Optional;

public class NodeLog {

	private long nodesCreated;
	private long nodeStackSize;
	private long currentNode;
	private Optional<Double> currentLp;
	private SolutionStatus currentLpStatus;

	public Optional<Double> getCurrentLp() {
		return currentLp;
	}

	public void setCurrentLp(Optional<Double> currentLp) {
		this.currentLp = currentLp;
	}

	public SolutionStatus getCurrentLpStatus() {
		return currentLpStatus;
	}

	public void setCurrentLpStatus(SolutionStatus currentLpStatus) {
		this.currentLpStatus = currentLpStatus;
	}

	private Optional<Double> bestBound;
	private Optional<Double> incumbent;

	private NewSolutionStatus newSolutionStatus;
	private Optional<String> newSolutionHeuristicShortName;

	public NewSolutionStatus getNewSolutionStatus() {
		return newSolutionStatus;
	}

	public void setNewSolutionStatus(NewSolutionStatus newSolutionStatus) {
		this.newSolutionStatus = newSolutionStatus;
	}

	public Optional<String> getNewSolutionHeuristicShortName() {
		return newSolutionHeuristicShortName;
	}

	public void setNewSolutionHeuristicShortName(
			String newSolutionHeuristicShortName) {
		this.newSolutionHeuristicShortName = Optional
				.of(newSolutionHeuristicShortName);
	}

	public NodeLog() {
	}

	public long getNodesCreated() {
		return nodesCreated;
	}

	public void setNodesCreated(long nodesCreated) {
		this.nodesCreated = nodesCreated;
	}

	public long getNodeStackSize() {
		return nodeStackSize;
	}

	public void setNodeStackSize(long nodeStackSize) {
		this.nodeStackSize = nodeStackSize;
	}

	public long getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(long currentNode) {
		this.currentNode = currentNode;
	}

	public Optional<Double> getBestBound() {
		return bestBound;
	}

	public void setBestBound(Optional<Double> bestBound) {
		this.bestBound = bestBound;
	}

	public Optional<Double> getIncumbent() {
		return incumbent;
	}

	public void setIncumbent(Optional<Double> incumbent) {
		this.incumbent = incumbent;
	}

	public static enum NewSolutionStatus {
		NONE, INTEGRAL, HEURISTIC;
	}

}
