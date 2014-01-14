package mipSolveJava;

public interface VariableBranchSelector {

	int selectVariableForBranching(Solution solution, boolean[] integerVariables);

}
