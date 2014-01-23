package wrappedMipSolver;


public interface WrappedCutCallback<V, C, O> {

	/**
	 * 
	 * @return true if the next cut callback should be processed.
	 */
	public boolean onCallback(WrappedCutCallbackMipView<V, C, O> mipView);

}
