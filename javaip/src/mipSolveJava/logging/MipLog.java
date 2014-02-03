package mipSolveJava.logging;

import mipSolveBase.logging.CutCallbackLogger;
import mipSolveBase.logging.TimedEventLogger;

public class MipLog {

	private TimedEventLogger branchingTimer;
	private TimedEventLogger lpTimer;

	private CutCallbackLogger userCutCallbackLog;
	private CutCallbackLogger lazyConstraintCallbackLog;

	private NodeLog lastNodeLog;

	private long startTime;
	private long endTime;
	private long solveTimeMs;

	public MipLog() {
		this.userCutCallbackLog = new CutCallbackLogger();
		this.lazyConstraintCallbackLog = new CutCallbackLogger();
		this.branchingTimer = new TimedEventLogger();
		this.lpTimer = new TimedEventLogger();
	}

	public NodeLog getLastNodeLog() {
		return this.lastNodeLog;
	}

	public void setLastNodeLog(NodeLog nodeLog) {
		this.lastNodeLog = nodeLog;
	}

	public long getEllapsedTimeMs() {
		return System.currentTimeMillis() - startTime;
	}

	public long getSolveTimeMs() {
		return this.solveTimeMs;
	}

	public void tic() {
		this.startTime = System.currentTimeMillis();
	}

	public void toc() {
		this.endTime = System.currentTimeMillis();
		this.solveTimeMs = endTime - startTime;
	}

	public TimedEventLogger getBranchingTimer() {
		return branchingTimer;
	}

	public TimedEventLogger getLpTimer() {
		return lpTimer;
	}

	public CutCallbackLogger getUserCutCallbackLog() {
		return userCutCallbackLog;
	}

	public CutCallbackLogger getLazyConstraintCallbackLog() {
		return lazyConstraintCallbackLog;
	}

	public void onNewNode() {
		this.userCutCallbackLog.onNewNode();
		this.lazyConstraintCallbackLog.onNewNode();
	}

}
