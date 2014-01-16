package mipSolveJava;

public abstract class CutCallback {

	private MipSolverInternal mipSolver;
	private int countChecked;
	private int countAtLeastOneConstraintAdded;
	private int countTotalConstraintsAdded;

	protected CutCallback(MipSolverInternal mipSolver) {
		this.mipSolver = mipSolver;
		countChecked = 0;
		countAtLeastOneConstraintAdded = 0;
		countTotalConstraintsAdded = 0;
	}

	protected int createConstr() {
		countTotalConstraintsAdded++;
		return mipSolver.createConstr();
	}

	protected void setConstrCoef(int constrIndex, int varIndex, double value) {
		mipSolver.setConstrCoef(constrIndex, varIndex, value);
	}

	protected void setConstrUB(int constrIndex, double value) {
		mipSolver.setConstrUB(constrIndex, value);
	}

	protected void setConstrLB(int constrIndex, double value) {
		mipSolver.setConstrLB(constrIndex, value);
	}

	public boolean onCallbackWrap(Solution solution) {
		this.countChecked++;
		int totalConstraintsInit = countTotalConstraintsAdded;
		boolean result = onCallback(solution);
		if (totalConstraintsInit < countTotalConstraintsAdded) {
			countAtLeastOneConstraintAdded++;
		}
		return result;
	}

	public int getCountChecked() {
		return countChecked;
	}

	public int getCountAtLeastOneConstraintAdded() {
		return countAtLeastOneConstraintAdded;
	}

	public int getCountTotalConstraintsAdded() {
		return countTotalConstraintsAdded;
	}

	/**
	 * 
	 * @return true if the next cut callback should be processed.
	 */
	protected abstract boolean onCallback(Solution solution);

}
