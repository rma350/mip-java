package mipSolveJava;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap;

import com.google.common.base.Optional;

public class Node {

	private long id;
	private OpenIntToDoubleHashMap branchingVariableLBs;
	private OpenIntToDoubleHashMap branchingVariableUBs;
	private Optional<Double> bestBound;

	public Node(long id, OpenIntToDoubleHashMap branchingVariableLBs,
			OpenIntToDoubleHashMap branchingVariableUBs,
			Optional<Double> bestBound) {
		this.id = id;
		this.branchingVariableLBs = branchingVariableLBs;
		this.branchingVariableUBs = branchingVariableUBs;
		this.bestBound = bestBound;
	}

	public Node(long id, double bestBound, Node parent) {
		this(id, new OpenIntToDoubleHashMap(parent.getBranchingVariableLBs()),
				new OpenIntToDoubleHashMap(parent.getBranchingVariableUBs()),
				Optional.of(bestBound));
	}

	public long getId() {
		return id;
	}

	public OpenIntToDoubleHashMap getBranchingVariableLBs() {
		return branchingVariableLBs;
	}

	public OpenIntToDoubleHashMap getBranchingVariableUBs() {
		return branchingVariableUBs;
	}

	public Optional<Double> getBestBound() {
		return bestBound;
	}

}