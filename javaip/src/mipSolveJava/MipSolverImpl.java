package mipSolveJava;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import lpSolveBase.BasicLpSolver;
import lpSolveBase.ObjectiveSense;
import lpSolveBase.SolutionStatus;
import mipSolveBase.CutCallback;
import mipSolveBase.CutCallbackMipView;
import mipSolveBase.MipSolver;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import easy.EasyLp;

public class MipSolverImpl implements MipSolver, CutCallbackMipView {

	private double mipNodeCompareTol = .0001;
	private double integralityTol = .0001;

	private BasicLpSolver basicLpSolver;
	private List<Boolean> integerVariables;
	private PriorityQueue<Node> nodeStack;
	private VariableBranchSelector variableBranchSelector;
	private Comparator<Node> nodeSelector;

	private OpenIntToDoubleHashMap variableLowerBoundsToRestore;
	private OpenIntToDoubleHashMap variableUpperBoundsToRestore;

	private SolutionStatus solutionStatus;
	private Optional<Solution> incumbent;

	private long nodesCreated;
	private List<CutCallback> userCutCallbacks;
	private List<CutCallback> lazyConstraintCallbacks;

	public MipSolverImpl() {
		this(EasyLp.easyLpSolver());
	}

	public MipSolverImpl(BasicLpSolver basicLpSolver) {
		this.basicLpSolver = basicLpSolver;
		if (basicLpSolver.getNumVars() > 0
				|| this.basicLpSolver.getNumConstrs() > 0) {
			throw new RuntimeException("Must begin with a clean LP solver");
		}
		this.integerVariables = Lists.newArrayList();
		incumbent = Optional.absent();
		this.nodesCreated = 0;
		this.solutionStatus = SolutionStatus.UNKNOWN;
		this.variableBranchSelector = VariableBranchMostFractional.INSTANCE;

		variableLowerBoundsToRestore = new OpenIntToDoubleHashMap();
		variableUpperBoundsToRestore = new OpenIntToDoubleHashMap();
		this.userCutCallbacks = Lists.newArrayList();
		this.lazyConstraintCallbacks = Lists.newArrayList();
	}

	@Override
	public void solve() {
		Optional<ObjectiveSense> objectiveSense = basicLpSolver.getObjSense();
		this.nodeSelector = objectiveSense.isPresent() ? BestBoundNodeSelector
				.getBestBoundNodeSelector(objectiveSense.get())
				: DiveNodeSelector.INSTANCE;
		this.nodeStack = new PriorityQueue<Node>(10, nodeSelector);
		Node first = new Node(nodesCreated++, new OpenIntToDoubleHashMap(),
				new OpenIntToDoubleHashMap(), Optional.<Double> absent());
		nodeStack.add(first);
		System.out
				.println("nodes created, node stack size, current node, current LP, incumbent");
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
				System.out.println(this.nodesCreated
						+ ","
						+ this.nodeStackSize()
						+ ","
						+ nextNode.getId()
						+ ","
						+ solution.getObjValue()
						+ ","
						+ (this.incumbent.isPresent() ? ""
								+ this.incumbent.get().getObjValue() : "?"));
				// System.out.println("Solution value: " +
				// solution.getObjValue());
				// System.out.println("Solution variables: "
				// + Arrays.toString(solution.getVariableValues()));
				if (pruneNode(solution.getObjValue())) {
					continue;
				}
				if (solution.isIntegral()) {
					this.proposeIntegerSolution(nextNode, solution);
				} else {
					boolean addedUserCutCallback = processCutCallbacks(
							nextNode, this.userCutCallbacks, solution);
					if (addedUserCutCallback) {

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
						Node up = new Node(nodesCreated++,
								solution.getObjValue(), nextNode);
						up.getBranchingVariableLBs()
								.put(branchVariable,
										Math.ceil(solution.getVariableValues()[branchVariable]));
						this.nodeStack.add(up);
					}
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

	@Override
	public boolean varIsInteger(int varIndex) {
		return this.integerVariables.get(varIndex);
	}

	@Override
	public double getLPVarValue(int varIndex) {
		return this.basicLpSolver.getVarValue(varIndex);
	}

	@Override
	public SolutionStatus getSolutionStatus() {
		return solutionStatus;
	}

	@Override
	public double getVarValue(int varIndex) {
		ensureAtLeastFeasible();
		return this.incumbent.get().getVariableValues()[varIndex];
	}

	@Override
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

	@Override
	public void destroy() {
		this.basicLpSolver.destroy();
	}

	private boolean pruneNode(double objAttained) {
		if (!incumbent.isPresent()) {
			return false;
		}
		Optional<ObjectiveSense> objSense = basicLpSolver.getObjSense();
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
		double[] values = new double[basicLpSolver.getNumVars()];
		for (int i = 0; i < values.length; i++) {
			values[i] = basicLpSolver.getVarValue(i);
		}
		return new Solution(nodeObj, values, this.integralityTol,
				this.integerVariables);
	}

	/**
	 * 
	 * @param cutCallbacks
	 * @param solution
	 * @return true if at least one constraint was added, otherwise false
	 */
	private boolean processCutCallbacks(Node node,
			List<? extends CutCallback> cutCallbacks, Solution solution) {
		int constraintCount = this.getNumConstrs();
		for (CutCallback cutCallback : cutCallbacks) {
			boolean keepLooking = cutCallback.onCallback(this
					.getCutCallbackMipView());
			if (!keepLooking) {
				break;
			}
		}
		boolean constraintAdded = constraintCount < this.getNumConstrs();
		if (constraintAdded) {
			node.setBestBound(solution.getObjValue());
			nodeStack.add(node);
		}
		return constraintAdded;
	}

	private void proposeIntegerSolution(Node sourceNode, Solution solution) {
		System.out.println("Found integer solution: " + solution.getObjValue());

		boolean constrAdded = processCutCallbacks(sourceNode,
				this.lazyConstraintCallbacks, solution);
		if (constrAdded) {
			System.out.println("Pruned solution in lazy callback!");
		} else {
			this.solutionStatus = SolutionStatus.FEASIBLE;
			this.incumbent = Optional.of(solution);
		}

	}

	@Override
	public int createIntVar() {
		this.integerVariables.add(true);
		return this.basicLpSolver.createVar();
	}

	@Override
	public int createNumVar() {
		this.integerVariables.add(false);
		return this.basicLpSolver.createVar();
	}

	@Override
	public int createConstr() {
		return this.basicLpSolver.createConstr();
	}

	@Override
	public void createObj(ObjectiveSense objectiveSense) {
		this.basicLpSolver.createObj(objectiveSense);
	}

	@Override
	public void setObjCoef(int variableIndex, double value) {
		this.basicLpSolver.setObjCoef(variableIndex, value);
	}

	@Override
	public void setVarLB(int variableIndex, double value) {
		this.basicLpSolver.setVarLB(variableIndex, value);
	}

	@Override
	public void setVarUB(int variableIndex, double value) {
		this.basicLpSolver.setVarUB(variableIndex, value);
	}

	@Override
	public void setConstrLB(int constraintIndex, double value) {
		this.basicLpSolver.setConstrLB(constraintIndex, value);
	}

	@Override
	public void setConstrUB(int constraintIndex, double value) {
		this.basicLpSolver.setConstrUB(constraintIndex, value);
	}

	@Override
	public void setConstrCoef(int constraintIndex, int variableIndex,
			double value) {
		this.basicLpSolver.setConstrCoef(constraintIndex, variableIndex, value);
	}

	@Override
	public int getNumConstrs() {
		return this.basicLpSolver.getNumConstrs();
	}

	@Override
	public int getNumVars() {
		return this.basicLpSolver.getNumVars();
	}

	@Override
	public Optional<ObjectiveSense> getObjSense() {
		return this.basicLpSolver.getObjSense();
	}

	@Override
	public double getVarLB(int variableIndex) {
		return this.basicLpSolver.getVarLB(variableIndex);
	}

	@Override
	public double getVarUB(int variableIndex) {
		return this.basicLpSolver.getVarUB(variableIndex);
	}

	@Override
	public long nodesCreated() {
		return this.nodesCreated;
	}

	@Override
	public long nodeStackSize() {
		return this.nodeStack.size();
	}

	@Override
	public void addLazyConstraintCallback(CutCallback cutCallback) {
		this.lazyConstraintCallbacks.add(cutCallback);
	}

	@Override
	public void addUserCutCallback(CutCallback cutCallback) {
		this.userCutCallbacks.add(cutCallback);
	}

	public CutCallbackMipView getCutCallbackMipView() {
		return this;
	}

}
