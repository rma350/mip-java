package lpSolveBase;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap;
import org.apache.commons.math3.util.OpenIntToDoubleHashMap.Iterator;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BasicLpSolverImpl<V, C, O> implements BasicLpSolver {

	private AbstractLpSolver<V, C, O> lpSolver;

	private int varCount;
	private int constraintCount;
	private List<V> variables;
	private List<C> constraints;
	private Optional<O> objective;

	public BasicLpSolverImpl(AbstractLpSolver<V, C, O> lpSolver) {
		this.lpSolver = lpSolver;
		this.varCount = 0;
		this.constraintCount = 0;

		this.variables = Lists.newArrayList();
		this.constraints = Lists.newArrayList();
		this.objective = Optional.absent();
	}

	@Override
	public int getNumConstrs() {
		return constraintCount;
	}

	@Override
	public int getNumVars() {
		return varCount;
	}

	@Override
	public void solve() {
		lpSolver.solve();
	}

	@Override
	public double getObjValue() {
		return lpSolver.getObjValue();
	}

	@Override
	public int createVar() {
		variables.add(lpSolver.createVar());
		return varCount++;
	}

	@Override
	public int createConstr() {
		constraints.add(lpSolver.createConstr());
		return constraintCount++;
	}

	@Override
	public void createObj(ObjectiveSense objectiveSense) {
		this.objective = Optional.of(lpSolver.createObj(objectiveSense));
	}

	@Override
	public void setObjCoef(int variableIndex, double value) {
		checkObjective();
		lpSolver.setObjCoef(objective.get(), this.variables.get(variableIndex),
				value);
	}

	@Override
	public void setVarLB(int variableIndex, double value) {
		lpSolver.setVarLB(variables.get(variableIndex), value);
	}

	@Override
	public void setVarUB(int variableIndex, double value) {
		lpSolver.setVarUB(variables.get(variableIndex), value);
	}

	@Override
	public void setConstrLB(int constraintIndex, double value) {
		lpSolver.setConstrLB(constraints.get(constraintIndex), value);
	}

	@Override
	public void setConstrUB(int constraintIndex, double value) {
		lpSolver.setConstrUB(constraints.get(constraintIndex), value);
	}

	@Override
	public void setConstrCoef(int constraintIndex, int variableIndex,
			double value) {
		lpSolver.setConstrCoef(constraints.get(constraintIndex),
				variables.get(variableIndex), value);
	}

	private void checkObjective() {
		if (!this.objective.isPresent()) {
			throw new RuntimeException("No objective has been created yet.");
		}
	}

	@Override
	public SolutionStatus getSolutionStatus() {
		return lpSolver.getSolutionStatus();
	}

	@Override
	public Optional<ObjectiveSense> getObjSense() {
		if (this.objective.isPresent()) {
			return Optional.of(lpSolver.getObjectiveSense(objective.get()));
		} else {
			return Optional.absent();
		}
	}

	@Override
	public double getVarValue(int variableIndex) {
		return lpSolver.getVarValue(variables.get(variableIndex));
	}

	@Override
	public double getVarLB(int variableIndex) {
		return lpSolver.getVarLB(variables.get(variableIndex));
	}

	@Override
	public double getVarUB(int variableIndex) {
		return lpSolver.getVarUB(variables.get(variableIndex));
	}

	@Override
	public void destroy() {
		lpSolver.destory();
	}

	@Override
	public double getDualVarValue(int constraintIndex) {
		return lpSolver.getDualVarValue(this.constraints.get(constraintIndex));
	}

	@Override
	public long getNumPivots() {
		return lpSolver.getNumPivots();
	}

	@Override
	public void setMaxPivots(long maxPivots) {
		lpSolver.setMaxPivots(maxPivots);
	}

	@Override
	public void saveBasis(String key) {
		lpSolver.saveBasis(key);
	}

	@Override
	public void setBasis(String key) {
		lpSolver.setBasis(key);
	}

	@Override
	public double getObjCoef(int variableIndex) {
		return lpSolver.getObjCoef(variables.get(variableIndex),
				this.objective.get());
	}

	@Override
	public double evaluateObjective(OpenIntToDoubleHashMap varVals) {
		Map<V, Double> convertedSolution = Maps.newHashMap();
		for (Iterator it = varVals.iterator(); it.hasNext();) {
			it.advance();
			int key = it.key();
			double val = it.value();
			convertedSolution.put(this.variables.get(key), val);
		}
		return lpSolver.evaluateObj(convertedSolution, objective.get());
	}

}
