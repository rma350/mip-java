package lpSolveBase;

import java.util.List;

import com.google.common.collect.Lists;

public abstract class AbstractLpSolver<V, C, O> implements LpSolver<V, C, O> {

	public List<V> createVariables(int numVars) {
		List<V> variables = Lists.newArrayList();
		for (int i = 0; i < numVars; i++) {
			variables.add(createVar());
		}
		return variables;
	}

	/*
	 * public List<V> createVariables(double[] lowerBounds, int numVars,
	 * double[] upperBounds) { List<V> variables = Lists.newArrayList(); for
	 * (int i = 0; i < numVars; i++) {
	 * variables.add(createVariable(lowerBounds[i], upperBounds[i])); } return
	 * variables; }
	 */

}
