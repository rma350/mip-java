package mipSolveJava;

import java.util.List;

import lpSolveBase.BasicLpSolver;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.google.common.collect.Lists;

public class VariableBranchPsuedoCost extends BestScoreBranching {

	private BasicLpSolver basicLpSolver;
	private List<SummaryStatistics> branchingUpPsuedoCosts;
	private List<SummaryStatistics> branchingDownPsuedoCosts;
	private int minimumReliability;

	public VariableBranchPsuedoCost(BasicLpSolver basicLpSolver) {
		super(basicLpSolver);
		this.branchingUpPsuedoCosts = Lists.newArrayList();
		this.branchingDownPsuedoCosts = Lists.newArrayList();
		this.minimumReliability = 4;
	}

	private SummaryStatistics getBranchingUpPsuedoCosts(int variableIndex) {
		return getSummaryStatistics(variableIndex, this.branchingUpPsuedoCosts);
	}

	private SummaryStatistics getBranchingDownPsuedoCosts(int variableIndex) {
		return getSummaryStatistics(variableIndex,
				this.branchingDownPsuedoCosts);
	}

	private SummaryStatistics getSummaryStatistics(int variableIndex,
			List<SummaryStatistics> psuedoCosts) {
		while (psuedoCosts.size() <= variableIndex) {
			psuedoCosts.add(new SummaryStatistics());
		}
		return psuedoCosts.get(variableIndex);
	}

	public boolean usePsuedoCosts(SummaryStatistics psuedoCostSummaryStatistics) {
		return psuedoCostSummaryStatistics.getN() >= minimumReliability;
	}

	@Override
	public double computeVariableScore(Solution solution, int variableIndex) {
		double varVal = solution.getVariableValues()[variableIndex];
		double distUp = Math.ceil(varVal) - varVal;
		double distDown = varVal - Math.floor(varVal);
		double branchUpImprovement;
		if (usePsuedoCosts(getBranchingUpPsuedoCosts(variableIndex))) {
			branchUpImprovement = getBranchingUpPsuedoCosts(variableIndex)
					.getMean() * distUp;
		} else {
			branchUpImprovement = computeExactObjectiveImprovementBranchingUp(
					solution, variableIndex);
			double realizedPsuedoCostUp = branchUpImprovement / distUp;
			getBranchingUpPsuedoCosts(variableIndex).addValue(
					realizedPsuedoCostUp);
		}
		double branchDownImprovement;
		if (usePsuedoCosts(getBranchingDownPsuedoCosts(variableIndex))) {
			branchDownImprovement = getBranchingDownPsuedoCosts(variableIndex)
					.getMean() * distDown;
		} else {
			branchDownImprovement = computeExactObjectiveImprovementBranchingDown(
					solution, variableIndex);
			double realizedPsuedoCostDown = branchDownImprovement / distDown;
			getBranchingDownPsuedoCosts(variableIndex).addValue(
					realizedPsuedoCostDown);
		}
		return score(branchDownImprovement, branchUpImprovement);
	}

}
