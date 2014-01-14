package mipSolveJava;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import lpSolveBase.BasicLpSolver;
import lpSolveBase.ObjectiveSense;
import lpSolveBase.SolutionStatus;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap;

import com.google.common.base.Optional;

public class MipSolver {

	private double mipNodeCompareTol = .0001;
	private double integralityTol = .0001;

	private BasicLpSolver basicLpSolver;
	private boolean[] integerVariables;
	private PriorityQueue<Node> nodeStack;
	private VariableBranchSelector variableBranchSelector;
	private Comparator<Node> nodeSelector;

	private OpenIntToDoubleHashMap variableLowerBoundsToRestore;
	private OpenIntToDoubleHashMap variableUpperBoundsToRestore;

	private SolutionStatus solutionStatus;
	private Optional<Solution> incumbent;

	private int nodesCreated;

	public MipSolver(BasicLpSolver basicLpSolver, boolean[] integerVariables) {
		this.basicLpSolver = basicLpSolver;
		this.integerVariables = integerVariables;
		incumbent = Optional.absent();
		this.nodesCreated = 0;
		this.solutionStatus = SolutionStatus.UNKNOWN;
		this.variableBranchSelector = VariableBranchLowestIndex.INSTANCE;
		Optional<ObjectiveSense> objectiveSense = basicLpSolver
				.getObjectiveSense();
		this.nodeSelector = objectiveSense.isPresent() ? BestBoundNodeSelector
				.getBestBoundNodeSelector(objectiveSense.get())
				: DiveNodeSelector.INSTANCE;
		this.nodeStack = new PriorityQueue<Node>(10, nodeSelector);
		variableLowerBoundsToRestore = new OpenIntToDoubleHashMap();
		variableUpperBoundsToRestore = new OpenIntToDoubleHashMap();
	}

	private boolean pruneNode(double objAttained) {
		if (!incumbent.isPresent()) {
			return false;
		}
		Optional<ObjectiveSense> objSense = basicLpSolver.getObjectiveSense();
		if (!objSense.isPresent()) {
			return false;
		}
		if (objSense.get() == ObjectiveSense.MAX) {
			return objAttained <= mipNodeCompareTol
					+ incumbent.get().getObjValue();
		} else if (objSense.get() == ObjectiveSense.MIN) {
			return objAttained >= incumbent.get().getObjValue()
					- mipNodeCompareTol;
		} else {
			throw new RuntimeException("Unexpected objective sense: "
					+ objSense.get());
		}
	}

	private void setVarLBs(OpenIntToDoubleHashMap newLowerBounds) {
		variableLowerBoundsToRestore = new OpenIntToDoubleHashMap();
		for (OpenIntToDoubleHashMap.Iterator iterator = newLowerBounds
				.iterator(); iterator.hasNext();) {
			iterator.advance();
			variableLowerBoundsToRestore.put(iterator.key(),
					basicLpSolver.getVarLB(iterator.key()));
			basicLpSolver.setVarLB(iterator.key(), iterator.value());
		}
	}

	private void revertVarLBs() {
		for (OpenIntToDoubleHashMap.Iterator iterator = variableLowerBoundsToRestore
				.iterator(); iterator.hasNext();) {
			iterator.advance();
			basicLpSolver.setVarLB(iterator.key(), iterator.value());
		}
	}

	private void setVarUBs(OpenIntToDoubleHashMap newUpperBounds) {
		variableUpperBoundsToRestore = new OpenIntToDoubleHashMap();
		for (OpenIntToDoubleHashMap.Iterator iterator = newUpperBounds
				.iterator(); iterator.hasNext();) {
			iterator.advance();
			variableUpperBoundsToRestore.put(iterator.key(),
					basicLpSolver.getVarUB(iterator.key()));
			basicLpSolver.setVarUB(iterator.key(), iterator.value());
		}
	}

	private void revertVarUBs() {
		for (OpenIntToDoubleHashMap.Iterator iterator = variableUpperBoundsToRestore
				.iterator(); iterator.hasNext();) {
			iterator.advance();
			basicLpSolver.setVarUB(iterator.key(), iterator.value());
		}
	}

	private void configureLp(Node node) {
		revertVarLBs();
		revertVarUBs();
		setVarLBs(node.getBranchingVariableLBs());
		setVarUBs(node.getBranchingVariableUBs());
	}

	private Solution extractSolution() {
		double nodeObj = basicLpSolver.getObjValue();
		double[] values = new double[basicLpSolver.getNumVariables()];
		for (int i = 0; i < values.length; i++) {
			values[i] = basicLpSolver.getVarValue(i);
		}
		return new Solution(nodeObj, values, this.integralityTol,
				this.integerVariables);
	}

	public void proposeIntegerSolution(Solution solution) {
		System.out.println("Found integer solution: " + solution.getObjValue());
		this.solutionStatus = SolutionStatus.FEASIBLE;
		this.incumbent = Optional.of(solution);
	}

	public void solve() {
		Node first = new Node(nodesCreated++, new OpenIntToDoubleHashMap(),
				new OpenIntToDoubleHashMap(), Optional.<Double> absent());
		nodeStack.add(first);
		while (!nodeStack.isEmpty()) {
			Node nextNode = nodeStack.poll();
			configureLp(nextNode);
			basicLpSolver.solve();
			// System.out.println("lp solution status: "
			// + basicLpSolver.getSolutionStatus());
			// System.out.println("lp solution value: "
			// + basicLpSolver.getObjValue());
			// System.out.println("lp var values: " +
			// basicLpSolver.getVarValue(0)
			// + ", " + basicLpSolver.getVarValue(1));

			if (basicLpSolver.getSolutionStatus() == SolutionStatus.UNBOUNDED) {
				// if(this.incumbent.isPresent()){
				// this.solutionStatus = SolutionStatus.UNBOUNDED;
				// }
				// else{
				this.solutionStatus = SolutionStatus.INFEASIBLE_OR_UNBOUNDED;
				// }
				return;
			} else if (basicLpSolver.getSolutionStatus() == SolutionStatus.INFEASIBLE) {
				continue;
			} else if (basicLpSolver.getSolutionStatus() == SolutionStatus.OPTIMAL
					|| basicLpSolver.getSolutionStatus() == SolutionStatus.FEASIBLE) {
				if (this.solutionStatus == SolutionStatus.UNKNOWN) {
					this.solutionStatus = SolutionStatus.BOUNDED;
				}
				Solution solution = this.extractSolution();
				System.out.println("Solution value: " + solution.getObjValue());
				System.out.println("Solution variables: "
						+ Arrays.toString(solution.getVariableValues()));
				if (pruneNode(solution.getObjValue())) {
					continue;
				}
				if (solution.isIntegral()) {
					this.proposeIntegerSolution(solution);
				} else {
					int branchVariable = variableBranchSelector
							.selectVariableForBranching(solution,
									this.integerVariables);
					Node down = new Node(nodesCreated++,
							solution.getObjValue(), nextNode);
					down.getBranchingVariableUBs()
							.put(branchVariable,
									Math.floor(solution.getVariableValues()[branchVariable]));
					this.nodeStack.add(down);
					Node up = new Node(nodesCreated++, solution.getObjValue(),
							nextNode);
					up.getBranchingVariableLBs()
							.put(branchVariable,
									Math.ceil(solution.getVariableValues()[branchVariable]));
					this.nodeStack.add(up);
				}
			} else {
				throw new RuntimeException("Unexpected solution status: "
						+ basicLpSolver.getSolutionStatus());
			}
		}
		if (this.incumbent.isPresent()) {
			solutionStatus = SolutionStatus.OPTIMAL;
		} else {
			solutionStatus = SolutionStatus.INFEASIBLE;
		}
	}

	public SolutionStatus getSolutionStatus() {
		return solutionStatus;
	}

	public double getVarValue(int varIndex) {
		ensureAtLeastFeasible();
		return this.incumbent.get().getVariableValues()[varIndex];
	}

	public double getObjValue() {
		ensureAtLeastFeasible();
		return this.incumbent.get().getObjValue();

	}

	private void ensureAtLeastFeasible() {
		if (!(this.solutionStatus == SolutionStatus.FEASIBLE)
				&& !(this.solutionStatus == SolutionStatus.OPTIMAL)) {
			throw new RuntimeException(
					"Solution must be feasible or optimal to call this method.");
		}
	}

}
