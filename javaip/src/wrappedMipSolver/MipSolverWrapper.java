package wrappedMipSolver;

import java.util.Map;

import lpSolveBase.ObjectiveSense;
import lpSolveBase.SolutionStatus;

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
public interface MipSolverWrapper<V, C, O> {

	public V createIntVar();

	public V createNumVar();

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

	public boolean isIntVar(V variable);

	public double getObjValue();

	public ObjectiveSense getObjectiveSense(O objective);

	public SolutionStatus getSolutionStatus();

	public void destroy();

	// public WrappedCutCallbackMipView<V, C, O> getLazyConstrMipView();

	// public WrappedCutCallbackMipView<V, C, O> getUserCutMipView();

	public void addLazyConstrCallback(
			WrappedCutCallback<V, C, O> lazyConstrCallback);

	public void addUserCutCallback(WrappedCutCallback<V, C, O> userCutCallback);

	public void suggestAdvancedStart(Map<V, Double> solution);

}
