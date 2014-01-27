package mipSolveJava;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class VariableBranchMostFractionalRandomized implements
		VariableBranchSelector {

	public static final VariableBranchMostFractionalRandomized INSTANCE = new VariableBranchMostFractionalRandomized();

	private double tolerance;

	private VariableBranchMostFractionalRandomized() {
		tolerance = .01;
	}

	@Override
	public int selectVariableForBranching(Solution solution,
			List<Boolean> integerVariables) {
		SortedSet<BranchingVariable> nearOpt = Sets.newTreeSet();
		for (int i = 0; i < solution.getVariableValues().length; i++) {
			if (integerVariables.get(i) && !solution.indexIntegral(i)) {
				double fraction = solution.getVariableValues()[i];
				fraction = Math.abs(fraction - Math.round(fraction));
				if (nearOpt.isEmpty()) {
					nearOpt.add(new BranchingVariable(fraction, i));
				} else if (fraction > nearOpt.last().getDistToInteger()) {
					nearOpt.add(new BranchingVariable(fraction, i));
					for (Iterator<BranchingVariable> it = nearOpt.iterator(); it
							.hasNext();) {
						BranchingVariable low = it.next();
						if (low.getDistToInteger() + tolerance < fraction) {
							it.remove();
						} else {
							break;
						}
					}
				} else if (fraction + tolerance > nearOpt.last()
						.getDistToInteger()) {
					nearOpt.add(new BranchingVariable(fraction, i));
				}
			}
		}
		if (nearOpt.isEmpty()) {
			throw new RuntimeException(
					"Expected solution not to be integral, but was integral");
		} else {
			return Iterables.get(nearOpt,
					(int) Math.floor(Math.random() * nearOpt.size()))
					.getVarIndex();
		}
	}

	private static class BranchingVariable implements
			Comparable<BranchingVariable> {
		private double distToInteger;
		private int varIndex;

		@Override
		public int compareTo(BranchingVariable other) {
			return Double.compare(this.distToInteger, other.distToInteger);
		}

		public double getDistToInteger() {
			return this.distToInteger;
		}

		public int getVarIndex() {
			return this.varIndex;
		}

		public BranchingVariable(double distToInteger, int varIndex) {
			this.distToInteger = distToInteger;
			this.varIndex = varIndex;
		}
	}

}
