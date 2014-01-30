package mipSolveJava;

import java.util.List;

import lpSolveBase.BasicLpSolver;

import com.google.common.collect.Lists;

public class StrongBranching implements VariableBranchSelector {

	private BasicLpSolver basicLpSolver;
	private double fractionUseMinimumImprovement;

	public StrongBranching(BasicLpSolver basicLpSolver) {
		this.basicLpSolver = basicLpSolver;
		this.fractionUseMinimumImprovement = .9;
	}

	@Override
	public int selectVariableForBranching(Solution solution,
			List<Boolean> integerVariables) {
		double bestScore = -1;
		int bestIndex = -1;
		List<Long> pivots = Lists.newArrayList();
		String basisFile = "strongBranchBasis";
		basicLpSolver.saveBasis(basisFile);
		for (int i = 0; i < integerVariables.size(); i++) {
			if (integerVariables.get(i) && !solution.indexIntegral(i)) {
				// increase the lower bound

				double initLowerBound = basicLpSolver.getVarLB(i);
				basicLpSolver.setVarLB(i,
						Math.ceil(solution.getVariableValues()[i]));
				basicLpSolver.solve();
				pivots.add(basicLpSolver.getNumPivots());
				double bestBoundImprovementChangingLower = Math.abs(solution
						.getObjValue() - basicLpSolver.getObjValue());
				// clean up
				basicLpSolver.setVarLB(i, initLowerBound);

				// decrease the upper bound
				double initUpperBound = basicLpSolver.getVarUB(i);
				basicLpSolver.setVarUB(i,
						Math.floor(solution.getVariableValues()[i]));
				basicLpSolver.solve();
				double bestBoundImprovementChangingUpper = Math.abs(solution
						.getObjValue() - basicLpSolver.getObjValue());
				// clean up
				basicLpSolver.setVarUB(i, initUpperBound);

				double maximumImprovement = Math.max(
						bestBoundImprovementChangingLower,
						bestBoundImprovementChangingUpper);
				double minimumImprovement = Math.min(
						bestBoundImprovementChangingLower,
						bestBoundImprovementChangingUpper);
				double score = fractionUseMinimumImprovement
						* minimumImprovement
						+ (1 - fractionUseMinimumImprovement)
						* maximumImprovement;
				if (score > bestScore) {
					bestScore = score;
					bestIndex = i;
				}
				basicLpSolver.setBasis(basisFile);
			}
		}
		System.out.println("Pivots: " + pivots);
		if (bestIndex >= 0) {
			return bestIndex;
		}
		throw new RuntimeException(
				"Error, no variable to branch on was ever selected");
	}

}
