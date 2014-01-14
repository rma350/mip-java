package lpSolveBase;

import com.google.common.base.Optional;

public interface BasicLpSolver {

	public int getNumConstraints();

	public int getNumVariables();

	public Optional<ObjectiveSense> getObjectiveSense();

	public void solve();

	public double getObjValue();

	public SolutionStatus getSolutionStatus();

	public double getVarValue(int variableIndex);

	public int createVar();

	public int createConstr();

	public void createObj(ObjectiveSense objectiveSense);

	public void setObjCoef(int variableIndex, double value);

	public void setVarLB(int variableIndex, double value);

	public void setVarUB(int variableIndex, double value);

	public void setConstrLB(int constraintIndex, double value);

	public void setConstrUB(int constraintIndex, double value);

	public void setConstrCoef(int constraintIndex, int variableIndex,
			double value);

	public double getVarLB(int variableIndex);

	public double getVarUB(int variableIndex);
	
	public void destroy();

}
