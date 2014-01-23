package mipSolveBase;

public interface CutCallbackMipView {

	public long nodesCreated();

	public long nodeStackSize();

	public double getLPVarValue(int index);

	public int createConstr();

	public void setConstrCoef(int constrIndex, int varIndex, double value);

	public void setConstrUB(int constrIndex, double value);

	public void setConstrLB(int constrIndex, double value);

}
