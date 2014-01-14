package mipSolveJava;

public class VariableBranchLowestIndex implements VariableBranchSelector {

	public static final VariableBranchLowestIndex INSTANCE = new VariableBranchLowestIndex();

	private VariableBranchLowestIndex() {
	}

	@Override
	public int selectVariableForBranching(Solution solution,
			boolean[] integerVariables) {
		for (int i = 0; i < solution.getVariableValues().length; i++) {
			if (integerVariables[i] && !solution.indexIntegral(i)) {
				return i;
			}
		}
		throw new RuntimeException(
				"Expected solution not to be integral, but was integral");
	}

}
