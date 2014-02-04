package lpSolveBase;

import java.util.Map;

/**
 * 
 * @author ross
 * 
 * @param <V>
 *            The type of variable objects. E.g. IloNumVar.
 * @param <C>
 *            The type of constraint objects. E.g. IloRange
 * 
 * @param <O>
 *            The type of the objective. E.g. IloObjective
 */
public interface LpSolver<V, C, O> {

	// public int getNumVariables();

	// public int getNumConstraints();

	public V createVar();

	public C createConstr();

	public O createObj(ObjectiveSense objectiveSense);

	public void setObjCoef(O objective, V variable, double value);

	public void setConstrCoef(C constraint, V variable, double value);

	public void setConstrUB(C constraint, double value);

	public void setConstrLB(C constraint, double value);

	public void setVarUB(V variable, double upperBound);

	public void setVarLB(V variable, double lowerBound);

	public void setMaxPivots(long maxPivots);

	public void solve();

	public double getVarUB(V variable);

	public double getVarLB(V variable);

	/**
	 * Warning: will run in linear time if CPLEX is used as LP solver
	 * 
	 * @param variable
	 * @param objective
	 * @return
	 */
	public double getObjCoef(V variable, O objective);

	/**
	 * Runs in linear time if CPLEX is used as LP solver. Preferable to
	 * getObjCoef.
	 * 
	 * @param varVals
	 * @param objective
	 * @return
	 */
	public double evaluateObj(Map<V, Double> varVals, O objective);

	public double getVarValue(V variable);

	public double getDualVarValue(C constraint);

	public double getObjValue();

	public long getNumPivots();

	public ObjectiveSense getObjectiveSense(O objective);

	public SolutionStatus getSolutionStatus();

	public void destory();

	public void saveBasis(String key);

	public void setBasis(String key);

}
