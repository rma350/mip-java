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
import mipSolveBase.logging.CutCallbackLogger;
import mipSolveBase.logging.CutCallbackLogger.CutCallbackLog;
import mipSolveJava.logging.MipLog;
import mipSolveJava.logging.MipLogFormatter;
import mipSolveJava.logging.NodeFormatter;
import mipSolveJava.logging.NodeLog;
import mipSolveJava.logging.NodeLog.NewSolutionStatus;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import easy.EasyLp;

public class MipSolverImpl implements MipSolver {

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

	private CutCallbackMipView cutCallbackMipView;

	private MipLog mipLog;
	private NodeFormatter nodeFormatter;
	private MipLogFormatter mipLogFormatter;

	private List<OpenIntToDoubleHashMap> advancedStartSuggestions;

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
		this.variableBranchSelector = new VariableBranchPsuedoCost(
				this.basicLpSolver);// VariableBranchMostFractionalRandomized.INSTANCE;

		variableLowerBoundsToRestore = new OpenIntToDoubleHashMap();
		variableUpperBoundsToRestore = new OpenIntToDoubleHashMap();
		this.userCutCallbacks = Lists.newArrayList();
		this.lazyConstraintCallbacks = Lists.newArrayList();
		this.mipLog = new MipLog();
		cutCallbackMipView = new CutCallbackMipViewImpl();

		this.nodeFormatter = new NodeFormatter(NodeFormatter.Mode.FIXED_WIDTH);
		this.mipLogFormatter = new MipLogFormatter();
		this.advancedStartSuggestions = Lists.newArrayList();

	}

	private void setBasicNodeLogInfo(Node node, NodeLog nodeLog) {
		nodeLog.setBestBound(node.getBestBound());
		nodeLog.setCurrentNode(node.getId());
		nodeLog.setIncumbent(this.incumbent.isPresent() ? Optional
				.of(this.incumbent.get().getObjValue()) : Optional
				.<Double> absent());
		nodeLog.setNodesCreated(this.nodesCreated);
		nodeLog.setNodeStackSize(this.nodeStack.size());
		SolutionStatus solutionStatus = basicLpSolver.getSolutionStatus();
		nodeLog.setCurrentLpStatus(solutionStatus);
		if (solutionStatus == SolutionStatus.OPTIMAL
				|| solutionStatus == SolutionStatus.FEASIBLE) {
			nodeLog.setCurrentLp(Optional.of(basicLpSolver.getObjValue()));
		} else {
			nodeLog.setCurrentLp(Optional.<Double> absent());
		}
		nodeLog.setNewSolutionStatus(NewSolutionStatus.NONE);
		this.mipLog.setLastNodeLog(nodeLog);
		this.mipLog.onNewNode();
	}

	private void printNode() {
		System.out.println(this.nodeFormatter.format(mipLog));
	}

	/** Returns true if we have reached a termination condition. */
	private boolean processNode(Node nextNode, NodeLog log) {
		configureLp(nextNode);
		this.mipLog.getLpTimer().tic();
		basicLpSolver.solve();
		this.mipLog.getLpTimer().toc();
		setBasicNodeLogInfo(nextNode, log);
		if (basicLpSolver.getSolutionStatus() == SolutionStatus.UNBOUNDED) {
			this.solutionStatus = SolutionStatus.INFEASIBLE_OR_UNBOUNDED;
			return true;
		} else if (basicLpSolver.getSolutionStatus() == SolutionStatus.INFEASIBLE) {
			return false;
		} else if (basicLpSolver.getSolutionStatus() == SolutionStatus.OPTIMAL
				|| basicLpSolver.getSolutionStatus() == SolutionStatus.FEASIBLE) {
			if (this.solutionStatus == SolutionStatus.UNKNOWN) {
				this.solutionStatus = SolutionStatus.BOUNDED;
			}
			Solution solution = this.extractSolution();
			if (isBeatenByIncumbent(solution.getObjValue())) {
				return false;
			}
			if (solution.isIntegral()) {
				this.proposeIntegerSolution(nextNode, solution, log);
				return false;
			} else {
				boolean addedUserCutCallback = processCutCallbacks(nextNode,
						this.userCutCallbacks, solution,
						this.mipLog.getUserCutCallbackLog());
				if (addedUserCutCallback) {
					return false;
				} else {
					this.mipLog.getBranchingTimer().tic();
					int branchVariable = variableBranchSelector
							.selectVariableForBranching(solution,
									this.integerVariables);
					this.mipLog.getBranchingTimer().toc();
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
					return false;
				}
			}
		} else {
			throw new RuntimeException("Unexpected solution status: "
					+ basicLpSolver.getSolutionStatus());
		}
	}

	private Solution advancedStartToSolution(
			OpenIntToDoubleHashMap advancedStart) {
		double[] values = new double[basicLpSolver.getNumVars()];
		for (int i = 0; i < values.length; i++) {
			if (advancedStart.containsKey(i)) {
				values[i] = advancedStart.get(i);
			} else {
				return null;
			}
		}
		return new Solution(
				this.basicLpSolver.evaluateObjective(advancedStart), values,
				this.integralityTol, this.integerVariables);
	}

	// TODO: check advanced start to make sure lazy constraints are not
	// violated?? Also check regular constraints?
	private void checkAdvancedStarts() {
		for (OpenIntToDoubleHashMap advancedStart : this.advancedStartSuggestions) {
			Solution solution = this.advancedStartToSolution(advancedStart);
			if (solution != null
					&& !this.isBeatenByIncumbent(solution.getObjValue())
					&& solution.isIntegral()) {
				// boolean constrAdded = processCutCallbacks(sourceNode,
				// this.lazyConstraintCallbacks, solution,
				// this.mipLog.getLazyConstraintCallbackLog());
				// if (!constrAdded) {
				this.solutionStatus = SolutionStatus.FEASIBLE;
				this.incumbent = Optional.of(solution);
				// }
			}
		}

	}

	@Override
	public void solve() {

		Optional<ObjectiveSense> objectiveSense = basicLpSolver.getObjSense();
		this.nodeSelector = objectiveSense.isPresent() ? BestBoundNodeSelector
				.getBestBoundNodeSelector(objectiveSense.get())
				: DiveNodeSelector.INSTANCE;
		this.nodeStack = new PriorityQueue<Node>(10, nodeSelector);
		mipLog.tic();
		checkAdvancedStarts();
		if (this.incumbent.isPresent()) {
			System.out.println("Advanced start found solution: "
					+ incumbent.get().getObjValue());
		}
		Node first = new Node(nodesCreated++, new OpenIntToDoubleHashMap(),
				new OpenIntToDoubleHashMap(), Optional.<Double> absent());
		nodeStack.add(first);
		System.out.println(this.nodeFormatter.header());
		while (!nodeStack.isEmpty()) {
			Node nextNode = nodeStack.poll();
			if (this.incumbent.isPresent()
					&& nextNode.getBestBound().isPresent()) {
				if (isBeatenByIncumbent(nextNode.getBestBound().get())) {
					break;
				}
			}
			NodeLog log = new NodeLog();
			boolean terminated = processNode(nextNode, log);
			if (terminated) {
				break;
			} else {
				this.printNode();
			}
		}
		mipLog.toc();
		System.out.println(mipLogFormatter.format(mipLog));
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

	private boolean isBeatenByIncumbent(double objAttained) {
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
			List<? extends CutCallback> cutCallbacks, Solution solution,
			CutCallbackLogger cutCallbackLogger) {

		int constraintCount = this.getNumConstrs();
		cutCallbackLogger.onStartCallback();
		for (CutCallback cutCallback : cutCallbacks) {
			CutCallbackLog log = cutCallbackLogger.getLog(cutCallback);
			if (cutCallback.skipCallback(getCutCallbackMipView())) {
				log.onSkipped();
			} else {
				int constraintCountThisCallback = this.getNumConstrs();
				log.cutTic();
				boolean keepLooking = cutCallback.onCallback(this
						.getCutCallbackMipView());
				int newConstraints = this.getNumConstrs()
						- constraintCountThisCallback;
				log.cutToc(newConstraints);
				if (!keepLooking) {
					break;
				}
			}
		}
		boolean constraintAdded = constraintCount < this.getNumConstrs();
		if (constraintAdded) {
			node.setBestBound(solution.getObjValue());
			nodeStack.add(node);
		}
		return constraintAdded;
	}

	private void proposeIntegerSolution(Node sourceNode, Solution solution,
			NodeLog nodeLog) {
		boolean constrAdded = processCutCallbacks(sourceNode,
				this.lazyConstraintCallbacks, solution,
				this.mipLog.getLazyConstraintCallbackLog());
		if (constrAdded) {

		} else {
			nodeLog.setIncumbent(Optional.of(solution.getObjValue()));
			nodeLog.setNewSolutionStatus(NewSolutionStatus.INTEGRAL);
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
	public void addLazyConstraintCallback(CutCallback cutCallback) {
		this.mipLog.getLazyConstraintCallbackLog().onAddCallback(cutCallback);
		this.lazyConstraintCallbacks.add(cutCallback);
	}

	@Override
	public void addUserCutCallback(CutCallback cutCallback) {
		this.mipLog.getUserCutCallbackLog().onAddCallback(cutCallback);
		this.userCutCallbacks.add(cutCallback);
	}

	public CutCallbackMipView getCutCallbackMipView() {
		return this.cutCallbackMipView;
	}

	public class CutCallbackMipViewImpl implements CutCallbackMipView {

		@Override
		public long nodesCreated() {
			return nodesCreated;
		}

		@Override
		public long nodeStackSize() {
			return nodeStack.size();
		}

		@Override
		public double getLPVarValue(int varIndex) {
			return basicLpSolver.getVarValue(varIndex);
		}

		@Override
		public int createConstr() {
			return MipSolverImpl.this.createConstr();
		}

		@Override
		public void setConstrCoef(int constrIndex, int varIndex, double value) {
			MipSolverImpl.this.setConstrCoef(constrIndex, varIndex, value);
		}

		@Override
		public void setConstrUB(int constrIndex, double value) {
			MipSolverImpl.this.setConstrUB(constrIndex, value);
		}

		@Override
		public void setConstrLB(int constrIndex, double value) {
			MipSolverImpl.this.setConstrLB(constrIndex, value);
		}

	}

	@Override
	public void suggestAdvancedStart(OpenIntToDoubleHashMap solution) {
		this.advancedStartSuggestions.add(solution);
	}

}
