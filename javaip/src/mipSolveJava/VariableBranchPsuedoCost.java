package mipSolveJava;

import java.util.List;

import lpSolveBase.BasicLpSolver;

public class VariableBranchPsuedoCost implements VariableBranchSelector {

	private BasicLpSolver basicLpSolver;
	private double fractionUseMinimumImprovement;

	public VariableBranchPsuedoCost(BasicLpSolver basicLpSolver) {
		this.basicLpSolver = basicLpSolver;
		this.fractionUseMinimumImprovement = .9;
	}

	@Override
	public int selectVariableForBranching(Solution solution,
			List<Boolean> integerVariables) {
		throw new RuntimeException(
				"Error, no variable to branch on was ever selected");
	}

}
