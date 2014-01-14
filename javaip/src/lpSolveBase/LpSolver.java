package lpSolveBase;

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

	public void solve();

	public double getVarUB(V variable);

	public double getVarLB(V variable);

	public double getVarValue(V variable);

	public double getObjValue();

	public ObjectiveSense getObjectiveSense(O objective);

	public SolutionStatus getSolutionStatus();

}
