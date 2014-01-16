package mipSolveJava;

import java.util.List;

public class VariableBranchLowestIndex implements VariableBranchSelector {

	public static final VariableBranchLowestIndex INSTANCE = new VariableBranchLowestIndex();

	private VariableBranchLowestIndex() {
	}

	@Override
	public int selectVariableForBranching(Solution solution,
			List<Boolean> integerVariables) {
		for (int i = 0; i < solution.getVariableValues().length; i++) {
			if (integerVariables.get(i) && !solution.indexIntegral(i)) {
				return i;
			}
		}
		throw new RuntimeException(
				"Expected solution not to be integral, but was integral");
	}

}
