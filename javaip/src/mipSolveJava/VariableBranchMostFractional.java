package mipSolveJava;

import java.util.List;

public class VariableBranchMostFractional implements VariableBranchSelector {

	public static final VariableBranchMostFractional INSTANCE = new VariableBranchMostFractional();

	private VariableBranchMostFractional() {
	}

	@Override
	public int selectVariableForBranching(Solution solution,
			List<Boolean> integerVariables) {
		double best = -1;
		int bestIndex = -1;
		for (int i = 0; i < solution.getVariableValues().length; i++) {
			if (integerVariables.get(i) && !solution.indexIntegral(i)) {
				double fraction = solution.getVariableValues()[i];
				fraction = Math.abs(fraction - Math.round(fraction));
				if (fraction > best) {
					best = fraction;
					bestIndex = i;
					if (best > .499) {
						return bestIndex;
					}
				}

			}
		}
		if (bestIndex >= 0) {
			return bestIndex;
		}
		throw new RuntimeException(
				"Expected solution not to be integral, but was integral");
	}

}
