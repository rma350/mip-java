package lpSolveBase;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap;

import com.google.common.base.Optional;

public interface BasicLpSolver {

	public int getNumConstrs();

	public int getNumVars();

	public Optional<ObjectiveSense> getObjSense();

	public void solve();

	public double getObjValue();

	public SolutionStatus getSolutionStatus();

	public double getVarValue(int variableIndex);

	public double getDualVarValue(int constraintIndex);

	public long getNumPivots();

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

	/**
	 * Warning: will run in linear time if base LP solver is CPLEX.
	 * 
	 * @param variableIndex
	 * @return
	 */
	public double getObjCoef(int variableIndex);

	public double evaluateObjective(OpenIntToDoubleHashMap varVals);

	public void destroy();

	public void setMaxPivots(long maxPivots);

	public void saveBasis(String key);

	public void setBasis(String key);

}
