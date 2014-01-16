package mipSolveJava;

import java.util.List;

public interface VariableBranchSelector {

	int selectVariableForBranching(Solution solution,
			List<Boolean> integerVariables);

}
