package mipSolveBase;

public interface CutCallback {

	/**
	 * 
	 * @param cutCallbackMipView
	 * @return Indicates if the callback will be processed for this node.
	 */
	public boolean skipCallback(CutCallbackMipView cutCallbackMipView);

	/**
	 * 
	 * @return true if the next cut callback should be processed.
	 */
	public boolean onCallback(CutCallbackMipView cutCallbackMipView);

}
