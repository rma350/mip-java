package mipSolveJava;

public interface MipSolverInternal extends MipSolver {

	public long nodesCreated();

	public long nodeStackSize();

	public double getLPVarValue(int index);

	// add callbacks

	public void addLazyConstraintCallback(CutCallback cutCallback);

	public void addUserCutCallback(CutCallback cutCallback);

}
