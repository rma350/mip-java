package mipSolveJava;

import java.util.List;

import lpSolveBase.BasicLpSolver;
import lpSolveBase.SolutionStatus;

public abstract class BestScoreBranching implements VariableBranchSelector {

	private BasicLpSolver basicLpSolver;
	private double fractionUseMinimumImprovement;
	private boolean cacheBasis;
	private int minPivotsResetBasis;
	private String basisKey;

	public BestScoreBranching(BasicLpSolver basicLpSolver) {
		this.basicLpSolver = basicLpSolver;
		this.fractionUseMinimumImprovement = .5;
		this.cacheBasis = true;
		this.minPivotsResetBasis = 10;
		this.basisKey = "cachedBranchingBasis";
	}

	public double score(double branchDownImprovement, double branchUpImprovement) {
		if (Math.abs(IMPROVEMENT_INFEASIBLE - branchDownImprovement) < .01
				|| Math.abs(IMPROVEMENT_INFEASIBLE - branchUpImprovement) < .01) {
			return IMPROVEMENT_INFEASIBLE;
		}
		double maximumImprovement = Math.max(branchDownImprovement,
				branchUpImprovement);
		double minimumImprovement = Math.min(branchDownImprovement,
				branchUpImprovement);
		double score = fractionUseMinimumImprovement * minimumImprovement
				+ (1 - fractionUseMinimumImprovement) * maximumImprovement;
		return score;
	}

	public static double IMPROVEMENT_INFEASIBLE = -1;

	public double computeExactObjectiveImprovementBranchingUp(
			Solution solution, int variableIndex) {
		double initLowerBound = basicLpSolver.getVarLB(variableIndex);
		basicLpSolver.setVarLB(variableIndex,
				Math.ceil(solution.getVariableValues()[variableIndex]));
		basicLpSolver.solve();
		double bestBoundImprovementChangingLower;
		if (basicLpSolver.getSolutionStatus() == SolutionStatus.INFEASIBLE
				|| basicLpSolver.getSolutionStatus() == SolutionStatus.INFEASIBLE_OR_UNBOUNDED) {
			System.err
					.println("Warning: found infeasible solution while branching, doing a hack");
			bestBoundImprovementChangingLower = IMPROVEMENT_INFEASIBLE;
		} else {
			bestBoundImprovementChangingLower = Math.abs(solution.getObjValue()
					- basicLpSolver.getObjValue());
		}

		// clean up
		basicLpSolver.setVarLB(variableIndex, initLowerBound);
		if (cacheBasis
				&& basicLpSolver.getNumPivots() > this.minPivotsResetBasis) {
			basicLpSolver.setBasis(basisKey);
		}
		return bestBoundImprovementChangingLower;
	}

	public double computeExactObjectiveImprovementBranchingDown(
			Solution solution, int variableIndex) {
		// decrease the upper bound
		double initUpperBound = basicLpSolver.getVarUB(variableIndex);
		basicLpSolver.setVarUB(variableIndex,
				Math.floor(solution.getVariableValues()[variableIndex]));
		basicLpSolver.solve();

		double bestBoundImprovementChangingUpper;
		if (basicLpSolver.getSolutionStatus() == SolutionStatus.INFEASIBLE
				|| basicLpSolver.getSolutionStatus() == SolutionStatus.INFEASIBLE_OR_UNBOUNDED) {
			System.err
					.println("Warning: found infeasible solution while branching, doing a hack");
			bestBoundImprovementChangingUpper = IMPROVEMENT_INFEASIBLE;
		} else {
			bestBoundImprovementChangingUpper = Math.abs(solution.getObjValue()
					- basicLpSolver.getObjValue());
		}
		// clean up
		basicLpSolver.setVarUB(variableIndex, initUpperBound);
		if (cacheBasis
				&& basicLpSolver.getNumPivots() > this.minPivotsResetBasis) {
			basicLpSolver.setBasis(basisKey);
		}
		return bestBoundImprovementChangingUpper;
	}

	public double computeVariableScore(Solution solution, int variableIndex) {
		double branchUpImprovement = computeExactObjectiveImprovementBranchingUp(
				solution, variableIndex);
		double branchDownImprovement = computeExactObjectiveImprovementBranchingDown(
				solution, variableIndex);
		return score(branchDownImprovement, branchUpImprovement);
	}

	@Override
	public int selectVariableForBranching(Solution solution,
			List<Boolean> integerVariables) {
		double bestScore = -1;
		int bestIndex = -1;
		if (cacheBasis) {
			basicLpSolver.saveBasis(basisKey);
		}
		for (int i = 0; i < integerVariables.size(); i++) {
			if (integerVariables.get(i) && !solution.indexIntegral(i)) {
				double score = computeVariableScore(solution, i);
				if (Math.abs(IMPROVEMENT_INFEASIBLE - score) < .01) {
					return i;
				}
				if (score > bestScore) {
					bestScore = score;
					bestIndex = i;
				}
			}
		}
		if (bestIndex >= 0) {
			return bestIndex;
		}
		throw new RuntimeException(
				"Error, no variable to branch on was ever selected");
	}
}
