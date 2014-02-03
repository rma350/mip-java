package mipSolveBase.logging;

import java.util.Map;
import java.util.Map.Entry;

import mipSolveBase.CutCallback;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.google.common.collect.Maps;

public class CutCallbackLogger {

	private Map<CutCallback, CutCallbackLog> logs;
	private long countCallbackStarted;

	public String getNodeLogString() {
		String ans = "";
		for (Entry<CutCallback, CutCallbackLog> entry : logs.entrySet()) {

			if (entry.getValue().getCurrentNodeStatus() == CallbackNodeStatus.RUN_HIT
					|| entry.getValue().getCurrentNodeStatus() == CallbackNodeStatus.RUN_MISS) {
				if (!ans.isEmpty()) {
					ans += ", ";
				}
				ans += entry.getKey().toString() + ": "
						+ entry.getValue().getCurrentNodeNumCutsAdded();
			}
		}
		return ans;
	}

	public CutCallbackLogger() {
		this.logs = Maps.newHashMap();
		this.countCallbackStarted = 0;
	}

	public void onAddCallback(CutCallback cutCallback) {
		logs.put(cutCallback, new CutCallbackLog());
	}

	public CutCallbackLog getLog(CutCallback cutCallback) {
		return logs.get(cutCallback);
	}

	public Map<CutCallback, CutCallbackLog> getLogs() {
		return this.logs;
	}

	public void onNewNode() {
		for (CutCallbackLog cutCallbackLog : logs.values()) {
			cutCallbackLog.onNewNode();
		}
	}

	public void onStartCallback() {
		this.countCallbackStarted++;
	}

	public long getCountCallbackStarted() {
		return this.countCallbackStarted;
	}

	public static enum CallbackNodeStatus {
		SKIPPED, RUN_MISS, RUN_HIT, UNATTEMPTED;
	}

	public static class CutCallbackLog {

		// cummulative state
		private long skippedCount;
		private TimedEventLogger cutTime;
		private SummaryStatistics cutsAdded;

		// current node state
		private long currentNodeCutTime;
		private int currentNodeNumCutsAdded;
		private CallbackNodeStatus currentNodeStatus;

		private CutCallbackLog() {
			// state cummulative state
			this.cutTime = new TimedEventLogger();
			this.cutsAdded = new SummaryStatistics();
			this.skippedCount = 0;
		}

		public void onNewNode() {
			this.currentNodeNumCutsAdded = 0;
			this.currentNodeStatus = CallbackNodeStatus.UNATTEMPTED;
			currentNodeCutTime = 0;
		}

		public void onSkipped() {
			skippedCount++;
			this.currentNodeStatus = CallbackNodeStatus.SKIPPED;
		}

		public void cutTic() {
			this.cutTime.tic();
		}

		public void cutToc(int numCutsAdded) {
			this.cutTime.toc();
			this.currentNodeStatus = numCutsAdded > 0 ? CallbackNodeStatus.RUN_HIT
					: CallbackNodeStatus.RUN_MISS;
			this.currentNodeNumCutsAdded = numCutsAdded;
			this.cutsAdded.addValue(numCutsAdded);
		}

		public int getCurrentNodeNumCutsAdded() {
			return currentNodeNumCutsAdded;
		}

		public long getCurrentNodeCutTime() {
			return this.currentNodeCutTime;
		}

		public CallbackNodeStatus getCurrentNodeStatus() {
			return this.currentNodeStatus;
		}

		public TimedEventLogger getCutTime() {
			return this.cutTime;
		}

		public SummaryStatistics getCutsAdded() {
			return this.cutsAdded;
		}

		public long getSkippedCount() {
			return this.skippedCount;
		}

	}

}
