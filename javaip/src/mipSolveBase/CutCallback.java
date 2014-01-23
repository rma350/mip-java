package mipSolveBase;

public interface CutCallback {

	/**
	 * 
	 * @return true if the next cut callback should be processed.
	 */
	public boolean onCallback(CutCallbackMipView cutCallbackMipView);

}
