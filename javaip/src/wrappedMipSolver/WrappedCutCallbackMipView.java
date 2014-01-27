package wrappedMipSolver;

import java.util.List;

public interface WrappedCutCallbackMipView<V, C, O> {

	public long nodesCreated();

	public long nodeStackSize();

	public double getLPVarValue(V var);

	/**
	 * Needed for cplex legacy reasons :(. Getting the value of one variable at
	 * a time in cplex is very slow.
	 * 
	 * @param vars
	 * @return
	 */
	public double[] getLPVarValues(List<V> vars);

	public C createConstr();

	public void setConstrCoef(C constr, V var, double value);

	public void setConstrUB(C constr, double value);

	public void setConstrLB(C constr, double value);

}
