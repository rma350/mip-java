package mipSolveJava;

public abstract class CutCallback {

	private MipSolver mipSolver;

	protected CutCallback(MipSolver mipSolver) {
		this.mipSolver = mipSolver;
	}

	public double getVarVal(int varIndex) {
		return mipSolver.getLPVarValue(varIndex);
	}

	// public int addConstraint(){
	//
	// }

}
