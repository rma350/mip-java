package mipSolveJava;

import lpSolveBase.ObjectiveSense;
import lpSolveBase.SolutionStatus;

import com.google.common.base.Optional;

public interface MipSolver {

	// create MIP elements

	public int createIntVar();

	public int createNumVar();

	public int createConstr();

	public void createObj(ObjectiveSense objectiveSense);

	// set MIP elements

	public void setObjCoef(int variableIndex, double value);

	public void setVarLB(int variableIndex, double value);

	public void setVarUB(int variableIndex, double value);

	public void setConstrLB(int constraintIndex, double value);

	public void setConstrUB(int constraintIndex, double value);

	public void setConstrCoef(int constraintIndex, int variableIndex,
			double value);

	// query MIP elements

	public int getNumConstrs();

	public int getNumVars();

	public Optional<ObjectiveSense> getObjSense();

	public double getVarLB(int variableIndex);

	public double getVarUB(int variableIndex);

	public boolean varIsInteger(int variableIndex);

	// solve

	public void solve();

	// query results

	public SolutionStatus getSolutionStatus();

	public double getVarValue(int varIndex);

	public double getObjValue();

	// clean up

	public void destroy();

}
