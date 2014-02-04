package mipSolveBase;

import java.util.List;
import java.util.Map;

import lpSolveBase.ObjectiveSense;
import lpSolveBase.SolutionStatus;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap;
import org.apache.commons.math3.util.OpenIntToDoubleHashMap.Iterator;

import wrappedMipSolver.MipSolverWrapper;
import wrappedMipSolver.WrappedCutCallback;
import wrappedMipSolver.WrappedCutCallbackMipView;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WrappedMipSolver<V, C, O> implements MipSolver {

	private MipSolverWrapper<V, C, O> wrappedSolver;

	private List<V> variables;
	private List<Boolean> isInteger;
	private List<C> constraints;
	private Optional<O> objective;

	public WrappedMipSolver(MipSolverWrapper<V, C, O> wrappedSolver) {
		this.wrappedSolver = wrappedSolver;

		this.variables = Lists.newArrayList();
		this.constraints = Lists.newArrayList();
		this.objective = Optional.absent();
		this.isInteger = Lists.newArrayList();
	}

	@Override
	public int createIntVar() {
		variables.add(wrappedSolver.createIntVar());
		isInteger.add(true);
		return variables.size() - 1;
	}

	@Override
	public int createNumVar() {
		variables.add(wrappedSolver.createNumVar());
		isInteger.add(false);
		return variables.size() - 1;
	}

	@Override
	public int createConstr() {
		constraints.add(wrappedSolver.createConstr());
		return constraints.size() - 1;
	}

	@Override
	public void createObj(ObjectiveSense objectiveSense) {
		this.objective = Optional.of(wrappedSolver.createObj(objectiveSense));
	}

	@Override
	public void setObjCoef(int variableIndex, double value) {
		checkObjective();
		wrappedSolver.setObjCoef(objective.get(),
				this.variables.get(variableIndex), value);
	}

	@Override
	public void setVarLB(int variableIndex, double value) {
		wrappedSolver.setVarLB(variables.get(variableIndex), value);
	}

	@Override
	public void setVarUB(int variableIndex, double value) {
		wrappedSolver.setVarUB(variables.get(variableIndex), value);
	}

	@Override
	public void setConstrLB(int constraintIndex, double value) {
		wrappedSolver.setConstrLB(constraints.get(constraintIndex), value);
	}

	@Override
	public void setConstrUB(int constraintIndex, double value) {
		wrappedSolver.setConstrUB(constraints.get(constraintIndex), value);
	}

	@Override
	public void setConstrCoef(int constraintIndex, int variableIndex,
			double value) {
		wrappedSolver.setConstrCoef(constraints.get(constraintIndex),
				variables.get(variableIndex), value);
	}

	private void checkObjective() {
		if (!this.objective.isPresent()) {
			throw new RuntimeException("No objective has been created yet.");
		}
	}

	@Override
	public SolutionStatus getSolutionStatus() {
		return wrappedSolver.getSolutionStatus();
	}

	@Override
	public Optional<ObjectiveSense> getObjSense() {
		if (this.objective.isPresent()) {
			return Optional
					.of(wrappedSolver.getObjectiveSense(objective.get()));
		} else {
			return Optional.absent();
		}
	}

	@Override
	public double getVarValue(int variableIndex) {
		return wrappedSolver.getVarValue(variables.get(variableIndex));
	}

	@Override
	public double getVarLB(int variableIndex) {
		return wrappedSolver.getVarLB(variables.get(variableIndex));
	}

	@Override
	public double getVarUB(int variableIndex) {
		return wrappedSolver.getVarUB(variables.get(variableIndex));
	}

	@Override
	public void destroy() {
		wrappedSolver.destroy();
	}

	@Override
	public boolean varIsInteger(int variableIndex) {
		return this.varIsInteger(variableIndex);
	}

	@Override
	public int getNumConstrs() {
		return this.constraints.size();
	}

	@Override
	public int getNumVars() {
		return variables.size();
	}

	@Override
	public void solve() {
		this.wrappedSolver.solve();
	}

	@Override
	public double getObjValue() {
		return this.wrappedSolver.getObjValue();
	}

	@Override
	public void addLazyConstraintCallback(final CutCallback cutCallback) {
		WrappedCutCallback<V, C, O> wrappedCutCalback = new WrappedCutCallbackFromCutCallback(
				cutCallback);
		this.wrappedSolver.addLazyConstrCallback(wrappedCutCalback);
	}

	@Override
	public void addUserCutCallback(CutCallback cutCallback) {
		WrappedCutCallback<V, C, O> wrappedCutCalback = new WrappedCutCallbackFromCutCallback(
				cutCallback);
		this.wrappedSolver.addUserCutCallback(wrappedCutCalback);

	}

	private class WrappedCutCallbackFromCutCallback implements
			WrappedCutCallback<V, C, O> {

		private CutCallback cutCallback;

		public WrappedCutCallbackFromCutCallback(CutCallback cutCallback) {
			this.cutCallback = cutCallback;
		}

		@Override
		public boolean onCallback(WrappedCutCallbackMipView<V, C, O> mipView) {
			return cutCallback.onCallback(new CutCallbackMipViewFromWrap(
					mipView));
		}

	}

	private class CutCallbackMipViewFromWrap implements CutCallbackMipView {

		private WrappedCutCallbackMipView<V, C, O> wrappedCutCallbackMipView;

		private double[] lpVarVals;

		public CutCallbackMipViewFromWrap(
				WrappedCutCallbackMipView<V, C, O> wrappedCutCallbackMipView) {
			this.wrappedCutCallbackMipView = wrappedCutCallbackMipView;
			lpVarVals = this.wrappedCutCallbackMipView
					.getLPVarValues(variables);
		}

		@Override
		public long nodesCreated() {
			return wrappedCutCallbackMipView.nodesCreated();
		}

		@Override
		public long nodeStackSize() {
			return this.wrappedCutCallbackMipView.nodeStackSize();
		}

		@Override
		public double getLPVarValue(int index) {
			return lpVarVals[index];// this.wrappedCutCallbackMipView.getLPVarValue(variables
									// .get(index));
		}

		@Override
		public int createConstr() {
			C constraint = this.wrappedCutCallbackMipView.createConstr();
			constraints.add(constraint);
			return constraints.size() - 1;
		}

		@Override
		public void setConstrCoef(int constrIndex, int varIndex, double value) {
			this.wrappedCutCallbackMipView.setConstrCoef(
					constraints.get(constrIndex), variables.get(varIndex),
					value);
		}

		@Override
		public void setConstrUB(int constrIndex, double value) {
			this.wrappedCutCallbackMipView.setConstrUB(
					constraints.get(constrIndex), value);
		}

		@Override
		public void setConstrLB(int constrIndex, double value) {
			this.wrappedCutCallbackMipView.setConstrLB(
					constraints.get(constrIndex), value);
		}

	}

	@Override
	public void suggestAdvancedStart(OpenIntToDoubleHashMap solution) {
		Map<V, Double> convertedSolution = Maps.newHashMap();
		for (Iterator it = solution.iterator(); it.hasNext();) {
			it.advance();
			int key = it.key();
			double val = it.value();
			convertedSolution.put(this.variables.get(key), val);
		}
		this.wrappedSolver.suggestAdvancedStart(convertedSolution);
	}

}
